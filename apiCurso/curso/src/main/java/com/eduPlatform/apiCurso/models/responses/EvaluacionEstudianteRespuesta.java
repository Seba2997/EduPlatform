package com.eduPlatform.apiCurso.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionEstudianteRespuesta {
    private String titulo;
    private String pregunta;
    private String respuesta;
    private int puntajeMaximo;
    private Integer puntajeObtenido;
    private double nota;
}