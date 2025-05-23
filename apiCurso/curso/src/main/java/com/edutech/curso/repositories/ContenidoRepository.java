package com.edutech.curso.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.curso.models.entities.Contenido;
import com.edutech.curso.models.entities.Curso;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Integer>{
    
    // Buscar por nombre
    List<Curso> findByNombreCurso(String nombre); 

}
