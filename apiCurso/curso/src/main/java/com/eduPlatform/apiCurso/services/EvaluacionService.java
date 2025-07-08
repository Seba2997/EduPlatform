package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.CalificacionRequest;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRepository;
import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRespuestaRepository;
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


    @Autowired
    private EvaluacionEstudianteRespuestaRepository respuestaRepo;

    public EvaluacionEstudianteRespuesta calificarRespuesta(CalificacionRequest request) {
    // Buscar la respuesta del estudiante
    EvaluacionEstudiante evaluacionEstudiante = evaluacionEstudianteRepo.findById(request.getIdEvaluacionEstudiante())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta del estudiante no encontrada"));

    // Obtener datos desde la evaluación
    Evaluacion evaluacion = evaluacionEstudiante.getEvaluacion();
    int puntajeMaximo = evaluacion.getPuntajeMaximo();
    String titulo = evaluacion.getTitulo();
    String pregunta = evaluacion.getPregunta();
    String respuestaTexto = evaluacionEstudiante.getRespuesta();

    // Calcular nota
    int puntajeObtenido = request.getPuntajeObtenido() == null ? 0 : request.getPuntajeObtenido();
    double nota = calcularNota(puntajeObtenido, puntajeMaximo);

    // Crear y guardar la respuesta final con la nota
    EvaluacionEstudianteRespuesta respuestaFinal = new EvaluacionEstudianteRespuesta();
    respuestaFinal.setTitulo(titulo);
    respuestaFinal.setPregunta(pregunta);
    respuestaFinal.setRespuesta(respuestaTexto);
    respuestaFinal.setPuntajeMaximo(puntajeMaximo);
    respuestaFinal.setPuntajeObtenido(puntajeObtenido);
    respuestaFinal.setNota(nota);

    return respuestaRepo.save(respuestaFinal);
}

private double calcularNota(int puntajeObtenido, int puntajeMaximo) {
    if (puntajeMaximo == 0) return 1.0;
    double nota = 1.0 + ((double) puntajeObtenido / puntajeMaximo) * 6.0;
    return Math.min(7.0, Math.round(nota * 100.0) / 100.0);
}
    
}


