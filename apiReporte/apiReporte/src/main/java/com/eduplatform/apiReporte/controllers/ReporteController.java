package com.eduplatform.apiReporte.controllers;

import com.eduplatform.apiReporte.assemblers.ReporteModelAssembler;
import com.eduplatform.apiReporte.models.entities.Reporte;
import com.eduplatform.apiReporte.models.request.ReporteCrear;
import com.eduplatform.apiReporte.services.ReporteService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Operaciones para gestionar reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private ReporteModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Crear un nuevo reporte", description = "Genera un reporte a partir de contenido JSON simulado o real")
    public EntityModel<Reporte> crearReporte(@RequestBody ReporteCrear dto) {
        Reporte nuevo = reporteService.crearReporteDesdeApi(dto);
        return assembler.toModel(nuevo);
    }
}