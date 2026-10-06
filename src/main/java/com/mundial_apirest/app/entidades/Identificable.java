package com.mundial_apirest.app.entidades;

/** Toda entidad con id numerico autoincremental implementa esta interfaz. */
public interface Identificable {
    Long getId();
    void setId(Long id);
}
