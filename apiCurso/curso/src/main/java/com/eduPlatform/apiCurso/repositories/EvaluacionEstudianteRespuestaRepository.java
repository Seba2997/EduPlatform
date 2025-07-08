package com.eduPlatform.apiCurso.repositories;




import org.springframework.data.jpa.repository.JpaRepository;

import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;

public interface EvaluacionEstudianteRespuestaRepository extends 
JpaRepository<EvaluacionEstudianteRespuesta, Integer> {
}