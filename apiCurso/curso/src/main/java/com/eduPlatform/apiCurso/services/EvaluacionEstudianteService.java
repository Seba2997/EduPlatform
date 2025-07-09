package com.eduPlatform.apiCurso.services;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.models.user.UsuarioDTO;
import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRepository;
import com.eduPlatform.apiCurso.repositories.EvaluacionRepository;

import org.springframework.http.HttpHeaders;

@Service
public class EvaluacionEstudianteService {

    @Autowired
    private EvaluacionEstudianteRepository evaluacionEstudianteRepo;

    @Autowired
    private EvaluacionRepository evaluacionRepo;





    // El estudiante responde (crea una respuesta)
    public EvaluacionEstudiante responder(EvaluacionEstudianteCrear crear) {

        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        
        WebClient webClient = WebClient.builder()
        .baseUrl("http://localhost:8082") // URL del microservicio apiUsuario
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .build();

        UsuarioDTO user = webClient.get()
        .uri("/mi-perfil")
        .retrieve()
        .bodyToMono(UsuarioDTO.class)
        .block();

        if (user == null || !user.getRoles().contains("ESTUDIANTE")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para responder evaluaciones");
        }

         Evaluacion evaluacion = evaluacionRepo.findById(crear.getEvaluacionId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluación no encontrada"));

        EvaluacionEstudiante respuesta = new EvaluacionEstudiante();
        respuesta.setRespuesta(crear.getRespuesta());
        respuesta.setNombreEstudiante(user.getName());
        respuesta.setEmailEstudiante(user.getEmail());
        respuesta.setEvaluacion(evaluacion);

    return evaluacionEstudianteRepo.save(respuesta);

    }



public EvaluacionEstudianteRespuesta obtenerCalificacionPorId(int id) {
    EvaluacionEstudiante entity = evaluacionEstudianteRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

    return new EvaluacionEstudianteRespuesta(
        entity.getEvaluacionEstudianteid(),
        entity.getNombreEstudiante(),
        entity.getEmailEstudiante(),
        entity.getRespuesta(),
        entity.getPuntajeObtenido(),
        entity.getNota(),
        entity.getEvaluacion().getTitulo()
    );
}

public Evaluacion obtenerEvaluacionPorEvaluacionEstudianteId(int evaluacionEstudianteId) {
    EvaluacionEstudiante evaluacionEstudiante = evaluacionEstudianteRepo.findById(evaluacionEstudianteId)
        .orElseThrow(() -> new RuntimeException("Evaluación del estudiante no encontrada con ID: " + evaluacionEstudianteId));

    return evaluacionEstudiante.getEvaluacion();
}

}