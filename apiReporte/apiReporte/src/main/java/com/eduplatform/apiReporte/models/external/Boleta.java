package com.eduplatform.apiReporte.models.external;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Boleta {
    private int id;
    private int numeroBoleta;
    private int precio;
    private String fechaCompra;
    private int inscripcionId; 
    

}
