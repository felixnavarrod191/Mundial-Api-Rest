package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Competicion;
import com.mundial_apirest.app.repositorios.competicion_repositorio;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CLIENTE-SERVIDOR: atiende las 2 paginas de Competiciones (formulario y lista). */
@Controller
@RequestMapping("/competiciones")
public class competicion_web {

    private final competicion_repositorio repositorio;
    private final ClubServicio clubes;

    public competicion_web(competicion_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // Pagina 1: formulario vacio
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("competicion", new Competicion());
        return "competicion_index";
    }

    // Pagina 1: formulario cargado con un registro existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repositorio.findById(id)
            .map(x -> {
                model.addAttribute("competicion", x);
                return "competicion_index";
            })
            .orElseGet(() -> {
                ra.addFlashAttribute("error", "El registro no existe");
                return "redirect:/competiciones";
            });
    }

    // Guarda (crea si no tiene id, actualiza si lo tiene)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("competicion") Competicion competicion,
                          BindingResult resultado, RedirectAttributes ra) {
        if (competicion.getFechaInicio() != null && competicion.getFechaFin() != null
                && competicion.getFechaFin().isBefore(competicion.getFechaInicio())) {
            resultado.rejectValue("fechaFin", "fecha.rango",
                "La fecha de fin no puede ser anterior a la de inicio");
        }
        if (resultado.hasErrors()) {
            return "competicion_index";
        }
        boolean nuevo = (competicion.getId() == null);
        repositorio.save(competicion);
        ra.addFlashAttribute("mensaje", nuevo
            ? "Competición guardado correctamente"
            : "Competición actualizado correctamente");
        return "redirect:/competiciones";      // patron Post-Redirect-Get
    }

    // Pagina 2: lista con buscador
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Competicion> lista = (buscar == null || buscar.isBlank())
            ? repositorio.findAll(Sort.by("id"))
            : repositorio.findByNombreContainingIgnoreCase(buscar.trim());
        model.addAttribute("lista", lista);
        model.addAttribute("buscar", buscar);
        return "competicion_listar";
    }

    // Elimina (si ningun club lo esta usando)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        if (!repositorio.existsById(id)) {
            ra.addFlashAttribute("error", "El registro no existe");
        } else {
            String club = clubes.clubDeCompeticion(id);
            if (club != null) {
                ra.addFlashAttribute("error",
                    "No se puede eliminar: está en uso por el club " + club);
            } else {
                repositorio.deleteById(id);
                ra.addFlashAttribute("mensaje", "Competición eliminado correctamente");
            }
        }
        return "redirect:/competiciones";
    }
}
