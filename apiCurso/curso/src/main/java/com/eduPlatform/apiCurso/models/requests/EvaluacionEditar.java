package com.eduPlatform.apiCurso.models.requests;



import lombok.Data;

@Data
public class EvaluacionEditar  {
    private int id;
    private String titulo;
    private String descripcion;
    private Integer puntajeMaximo;
}    



