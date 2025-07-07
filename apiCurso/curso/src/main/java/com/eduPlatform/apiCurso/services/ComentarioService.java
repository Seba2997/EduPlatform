package com.eduPlatform.apiCurso.services;

import com.eduPlatform.apiCurso.models.entities.Comentario;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ComentarioCrear;
import com.eduPlatform.apiCurso.models.requests.ComentarioEditar;
import com.eduPlatform.apiCurso.repositories.ComentarioRepository;
import com.eduPlatform.apiCurso.repositories.CursoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailAutor = auth.getName();

        Comentario comentario = new Comentario();
        comentario.setDetalle(comentarioCrear.getDetalle());
        comentario.setEmailAutor(emailAutor);
        comentario.setFechaCreacion(LocalDateTime.now());
        comentario.setCurso(curso);

        return comentarioRepo.save(comentario);
    }

    public Comentario editarComentario(ComentarioEditar comentarioEditar, int idComentario) {
        Comentario comentario = comentarioRepo.findById(idComentario).orElse(null);
        if (comentario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailAutor = auth.getName();

        if (!comentario.getEmailAutor().equals(emailAutor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar este comentario");
        }
    
        comentario.setDetalle(comentarioEditar.getDetalle());
        return comentarioRepo.save(comentario);
    }
}