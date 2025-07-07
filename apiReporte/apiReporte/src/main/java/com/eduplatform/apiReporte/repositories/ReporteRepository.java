package com.eduplatform.apiReporte.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eduplatform.apiReporte.models.entities.Reporte;


@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

}