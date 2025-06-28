package com.eduPlatform.apiCurso.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eduPlatform.apiCurso.assemblers.CursoModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.CursoCrear;
import com.eduPlatform.apiCurso.models.requests.CursoEditar;
import com.eduPlatform.apiCurso.services.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/cursos")
public class CursoController {
    
    @Autowired
    private CursoService cursoService;

    @Autowired
    private CursoModelAssembler cursoAssembler;

    @GetMapping("/")
    @Operation(summary = "Obtiene todos los cursos",
            description = "Devuelve una lista de todos los cursos registrados en el sistema.")
    public CollectionModel<EntityModel<Curso>> traerTodos() {
        List<EntityModel<Curso>> cursos = cursoService.obtenerTodos()
            .stream()
            .map(cursoAssembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(cursos,
            linkTo(methodOn(CursoController.class).traerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por id",
            description = "Obtener un curso registrado en el sistema por id.")
    public EntityModel<Curso> traerPorId(@PathVariable int id) {
        Curso curso = cursoService.obtenerCursoPorId(id);
        return cursoAssembler.toModel(curso);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtiene todos los cursos activos",
            description = "Devuelve una lista de todos los cursos activos registrados en el sistema.")
    public CollectionModel<EntityModel<Curso>> traerActivos() {
        List<EntityModel<Curso>> cursos = cursoService.obtenerActivos()
            .stream()
            .map(cursoAssembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(cursos,
            linkTo(methodOn(CursoController.class).traerActivos()).withSelfRel());
    }

    @PostMapping("/{idUsuario}")
    @Operation(summary = "Crea un nuevo curso",
            description = "Crea un nuevo curso en el sistema.")
    public EntityModel<Curso> crearCurso(@Valid @RequestBody CursoCrear nuevo, @PathVariable int idUsuario) {
        Curso nuevoCurso = cursoService.registrar(nuevo, idUsuario);
        return cursoAssembler.toModelSoloModificar(nuevoCurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un curso",
            description = "Modifica un curso en el sistema.")
    public EntityModel<Curso> modificar(@PathVariable Integer id, @Valid @RequestBody CursoEditar body) {
        body.setId(id);
        Curso actualizado = cursoService.modificar(body);
        return cursoAssembler.toModel(actualizado);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca un curso por nombre",
            description = "Busca un curso por su nombre en el sistema.")
    public CollectionModel<EntityModel<Curso>> obtenerPorNombre(@RequestParam String nombre) {
        List<EntityModel<Curso>> cursos = cursoService.obtenerPorNombre(nombre)
            .stream()
            .map(cursoAssembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(cursos,
            linkTo(methodOn(CursoController.class).obtenerPorNombre(nombre)).withSelfRel());
    }

    @PutMapping("/estado/{id}")
    @Operation(summary = "Cambiar estado de un curso",
            description = "Cambia el estado de un curso entre Activo/Inactivo.")
    public EntityModel<Curso> cambiarEstado(@PathVariable Integer id) {
        Curso cursoActualizado = cursoService.cambiarEstado(id);
        return cursoAssembler.toModel(cursoActualizado);
    }
}