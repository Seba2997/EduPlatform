package com.edutech.curso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.edutech.curso.models.entities.Curso;
import com.edutech.curso.models.requests.CursoCrear;
import com.edutech.curso.models.requests.CursoEditar;
import com.edutech.curso.repositories.CursoRepository;



@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepo;

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
    

    public Curso registrar(CursoCrear cursoCrear) {
        try {
            Curso nuevoCurso = new Curso();
            nuevoCurso.setEstado(true); 
            nuevoCurso.setNombreCurso(cursoCrear.getNombreCurso());
            nuevoCurso.setDescripcion(cursoCrear.getDescripcion());
            nuevoCurso.setPrecio(cursoCrear.getPrecio());
            return cursoRepo.save(nuevoCurso);
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
