package com.eduPlatform.apiCurso.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.services.EvaluacionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/evaluaciones")
@Tag(name = "Evaluaciones", description = "Administración de evaluaciones para cursos")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @Operation(summary = "Registrar una nueva evaluación para un curso")
    @PostMapping("/curso/{idCurso}")
    public ResponseEntity<Evaluacion> crearEvaluacion(@PathVariable int idCurso,
                                                      @RequestBody EvaluacionCrear crear) {
        return ResponseEntity.ok(evaluacionService.registrar(crear, idCurso));
    }

    @Operation(summary = "Listar evaluaciones por curso")
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<Evaluacion>> listarPorCurso(@PathVariable int idCurso) {
        return ResponseEntity.ok(evaluacionService.listarEvaluacionPorCurso(idCurso));
    }

    @Operation(summary = "Modificar una evaluación existente")
    @PutMapping
    public ResponseEntity<Evaluacion> modificarEvaluacion(@RequestBody EvaluacionEditar editar) {
        return ResponseEntity.ok(evaluacionService.modificarEvaluacion(editar));
    }

    @Operation(summary = "Eliminar una evaluación")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEvaluacion(@PathVariable int id) {
        evaluacionService.eliminarEvaluacion(id);
        return ResponseEntity.ok("Evaluación eliminada correctamente");
    }
}
