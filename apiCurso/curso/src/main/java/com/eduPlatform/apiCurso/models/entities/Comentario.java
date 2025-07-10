package com.eduPlatform.apiCurso.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 500)
    private String detalle;
    private String emailAutor; 
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnore
    private Curso curso;
}