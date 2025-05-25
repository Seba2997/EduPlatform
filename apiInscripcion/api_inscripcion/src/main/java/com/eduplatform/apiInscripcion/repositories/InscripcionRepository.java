package com.eduplatform.apiInscripcion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduplatform.apiInscripcion.models.entities.Inscripcion;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    
    List<Inscripcion> findByIdEstudiante(int idEstudiante);

    List<Inscripcion> findByIdCurso(int idCurso);

    Boolean existsByIdEstudianteAndIdCurso(int idEstudiante, int idCurso);

}
