package com.eduplatform.apiReporte.models.external;


import lombok.Data;


@Data
public class Boleta {
     private int id;
    private int numeroBoleta;
    private int precio;
    private String fechaCompra;

}
