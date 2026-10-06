package com.mundial_apirest.app.controladores;

import com.mundial_apirest.app.repositorios.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Pagina de aterrizaje (landing): resumen, rol de cada entidad y orden de creacion. */
@Controller
public class inicio_web {

    private final asociacion_repositorio asociaciones;
    private final competicion_repositorio competiciones;
    private final entrenador_repositorio entrenadores;
    private final jugador_repositorio jugadores;
    private final club_repositorio clubes;

    public inicio_web(asociacion_repositorio asociaciones, competicion_repositorio competiciones,
                      entrenador_repositorio entrenadores, jugador_repositorio jugadores,
                      club_repositorio clubes) {
        this.asociaciones = asociaciones;
        this.competiciones = competiciones;
        this.entrenadores = entrenadores;
        this.jugadores = jugadores;
        this.clubes = clubes;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalAsociaciones", asociaciones.count());
        model.addAttribute("totalCompeticiones", competiciones.count());
        model.addAttribute("totalEntrenadores", entrenadores.count());
        model.addAttribute("totalJugadores", jugadores.count());
        model.addAttribute("totalClubes", clubes.count());
        return "index";
    }
}
