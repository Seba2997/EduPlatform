package com.eduplatform.api_inscripcion.entities.request;

import lombok.Data;

@Data
public class CompraRequest {
    private String nombreTarjeta;
    private String numeroTarjeta;
    private String codigoTarjeta;
}
