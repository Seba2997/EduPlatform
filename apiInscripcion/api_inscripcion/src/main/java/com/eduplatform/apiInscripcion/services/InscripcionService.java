package com.eduplatform.apiInscripcion.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.eduplatform.apiInscripcion.models.Curso;
import com.eduplatform.apiInscripcion.models.Usuario;
import com.eduplatform.apiInscripcion.models.entities.Boleta;
import com.eduplatform.apiInscripcion.models.entities.Inscripcion;
import com.eduplatform.apiInscripcion.models.request.CompraRequest;
import com.eduplatform.apiInscripcion.models.responses.CompraResponse;
import com.eduplatform.apiInscripcion.repositories.BoletaRepository;
import com.eduplatform.apiInscripcion.repositories.InscripcionRepository;


@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepo;
    @Autowired
    private WebClient webClient;

    @Autowired
    private BoletaRepository boletaRepository;
    
    public CompraResponse inscribirUsuario(int idEstudiante, int idCurso, CompraRequest compraRequest) {
        Usuario usuario = obtenerUsuario(idEstudiante);
        Curso curso = obtenerCurso(idCurso);
        validarInscripcion(usuario, curso);
        validarCompra(usuario, compraRequest);

        Inscripcion inscripcion = registrarInscripcion(usuario, curso);
        int numeroBoleta = generarNumeroBoletaUnico();
        Boleta boleta = crearYGuardarBoleta(inscripcion, curso, numeroBoleta);
        if (boleta == null) {
            throw new RuntimeException("Error al crear la boleta");
        }

        return construirRespuestaCompra(usuario, curso, numeroBoleta);
    }

    private Usuario obtenerUsuario(int idEstudiante) {
        Usuario usuario = webClient.get()
                                   .uri("http://localhost:8082/user/{id}", idEstudiante)
                                   .retrieve()
                                   .bodyToMono(Usuario.class)
                                   .block();
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return usuario;
    }

    private Curso obtenerCurso(int idCurso) {
        Curso curso = webClient.get()
                               .uri("http://localhost:8081/cursos/{id}", idCurso)
                               .retrieve()
                               .bodyToMono(Curso.class)
                               .block();
        if (curso == null) {
            throw new RuntimeException("Curso no encontrado");
        }
        return curso;
    }

    private void validarInscripcion(Usuario usuario, Curso curso) {
        if (inscripcionRepo.existsByIdEstudianteAndIdCurso(usuario.getId(), curso.getId())) {
            throw new RuntimeException("Usuario ya inscrito");
        }
        if (!curso.getEstado()) {
            throw new RuntimeException("El curso con ID " + curso.getId() + " no está activo y no permite inscripciones.");
        }
    }

    private void validarCompra(Usuario usuario, CompraRequest compraRequest) {
        if (!compraRequest.getNombreTarjeta().equals(usuario.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de usuario Incorrecto");
        }
        if (compraRequest.getNumeroTarjeta().length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de tarjeta Incorrecto");
        }
        if (compraRequest.getCodigoTarjeta().length() != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de tarjeta Incorrecto");
        }
    }

    private Inscripcion registrarInscripcion(Usuario usuario, Curso curso) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdEstudiante(usuario.getId());
        inscripcion.setNombreEstudiante(usuario.getName());
        inscripcion.setEmailEstudiante(usuario.getEmail());
        inscripcion.setIdCurso(curso.getId());
        inscripcion.setNombreCurso(curso.getNombreCurso());
        inscripcion.setFechaInscripcion(LocalDate.now());
        return inscripcionRepo.save(inscripcion);
    }

    private int generarNumeroBoletaUnico() {
        Random random = new Random();
        int numero;
        do {
            numero = 100000 + random.nextInt(900000);
        } while (boletaRepository.existsByNumeroBoleta(numero));
        return numero;
    }

    private Boleta crearYGuardarBoleta(Inscripcion inscripcion, Curso curso, int numeroBoleta) {
        Boleta boleta = new Boleta();
        boleta.setNumeroBoleta(numeroBoleta);
        boleta.setPrecio(curso.getPrecio());
        boleta.setFechaCompra(LocalDate.now().toString());
        boleta.setInscripcion(inscripcion);
        return boletaRepository.save(boleta);
    }

    private CompraResponse construirRespuestaCompra(Usuario usuario, Curso curso, int numeroBoleta) {
        CompraResponse response = new CompraResponse();
        response.setNumeroBoleta(numeroBoleta);
        response.setNombreUsuario(usuario.getName());
        response.setPrecio(curso.getPrecio());
        response.setEmail(usuario.getEmail());
        response.setNombreCurso(curso.getNombreCurso());
        response.setFechaCompra(LocalDate.now());
        response.setMensaje("Compra/inscripción exitosa al curso: " + curso.getNombreCurso());
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

}



