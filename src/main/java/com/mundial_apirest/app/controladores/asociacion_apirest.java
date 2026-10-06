package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Asociacion;
import com.mundial_apirest.app.repositorios.asociacion_repositorio;
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

/** API REST de Asociaciones: responde JSON en /api/asociaciones */
@RestController
@RequestMapping("/api/asociaciones")
@CrossOrigin
public class asociacion_apirest {

    private final asociacion_repositorio repositorio;
    private final ClubServicio clubes;

    public asociacion_apirest(asociacion_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // GET /api/asociaciones -> lista completa
    @GetMapping
    public List<Asociacion> listar() {
        return repositorio.findAll(Sort.by("id"));
    }

    // GET /api/asociaciones/1 -> 200 o 404
    @GetMapping("/{id}")
    public ResponseEntity<Asociacion> obtener(@PathVariable Long id) {
        return repositorio.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/asociaciones/buscar?nombre=abc
    @GetMapping("/buscar")
    public List<Asociacion> buscar(@RequestParam String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    // POST /api/asociaciones -> 201 Created
    @PostMapping
    public ResponseEntity<Object> crear(@Valid @RequestBody Asociacion x) {
        x.setId(null);                          // el id lo genera el servidor
        Asociacion guardado = repositorio.save(x);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(guardado);
    }

    // PUT /api/asociaciones/1 -> 200 o 404
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Asociacion x) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        x.setId(id);
        return ResponseEntity.ok(repositorio.save(x));
    }

    // DELETE /api/asociaciones/1 -> 204, 404 o 409 (si lo usa un club)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String club = clubes.clubDeAsociacion(id);
        if (club != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", "No se puede eliminar: está en uso por el club " + club));
        }
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
