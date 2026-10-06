package com.mundial_apirest.app.repositorios;

import com.mundial_apirest.app.entidades.Jugador;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface jugador_repositorio extends MongoRepository<Jugador, Long> {

    // Busqueda por parte del nombre, sin distinguir mayusculas
    List<Jugador> findByNombreContainingIgnoreCase(String nombre);
}
