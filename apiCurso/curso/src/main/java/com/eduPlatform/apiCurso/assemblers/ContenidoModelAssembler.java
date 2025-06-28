package com.eduPlatform.apiCurso.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.eduPlatform.apiCurso.controllers.ContenidoController;
import com.eduPlatform.apiCurso.models.entities.Contenido;

@Component
public class ContenidoModelAssembler implements RepresentationModelAssembler<Contenido, EntityModel<Contenido>> {

    @Override
    public @NonNull EntityModel<Contenido> toModel(@NonNull Contenido contenido) {
        return EntityModel.of(contenido,
            linkTo(methodOn(ContenidoController.class).traerPorId(contenido.getIdContenido())).withSelfRel()
        );
    }

    public EntityModel<Contenido> toModelMostrarTodos(Contenido contenido) {
        return EntityModel.of(contenido,
            linkTo(methodOn(ContenidoController.class).traerPorId(contenido.getIdContenido())).withSelfRel(),
            linkTo(methodOn(ContenidoController.class).traerTodos()).withRel("todos-los-contenidos")
        );
    }


    public EntityModel<Contenido> toModelSoloModificar(Contenido contenido) {
        return EntityModel.of(contenido,
            linkTo(methodOn(ContenidoController.class).traerPorId(contenido.getIdContenido())).withSelfRel(),
            linkTo(methodOn(ContenidoController.class).modificar(contenido.getIdContenido(), null)).withRel("modificar-contenido")
        );
    }
}