package com.eduPlatform.apiCurso.models.requests;



import lombok.Data;

@Data
public class EvaluacionCrear {
    private String titulo;
    private String descripcion;
    private int puntajeMaximo;
}