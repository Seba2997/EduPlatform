package com.edutech.curso.models.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="profesor")
public class Profesor {
    
    @Id
    private int id;
    private String nombre;
    private String apellidos;

    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL)
    private List<Curso> cursos = new ArrayList<>();

}
