package com.eduplatform.apiInscripcion.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping("/inscribir")
    @Operation(summary = "Inscribe al usuario autenticado en un curso",
               description = "Permite a un estudiante autenticado inscribirse en un curso.")
    public CompraResponse inscribirUsuarioAutenticado(@RequestParam int idCurso, @RequestBody CompraRequest compraRequest) {
        return inscripcionService.inscribirUsuarioAutenticado(idCurso, compraRequest);
        }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE', 'COORDINADOR', 'PROFESOR')")
    @GetMapping("/")
    @Operation(summary = "Obtiene todas las inscripciones",
               description = "Devuelve una lista de todas las inscripciones registradas.")
    public CollectionModel<EntityModel<Inscripcion>> traerTodos() {
        List<Inscripcion> inscripciones = inscripcionService.obtenerTodos();

        List<EntityModel<Inscripcion>> inscripcionesConLinks = inscripciones.stream()
            .map(assembler::toModel)
            .toList();

        return CollectionModel.of(inscripcionesConLinks,
            linkTo(methodOn(InscripcionController.class).traerTodos()).withSelfRel());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE', 'COORDINADOR', 'PROFESOR')")
    @GetMapping("/obtenerUno{id}")
    @Operation(summary = "Obtiene una inscripción por su ID",
               description = "Devuelve los detalles de una inscripción específica.")
    public EntityModel<Inscripcion> obtenerUno(@PathVariable int id) {
        Inscripcion inscripcion = inscripcionService.obtenerInscripcionId(id);
        return assembler.toModel(inscripcion);
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/mis-inscripciones")
    @Operation(summary = "Inscripciones del usuario autenticado", description = "Devuelve todas las inscripciones del usuario autenticado.")
    public CollectionModel<EntityModel<Inscripcion>> obtenerInscripcionesUsuario() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Inscripcion> inscripciones = inscripcionService.obtenerPorEmail(email);

        List<EntityModel<Inscripcion>> modelos = inscripciones.stream()
            .map(assembler::toModel)
            .toList();

        return CollectionModel.of(modelos,
            linkTo(methodOn(InscripcionController.class).obtenerInscripcionesUsuario()).withSelfRel());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reporte")
    public List<Inscripcion> obtenerTodasLasInscripcionesInterno(){
        List<Inscripcion> inscripciones = inscripcionService.obtenerTodos();
        if (inscripciones == null || inscripciones.isEmpty()) {
            throw new RuntimeException("No se encontraron inscripciones para el reporte.");
        }
        return inscripciones;
    }

    
}