package com.eduPlatform.apiCurso.models.responses;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "evaluacion_estudiante_respuestas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionEstudianteRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String pregunta;
    private String respuesta;
    private int puntajeMaximo;
    private Integer puntajeObtenido;
    private double nota;
}