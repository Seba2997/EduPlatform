package com.eduplatform.apiUsuario.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiUsuario.assemblers.UserModelAssembler;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.models.request.UserUpdate;
import com.eduplatform.apiUsuario.services.UserService;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;



import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserControllers {
    
    @Autowired
    private UserModelAssembler assembler;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    @Operation(summary = "Obtiene todos los usuarios",
               description = "Devuelve una lista de todos los usuarios registrados en el sistema.")
    public CollectionModel<EntityModel<User>> obtenerTodo() {
        List<User> usuarios = userService.obtenerTodos();

        List<EntityModel<User>> usuariosConLinks = usuarios.stream()
            .map(assembler::toModel)
            .toList();

        return CollectionModel.of(usuariosConLinks,
            linkTo(methodOn(UserControllers.class).obtenerTodo()).withSelfRel(),
            linkTo(methodOn(UserControllers.class).obtenerActivos()).withRel("usuariosActivos"));
}

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por su ID",
               description = "Devuelve los detalles de un usuario específico basado en su ID.")
    public EntityModel<User> obtenerUno(@PathVariable int id){
        User user = userService.obtenerUno(id);
        return assembler.toModelSoloModificar(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtiene un usuario por su email",
               description = "Devuelve los detalles de un usuario específico basado en su email.")
    public User obtenerPorEmail(@PathVariable String email){
        return userService.obtenerPorEmail(email);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtiene todos los usuarios activos",
               description = "Devuelve una lista de todos los usuarios que están activos en el sistema.")
    public List<User> obtenerActivos(){
        return userService.obtenerActivos();
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registra un nuevo usuario",
               description = "Crea un nuevo usuario en el sistema con los datos proporcionados.")
    public EntityModel<User> registrar(@Valid @RequestBody UserCrear user){
        User nuevo = userService.registrar(user);
        return assembler.toModelSoloModificar(nuevo);
    }

    @PutMapping("/modificar/{id}")
    @Operation(summary = "Modifica un usuario existente",
               description = "Actualiza los detalles de un usuario específico basado en su ID.")
    public User modificar(@PathVariable int id,@Valid @RequestBody UserUpdate body){
        body.setId(id);
        return userService.modificar(body);
    }

    
    @PutMapping("/estado/{id}")
    @Operation(summary = "Cambia el estado de un usuario (activo/desactivado)")
    public ResponseEntity<String> cambiarEstado(@PathVariable int id) {
        User usuarioModificado = userService.cambiarEstado(id);
        String mensaje;
        if (usuarioModificado.getActive()) {
            mensaje = "Usuario " + usuarioModificado.getName() + " activado correctamente";
        } else {
            mensaje = "Usuario " + usuarioModificado.getName() + " desactivado correctamente";
        }
        return ResponseEntity.ok(mensaje);
    }   

}
