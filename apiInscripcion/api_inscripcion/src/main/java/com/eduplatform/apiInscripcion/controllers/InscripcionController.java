package com.eduplatform.apiInscripcion.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiInscripcion.assemblers.InscripcionModelAssembler;
import com.eduplatform.apiInscripcion.models.entities.Inscripcion;
import com.eduplatform.apiInscripcion.models.request.CompraRequest;
import com.eduplatform.apiInscripcion.models.responses.CompraResponse;
import com.eduplatform.apiInscripcion.services.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {
    
    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private InscripcionModelAssembler assembler;

    @GetMapping("/")
    @Operation(summary = "Obtiene todas las inscripciones",
               description = "Devuelve una lista de todas las inscripciones registradas en el sistema.")
    public CollectionModel<EntityModel<Inscripcion>> traerTodos(){
        List<Inscripcion> inscripciones = inscripcionService.obtenerTodos();
        
        List<EntityModel<Inscripcion>> inscripcionesConLinks = inscripciones.stream()
            .map(assembler::toModel)
            .toList();
            
        return CollectionModel.of(inscripcionesConLinks,
            linkTo(methodOn(InscripcionController.class).traerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una inscripción por su ID",
               description = "Devuelve los detalles de una inscripción específica basada en su ID.")
    public EntityModel<Inscripcion> obtenerUno(@PathVariable int id) {
        Inscripcion inscripcion = inscripcionService.obtenerInscripcionId(id);
        return assembler.toModel(inscripcion);
    }

    @PostMapping("/inscribir")
    @Operation(summary = "Inscribe a un usuario en un curso",
               description = "Permite inscribir a un estudiante en un curso específico utilizando los datos de la tarjeta de compra.")
    public CompraResponse InscribirUsuario(@RequestParam int idEstudiante, @RequestParam int idCurso, @RequestBody CompraRequest datoTarjeta){
        return inscripcionService.inscribirUsuario(idEstudiante, idCurso, datoTarjeta);
    }


}
