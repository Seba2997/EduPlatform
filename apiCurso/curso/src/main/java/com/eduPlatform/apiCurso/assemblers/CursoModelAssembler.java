package com.eduPlatform.apiCurso.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.eduPlatform.apiCurso.controllers.CursoController;
import com.eduPlatform.apiCurso.models.entities.Curso;

@Component
public class CursoModelAssembler implements RepresentationModelAssembler<Curso, EntityModel<Curso>> {

    @Override
    public EntityModel<Curso> toModel(Curso curso) {
        return EntityModel.of(curso,
            linkTo(methodOn(CursoController.class).traerPorId(curso.getId())).withSelfRel(),
            linkTo(methodOn(CursoController.class).traerTodos()).withRel("todos-los-cursos"),
            linkTo(methodOn(CursoController.class).traerActivos()).withRel("cursos-activos"),
            linkTo(methodOn(CursoController.class).modificar(curso.getId(), null)).withRel("modificar-curso")
        );
    }

    public EntityModel<Curso> toModelSoloModificar(Curso curso) {
        return EntityModel.of(curso,
            linkTo(methodOn(CursoController.class).modificar(curso.getId(), null)).withRel("modificar-curso")
        );
    }
}