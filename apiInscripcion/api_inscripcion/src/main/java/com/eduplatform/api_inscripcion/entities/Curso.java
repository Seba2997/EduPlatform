package com.eduplatform.api_inscripcion.entities;

import lombok.Data;

@Data
public class Curso {
    private int id;
    private String nombre;
    private Boolean estado;
    private String precio;
}
