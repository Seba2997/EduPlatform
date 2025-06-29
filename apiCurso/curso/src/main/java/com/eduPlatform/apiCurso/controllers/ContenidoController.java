package com.eduPlatform.apiCurso.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eduPlatform.apiCurso.assemblers.ContenidoModelAssembler;
import com.eduPlatform.apiCurso.models.entities.Contenido;
import com.eduPlatform.apiCurso.models.requests.ContenidoCrear;
import com.eduPlatform.apiCurso.models.requests.ContenidoEditar;
import com.eduPlatform.apiCurso.services.ContenidoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/contenidos")
public class ContenidoController {

    @Autowired
    private ContenidoService contenidoService;

    @Autowired
    private ContenidoModelAssembler contenidoAssembler;

    @GetMapping("/")
    @Operation(summary = "Obtiene todos los contenidos",
                description = "Devuelve una lista de todos los contenidos registrados.")
    public CollectionModel<EntityModel<Contenido>> traerTodos() {
        List<EntityModel<Contenido>> contenidos = contenidoService.obtenerTodos()
            .stream()
            .map(contenidoAssembler::toModelMostrarTodos)
            .collect(Collectors.toList());

        return CollectionModel.of(contenidos,
            linkTo(methodOn(ContenidoController.class).traerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un contenido por ID",
                description = "Devuelve un contenido específico según su ID.")
    public EntityModel<Contenido> traerPorId(@PathVariable int id) {
        Contenido contenido = contenidoService.obtenerContenidoPorId(id);
        return contenidoAssembler.toModel(contenido);
    }

    @PostMapping("/{idCurso}")
    @Operation(summary = "Crea un nuevo contenido",
                description = "Crea un nuevo contenido asociado a un curso.")
    public EntityModel<Contenido> crearContenido(@PathVariable int idCurso, @Valid @RequestBody ContenidoCrear body) {
        Contenido creado = contenidoService.registrar(body, idCurso);
        return contenidoAssembler.toModelSoloModificar(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica un contenido existente",
                description = "Permite modificar los datos de un contenido.")
    public EntityModel<Contenido> modificar(@PathVariable int id, @Valid @RequestBody ContenidoEditar body) {
        body.setIdContenido(id);
        Contenido actualizado = contenidoService.modificar(body);
        return contenidoAssembler.toModelSoloModificar(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un contenido por ID",
                description = "Elimina un contenido específico por su ID.")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        contenidoService.eliminarContenido(id);
        return ResponseEntity.noContent().build();
    }
}