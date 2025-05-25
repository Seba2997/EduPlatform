package com.eduplatform.apiInscripcion.models;

import lombok.Data;

@Data
public class Curso {
    private int id;
    private String nombreCurso;
    private Boolean estado;
    private int precio;
}
