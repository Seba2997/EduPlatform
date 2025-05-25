package com.eduplatform.apiInscripcion.models.request;

import lombok.Data;

@Data
public class CompraRequest {
    private String nombreTarjeta;
    private String numeroTarjeta;
    private String codigoTarjeta;
}
