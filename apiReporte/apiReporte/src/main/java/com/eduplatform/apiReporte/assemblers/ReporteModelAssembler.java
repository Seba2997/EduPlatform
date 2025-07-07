package com.eduplatform.apiReporte.assemblers;



import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.eduplatform.apiReporte.controllers.ReporteController;
import com.eduplatform.apiReporte.models.entities.Reporte;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ReporteModelAssembler implements RepresentationModelAssembler<Reporte, EntityModel<Reporte>> {

    @Override
    public @NonNull EntityModel<Reporte> toModel(@NonNull Reporte reporte) {
        return EntityModel.of(reporte,
            linkTo(methodOn(ReporteController.class).crearReporte(null)).withSelfRel()

        );
    }
}