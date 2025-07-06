package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.requests.CategoriaCrear;
import com.eduPlatform.apiCurso.models.requests.CategoriaEditar;
import com.eduPlatform.apiCurso.services.CategoriaService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    //Obtener todas las categorías
    @GetMapping
    public List<Categoria> obtenerTodas() {
        return categoriaService.obtenerTodos();
    }

    //Obtener una categoría por ID
    @GetMapping("/{id}")
    public Categoria obtenerPorId(@PathVariable Integer id) {
        return categoriaService.obtenerCategoriaPorId(id);
    }

    //Crear una nueva categoría
    @PostMapping
    public Categoria crear(@RequestBody CategoriaCrear categoriaCrear) {
        return categoriaService.crearCategoria(categoriaCrear);
    }

    //Modificar una categoría existente
    @PutMapping
    public Categoria modificar(@RequestBody CategoriaEditar categoriaEditar) {
        return categoriaService.modificarCategoria(categoriaEditar);
    }
}