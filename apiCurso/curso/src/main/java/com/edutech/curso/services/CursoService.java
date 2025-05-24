package com.edutech.curso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import com.edutech.curso.models.entities.Curso;
import com.edutech.curso.models.requests.CursoCrear;
import com.edutech.curso.models.requests.CursoEditar;
import com.edutech.curso.models.user.Rol;
import com.edutech.curso.models.user.Usuario;
import com.edutech.curso.repositories.CursoRepository;



@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private WebClient webClient;

    public List<Curso> obtenerTodos() {
        return cursoRepo.findAll();
    }

    public List<Curso> obtenerActivos() {
        return cursoRepo.findByEstado(true);
    }

    public Curso obtenerCursoPorId(Integer id) {
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
                                        .uri("http://localhost:8082/api/user/" + idUsuario)
                                        .retrieve()
                                        .bodyToMono(Usuario.class)
                                        .block();

            if (usuario == null || usuario.getRol() != Rol.PROFESOR) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los profesores pueden crear cursos");
            }


            Curso nuevoCurso = new Curso();
            nuevoCurso.setEstado(true); 
            nuevoCurso.setNombreCurso(cursoCrear.getNombreCurso());
            nuevoCurso.setDescripcion(cursoCrear.getDescripcion());
            nuevoCurso.setEstado(cursoCrear.getEstado());
            nuevoCurso.setPrecio(cursoCrear.getPrecio());
            nuevoCurso.setIdProfesor(idUsuario);
            return cursoRepo.save(nuevoCurso);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
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

/* public void eliminar(int id) {
    Curso curso = cursoRepo.findById(id).orElse(null);
    if (curso == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
    }
    cursoRepo.delete(curso);
} */

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

