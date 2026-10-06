package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Entrenador;
import com.mundial_apirest.app.repositorios.entrenador_repositorio;
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

/** API REST de Entrenadores: responde JSON en /api/entrenadores */
@RestController
@RequestMapping("/api/entrenadores")
@CrossOrigin
public class entrenador_apirest {

    private final entrenador_repositorio repositorio;
    private final ClubServicio clubes;

    public entrenador_apirest(entrenador_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // GET /api/entrenadores -> lista completa
    @GetMapping
    public List<Entrenador> listar() {
        return repositorio.findAll(Sort.by("id"));
    }

    // GET /api/entrenadores/1 -> 200 o 404
    @GetMapping("/{id}")
    public ResponseEntity<Entrenador> obtener(@PathVariable Long id) {
        return repositorio.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/entrenadores/buscar?nombre=abc
    @GetMapping("/buscar")
    public List<Entrenador> buscar(@RequestParam String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    // POST /api/entrenadores -> 201 Created
    @PostMapping
    public ResponseEntity<Object> crear(@Valid @RequestBody Entrenador x) {
        x.setId(null);                          // el id lo genera el servidor
        Entrenador guardado = repositorio.save(x);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(guardado);
    }

    // PUT /api/entrenadores/1 -> 200 o 404
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Entrenador x) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        x.setId(id);
        return ResponseEntity.ok(repositorio.save(x));
    }

    // DELETE /api/entrenadores/1 -> 204, 404 o 409 (si lo usa un club)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String club = clubes.clubDeEntrenador(id);
        if (club != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", "No se puede eliminar: está en uso por el club " + club));
        }
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
