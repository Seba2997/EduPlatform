package com.eduplatform.api_inscripcion.models.responses;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CompraResponse {
    private int numeroBoleta;
    private String nombreUsuario;
    private int precio;
    private String email;
    private String mensaje;
    private String nombreCurso;
    private LocalDate fechaCompra;
}
