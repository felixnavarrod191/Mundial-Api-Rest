package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Club;
import com.mundial_apirest.app.repositorios.club_repositorio;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * API REST de Clubes: /api/clubes
 * En POST y PUT las relaciones se envian solo con el id:
 * {"nombre":"..","entrenador":{"id":1},"asociacion":{"id":1},
 *  "jugadores":[{"id":1}],"competiciones":[{"id":1}]}
 */
@RestController
@RequestMapping("/api/clubes")
@CrossOrigin
public class club_apirest {

    private final club_repositorio repositorio;
    private final ClubServicio servicio;

    public club_apirest(club_repositorio repositorio, ClubServicio servicio) {
        this.repositorio = repositorio;
        this.servicio = servicio;
    }

    // GET /api/clubes -> lista con todas las relaciones expandidas
    @GetMapping
    public List<Club> listar() {
        return repositorio.findAll(Sort.by("id"));
    }

    // GET /api/clubes/1 -> 200 o 404
    @GetMapping("/{id}")
    public ResponseEntity<Club> obtener(@PathVariable Long id) {
        return repositorio.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/clubes/buscar?nombre=mill
    @GetMapping("/buscar")
    public List<Club> buscar(@RequestParam String nombre) {
        return repositorio.findByNombreContainingIgnoreCase(nombre);
    }

    // POST /api/clubes -> 201 (400 si algun id no existe o esta ocupado)
    @PostMapping
    public ResponseEntity<Club> crear(@Valid @RequestBody Club club) {
        club.setId(null);
        Club guardado = servicio.guardar(club);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(guardado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(guardado);
    }

    // PUT /api/clubes/1 -> 200, 400 o 404
    @PutMapping("/{id}")
    public ResponseEntity<Club> actualizar(@PathVariable Long id, @Valid @RequestBody Club club) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        club.setId(id);
        return ResponseEntity.ok(servicio.guardar(club));
    }

    // DELETE /api/clubes/1 -> 204 o 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
