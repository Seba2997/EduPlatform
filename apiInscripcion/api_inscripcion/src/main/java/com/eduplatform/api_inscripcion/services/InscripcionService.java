package com.eduplatform.api_inscripcion.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.eduplatform.api_inscripcion.models.Curso;
import com.eduplatform.api_inscripcion.models.Usuario;
import com.eduplatform.api_inscripcion.models.entities.Boleta;
import com.eduplatform.api_inscripcion.models.entities.Inscripcion;
import com.eduplatform.api_inscripcion.models.request.CompraRequest;
import com.eduplatform.api_inscripcion.models.responses.CompraResponse;
import com.eduplatform.api_inscripcion.repositories.BoletaRepository;
import com.eduplatform.api_inscripcion.repositories.InscripcionRepository;


@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepo;
    @Autowired
    private WebClient webClient;

    @Autowired
    private BoletaRepository boletaRepository;
    
    public CompraResponse inscribirUsuario(int idEstudiante, int idCurso, CompraRequest compraRequest){
        CompraResponse response = new CompraResponse();

        Usuario usuario = webClient.get()
                                    .uri("http://localhost:8082/user/{id}", idEstudiante)
                                    .retrieve()
                                    .bodyToMono(Usuario.class)
                                    .block();

        if(usuario == null){
            throw new RuntimeException("Usuario no encontrado");
        }


        Curso curso = webClient.get()
                                    .uri("http://localhost:8081/cursos/{id}", idCurso)
                                    .retrieve()
                                    .bodyToMono(Curso.class)
                                    .block();

        if(curso == null){
            throw new RuntimeException("Curso no encontrado");
        }
        
        if(inscripcionRepo.existsByIdEstudianteAndIdCurso(idEstudiante, idCurso)){
            throw new RuntimeException("Usuario ya inscrito");
        }


        if (curso.getEstado()==false) {
            throw new RuntimeException("El curso con ID " + curso.getId() + " no está activo y no permite inscripciones.");
        }
        
        //validacion de tarjeta de compras
        if (!compraRequest.getNombreTarjeta().equals(usuario.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de usuario Incorrecto");
        }
        
        if (compraRequest.getNumeroTarjeta().length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de tarjeta Incorrecto");
        }

        if (compraRequest.getCodigoTarjeta().length() != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo de tarjeta Incorrecto");
        }

        Inscripcion inscripcion= new Inscripcion();
        inscripcion.setIdEstudiante(usuario.getId());
        inscripcion.setNombreEstudiante(usuario.getName());
        inscripcion.setEmailEstudiante(usuario.getEmail());
        
        inscripcion.setIdCurso(curso.getId());
        inscripcion.setNombreCurso(curso.getNombreCurso());
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcionRepo.save(inscripcion);
        
        Random random = new Random();
        int numeroAleatorio = 100000 + random.nextInt(900000); 

        response.setNumeroBoleta(numeroAleatorio);
        response.setNombreUsuario(usuario.getName());
        response.setPrecio(curso.getPrecio());
        response.setEmail(usuario.getEmail());
        response.setNombreCurso(curso.getNombreCurso());
        response.setFechaCompra(LocalDate.now());
        response.setMensaje("Compra/inscripción exitosa al curso: " + curso.getNombreCurso());

        Boleta boleta = new Boleta();
        boleta.setNumeroBoleta(response.getNumeroBoleta());
        boleta.setPrecio(response.getPrecio());
        boleta.setFechaCompra(response.getFechaCompra().toString());
        boleta.setInscripcion(inscripcion);
        boletaRepository.save(boleta);
        
        return response;
    }

    public List<Inscripcion> obtenerTodos() {
        return inscripcionRepo.findAll();
    }

    public Inscripcion obtenerInscripcionId(int id) {
        Inscripcion inscripcion = inscripcionRepo.findById(id).orElse(null);
        if (inscripcion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripcion no encontrado");
        }
        return inscripcion;
    }

    public void eliminar(int id) {
        Inscripcion inscripcion = inscripcionRepo.findById(id).orElse(null);
    if (inscripcion == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada");
    }
        inscripcionRepo.delete(inscripcion);
    }

}



