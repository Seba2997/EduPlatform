package com.eduplatform.apiInscripcion.models.responses;

import com.eduplatform.apiInscripcion.models.entities.Boleta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletaDTO {
    private int id;
    private int numeroBoleta;
    private int precio;
    private String fechaCompra;
    private int inscripcionId;

    public static BoletaDTO fromEntity(Boleta boleta) {
        BoletaDTO dto = new BoletaDTO();
        dto.setId(boleta.getId());
        dto.setNumeroBoleta(boleta.getNumeroBoleta());
        dto.setPrecio(boleta.getPrecio());
        dto.setFechaCompra(boleta.getFechaCompra());
        dto.setInscripcionId(boleta.getInscripcion().getId());
        return dto;
    }
}