package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Club;
import com.mundial_apirest.app.repositorios.*;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CLIENTE-SERVIDOR: atiende las 2 paginas de Clubes (formulario y lista). */
@Controller
@RequestMapping("/clubes")
public class club_web {

    private final club_repositorio repositorio;
    private final entrenador_repositorio entrenadores;
    private final jugador_repositorio jugadores;
    private final asociacion_repositorio asociaciones;
    private final competicion_repositorio competiciones;
    private final ClubServicio servicio;

    public club_web(club_repositorio repositorio, entrenador_repositorio entrenadores,
                    jugador_repositorio jugadores, asociacion_repositorio asociaciones,
                    competicion_repositorio competiciones, ClubServicio servicio) {
        this.repositorio = repositorio;
        this.entrenadores = entrenadores;
        this.jugadores = jugadores;
        this.asociaciones = asociaciones;
        this.competiciones = competiciones;
        this.servicio = servicio;
    }

    // Carga las listas que se usan en los select / checkbox del formulario
    private void cargarCombos(Model model, Long entrenadorSel, List<Long> jugadoresSel,
                              Long asociacionSel, List<Long> competicionesSel) {
        model.addAttribute("entrenadores", entrenadores.findAll(Sort.by("id")));
        model.addAttribute("jugadores", jugadores.findAll(Sort.by("id")));
        model.addAttribute("asociaciones", asociaciones.findAll(Sort.by("id")));
        model.addAttribute("competiciones", competiciones.findAll(Sort.by("id")));
        model.addAttribute("ocupEnt", servicio.clubPorEntrenador());
        model.addAttribute("ocupJug", servicio.clubPorJugador());
        model.addAttribute("entrenadorSel", entrenadorSel);
        model.addAttribute("jugadoresSel", jugadoresSel);
        model.addAttribute("asociacionSel", asociacionSel);
        model.addAttribute("competicionesSel", competicionesSel);
    }

    // Pagina 1: formulario vacio
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("club", new Club());
        cargarCombos(model, null, List.of(), null, List.of());
        return "club_index";
    }

    // Pagina 1: formulario cargado con un club existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Club> encontrado = repositorio.findById(id);
        if (encontrado.isEmpty()) {
            ra.addFlashAttribute("error", "El club no existe");
            return "redirect:/clubes";
        }
        Club c = encontrado.get();
        model.addAttribute("club", c);
        cargarCombos(model, ClubServicio.idEntrenador(c), ClubServicio.idsJugadores(c),
                     ClubServicio.idAsociacion(c), ClubServicio.idsCompeticiones(c));
        return "club_index";
    }

    // Guarda el club con sus relaciones
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("club") Club club, BindingResult resultado,
                          @RequestParam(required = false) Long entrenadorId,
                          @RequestParam(required = false) List<Long> jugadoresIds,
                          @RequestParam(required = false) Long asociacionId,
                          @RequestParam(required = false) List<Long> competicionesIds,
                          Model model, RedirectAttributes ra) {

        List<Long> jug = (jugadoresIds == null) ? List.of() : jugadoresIds;
        List<Long> comp = (competicionesIds == null) ? List.of() : competicionesIds;

        if (resultado.hasErrors()) {
            cargarCombos(model, entrenadorId, jug, asociacionId, comp);
            return "club_index";
        }
        try {
            boolean nuevo = (club.getId() == null);
            servicio.guardar(club, entrenadorId, jug, asociacionId, comp);
            ra.addFlashAttribute("mensaje", nuevo
                ? "Club guardado correctamente"
                : "Club actualizado correctamente");
            return "redirect:/clubes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarCombos(model, entrenadorId, jug, asociacionId, comp);
            return "club_index";
        }
    }

    // Pagina 2: lista con buscador
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Club> lista = (buscar == null || buscar.isBlank())
            ? repositorio.findAll(Sort.by("id"))
            : repositorio.findByNombreContainingIgnoreCase(buscar.trim());
        model.addAttribute("lista", lista);
        model.addAttribute("buscar", buscar);
        return "club_listar";
    }

    // Elimina un club (los demas registros quedan intactos)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        if (repositorio.existsById(id)) {
            repositorio.deleteById(id);
            ra.addFlashAttribute("mensaje", "Club eliminado correctamente");
        } else {
            ra.addFlashAttribute("error", "El club no existe");
        }
        return "redirect:/clubes";
    }
}
