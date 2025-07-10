package com.eduplatform.apiInscripcion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiInscripcion.models.entities.Boleta;
import com.eduplatform.apiInscripcion.models.responses.BoletaDTO;
import com.eduplatform.apiInscripcion.services.BoletaService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/boletas")
public class BoletaController {
    
    @Autowired
    private BoletaService boletaService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/inscripcion/{inscripcionId}")
    @Operation(summary = "Gestion interna del sistema", description = "Funcion para trabajar de forma interna en sistema.")
    public ResponseEntity<Boleta> obtenerBoletasPorInscripcionId(int inscripcionId) {
        return ResponseEntity.ok(boletaService.obtenerBoletasPorInscripcionId(inscripcionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reporte")
    @Operation(summary = "Gestion interna del sistema", description = "Funcion para trabajar de forma interna en sistema.")
    public List<BoletaDTO> obtenerTodasLasBoletas() {
    List<Boleta> boletas = boletaService.obtenerTodasBoletas();

        if (boletas == null || boletas.isEmpty()) {
            throw new RuntimeException("No se encontraron boletas para el reporte.");
        }

        // Convertir las entidades a DTOs para exponer inscripcionId
        return boletas.stream()
            .map(BoletaDTO::fromEntity)
            .toList();
    }   

    
}
