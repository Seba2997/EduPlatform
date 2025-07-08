package com.eduplatform.apiReporte.models.external;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inscripcion {
    private int idInscripcion;
    private String nombreEstudiante;
    private String emailEstudiante;
    private String nombreCurso;
    private LocalDate fechaInscripcion;
    
}
