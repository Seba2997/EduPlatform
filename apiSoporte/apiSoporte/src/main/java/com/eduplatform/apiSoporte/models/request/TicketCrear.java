package com.eduplatform.apiSoporte.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketCrear {
    private String asunto;
    private String descripcion;
}
