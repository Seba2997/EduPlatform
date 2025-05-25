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

import com.eduPlatform.apiCurso.models.entities.Contenido;
import com.eduPlatform.apiCurso.models.requests.ContenidoCrear;
import com.eduPlatform.apiCurso.models.requests.ContenidoEditar;
import com.eduPlatform.apiCurso.services.ContenidoService;

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

    @PostMapping("/{idCurso}")
    public Contenido registrarContenido(@Valid @RequestBody ContenidoCrear nuevo, @PathVariable int idCurso) {
        return contenidoService.registrar(nuevo, idCurso);
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
