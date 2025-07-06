package com.eduplatform.apiSoporte.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketCrear {

    @NotBlank(message = "El asunto no puede estar vacío")
    private String asunto;
    
    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;
}
