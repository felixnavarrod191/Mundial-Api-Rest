package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Jugador;
import com.mundial_apirest.app.repositorios.jugador_repositorio;
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

/** API REST de Jugadores: responde JSON en /api/jugadores */
@RestController
@RequestMapping("/api/jugadores")
@CrossOrigin
public class jugador_apirest {

    private final jugador_repositorio repositorio;
    private final ClubServicio clubes;

    public jugador_apirest(jugador_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // GET /api/jugadores -> lista completa
    @GetMapping
    public List<Jugador> listar() {
        return repositorio.findAll(Sort.by("id"));
    }

    // GET /api/jugadores/1 -> 200 o 404
    @GetMapping("/{id}")
    public ResponseEntity<Jugador> obtener(@PathVariable Long id) {
        return repositorio.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/jugadores/buscar?nombre=abc
    @GetMapping("/buscar")
    public List<Jugador> buscar(@RequestParam String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    // POST /api/jugadores -> 201 Created
    @PostMapping
    public ResponseEntity<Object> crear(@Valid @RequestBody Jugador x) {
        x.setId(null);                          // el id lo genera el servidor
        Jugador guardado = repositorio.save(x);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(guardado);
    }

    // PUT /api/jugadores/1 -> 200 o 404
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Jugador x) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        x.setId(id);
        return ResponseEntity.ok(repositorio.save(x));
    }

    // DELETE /api/jugadores/1 -> 204, 404 o 409 (si lo usa un club)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String club = clubes.clubDeJugador(id);
        if (club != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", "No se puede eliminar: está en uso por el club " + club));
        }
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
