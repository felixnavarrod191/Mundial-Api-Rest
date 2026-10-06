package com.mundial_apirest.app.servicios;

import com.mundial_apirest.app.entidades.*;
import com.mundial_apirest.app.repositorios.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Reglas de negocio de las relaciones:
 *  - Club -> Entrenador   (uno a uno: un entrenador dirige a un solo club)
 *  - Club -> Jugadores    (uno a muchos: un jugador pertenece a un solo club)
 *  - Club -> Asociacion   (muchos a uno)
 *  - Club -> Competiciones (muchos a muchos)
 * Tambien simula la restriccion "foreign key": no se puede borrar algo que un club usa.
 */
@Service
public class ClubServicio {

    private final club_repositorio clubes;
    private final entrenador_repositorio entrenadores;
    private final jugador_repositorio jugadores;
    private final asociacion_repositorio asociaciones;
    private final competicion_repositorio competiciones;

    public ClubServicio(club_repositorio clubes, entrenador_repositorio entrenadores,
                        jugador_repositorio jugadores, asociacion_repositorio asociaciones,
                        competicion_repositorio competiciones) {
        this.clubes = clubes;
        this.entrenadores = entrenadores;
        this.jugadores = jugadores;
        this.asociaciones = asociaciones;
        this.competiciones = competiciones;
    }

    // ------------------------------------------------------------------
    // Consultas de uso (para mostrar "club: X" y para impedir borrados)
    // ------------------------------------------------------------------
    public Map<Long, Club> clubPorEntrenador() {
        Map<Long, Club> mapa = new HashMap<>();
        for (Club c : clubes.findAll()) {
            if (c.getEntrenador() != null && c.getEntrenador().getId() != null) {
                mapa.put(c.getEntrenador().getId(), c);
            }
        }
        return mapa;
    }

    public Map<Long, Club> clubPorJugador() {
        Map<Long, Club> mapa = new HashMap<>();
        for (Club c : clubes.findAll()) {
            if (c.getJugadores() == null) continue;
            for (Jugador j : c.getJugadores()) {
                if (j != null && j.getId() != null) {
                    mapa.put(j.getId(), c);
                }
            }
        }
        return mapa;
    }

    public String clubDeEntrenador(Long id) {
        Club c = clubPorEntrenador().get(id);
        return (c == null) ? null : c.getNombre();
    }

    public String clubDeJugador(Long id) {
        Club c = clubPorJugador().get(id);
        return (c == null) ? null : c.getNombre();
    }

    public String clubDeAsociacion(Long id) {
        for (Club c : clubes.findAll()) {
            if (c.getAsociacion() != null && id.equals(c.getAsociacion().getId())) {
                return c.getNombre();
            }
        }
        return null;
    }

    public String clubDeCompeticion(Long id) {
        for (Club c : clubes.findAll()) {
            if (c.getCompeticiones() == null) continue;
            for (Competicion k : c.getCompeticiones()) {
                if (k != null && id.equals(k.getId())) {
                    return c.getNombre();
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Utilidades para extraer ids de un club
    // ------------------------------------------------------------------
    public static Long idEntrenador(Club c) {
        return (c.getEntrenador() == null) ? null : c.getEntrenador().getId();
    }

    public static Long idAsociacion(Club c) {
        return (c.getAsociacion() == null) ? null : c.getAsociacion().getId();
    }

    public static List<Long> idsJugadores(Club c) {
        List<Long> ids = new ArrayList<>();
        if (c.getJugadores() != null) {
            for (Jugador j : c.getJugadores()) {
                if (j != null && j.getId() != null) ids.add(j.getId());
            }
        }
        return ids;
    }

    public static List<Long> idsCompeticiones(Club c) {
        List<Long> ids = new ArrayList<>();
        if (c.getCompeticiones() != null) {
            for (Competicion k : c.getCompeticiones()) {
                if (k != null && k.getId() != null) ids.add(k.getId());
            }
        }
        return ids;
    }

    private static List<Long> sinRepetir(List<Long> ids) {
        List<Long> resultado = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                if (id != null && !resultado.contains(id)) resultado.add(id);
            }
        }
        return resultado;
    }

    // ------------------------------------------------------------------
    // Guardar un club validando que todo lo que referencia exista
    // ------------------------------------------------------------------

    /** Version para la API REST: los ids vienen dentro del propio objeto Club (JSON). */
    public Club guardar(Club club) {
        return guardar(club, idEntrenador(club), idsJugadores(club),
                       idAsociacion(club), idsCompeticiones(club));
    }

    /** Version para el formulario web: los ids vienen como parametros sueltos. */
    public Club guardar(Club club, Long entrenadorId, List<Long> jugadoresIds,
                        Long asociacionId, List<Long> competicionesIds) {

        if (asociacionId == null) {
            throw new IllegalArgumentException("Debe seleccionar una asociación");
        }
        if (entrenadorId == null) {
            throw new IllegalArgumentException("Debe seleccionar un entrenador");
        }

        Asociacion asociacion = asociaciones.findById(asociacionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "La asociación con id " + asociacionId + " no existe"));
        Entrenador entrenador = entrenadores.findById(entrenadorId)
            .orElseThrow(() -> new IllegalArgumentException(
                "El entrenador con id " + entrenadorId + " no existe"));

        List<Jugador> plantel = new ArrayList<>();
        for (Long idJ : sinRepetir(jugadoresIds)) {
            plantel.add(jugadores.findById(idJ).orElseThrow(() ->
                new IllegalArgumentException("El jugador con id " + idJ + " no existe")));
        }

        List<Competicion> torneos = new ArrayList<>();
        for (Long idC : sinRepetir(competicionesIds)) {
            torneos.add(competiciones.findById(idC).orElseThrow(() ->
                new IllegalArgumentException("La competición con id " + idC + " no existe")));
        }

        // Relaciones exclusivas: el entrenador (1-1) y los jugadores (1-N) solo en un club
        for (Club otro : clubes.findAll()) {
            if (club.getId() != null && club.getId().equals(otro.getId())) continue;

            if (otro.getEntrenador() != null && entrenadorId.equals(otro.getEntrenador().getId())) {
                throw new IllegalArgumentException(
                    "El entrenador ya dirige al club " + otro.getNombre() + " (relación uno a uno)");
            }
            if (otro.getJugadores() != null) {
                for (Jugador nuevo : plantel) {
                    for (Jugador existente : otro.getJugadores()) {
                        if (existente != null && nuevo.getId().equals(existente.getId())) {
                            throw new IllegalArgumentException(
                                "El jugador " + nuevo.getNombre() + " " + nuevo.getApellido()
                                + " ya pertenece al club " + otro.getNombre());
                        }
                    }
                }
            }
        }

        club.setAsociacion(asociacion);
        club.setEntrenador(entrenador);
        club.setJugadores(plantel);
        club.setCompeticiones(torneos);

        Club guardado = clubes.save(club);
        return clubes.findById(guardado.getId()).orElse(guardado);
    }
}
