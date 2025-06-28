package com.eduplatform.apiInscripcion.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.eduplatform.apiInscripcion.controllers.InscripcionController;
import com.eduplatform.apiInscripcion.models.entities.Inscripcion;

@Component
public class InscripcionModelAssembler implements RepresentationModelAssembler<Inscripcion, EntityModel<Inscripcion>> {

    @Override
    public @NonNull EntityModel<Inscripcion> toModel(@NonNull Inscripcion inscripcion) {
        return EntityModel.of(inscripcion,
            linkTo(methodOn(InscripcionController.class).obtenerUno(inscripcion.getId())).withSelfRel()
        );
    }

    
    
}
