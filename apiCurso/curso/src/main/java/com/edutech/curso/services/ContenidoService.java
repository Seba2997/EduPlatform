package com.edutech.curso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.edutech.curso.models.entities.Contenido;
import com.edutech.curso.models.requests.ContenidoCrear;
import com.edutech.curso.models.requests.ContenidoEditar;
import com.edutech.curso.repositories.ContenidoRepository;


public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepo;

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

public Contenido registrar(ContenidoCrear crear) {
        try {
            Contenido nuevoContenido = new Contenido();
            
            nuevoContenido.setTituloContenido(crear.getContenido());
            nuevoContenido.setContenido(crear.getContenido());
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
