package com.eduPlatform.apiCurso.models.requests;



import lombok.Data;

@Data
public class Evaluacion {
    private String titulo;
    private String descripcion;
    private int puntajeMaximo;
}