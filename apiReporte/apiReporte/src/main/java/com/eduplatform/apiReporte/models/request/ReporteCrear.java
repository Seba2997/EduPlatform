package com.eduplatform.apiReporte.models.request;

import lombok.Data;

@Data
public class ReporteCrear {
    private String titulo;
    private String contenido;
    private boolean exportarPdf;
   
}
