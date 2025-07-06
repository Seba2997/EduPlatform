package com.eduPlatform.apiCurso.models.requests;

import lombok.Data;

@Data
public class ComentarioCrear {
   
    private String detalle;
    private String autor;
    private Integer cursoId;
}

