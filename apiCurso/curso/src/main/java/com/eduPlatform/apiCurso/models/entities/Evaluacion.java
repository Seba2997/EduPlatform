package com.eduPlatform.apiCurso.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "evaluacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String titulo;
    
    private String pregunta;
    
    private int puntajeMaximo;
    
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnore
    private Curso curso;
}