package com.eduPlatform.apiCurso.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.CursoCrear;
import com.eduPlatform.apiCurso.models.requests.CursoEditar;
import com.eduPlatform.apiCurso.services.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
public class CursoController {
    
    @Autowired
    private CursoService cursoService;

    
    @GetMapping("/")
    @Operation(summary = "Obtiene todos los cursos",
            description = "Devuelve una lista de todos los cursos registrados en el sistema.")
    public List<Curso> traerTodos(){
        return cursoService.obtenerTodos();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtiene todos los cursos por id",
            description = "Devuelve una lista de todos los cursos registrados en el sistema por id.")
    public Curso traerPorId(@PathVariable int id) {
        return cursoService.obtenerCursoPorId(id);
    }


    @GetMapping("/activos")
    @Operation(summary = "Obtiene todos los cursos activos",
            description = "Devuelve una lista de todos los cursos activos registrados en el sistema.")
    public List<Curso> traerActivos(){
        return cursoService.obtenerActivos();
    }
    
    @PostMapping("/{idUsuario}")
    @Operation(summary = "Crea un nuevo curso",
            description = "Crea un cursos en el sistema.")
    public Curso crearCurso(@Valid @RequestBody CursoCrear nuevo, @PathVariable int idUsuario){
        return cursoService.registrar(nuevo, idUsuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica un nuevo curso",
            description = "Modifica un cursos en el sistema.")
    public Curso modificar(@PathVariable Integer id, @Valid @RequestBody CursoEditar body) {
    body.setId(id); 
    return cursoService.modificar(body);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Busca un curso",
            description = "busca un curso por su nombre.")
    public List<Curso> obtenerPorNombre(@RequestParam String nombre) {
        return cursoService.obtenerPorNombre(nombre);
    }



    @PutMapping("/estado/{id}")
    @Operation(summary = "Cambiar estado de un curso",
            description = "Cambia el estado de un curso de activo a inactivo o biciversa.")
    public ResponseEntity<String> cambiarEstado(@PathVariable Integer id) {
    Curso cursoActualizado = cursoService.cambiarEstado(id);
    String mensaje;
    if (cursoActualizado.getEstado()) {
        mensaje = "Curso id: "+id+" activado correctamente.";
    } else {
        mensaje = "Curso id: "+id+" desactivado correctamente.";
    }

    return ResponseEntity.ok(mensaje);
}

}
