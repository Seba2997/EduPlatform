package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.models.entities.Comentario;
import com.eduPlatform.apiCurso.models.requests.ComentarioCrear;
import com.eduPlatform.apiCurso.models.requests.ComentarioEditar;
import com.eduPlatform.apiCurso.services.ComentarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;


    @GetMapping("/curso/{cursoId}")
    public List<Comentario> listarPorCurso(@PathVariable Integer cursoId) {
        return comentarioService.listarPorCurso(cursoId);
    }

    
    @PostMapping
    public Comentario crear(@RequestBody ComentarioCrear comentarioCrear) {
        return comentarioService.crearComentario(comentarioCrear);
    }

    @PutMapping
    public Comentario editar(@RequestBody ComentarioEditar comentarioEditar) {
        return comentarioService.editarComentario(comentarioEditar);
    }
}