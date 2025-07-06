package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.repositories.EvaluacionRepository;

public class EvaluacionService {
    
    @Autowired
    private CursoService cursoService;

    @Autowired
    private EvaluacionRepository evaluacionRepo;

    public Evaluacion registrar(EvaluacionCrear crear, int idCurso) {
        try {
            Curso curso = cursoService.obtenerCursoPorId(idCurso);

            Evaluacion nueva = new Evaluacion();
            nueva.setTitulo(crear.getTitulo());
            nueva.setDescripcion(crear.getDescripcion());
            nueva.setPuntajeMaximo(crear.getPuntajeMaximo());
            nueva.setCurso(curso);

            return evaluacionRepo.save(nueva);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar evaluación");
        }
    }

    public List<Evaluacion> listarEvaluacionPorCurso(int idCurso) {
        Curso curso = cursoService.obtenerCursoPorId(idCurso);
        return curso.getEvaluaciones(); // ya está en la entidad
    }

    
    public Evaluacion modificarEvaluacion(EvaluacionEditar editar) {
        Evaluacion evaluacion = evaluacionRepo.findById(editar.getId()).orElse(null);
        if (evaluacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada");
        }

        if (editar.getTitulo() != null) {
            evaluacion.setTitulo(editar.getTitulo());
        }
        if (editar.getDescripcion() != null) {
            evaluacion.setDescripcion(editar.getDescripcion());
        }
        if (editar.getPuntajeMaximo() != null) {
            evaluacion.setPuntajeMaximo(editar.getPuntajeMaximo());
        }

        return evaluacionRepo.save(evaluacion);
    }


    public void eliminarEvaluacion(int id) {
        Evaluacion evaluacion = evaluacionRepo.findById(id).orElse(null);
        if (evaluacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada");
        }
        evaluacionRepo.delete(evaluacion);
    }
}
