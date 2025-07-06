package com.eduPlatform.apiCurso.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;

@Repository
public interface EvaluacionEstudianteRepository extends JpaRepository<EvaluacionEstudiante, Integer> {
    List<EvaluacionEstudiante> findByEstudianteId(int estudianteId);
}