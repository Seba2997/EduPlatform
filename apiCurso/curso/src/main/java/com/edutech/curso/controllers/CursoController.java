package com.edutech.curso.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.curso.models.entities.Curso;
import com.edutech.curso.models.requests.CursoCrear;
import com.edutech.curso.models.requests.CursoEditar;
import com.edutech.curso.services.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
public class CursoController {
    
    @Autowired
    private CursoService cursoService;

    @GetMapping("/")
    public List<Curso> traerTodos(){
        return cursoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Curso traerPorId(@PathVariable int id) {
        return cursoService.obtenerCursoPorId(id);
    }

    @GetMapping("/activos")
    public List<Curso> traerActivos(){
        return cursoService.obtenerActivos();
    }
    
    @PostMapping("/{idUsuario}")
    public Curso crearCurso(@Valid @RequestBody CursoCrear nuevo, @PathVariable int idUsuario){
        return cursoService.registrar(nuevo, idUsuario);
    }

    @PutMapping("/{id}")
    public Curso modificar(@PathVariable Integer id, @Valid @RequestBody CursoEditar body) {
    body.setId(id); 
    return cursoService.modificar(body);
    }
    
    @GetMapping("/buscar")
    public List<Curso> obtenerPorNombre(@RequestParam String nombre) {
        return cursoService.obtenerPorNombre(nombre);
    }

/*     @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCurso(@PathVariable int id) {
    cursoService.eliminar(id);
    return ResponseEntity.ok("Curso Eliminado");
} */

    @PutMapping("/{id}/estado")
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
