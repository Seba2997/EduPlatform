package com.eduPlatform.apiCurso.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.eduPlatform.apiCurso.assemblers.EvaluacionModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.requests.CalificacionRequest;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.services.EvaluacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/evaluaciones")
@Tag(name = "Evaluaciones", description = "Administración de evaluaciones para cursos")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private EvaluacionModelAssembler assembler;

    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    @PostMapping("/curso/{idCurso}")
    @Operation(summary = "Registrar una nueva evaluación para un curso",
                description = "Crea una evaluación asociada a un curso existente.")
    public EntityModel<Evaluacion> crearEvaluacion(@PathVariable int idCurso,
                                                    @Valid @RequestBody EvaluacionCrear crear) {
        Evaluacion evaluacion = evaluacionService.registrar(crear, idCurso);
        return assembler.toModel(evaluacion);
    }

    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    @GetMapping("/curso/{idCurso}")
    @Operation(summary = "Listar evaluaciones por curso",
                description = "Obtiene todas las evaluaciones asignadas a un curso.")
    public CollectionModel<EntityModel<Evaluacion>> listarPorCurso(@PathVariable int idCurso) {
        List<EntityModel<Evaluacion>> evaluaciones = evaluacionService.listarEvaluacionPorCurso(idCurso)
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(evaluaciones,
            linkTo(methodOn(EvaluacionController.class).listarPorCurso(idCurso)).withSelfRel());
    }

    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Modificar una evaluación existente",
                description = "Actualiza los datos de una evaluación.")
    public EntityModel<Evaluacion> modificarEvaluacion(@PathVariable int id,
                                                        @Valid @RequestBody EvaluacionEditar editar) {
        
        Evaluacion evaluacion = evaluacionService.modificarEvaluacion(editar,id);
        return assembler.toModel(evaluacion);
    }

    @PreAuthorize("hasAnyRole('PROFESOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una evaluación",
                description = "Elimina una evaluación según su ID.")
    public ResponseEntity<String> eliminarEvaluacion(@PathVariable int id) {
        evaluacionService.eliminarEvaluacion(id);
        return ResponseEntity.ok("Evaluación eliminada correctamente");
    }


//Calificar evaluacion de estudiante

    @PreAuthorize("hasRole('PROFESOR','ADMIN')")
    @PostMapping("/calificar")
    @Operation(
        summary = "Registrar puntaje de evaluación del estudiante",
        description = "El profesor califica al estudiante, el sistema calcula la nota y guarda la información."
    )
    public EvaluacionEstudianteRespuesta calificarEvaluacion(@RequestBody CalificacionRequest request) {
        return evaluacionService.calificarRespuesta(request);
    }

}
