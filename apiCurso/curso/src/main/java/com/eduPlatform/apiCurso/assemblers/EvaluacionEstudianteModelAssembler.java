package com.eduPlatform.apiCurso.assemblers;






import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.eduPlatform.apiCurso.controllers.EvaluacionEstudianteController;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class EvaluacionEstudianteModelAssembler implements RepresentationModelAssembler<EvaluacionEstudiante, EntityModel<EvaluacionEstudiante>> {

    @Override
    @NonNull
    public EntityModel<EvaluacionEstudiante> toModel(@NonNull EvaluacionEstudiante evaluacionEstudiante) {
        return EntityModel.of(evaluacionEstudiante,
            linkTo(methodOn(EvaluacionEstudianteController.class).responder(null)).withRel("responder"),
            linkTo(methodOn(EvaluacionEstudianteController.class).obtenerCalificacion(evaluacionEstudiante.getEvaluacionEstudianteid())).withSelfRel(),
            linkTo(methodOn(EvaluacionEstudianteController.class).verEvaluacion(evaluacionEstudiante.getEvaluacion().getId())).withRel("evaluacion")
        );
    }
}
