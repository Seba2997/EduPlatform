package com.eduplatform.api_inscripcion.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.eduplatform.api_inscripcion.entities.Curso;
import com.eduplatform.api_inscripcion.entities.Inscripcion;
import com.eduplatform.api_inscripcion.entities.Usuario;
import com.eduplatform.api_inscripcion.repositories.InscripcionRepository;


@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepo;
    @Autowired
    private WebClient webClient;
    
    public Inscripcion inscribirUsuario(int idEstudiante, int idCurso){

        Usuario usuario = webClient.get()
                                    .uri("http://localhost:8082/user/{id}", idEstudiante)
                                    .retrieve()
                                    .bodyToMono(Usuario.class)
                                    .block();

        if(usuario == null){
            throw new RuntimeException("Usuario no encontrado");
        }


        Curso curso = webClient.get()
                                    .uri("http://localhost:8081/curso/{id}", idCurso)
                                    .retrieve()
                                    .bodyToMono(Curso.class)
                                    .block();

        if(curso == null){
            throw new RuntimeException("Curso no encontrado");
        }
        
        if(inscripcionRepo.existsByIdEstudianteAndIdCurso(idEstudiante, idCurso)){
            throw new RuntimeException("Usuario ya inscrito");
        }
        Inscripcion inscripcion= new Inscripcion();
        inscripcion.setIdEstudiante(usuario.getId());
        inscripcion.setNombreEstudiante(usuario.getNombre());
        inscripcion.setEmailEstudiante(usuario.getEmail());
        
        inscripcion.setIdCurso(curso.getId());
        inscripcion.setNombreCurso(curso.getNombre());
        inscripcion.setPrecioCurso(curso.getPrecio());
        inscripcion.setFechaInscripcion(LocalDate.now());

        return inscripcion;
    }

    public List<Inscripcion> obtenerTodos() {
        return inscripcionRepo.findAll();
    }

    public Inscripcion obtenerCursoPorId(Integer id) {
    Inscripcion inscripcion = inscripcionRepo.findById(id).orElse(null);
    if (inscripcion == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripcion no encontrado");
    }
    return inscripcion;
}

}
