package com.edutech.curso.models.entities;

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
@Table(name = "contenido")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Contenido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idContenido;
    private int numeroUnidad;
    private String tituloUnidad;
    private int numeroContenido;
    private String contenido;


    @ManyToOne
    @JoinColumn(name = "curso")
    private Curso curso;
    //posiblemente
    /*private String quiz;
    private int puntajeQuiz;
    private int notaQuiz;*/


}
