package com.eduPlatform.apiCurso.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.eduPlatform.apiCurso.controllers.CategoriaController;
import com.eduPlatform.apiCurso.models.entities.Categoria;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<Categoria, EntityModel<Categoria>> {

    @Override
    public @NonNull EntityModel<Categoria> toModel(@NonNull Categoria categoria) {
        return EntityModel.of(categoria,
            linkTo(methodOn(CategoriaController.class).obtenerPorId(categoria.getId())).withSelfRel(),
            linkTo(methodOn(CategoriaController.class).obtenerTodas()).withRel("todas-las-categorias"),
            linkTo(methodOn(CategoriaController.class).modificar(null)).withRel("modificar-categoria")
        );
    }
}