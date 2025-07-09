package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRepository;
import com.eduPlatform.apiCurso.repositories.EvaluacionRepository;

@Service
public class EvaluacionService {
    
    @Autowired
    private CursoService cursoService;

    @Autowired
    private EvaluacionRepository evaluacionRepo;

    @Autowired
    private EvaluacionEstudianteRepository evaluacionEstudianteRepo;

    public Evaluacion registrar(EvaluacionCrear crear, int idCurso) {
        try {
            Curso curso = cursoService.obtenerCursoPorId(idCurso);

            Evaluacion nueva = new Evaluacion();
            nueva.setTitulo(crear.getTitulo());
            nueva.setPregunta(crear.getPregunta());
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

    
    public Evaluacion modificarEvaluacion(EvaluacionEditar editar, int idEvaluacion) {
        Evaluacion evaluacion = evaluacionRepo.findById(idEvaluacion).orElse(null);
        if (evaluacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada");
        }

        if (editar.getTitulo() != null) {
            evaluacion.setTitulo(editar.getTitulo());
        }
        if (editar.getPregunta() != null) {
            evaluacion.setPregunta(editar.getPregunta());
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

//Funciones de Evaluacion Profesor a Estudiante 


   


     public EvaluacionEstudianteRespuesta calificarRespuesta(int evaluacionEstudianteId, int puntajeObtenido) {
    // Obtener EvaluacionEstudiante existente
    EvaluacionEstudiante evaluacionEstudiante = evaluacionEstudianteRepo
            .findById(evaluacionEstudianteId)
            .orElseThrow(() -> new RuntimeException("Evaluación del estudiante no encontrada"));

    // Obtener puntaje máximo desde la Evaluacion asociada
    Evaluacion evaluacion = evaluacionEstudiante.getEvaluacion();
    int puntajeMaximo = evaluacion.getPuntajeMaximo();

    // Validación: puntaje obtenido no puede ser mayor al máximo
    if (puntajeObtenido > puntajeMaximo) {
        throw new IllegalArgumentException("Puntaje obtenido no puede ser mayor que el puntaje máximo.");
    }

    // Calcular nota en escala de 1 a 7 (lineal)
    double nota = 1.0 + ((double) puntajeObtenido / puntajeMaximo) * 6.0;

    // Asignar y guardar
    evaluacionEstudiante.setPuntajeObtenido(puntajeObtenido);
    evaluacionEstudiante.setNota(Math.round(nota * 100.0) / 100.0); // redondeo a 2 decimales

    EvaluacionEstudiante guardado = evaluacionEstudianteRepo.save(evaluacionEstudiante);

    // Mapear a DTO antes de retornar
    EvaluacionEstudianteRespuesta dto = new EvaluacionEstudianteRespuesta(
        guardado.getEvaluacionEstudianteid(),
        guardado.getNombreEstudiante(),
        guardado.getEmailEstudiante(),
        guardado.getRespuesta(),
        guardado.getPuntajeObtenido(),
        guardado.getNota(),
        guardado.getEvaluacion().getTitulo()
    );

    return dto;
}


    public EvaluacionEstudiante verRespuestaEstudiantePorId(int evaluacionEstudianteId) {
    return evaluacionEstudianteRepo.findById(evaluacionEstudianteId)
        .orElseThrow(() -> new RuntimeException("Respuesta de estudiante no encontrada con ID: " + evaluacionEstudianteId));
}


}
    



