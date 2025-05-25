package com.eduplatform.apiInscripcion.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiInscripcion.models.entities.Inscripcion;
import com.eduplatform.apiInscripcion.models.request.CompraRequest;
import com.eduplatform.apiInscripcion.models.responses.CompraResponse;
import com.eduplatform.apiInscripcion.services.InscripcionService;

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
    public CompraResponse InscribirUsuario(@RequestParam int idEstudiante, @RequestParam int idCurso, @RequestBody CompraRequest datoTarjeta){
        return inscripcionService.inscribirUsuario(idEstudiante, idCurso, datoTarjeta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInscripcion(@PathVariable int id){
        inscripcionService.eliminar(id);
        return ResponseEntity.ok("Inscripcion Eliminada");
    } 


}
