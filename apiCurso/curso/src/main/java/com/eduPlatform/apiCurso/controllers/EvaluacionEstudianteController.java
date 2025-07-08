package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.assemblers.EvaluacionEstudianteModelAssembler;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.services.EvaluacionEstudianteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluacionestudiante")
@Tag(name = "Evaluaciones por estudiante", description = "Respuestas que los estudiantes entregan a evaluaciones")
public class EvaluacionEstudianteController {

    @Autowired
    private EvaluacionEstudianteService service;

    @Autowired
    private EvaluacionEstudianteModelAssembler assembler;

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping
    @Operation(summary = "Responder una evaluación", description = "Permite que un estudiante responda una evaluación.")
    public EntityModel<EvaluacionEstudiante> responder(@Valid @RequestBody EvaluacionEstudianteCrear crear) {
        EvaluacionEstudiante respuesta = service.responder(crear);
        return assembler.toModel(respuesta);
    }

    
}