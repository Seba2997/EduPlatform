package com.eduPlatform.apiCurso.repositories;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.eduPlatform.apiCurso.models.entities.Curso;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
    //Buscar cursos activos
    List<Curso> findByEstado(Boolean estado);

    // Buscar por nombre
    List<Curso> findByNombreCurso(String nombre); 

    
}
