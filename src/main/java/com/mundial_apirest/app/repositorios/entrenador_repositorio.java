package com.mundial_apirest.app.repositorios;

import com.mundial_apirest.app.entidades.Entrenador;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface entrenador_repositorio extends MongoRepository<Entrenador, Long> {

    // Busqueda por parte del nombre, sin distinguir mayusculas
    List<Entrenador> findByNombreContainingIgnoreCase(String nombre);
}
