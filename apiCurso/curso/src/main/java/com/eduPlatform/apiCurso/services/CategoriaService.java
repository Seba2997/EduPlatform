package com.eduPlatform.apiCurso.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.requests.CategoriaCrear;
import com.eduPlatform.apiCurso.models.requests.CategoriaEditar;
import com.eduPlatform.apiCurso.repositories.CategoriaRepository;


@Service
public class CategoriaService {
    
@Autowired
private CategoriaRepository categoriaRepo;

    


    public List<Categoria> obtenerTodos() {
        return categoriaRepo.findAll();
    }

    public Categoria obtenerCategoriaPorId(Integer id) {
        Categoria categoria = categoriaRepo.findById(id).orElse(null);
        if (categoria == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "categoria no encontrada");
    }
        return categoria;
    }

    public Categoria crearCategoria(CategoriaCrear categoriaCrear) {
    if (categoriaCrear == null || categoriaCrear.getNombreCategoria() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de categoría inválidos");
    }
    Categoria existente = categoriaRepo.findByNombreCategoriaIgnoreCase(categoriaCrear.getNombreCategoria());
    if (existente != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "La categoría ya existe");
    }
    Categoria categoria = new Categoria();
    categoria.setNombreCategoria(categoriaCrear.getNombreCategoria());
    return categoriaRepo.save(categoria);
    }


    public Categoria modificarCategoria(CategoriaEditar categoriaEditar) {
    Categoria categoria = categoriaRepo.findById(categoriaEditar.getId()).orElse(null);
    if (categoria == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
    }
    String nuevoNombre = categoriaEditar.getNombreCategoria();
    Categoria existente = categoriaRepo.findByNombreCategoriaIgnoreCase(nuevoNombre);
    if (existente != null && existente.getId() != categoria.getId()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otra categoría con ese nombre");
    }
    categoria.setNombreCategoria(nuevoNombre);
    return categoriaRepo.save(categoria);
    }

}
