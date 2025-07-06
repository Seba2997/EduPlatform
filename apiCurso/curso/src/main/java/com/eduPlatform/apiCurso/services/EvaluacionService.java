package com.eduPlatform.apiCurso.services;

import java.util.ArrayList;
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

    
    public Evaluacion modificarEvaluacion(EvaluacionEditar editar) {
        Evaluacion evaluacion = evaluacionRepo.findById(editar.getId()).orElse(null);
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

// NUEVA FUNCIÓN: calcular nota en escala 1-7 y devolver EvaluacionEstudianteRespuesta con datos para el estudiante
    public EvaluacionEstudianteRespuesta obtenerRespuestaConNota(int idRespuesta) {
        EvaluacionEstudiante respuesta = evaluacionEstudianteRepo.findById(idRespuesta)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada"));

        int puntajeMax = respuesta.getEvaluacion().getPuntajeMaximo();
        Integer puntajeObtenido = respuesta.getPuntajeObtenido();
        puntajeObtenido = puntajeObtenido == null ? 0 : puntajeObtenido;

        double nota = calcularNota(puntajeObtenido, puntajeMax);

        return new EvaluacionEstudianteRespuesta(
            respuesta.getEvaluacion().getTitulo(),
            respuesta.getEvaluacion().getPregunta(),
            respuesta.getRespuesta(),
            puntajeMax,
            puntajeObtenido,
            nota
        );
    }

    // --- NUEVA FUNCIÓN: listar todas las respuestas de estudiantes con nota ---
    public List<EvaluacionEstudianteRespuesta> listarTodasRespuestas() {
        List<EvaluacionEstudiante> respuestas = evaluacionEstudianteRepo.findAll();

        List<EvaluacionEstudianteRespuesta> resultado = new ArrayList<>();
        for (EvaluacionEstudiante r : respuestas) {
            int puntajeMax = r.getEvaluacion().getPuntajeMaximo();
            Integer puntajeObtenido = r.getPuntajeObtenido();
            puntajeObtenido = puntajeObtenido == null ? 0 : puntajeObtenido;

            double nota = calcularNota(puntajeObtenido, puntajeMax);

            EvaluacionEstudianteRespuesta mensaje = new EvaluacionEstudianteRespuesta(
                r.getEvaluacion().getTitulo(),
                r.getEvaluacion().getPregunta(),
                r.getRespuesta(),
                puntajeMax,
                puntajeObtenido,
                nota
            );
            resultado.add(mensaje);
        }
        return resultado;
    }

    // Método para calcular la nota en escala 1-7
    private double calcularNota(int puntajeObtenido, int puntajeMaximo) {
        if (puntajeMaximo == 0) return 1.0; // evitar división por cero

        // Fórmula: escala lineal de 1 a 7
        double nota = 1.0 + ((double) puntajeObtenido / puntajeMaximo) * 6.0;

        // limitar nota a máximo 7
        if (nota > 7.0) nota = 7.0;

        // redondear a 2 decimales
        return Math.round(nota * 100.0) / 100.0;
    }

}
