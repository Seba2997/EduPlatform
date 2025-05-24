package com.eduplatform.api_inscripcion.models.entities;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "inscripcion")
@Data
public class Inscripcion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //Fecha de inscripción
    private LocalDate fechaInscripcion;

    //Datos del estudiante
    private int idEstudiante;
    private String nombreEstudiante;
    private String emailEstudiante;
    

    //datos del curso
    private int idCurso;
    private String nombreCurso;

    @OneToOne(mappedBy = "inscripcion", cascade = CascadeType.ALL)
    private Boleta boleta;
}