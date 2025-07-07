package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.assemblers.EvaluacionEstudianteModelAssembler;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteEditar;
import com.eduPlatform.apiCurso.services.EvaluacionEstudianteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluacionestudiante")
@Tag(name = "Evaluaciones por estudiante", description = "Respuestas que los estudiantes entregan a evaluaciones")
public class EvaluacionEstudianteController {

    @Autowired
    private EvaluacionEstudianteService service;

    @Autowired
    private EvaluacionEstudianteModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Responder una evaluación", description = "Permite que un estudiante responda una evaluación.")
    public EntityModel<EvaluacionEstudiante> responder(@Valid @RequestBody EvaluacionEstudianteCrear crear) {
        EvaluacionEstudiante respuesta = service.responder(crear);
        return assembler.toModel(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar respuesta", description = "Permite al estudiante editar su respuesta antes de ser evaluada.")
    public EntityModel<EvaluacionEstudiante> editarEvaluacionEstudiante(@PathVariable int id,
                                                                        @Valid @RequestBody EvaluacionEstudianteEditar editar) {
        editar.setId(id);
        EvaluacionEstudiante actualizada = service.editarRespuesta(editar);
        return assembler.toModel(actualizada);
    }
}