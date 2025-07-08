package com.eduplatform.apiInscripcion.repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import com.eduplatform.apiInscripcion.models.entities.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Integer> {
    

    Boolean existsByNumeroBoleta(int numeroBoleta);

    Boleta findByInscripcionId(int inscripcionId);
    
}
