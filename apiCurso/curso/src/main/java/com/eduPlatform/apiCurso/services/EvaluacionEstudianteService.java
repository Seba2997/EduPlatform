package com.eduPlatform.apiCurso.services;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteEditar;

import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRepository;
import com.eduPlatform.apiCurso.repositories.EvaluacionRepository;

@Service
public class EvaluacionEstudianteService {

    @Autowired
    private EvaluacionEstudianteRepository evaluacionEstudianteRepo;

    @Autowired
    private EvaluacionRepository evaluacionRepo;

//@Autowired
//private EvaluacionEstudianteService evaluacionEetudianteService;

    // El estudiante responde (crea una respuesta)
    public EvaluacionEstudiante responder(EvaluacionEstudianteCrear crear) {
        Evaluacion evaluacion = evaluacionRepo.findById(crear.getEvaluacionId()).orElse(null);
        if (evaluacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada");
        }

//***Pendiante Estudiante estudiante = evaluacionEetudianteService.obtenerPorId(crear.getEstudianteId());

        EvaluacionEstudiante nueva = new EvaluacionEstudiante();
        nueva.setEvaluacion(evaluacion);
//***Pendiente -> nueva.setEstudiante(estudiante);
        nueva.setRespuesta(crear.getRespuesta());

        return evaluacionEstudianteRepo.save(nueva);
    }

    // El estudiante edita su respuesta (antes de ser evaluada)
    public EvaluacionEstudiante editarRespuesta(EvaluacionEstudianteEditar editar) {
        EvaluacionEstudiante evaluacionEstudiante = evaluacionEstudianteRepo.findById(editar.getId()).orElse(null);
        if (evaluacionEstudiante == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada");
        }

        evaluacionEstudiante.setRespuesta(editar.getRespuesta());

        return evaluacionEstudianteRepo.save(evaluacionEstudiante);
    }
}