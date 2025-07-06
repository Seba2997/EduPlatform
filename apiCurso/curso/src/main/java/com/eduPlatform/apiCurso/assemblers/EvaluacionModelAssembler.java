package com.eduPlatform.apiCurso.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.eduPlatform.apiCurso.controllers.EvaluacionController;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class EvaluacionModelAssembler implements RepresentationModelAssembler<Evaluacion, EntityModel<Evaluacion>> {

    @Override
    public @NonNull EntityModel<Evaluacion> toModel(@NonNull Evaluacion evaluacion) {
        return EntityModel.of(evaluacion,
            linkTo(methodOn(EvaluacionController.class).listarPorCurso(evaluacion.getCurso().getId())).withRel("evaluaciones-del-curso"),
            linkTo(methodOn(EvaluacionController.class).modificarEvaluacion(null)).withRel("modificar-evaluacion"),
            linkTo(methodOn(EvaluacionController.class).eliminarEvaluacion(evaluacion.getId())).withRel("eliminar-evaluacion")
        );
    }
}