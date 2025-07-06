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

import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.services.EvaluacionService;

public class EvaluacionController {
    
    @Autowired
    private EvaluacionService evaluacionService;

    @PostMapping("/curso/{idCurso}")
    public ResponseEntity<Evaluacion> crearEvaluacion(@PathVariable int idCurso,
                                                        @RequestBody EvaluacionCrear crear) {
        return ResponseEntity.ok(evaluacionService.registrar(crear, idCurso));
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<Evaluacion>> listarPorCurso(@PathVariable int idCurso) {
        return ResponseEntity.ok(evaluacionService.listarEvaluacionPorCurso(idCurso));
    }


    @PutMapping
    public ResponseEntity<Evaluacion> modificarEvaluacion(@RequestBody EvaluacionEditar editar) {
        return ResponseEntity.ok(evaluacionService.modificarEvaluacion(editar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEvaluacion(@PathVariable int id) {
        evaluacionService.eliminarEvaluacion(id);
        return ResponseEntity.ok("Evaluación eliminada correctamente");
    }
}
