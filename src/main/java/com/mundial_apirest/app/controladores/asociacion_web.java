package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.entidades.Asociacion;
import com.mundial_apirest.app.repositorios.asociacion_repositorio;
import com.mundial_apirest.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CLIENTE-SERVIDOR: atiende las 2 paginas de Asociaciones (formulario y lista). */
@Controller
@RequestMapping("/asociaciones")
public class asociacion_web {

    private final asociacion_repositorio repositorio;
    private final ClubServicio clubes;

    public asociacion_web(asociacion_repositorio repositorio, ClubServicio clubes) {
        this.repositorio = repositorio;
        this.clubes = clubes;
    }

    // Pagina 1: formulario vacio
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("asociacion", new Asociacion());
        return "asociacion_index";
    }

    // Pagina 1: formulario cargado con un registro existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repositorio.findById(id)
            .map(x -> {
                model.addAttribute("asociacion", x);
                return "asociacion_index";
            })
            .orElseGet(() -> {
                ra.addFlashAttribute("error", "El registro no existe");
                return "redirect:/asociaciones";
            });
    }

    // Guarda (crea si no tiene id, actualiza si lo tiene)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("asociacion") Asociacion asociacion,
                          BindingResult resultado, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return "asociacion_index";
        }
        boolean nuevo = (asociacion.getId() == null);
        repositorio.save(asociacion);
        ra.addFlashAttribute("mensaje", nuevo
            ? "Asociación guardado correctamente"
            : "Asociación actualizado correctamente");
        return "redirect:/asociaciones";      // patron Post-Redirect-Get
    }

    // Pagina 2: lista con buscador
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<Asociacion> lista = (buscar == null || buscar.isBlank())
            ? repositorio.findAll(Sort.by("id"))
            : repositorio.findByNombreContainingIgnoreCase(buscar.trim());
        model.addAttribute("lista", lista);
        model.addAttribute("buscar", buscar);
        return "asociacion_listar";
    }

    // Elimina (si ningun club lo esta usando)
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        if (!repositorio.existsById(id)) {
            ra.addFlashAttribute("error", "El registro no existe");
        } else {
            String club = clubes.clubDeAsociacion(id);
            if (club != null) {
                ra.addFlashAttribute("error",
                    "No se puede eliminar: está en uso por el club " + club);
            } else {
                repositorio.deleteById(id);
                ra.addFlashAttribute("mensaje", "Asociación eliminado correctamente");
            }
        }
        return "redirect:/asociaciones";
    }
}
