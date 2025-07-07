package com.eduPlatform.apiCurso.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.eduPlatform.apiCurso.assemblers.CategoriaModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.requests.CategoriaCrear;
import com.eduPlatform.apiCurso.models.requests.CategoriaEditar;
import com.eduPlatform.apiCurso.services.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaModelAssembler assembler;

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    @GetMapping("/obtenerTodas")
    @Operation(summary = "Lista todas las categorías",
               description = "Devuelve una lista de todas las categorías registradas.")
    public CollectionModel<EntityModel<Categoria>> obtenerTodas() {
        List<EntityModel<Categoria>> categorias = categoriaService.obtenerTodos()
            .stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(categorias,
            linkTo(methodOn(CategoriaController.class).obtenerTodas()).withSelfRel());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    @GetMapping("/obtenerPorId{id}")
    @Operation(summary = "Obtiene una categoría por ID",
               description = "Devuelve los detalles de una categoría específica.")
    public EntityModel<Categoria> obtenerPorId(@PathVariable int id) {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(id);
        return assembler.toModel(categoria);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    @PostMapping("/crear")
    @Operation(summary = "Crea una nueva categoría",
               description = "Permite registrar una nueva categoría.")
    public EntityModel<Categoria> crear(@Valid @RequestBody CategoriaCrear categoriaCrear) {
        Categoria creada = categoriaService.crearCategoria(categoriaCrear);
        return assembler.toModel(creada);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    @PutMapping("/modificar{id}")
    @Operation(summary = "Modifica una categoría existente",
               description = "Permite actualizar los datos de una categoría.")
    public EntityModel<Categoria> modificar(@Valid @RequestBody CategoriaEditar categoriaEditar) {
        Categoria actualizada = categoriaService.modificarCategoria(categoriaEditar);
        return assembler.toModel(actualizada);
    }
}