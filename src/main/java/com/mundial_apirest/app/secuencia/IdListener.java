package com.mundial_apirest.app.secuencia;

import com.mundial_apirest.app.entidades.Identificable;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

/**
 * Antes de guardar cualquier entidad Identificable sin id, le asigna el siguiente
 * numero de su propia secuencia (Asociacion, Entrenador, Jugador, Competicion, Club).
 */
@Component
public class IdListener extends AbstractMongoEventListener<Object> {

    private final SecuenciaServicio secuencias;

    public IdListener(SecuenciaServicio secuencias) {
        this.secuencias = secuencias;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> evento) {
        Object origen = evento.getSource();
        if (origen instanceof Identificable entidad && entidad.getId() == null) {
            entidad.setId(secuencias.siguiente(origen.getClass().getSimpleName()));
        }
    }
}
