package com.eduPlatform.apiCurso.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.eduPlatform.apiCurso.controllers.ComentarioController;
import com.eduPlatform.apiCurso.models.entities.Comentario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ComentarioModelAssembler implements RepresentationModelAssembler<Comentario, EntityModel<Comentario>> {

    @Override
    public @NonNull EntityModel<Comentario> toModel(@NonNull Comentario comentario) {
        return EntityModel.of(comentario,
            linkTo(methodOn(ComentarioController.class).listarPorCurso(comentario.getCurso().getId())).withRel("comentarios-del-curso"),
            linkTo(methodOn(ComentarioController.class).editar(comentario.getId(), null)).withRel("modificar-comentario")
        );
    }
}