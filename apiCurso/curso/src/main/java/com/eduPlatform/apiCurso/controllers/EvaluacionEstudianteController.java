package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.assemblers.EvaluacionEstudianteModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.services.EvaluacionEstudianteService;
import com.eduPlatform.apiCurso.services.EvaluacionService;

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
    @PostMapping("/responder/{id}")
    @Operation(summary = "Responder una evaluación", description = "Permite que un estudiante responda una evaluación.")
    public EntityModel<EvaluacionEstudiante> responder(@Valid @RequestBody EvaluacionEstudianteCrear crear) {
        EvaluacionEstudiante respuesta = service.responder(crear);
        return assembler.toModel(respuesta);
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/calificacion/{id}")
    @Operation(summary = "Ver una respuesta enviada", description = "Permite al estudiante ver una respuesta enviada por ID y saber su puntuacion con su respectiva nota.")
    public EvaluacionEstudianteRespuesta obtenerRespuesta(@PathVariable int id) {
        return service.obtenerRespuestaPorId(id);
    }


    @PreAuthorize("hasRole('ESTUDIANTE')")
@GetMapping("/evaluaciones/{id}")
@Operation(
    summary = "Ver una evaluación asignada",
    description = "Permite al estudiante ver los datos de una evaluación creada por el profesor, usando su ID."
)
public Evaluacion verEvaluacion(@PathVariable int id) {
    return service.obtenerEvaluacionPorId(id);
}
}