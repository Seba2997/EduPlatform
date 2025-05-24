package com.edutech.curso.models.requests;

import lombok.Data;

@Data
public class CursoCrear {
    private int id;
    private String nombreCurso;
    private String descripcion;
    private Boolean estado;
    private Integer precio;


}
