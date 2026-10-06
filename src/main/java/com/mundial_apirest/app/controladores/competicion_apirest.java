package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Competicion;
import com.mundial_apirest.app.repositorios.competicion_repositorio;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** API REST de Competiciones: responde JSON en /api/competiciones */
@RestController
@RequestMapping("/api/competiciones")
@CrossOrigin
public class competicion_apirest {

    private final competicion_repositorio repositorio;
    private final ClubServicio clubes;

    public competicion_apirest(competicion_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // GET /api/competiciones -> lista completa
    @GetMapping
    public List<Competicion> listar() {
        return repositorio.findAll(Sort.by("id"));
    }

    // GET /api/competiciones/1 -> 200 o 404
    @GetMapping("/{id}")
    public ResponseEntity<Competicion> obtener(@PathVariable Long id) {
        return repositorio.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/competiciones/buscar?nombre=abc
    @GetMapping("/buscar")
    public List<Competicion> buscar(@RequestParam String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    // POST /api/competiciones -> 201 Created
    @PostMapping
    public ResponseEntity<Object> crear(@Valid @RequestBody Competicion x) {
        if (x.getFechaFin().isBefore(x.getFechaInicio())) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "La fecha de fin no puede ser anterior a la de inicio"));
        }
        x.setId(null);                          // el id lo genera el servidor
        Competicion guardado = repositorio.save(x);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(guardado);
    }

    // PUT /api/competiciones/1 -> 200 o 404
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Competicion x) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (x.getFechaFin().isBefore(x.getFechaInicio())) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "La fecha de fin no puede ser anterior a la de inicio"));
        }
        x.setId(id);
        return ResponseEntity.ok(repositorio.save(x));
    }

    // DELETE /api/competiciones/1 -> 204, 404 o 409 (si lo usa un club)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String club = clubes.clubDeCompeticion(id);
        if (club != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", "No se puede eliminar: está en uso por el club " + club));
        }
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
