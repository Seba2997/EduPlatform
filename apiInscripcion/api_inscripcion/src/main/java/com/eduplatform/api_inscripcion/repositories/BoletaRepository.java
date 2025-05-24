package com.eduplatform.api_inscripcion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduplatform.api_inscripcion.models.entities.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Integer> {
    
}
