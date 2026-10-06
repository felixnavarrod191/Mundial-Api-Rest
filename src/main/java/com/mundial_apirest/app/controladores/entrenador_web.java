package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Entrenador;
import com.mundial_apirest.app.repositorios.entrenador_repositorio;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CLIENTE-SERVIDOR: atiende las 2 paginas de Entrenadores (formulario y lista). */
@Controller
@RequestMapping("/entrenadores")
public class entrenador_web {

    private final entrenador_repositorio repositorio;
    private final ClubServicio clubes;

    public entrenador_web(entrenador_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // Pagina 1: formulario vacio
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("entrenador", new Entrenador());
        return "entrenador_index";
    }

    // Pagina 1: formulario cargado con un registro existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repositorio.findById(id)
            .map(x -> {
                model.addAttribute("entrenador", x);
                return "entrenador_index";
            })
            .orElseGet(() -> {
                ra.addFlashAttribute("error", "El registro no existe");
                return "redirect:/entrenadores";
            });
    }

    // Guarda (crea si no tiene id, actualiza si lo tiene)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("entrenador") Entrenador entrenador,
                          BindingResult resultado, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "entrenador_index";
        }
        boolean nuevo = (entrenador.getId() == null);
        repositorio.save(entrenador);
        ra.addFlashAttribute("mensaje", nuevo
            ? "Entrenador guardado correctamente"
            : "Entrenador actualizado correctamente");
        return "redirect:/entrenadores";      // patron Post-Redirect-Get
    }

    // Pagina 2: lista con buscador
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Entrenador> lista = (buscar == null || buscar.isBlank())
            ? repositorio.findAll(Sort.by("id"))
            : repositorio.findByNombreContainingIgnoreCase(buscar.trim());
        model.addAttribute("lista", lista);
        model.addAttribute("buscar", buscar);
        model.addAttribute("clubDe", clubes.clubPorEntrenador());
        return "entrenador_listar";
    }

    // Elimina (si ningun club lo esta usando)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        if (!repositorio.existsById(id)) {
            ra.addFlashAttribute("error", "El registro no existe");
        } else {
            String club = clubes.clubDeEntrenador(id);
            if (club != null) {
                ra.addFlashAttribute("error",
                    "No se puede eliminar: está en uso por el club " + club);
            } else {
                repositorio.deleteById(id);
                ra.addFlashAttribute("mensaje", "Entrenador eliminado correctamente");
            }
        }
        return "redirect:/entrenadores";
    }
}
