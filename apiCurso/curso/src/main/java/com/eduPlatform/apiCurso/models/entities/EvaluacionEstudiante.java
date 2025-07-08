package com.eduPlatform.apiCurso.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluacion_estudiante")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionEstudiante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int evaluacionEstudianteid;

    private String respuesta;

    private int puntajeObtenido;

    private String nombreEstudiante;
    private String emailEstudiante;

    @ManyToOne
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;


}
