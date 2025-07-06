package com.eduPlatform.apiCurso.repositories;

import com.eduPlatform.apiCurso.models.entities.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    List<Comentario> findByCursoId(Integer cursoId);
}