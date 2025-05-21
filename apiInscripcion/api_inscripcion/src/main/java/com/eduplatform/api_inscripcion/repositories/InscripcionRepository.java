package com.eduplatform.api_inscripcion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduplatform.api_inscripcion.entities.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    
    List<Inscripcion> findByIdEstudiante(int idEstudiante);

    List<Inscripcion> findByIdCurso(int idCurso);

    Boolean existsByIdEstudianteAndIdCurso(int idEstudiante, int idCurso);

}
