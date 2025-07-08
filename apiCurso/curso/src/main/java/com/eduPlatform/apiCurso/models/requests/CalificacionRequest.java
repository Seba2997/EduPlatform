package com.eduPlatform.apiCurso.models.requests;

import lombok.Data;

@Data
public class CalificacionRequest {
    private Integer idEvaluacionEstudiante;
    private Integer puntajeObtenido;
}