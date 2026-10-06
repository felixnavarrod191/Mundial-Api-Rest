package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Jugador;
import com.mundial_apirest.app.repositorios.jugador_repositorio;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CLIENTE-SERVIDOR: atiende las 2 paginas de Jugadores (formulario y lista). */
@Controller
@RequestMapping("/jugadores")
public class jugador_web {

    private final jugador_repositorio repositorio;
    private final ClubServicio clubes;

    public jugador_web(jugador_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    @ModelAttribute("posiciones")
    public List<String> posiciones() {
        return List.of("Portero", "Defensa", "Mediocampista", "Delantero");
    }

    // Pagina 1: formulario vacio
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("jugador", new Jugador());
        return "jugador_index";
    }

    // Pagina 1: formulario cargado con un registro existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repositorio.findById(id)
            .map(x -> {
                model.addAttribute("jugador", x);
                return "jugador_index";
            })
            .orElseGet(() -> {
                ra.addFlashAttribute("error", "El registro no existe");
                return "redirect:/jugadores";
            });
    }

    // Guarda (crea si no tiene id, actualiza si lo tiene)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("jugador") Jugador jugador,
                          BindingResult resultado, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "jugador_index";
        }
        boolean nuevo = (jugador.getId() == null);
        repositorio.save(jugador);
        ra.addFlashAttribute("mensaje", nuevo
            ? "Jugador guardado correctamente"
            : "Jugador actualizado correctamente");
        return "redirect:/jugadores";      // patron Post-Redirect-Get
    }

    // Pagina 2: lista con buscador
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Jugador> lista = (buscar == null || buscar.isBlank())
            ? repositorio.findAll(Sort.by("id"))
            : repositorio.findByNombreContainingIgnoreCase(buscar.trim());
        model.addAttribute("lista", lista);
        model.addAttribute("buscar", buscar);
        model.addAttribute("clubDe", clubes.clubPorJugador());
        return "jugador_listar";
    }

    // Elimina (si ningun club lo esta usando)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        if (!repositorio.existsById(id)) {
            ra.addFlashAttribute("error", "El registro no existe");
        } else {
            String club = clubes.clubDeJugador(id);
            if (club != null) {
                ra.addFlashAttribute("error",
                    "No se puede eliminar: está en uso por el club " + club);
            } else {
                repositorio.deleteById(id);
                ra.addFlashAttribute("mensaje", "Jugador eliminado correctamente");
            }
        }
        return "redirect:/jugadores";
    }
}
