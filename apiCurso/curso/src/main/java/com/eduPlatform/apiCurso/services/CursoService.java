package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Profesor;
import com.eduPlatform.apiCurso.models.requests.CursoCrear;
import com.eduPlatform.apiCurso.models.requests.CursoEditar;
import com.eduPlatform.apiCurso.models.user.Rol;
import com.eduPlatform.apiCurso.models.user.Usuario;
import com.eduPlatform.apiCurso.repositories.CursoRepository;
import com.eduPlatform.apiCurso.repositories.ProfesorRepository;



@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ProfesorRepository profesorRepo;

    public List<Curso> obtenerTodos() {
        return cursoRepo.findAll();
    }

    public List<Curso> obtenerActivos() {
        return cursoRepo.findByEstado(true);
    }

    public Curso obtenerCursoPorId(int id) {
    Curso curso = cursoRepo.findById(id).orElse(null);
    if (curso == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
    }
    return curso;
}

public List<Curso> obtenerPorNombre(String nombre) {
        return cursoRepo.findByNombreCurso(nombre);
    }
    

    public Curso registrar(CursoCrear cursoCrear, int idUsuario) {
        try {

            Usuario usuario = webClient.get()
                                        .uri("http://localhost:8082/user/" + idUsuario)
                                        .retrieve()
                                        .bodyToMono(Usuario.class)
                                        .block();

            if (usuario == null || usuario.getRol() != Rol.PROFESOR) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los profesores pueden crear cursos");
            }

            Profesor profesor = profesorRepo.findById(idUsuario).orElse(null);
            if (profesor == null) {
                Profesor nuevo = new Profesor();
                nuevo.setId(usuario.getId());
                nuevo.setNombre(usuario.getName());
                nuevo.setEmail(usuario.getEmail());
                profesor = profesorRepo.save(nuevo);
            }


            Curso nuevoCurso = new Curso();
            nuevoCurso.setEstado(true); 
            nuevoCurso.setNombreCurso(cursoCrear.getNombreCurso());
            nuevoCurso.setDescripcion(cursoCrear.getDescripcion());
            nuevoCurso.setEstado(cursoCrear.getEstado());
            nuevoCurso.setPrecio(cursoCrear.getPrecio());
            nuevoCurso.setProfesor(profesor);
            return cursoRepo.save(nuevoCurso);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar curso");
        }
    }


    public Curso modificar(CursoEditar modificado) {
        Curso curso = cursoRepo.findById(modificado.getId()).orElse(null);
        if (curso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }
        if (modificado.getNombreCurso() != null) {
            curso.setNombreCurso(modificado.getNombreCurso());
        }
        if (modificado.getDescripcion() != null) {
            curso.setDescripcion(modificado.getDescripcion());
        }   
        if (modificado.getEstado()!= null) {
            curso.setEstado(modificado.getEstado());
        }    
        if (modificado.getPrecio()!= null) {
            curso.setPrecio(modificado.getPrecio());
        }  
        return cursoRepo.save(curso);
    }


    public Curso cambiarEstado(int id) {
        Curso curso = cursoRepo.findById(id).orElse(null);
        if (curso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }
        if (curso.getEstado() == true) {
            curso.setEstado(false);
        } else {
            curso.setEstado(true);
        }
            return cursoRepo.save(curso);  
    }

}

