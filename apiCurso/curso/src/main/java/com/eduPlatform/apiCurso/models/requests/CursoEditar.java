package com.eduPlatform.apiCurso.models.requests;

import lombok.Data;

@Data
public class CursoEditar {
    private int id;
    private String nombreCurso;
    private String descripcion;
    private Boolean estado;
    private Integer precio;
}
