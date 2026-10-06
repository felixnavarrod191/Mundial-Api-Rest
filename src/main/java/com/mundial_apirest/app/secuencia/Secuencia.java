package com.mundial_apirest.app.secuencia;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Contador por entidad: _id = nombre de la entidad, valor = ultimo numero entregado. */
@Document(collection = "secuencias")
public class Secuencia {

    @Id
    private String id;
    private long valor;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getValor() { return valor; }
    public void setValor(long valor) { this.valor = valor; }
}
