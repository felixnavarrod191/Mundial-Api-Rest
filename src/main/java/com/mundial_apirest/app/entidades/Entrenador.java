package com.mundial_apirest.app.entidades;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "entrenadores")
public class Entrenador implements Identificable {

    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String apellido;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "La edad mínima es 18")
    @Max(value = 100, message = "La edad máxima es 100")
    private Integer edad;

    @NotBlank(message = "La nacionalidad es obligatoria")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String nacionalidad;

    public Entrenador() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

}
