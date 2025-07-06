package com.eduPlatform.apiCurso.controllers;

import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.requests.CategoriaCrear;
import com.eduPlatform.apiCurso.models.requests.CategoriaEditar;
import com.eduPlatform.apiCurso.services.CategoriaService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categorías", description = "Operaciones para gestionar las categorías de los cursos")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Obtener todas las categorías")
    @GetMapping
    public List<Categoria> obtenerTodas() {
        return categoriaService.obtenerTodos();
    }

    @Operation(summary = "Obtener categoría por ID")
    @GetMapping("/{id}")
    public Categoria obtenerPorId(@PathVariable Integer id) {
        return categoriaService.obtenerCategoriaPorId(id);
    }

    @Operation(summary = "Crear una nueva categoría")
    @PostMapping
    public Categoria crear(@RequestBody CategoriaCrear categoriaCrear) {
        return categoriaService.crearCategoria(categoriaCrear);
    }

    @Operation(summary = "Modificar una categoría existente")
    @PutMapping
    public Categoria modificar(@RequestBody CategoriaEditar categoriaEditar) {
        return categoriaService.modificarCategoria(categoriaEditar);
    }
}