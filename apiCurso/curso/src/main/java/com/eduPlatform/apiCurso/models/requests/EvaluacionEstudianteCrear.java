package com.eduPlatform.apiCurso.models.requests;

import lombok.Data;

@Data
public class EvaluacionEstudianteCrear {
    private int id;
    private int evaluacionId;
    private int estudianteId;
    private String respuesta;
}
