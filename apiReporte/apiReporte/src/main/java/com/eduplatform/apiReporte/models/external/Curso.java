package com.eduplatform.apiReporte.models.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Curso {
      private int id;
    private String nombreCurso;
    private int precio;
    private boolean estado;

}
