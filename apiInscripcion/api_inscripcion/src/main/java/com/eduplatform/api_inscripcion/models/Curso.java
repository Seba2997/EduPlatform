package com.eduplatform.api_inscripcion.models;

import lombok.Data;

@Data
public class Curso {
    private int id;
    private String nombreCurso;
    private Boolean estado;
    private int precio;
}
