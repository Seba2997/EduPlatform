package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Contenido;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ContenidoCrear;
import com.eduPlatform.apiCurso.models.requests.ContenidoEditar;
import com.eduPlatform.apiCurso.repositories.ContenidoRepository;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepo;

    @Autowired
    private CursoService cursoService;

    public List<Contenido> obtenerTodos() {
        return contenidoRepo.findAll();
    }

    public Contenido obtenerContenidoPorId(Integer id) {
        Contenido contenido = contenidoRepo.findById(id).orElse(null);
        if (contenido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido de este curso no encontrado");
    }
        return contenido;
    }

    public Contenido registrar(ContenidoCrear crear, int idCurso) {
        try {
            Curso curso = cursoService.obtenerCursoPorId(idCurso);
            Contenido nuevoContenido = new Contenido();
            
            nuevoContenido.setTituloContenido(crear.getContenido());
            nuevoContenido.setContenido(crear.getContenido());
            nuevoContenido.setCurso(curso);
            return contenidoRepo.save(nuevoContenido);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar contenido");
        }
    }

    public Contenido modificar(ContenidoEditar modificado) {
        Contenido contenido = contenidoRepo.findById(modificado.getIdContenido()).orElse(null);
        if (contenido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido de este curso no encontrado");
        }
        if (modificado.getTituloContenido() != null) {
            contenido.setTituloContenido(modificado.getTituloContenido());
        }
        if (modificado.getContenido() != null) {
            contenido.setContenido(modificado.getContenido());
        }
        return contenidoRepo.save(contenido);
    }

    public void eliminarContenido(int id) {
        Contenido contenido = contenidoRepo.findById(id).orElse(null);
        if (contenido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido de este curso no encontrado");
        }
        contenidoRepo.delete(contenido);
    }

}
