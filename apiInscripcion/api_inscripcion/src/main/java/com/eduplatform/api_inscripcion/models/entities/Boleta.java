package com.eduplatform.api_inscripcion.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Boleta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private int numeroBoleta;
    private int precio;
    private String fechaCompra;


    @OneToOne
    @JoinColumn(name = "inscripcion_id", nullable = false, unique = true)
    @JsonBackReference
    private Inscripcion inscripcion;
}
