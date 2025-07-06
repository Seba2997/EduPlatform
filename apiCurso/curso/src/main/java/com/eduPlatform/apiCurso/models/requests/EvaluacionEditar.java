package com.eduPlatform.apiCurso.models.requests;



import lombok.Data;

@Data
public class EvaluacionEditar  {
    private int id;
    private String titulo;
    private String pregunta;
    private Integer puntajeMaximo;
}    



