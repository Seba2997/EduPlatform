package com.eduPlatform.apiCurso.models.requests;

import lombok.Data;

@Data
public class CursoCrear {
    
    private String nombreCurso;
    private String descripcion;
    private Boolean estado;
    private Integer precio;

    private String CategoriaNombre;

    private String idProfesor;

}
