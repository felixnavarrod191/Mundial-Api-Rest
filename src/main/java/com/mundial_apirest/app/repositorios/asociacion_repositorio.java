package com.mundial_apirest.app.repositorios;

import com.mundial_apirest.app.entidades.Asociacion;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface asociacion_repositorio extends MongoRepository<Asociacion, Long> {

    // Busqueda por parte del nombre, sin distinguir mayusculas
    List<Asociacion> findByNombreContainingIgnoreCase(String nombre);
}
