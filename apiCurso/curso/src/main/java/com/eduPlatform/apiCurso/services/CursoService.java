package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Profesor;
import com.eduPlatform.apiCurso.models.requests.CursoCrear;
import com.eduPlatform.apiCurso.models.requests.CursoEditar;
import com.eduPlatform.apiCurso.models.user.UsuarioDTO;
import com.eduPlatform.apiCurso.repositories.CategoriaRepository;
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

    @Autowired
    private CategoriaRepository categoriaRepo;

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
    

    public Curso registrar(CursoCrear cursoCrear) {
    try {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String jwt = (String) auth.getCredentials();

        // 1. Consultar el usuario por ID (el profesor asignado)
        UsuarioDTO profesorUsuario = webClient.get()
            .uri("http://localhost:8082/user/internal/" + cursoCrear.getIdProfesor())
            .header("Authorization", "Bearer " + jwt)
            .retrieve()
            .bodyToMono(UsuarioDTO.class)
            .block();

        if (profesorUsuario == null || !profesorUsuario.tieneRol("PROFESOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario asignado no es un profesor válido");
        }

        // 2. Buscar o crear al profesor localmente
        Profesor profesor = profesorRepo.findById(profesorUsuario.getId()).orElse(null);
        if (profesor == null) {
            profesor = new Profesor();
            profesor.setId(profesorUsuario.getId());
            profesor.setNombre(profesorUsuario.getName());
            profesor.setEmail(profesorUsuario.getEmail());
            profesor = profesorRepo.save(profesor);
        }

        // 3. Procesar la categoría
        String nuevoNombreCategoria = cursoCrear.getCategoriaNombre();
        if (nuevoNombreCategoria == null || nuevoNombreCategoria.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría es obligatorio");
        }

        Categoria categoria = categoriaRepo.findByNombreCategoriaIgnoreCase(nuevoNombreCategoria);
        if (categoria == null) {
            categoria = new Categoria();
        }
        categoria.setNombreCategoria(nuevoNombreCategoria);
        categoria = categoriaRepo.save(categoria);

        // 4. Crear y guardar el curso
        Curso nuevoCurso = new Curso();
        nuevoCurso.setNombreCurso(cursoCrear.getNombreCurso());
        nuevoCurso.setDescripcion(cursoCrear.getDescripcion());
        nuevoCurso.setEstado(cursoCrear.getEstado());
        nuevoCurso.setPrecio(cursoCrear.getPrecio());
        nuevoCurso.setProfesor(profesor);
        nuevoCurso.setCategoria(categoria);

        return cursoRepo.save(nuevoCurso);
        
    } catch (Exception e) {
        e.printStackTrace();
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar curso: " + e.getMessage());
    }
    }


    public Curso modificar(CursoEditar cursoEditar) {
        Curso curso = cursoRepo.findById(cursoEditar.getId()).orElse(null);
        if (curso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        }
        if (cursoEditar.getNombreCurso() != null) {
            curso.setNombreCurso(cursoEditar.getNombreCurso());
        }
        if (cursoEditar.getDescripcion() != null) {
            curso.setDescripcion(cursoEditar.getDescripcion());
        }   
        if (cursoEditar.getEstado()!= null) {
            curso.setEstado(cursoEditar.getEstado());
        }    
        if (cursoEditar.getPrecio()!= null) {
            curso.setPrecio(cursoEditar.getPrecio());
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

