package com.eduPlatform.apiCurso.models.entities;

import jakarta.persistence.*;
import lombok.*;

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

    private String descripcion;

    private int puntajeMaximo;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
}