package com.eduplatform.apiInscripcion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiInscripcion.models.entities.Boleta;
import com.eduplatform.apiInscripcion.services.BoletaService;

@RestController
@RequestMapping("/boletas")
public class BoletaController {
    
    @Autowired
    private BoletaService boletaService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/inscripcion/{inscripcionId}")
    public ResponseEntity<Boleta> obtenerBoletasPorInscripcionId(int inscripcionId) {
        return ResponseEntity.ok(boletaService.obtenerBoletasPorInscripcionId(inscripcionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/todas")
    public ResponseEntity<List<Boleta>> obtenerTodasBoletas() {
        return ResponseEntity.ok(boletaService.obtenerTodasBoletas());
    }
}
