package com.mundial_apirest.app.entidades;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "asociaciones")
public class Asociacion implements Identificable {

    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String pais;

    @NotBlank(message = "Las siglas son obligatorias")
    @Size(max = 10, message = "Máximo 10 caracteres")
    private String siglas;

    @NotBlank(message = "El presidente es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String presidente;

    public Asociacion() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getSiglas() { return siglas; }
    public void setSiglas(String siglas) { this.siglas = siglas; }

    public String getPresidente() { return presidente; }
    public void setPresidente(String presidente) { this.presidente = presidente; }

}
