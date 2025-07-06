package com.eduPlatform.apiCurso.services;

import com.eduPlatform.apiCurso.models.entities.Comentario;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ComentarioCrear;
import com.eduPlatform.apiCurso.models.requests.ComentarioEditar;
import com.eduPlatform.apiCurso.repositories.ComentarioRepository;
import com.eduPlatform.apiCurso.repositories.CursoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepo;

    @Autowired
    private CursoRepository cursoRepo;

    public List<Comentario> listarPorCurso(Integer cursoId) {
        return comentarioRepo.findByCursoId(cursoId);
    }

    public Comentario crearComentario(ComentarioCrear comentarioCrear) {
        Curso curso = cursoRepo.findById(comentarioCrear.getCursoId()).orElse(null);
        if (curso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }

        Comentario comentario = new Comentario();
        comentario.setDetalle(comentarioCrear.getDetalle());
        comentario.setAutor(comentarioCrear.getAutor());
        comentario.setFechaCreacion(LocalDateTime.now());
        comentario.setCurso(curso);

        return comentarioRepo.save(comentario);
    }

    public Comentario editarComentario(ComentarioEditar comentarioEditar) {
        Comentario comentario = comentarioRepo.findById(comentarioEditar.getId()).orElse(null);
        if (comentario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado");
        }

        comentario.setDetalle(comentarioEditar.getDetalle());
        return comentarioRepo.save(comentario);
    }
}