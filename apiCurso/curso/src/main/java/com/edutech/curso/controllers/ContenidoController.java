package com.edutech.curso.controllers;

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
import com.edutech.curso.models.entities.Contenido;
import com.edutech.curso.models.requests.ContenidoCrear;
import com.edutech.curso.models.requests.ContenidoEditar;
import com.edutech.curso.services.ContenidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contenidos")
public class ContenidoController {
    
    @Autowired
    private ContenidoService contenidoService;

    @GetMapping("/")
    public List<Contenido> traerTodos(){
        return contenidoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Contenido traerPorId(@PathVariable int id) {
        return contenidoService.obtenerContenidoPorId(id);
    }

    @PostMapping("/{id}")
    public Contenido registrarContenido(@Valid @RequestBody ContenidoCrear nuevo){
        return contenidoService.registrar(nuevo);
    }

    @PutMapping("/{id}")
    public Contenido modificar(@PathVariable Integer id, @Valid @RequestBody ContenidoEditar body) {
    body.setIdContenido(id); 
    return contenidoService.modificar(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCurso(@PathVariable int id) {
    contenidoService.eliminarContenido(id);
    return ResponseEntity.ok("Contenido Eliminado");
    }
}
