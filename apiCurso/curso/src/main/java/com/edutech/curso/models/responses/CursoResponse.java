package com.edutech.curso.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CursoResponse {
    
    private int id;
    private String nombreCurso;
    private String descripcion;
    private Boolean estado;
    private int precio;

    private Profesor profesor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profesor {
        private int id;
        private String nombre;
        private String email;
    }

}
