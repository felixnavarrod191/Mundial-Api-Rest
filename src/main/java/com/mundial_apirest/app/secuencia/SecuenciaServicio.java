package com.mundial_apirest.app.secuencia;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SecuenciaServicio {

    private final MongoOperations mongo;

    public SecuenciaServicio(MongoOperations mongo) {
        this.mongo = mongo;
    }

    /** Incrementa de forma atomica el contador y devuelve el nuevo valor (1, 2, 3...). */
    public long siguiente(String nombre) {
        Secuencia s = mongo.findAndModify(
            Query.query(Criteria.where("_id").is(nombre)),
            new Update().inc("valor", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            Secuencia.class);
        return (s != null) ? s.getValor() : 1;
    }
}
