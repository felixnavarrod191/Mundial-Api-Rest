package com.mundial_apirest.app.entidades;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jugadores")
public class Jugador implements Identificable {

    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String apellido;

    @NotNull(message = "El número es obligatorio")
    @Min(value = 1, message = "El número mínimo es 1")
    @Max(value = 99, message = "El número máximo es 99")
    private Integer numero;

    @NotBlank(message = "La posición es obligatoria")
    @Pattern(regexp = "Portero|Defensa|Mediocampista|Delantero", message = "Posición no válida")
    private String posicion;

    public Jugador() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }

}
