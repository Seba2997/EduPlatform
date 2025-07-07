package com.eduPlatform.apiCurso.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.eduPlatform.apiCurso.assemblers.ComentarioModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Comentario;
import com.eduPlatform.apiCurso.models.requests.ComentarioCrear;
import com.eduPlatform.apiCurso.models.requests.ComentarioEditar;
import com.eduPlatform.apiCurso.services.ComentarioService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ComentarioModelAssembler comentarioAssembler;

    
    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Lista comentarios de un curso",
                description = "Devuelve todos los comentarios asociados a un curso específico.")
    public CollectionModel<EntityModel<Comentario>> listarPorCurso(@PathVariable int cursoId) {
        List<EntityModel<Comentario>> comentarios = comentarioService.listarPorCurso(cursoId)
            .stream()
            .map(comentarioAssembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(comentarios,
            linkTo(methodOn(ComentarioController.class).listarPorCurso(cursoId)).withSelfRel());
    }


    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping
    @Operation(summary = "Crea un nuevo comentario",
                description = "Crea un comentario para un curso determinado.")
    public EntityModel<Comentario> crear(@Valid @RequestBody ComentarioCrear comentarioCrear) {
        Comentario creado = comentarioService.crearComentario(comentarioCrear);
        return comentarioAssembler.toModel(creado);
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PutMapping("/editar/{id}")
    @Operation(summary = "Modifica un comentario existente",
                description = "Actualiza el contenido de un comentario.")
    public EntityModel<Comentario> editar(@PathVariable int id, @Valid @RequestBody ComentarioEditar comentarioEditar) {
        
        Comentario actualizado = comentarioService.editarComentario(comentarioEditar, id);
        return comentarioAssembler.toModel(actualizado);
    }

}