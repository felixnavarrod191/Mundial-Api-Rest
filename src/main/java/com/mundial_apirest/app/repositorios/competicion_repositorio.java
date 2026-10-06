package com.mundial_apirest.app.repositorios;

import com.mundial_apirest.app.entidades.Competicion;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface competicion_repositorio extends MongoRepository<Competicion, Long> {

    // Busqueda por parte del nombre, sin distinguir mayusculas
    List<Competicion> findByNombreContainingIgnoreCase(String nombre);
}
