package com.mundial_apirest.app.repositorios;

import com.mundial_apirest.app.entidades.Club;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface club_repositorio extends MongoRepository<Club, Long> {

    // Busqueda por parte del nombre, sin distinguir mayusculas
    List<Club> findByNombreContainingIgnoreCase(String nombre);
}
