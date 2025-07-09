package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.assemblers.EvaluacionEstudianteModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.services.EvaluacionEstudianteService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluacionestudiante")
@Tag(name = "Evaluaciones por estudiante", description = "Respuestas que los estudiantes entregan a evaluaciones")
public class EvaluacionEstudianteController {

    @Autowired
    private EvaluacionEstudianteService evaluacionEstudianteService;

    @Autowired
    private EvaluacionEstudianteModelAssembler assembler;

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping("/responder/{id}")
    @Operation(summary = "Responder una evaluación", description = "Permite que un estudiante responda una evaluación .")
    public EntityModel<EvaluacionEstudiante> responder(@Valid @RequestBody EvaluacionEstudianteCrear crear) {
        EvaluacionEstudiante respuesta = evaluacionEstudianteService.responder(crear);
        return assembler.toModel(respuesta);
    }

    @GetMapping("/ver-calificacion/{id}")
@PreAuthorize("hasRole('ESTUDIANTE')")
@Operation(summary = "Responder una evaluación", description = "Permite que un estudiante responda una evaluación .")
public ResponseEntity<EvaluacionEstudianteRespuesta> verCalificacion(@PathVariable int id) {
    EvaluacionEstudianteRespuesta calificacion = evaluacionEstudianteService.obtenerCalificacionPorId(id);
    return ResponseEntity.ok(calificacion);
}

@GetMapping("/evaluacion-estudiante/{id}/evaluacion")
@PreAuthorize("hasRole('ESTUDIANTE')")
@Operation(summary = "Ver una evaluación", description = "Permite que un estudiante vea una evaluación .")
public ResponseEntity<Evaluacion> obtenerEvaluacionPorEstudiante(@PathVariable int id) {
    Evaluacion evaluacion = evaluacionEstudianteService.obtenerEvaluacionPorEvaluacionEstudianteId(id);
    return ResponseEntity.ok(evaluacion);
}

}