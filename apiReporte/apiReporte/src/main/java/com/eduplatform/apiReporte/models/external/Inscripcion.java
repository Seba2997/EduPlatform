package com.eduplatform.apiReporte.models.external;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inscripcion {
    private int id;
    private int idEstudiante;
    private String nombreEstudiante;
    private String emailEstudiante;
    private int idCurso;
    private String nombreCurso;
    private LocalDate fechaInscripcion;
}



    
    