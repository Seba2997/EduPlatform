package com.eduplatform.api_inscripcion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.api_inscripcion.entities.Inscripcion;
import com.eduplatform.api_inscripcion.services.InscripcionService;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {
    
    @Autowired
    private InscripcionService inscripcionService;

    @GetMapping("/")
    public List<Inscripcion> traerTodos(){
        return inscripcionService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Inscripcion obtenerUno(@PathVariable int id) {
        return inscripcionService.obtenerInscripcionId(id);
    }

    @PostMapping("/")
    public Inscripcion InscribirUsuario(@RequestParam int idEstudiante, @RequestParam int idCurso){
        return inscripcionService.inscribirUsuario(idEstudiante, idCurso);
    }


}
