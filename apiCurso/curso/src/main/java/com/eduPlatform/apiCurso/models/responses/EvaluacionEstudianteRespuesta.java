package com.eduPlatform.apiCurso.models.responses;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionEstudianteRespuesta {

    private int id;
    private String nombreEstudiante;
    private String emailEstudiante;
    private String respuesta;
    private int puntajeObtenido;
    private double nota;
    private String tituloEvaluacion;
}