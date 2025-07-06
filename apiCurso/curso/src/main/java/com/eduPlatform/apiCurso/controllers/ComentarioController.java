package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.models.entities.Comentario;
import com.eduPlatform.apiCurso.models.requests.ComentarioCrear;
import com.eduPlatform.apiCurso.models.requests.ComentarioEditar;
import com.eduPlatform.apiCurso.services.ComentarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/comentarios")
@Tag(name = "Comentarios", description = "Gestión de comentarios en los cursos")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Operation(summary = "Listar comentarios de un curso")
    @GetMapping("/curso/{cursoId}")
    public List<Comentario> listarPorCurso(@PathVariable Integer cursoId) {
        return comentarioService.listarPorCurso(cursoId);
    }

    @Operation(summary = "Crear nuevo comentario")
    @PostMapping
    public Comentario crear(@RequestBody ComentarioCrear comentarioCrear) {
        return comentarioService.crearComentario(comentarioCrear);
    }

    @Operation(summary = "Editar un comentario existente")
    @PutMapping
    public Comentario editar(@RequestBody ComentarioEditar comentarioEditar) {
        return comentarioService.editarComentario(comentarioEditar);
    }
}