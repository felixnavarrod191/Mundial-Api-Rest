package com.mundial_apirest.app.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "clubes")
public class Club implements Identificable {

    @Id
    private Long id;

    @NotBlank(message = "El nombre del club es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @DocumentReference
    private Entrenador entrenador;

    @DocumentReference
    private List<Jugador> jugadores = new ArrayList<>();

    @DocumentReference
    private Asociacion asociacion;

    @DocumentReference
    private List<Competicion> competiciones = new ArrayList<>();

    public Club() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }

    public List<Jugador> getJugadores() { return jugadores; }
    public void setJugadores(List<Jugador> jugadores) { this.jugadores = jugadores; }

    public Asociacion getAsociacion() { return asociacion; }
    public void setAsociacion(Asociacion asociacion) { this.asociacion = asociacion; }

    public List<Competicion> getCompeticiones() { return competiciones; }
    public void setCompeticiones(List<Competicion> competiciones) { this.competiciones = competiciones; }

}
