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

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/contenidos")
public class ContenidoController {
    
    @Autowired
    private ContenidoService contenidoService;

    @GetMapping("/")
    @Operation(summary = "Traer todos los contenidos",
            description = "Obtiene todos los contenidos cargados en el sistema.")
    public List<Contenido> traerTodos(){
        return contenidoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Traer contenidos por id",
            description = "Obtiene un contenido por su id correspondient.")
    public Contenido traerPorId(@PathVariable int id) {
        return contenidoService.obtenerContenidoPorId(id);
    }

    @PostMapping("/{idCurso}")
    @Operation(summary = "Registrar contenido",
            description = "Registra un nuevo contenido en el sistema.")
    public Contenido registrarContenido(@Valid @RequestBody ContenidoCrear nuevo, @PathVariable int idCurso) {
        return contenidoService.registrar(nuevo, idCurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar contenido",
            description = "Modifica un contenido en el sistema.")
    public Contenido modificar(@PathVariable Integer id, @Valid @RequestBody ContenidoEditar body) {
        body.setIdContenido(id); 
        return contenidoService.modificar(body);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar contenido",
            description = "Elimina un contenido en el sistema.")
    public ResponseEntity<String> eliminarContenido(@PathVariable int id) {
        contenidoService.eliminarContenido(id);
        return ResponseEntity.ok("Contenido Eliminado");
    }
}
