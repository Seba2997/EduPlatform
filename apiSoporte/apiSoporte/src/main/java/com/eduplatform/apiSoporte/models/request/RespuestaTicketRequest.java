package com.eduplatform.apiSoporte.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RespuestaTicketRequest {
    
    @NotBlank
    private String respuesta;

    private boolean cerrar;
}
