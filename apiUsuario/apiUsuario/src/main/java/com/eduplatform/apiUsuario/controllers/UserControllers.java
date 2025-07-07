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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eduplatform.apiUsuario.assemblers.UserDTOModelAssembler;
import com.eduplatform.apiUsuario.models.RolNombre;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.models.request.UserUpdate;
import com.eduplatform.apiUsuario.models.response.UserDTO;
import com.eduplatform.apiUsuario.services.UserService;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserControllers {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDTOModelAssembler dtoAssembler;

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SOPORTE')")
    @GetMapping("/dto")
    @Operation(summary = "Obtiene todos los usuarios ",
               description = "Devuelve una lista de todos los usuarios registrados en el sistema.")
    public CollectionModel<EntityModel<UserDTO>> obtenerTodos() {
        List<UserDTO> usuarios = userService.obtenerTodosDTO();

        List<EntityModel<UserDTO>> usuariosConLinks = usuarios.stream()
            .map(dtoAssembler::generarModeloGeneral)
            .toList();

        return CollectionModel.of(usuariosConLinks,
            linkTo(methodOn(UserControllers.class).obtenerTodos()).withSelfRel());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SOPORTE')")
    @GetMapping("/dto/{id}")
    @Operation(summary = "Obtiene un usuario por su ID.",
               description = "Devuelve los detalles de un usuario específico basado en su ID.")
    public EntityModel<UserDTO> obtenerUno(@PathVariable int id) {
        UserDTO user = userService.obtenerUnoDTO(id);
        return dtoAssembler.generarModeloGeneral(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SOPORTE')")
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtiene un usuario por su email",
               description = "Devuelve los detalles de un usuario específico basado en su email.")
    public EntityModel<UserDTO> obtenerPorEmail(@PathVariable String email) {
        UserDTO user = UserDTO.fromEntity(userService.obtenerPorEmail(email));
        return dtoAssembler.modeloSoloLectura(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    @GetMapping("/rol/{rol}")
    @Operation(summary = "Lista usuarios por rol",
               description = "Devuelve una lista de usuarios que tienen el rol especificado.")
    public CollectionModel<EntityModel<UserDTO>> listarPorRol(
            @Parameter(description = "Rol a buscar: ADMIN, ESTUDIANTE, PROFESOR, COORDINADOR")
            @PathVariable RolNombre rol) {

        List<UserDTO> usuarios = userService.obtenerPorRol(rol);

        List<EntityModel<UserDTO>> usuariosConLinks = usuarios.stream()
            .map(dtoAssembler::modeloSoloLectura)
            .toList();

        return CollectionModel.of(usuariosConLinks,
            linkTo(methodOn(UserControllers.class).listarPorRol(rol)).withSelfRel());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SOPORTE')")
    @PutMapping("/modificar/{id}")
    @Operation(summary = "Modifica un usuario existente",
               description = "Actualiza los detalles de un usuario específico basado en su ID.")
    public ResponseEntity<String> modificar(@PathVariable int id, @Valid @RequestBody UserUpdate body) {
        body.setId(id);
        userService.modificar(body);
        return ResponseEntity.ok("Usuario modificado correctamente");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    @PostMapping("/registrar")
    @Operation(summary = "Registra un usuario con cualquier rol (ADMIN)", description = "Permite al admin registrar usuarios con roles personalizados (ADMIN, PROFESOR, ESTUDIANTE, COORDINADOR, SOPORTE)")
    public ResponseEntity<String> registrar(@Valid @RequestBody UserCrear user){
        userService.registrarComo(user, user.getRol());
        return ResponseEntity.ok("Usuario registrado correctamente como " + user.getRol());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SOPORTE')")
    @PutMapping("/estado/{id}")
    @Operation(summary = "Cambia el estado de un usuario (activo/desactivado)")
    public ResponseEntity<String> cambiarEstado(@PathVariable int id) {
        var usuarioModificado = userService.cambiarEstado(id);
        String mensaje = usuarioModificado.getActive()
            ? "Usuario " + usuarioModificado.getName() + " activado correctamente"
            : "Usuario " + usuarioModificado.getName() + " desactivado correctamente";
        return ResponseEntity.ok(mensaje);
    }

    @PostMapping("/signup")
    @Operation(summary = "Registro libre de estudiantes", description = "Permite a cualquier persona registrarse como estudiante.")
    public EntityModel<UserDTO> registroLibre(@Valid @RequestBody UserCrear user) {
        var nuevo = userService.registrarComo(user, RolNombre.ESTUDIANTE);
        return dtoAssembler.modeloRegistro(UserDTO.fromEntity(nuevo));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mi-perfil")
    @Operation(summary = "Datos del usuario actual", description = "Devuelve los datos del usuario autenticado.")
    public EntityModel<UserDTO> miPerfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🟡 Email autenticado: [" + email + "]");
        UserDTO user = UserDTO.fromEntity(userService.obtenerPorEmail(email));
        return dtoAssembler.modeloPerfil(user);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/modificar-mi-perfil")
    @Operation(summary = "Modificar perfil del usuario autenticado", description = "Permite al usuario autenticado actualizar su información personal.")
    public ResponseEntity<String> modificarMiPerfil(@Valid @RequestBody UserUpdate body) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User usuario = userService.obtenerPorEmail(email);
        body.setId(usuario.getId()); // establecemos el ID del usuario autenticado
        userService.modificar(body);
        return ResponseEntity.ok("Perfil actualizado correctamente");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'SOPORTE')")
    @GetMapping("/internal/{id}")
    @Operation(summary = "Obtiene un usuario interno por ID", description = "Devuelve los detalles de un usuario específico basado en su ID para uso interno.")
    public ResponseEntity<UserDTO> obtenerInterno(@PathVariable int id) {
        UserDTO user = userService.obtenerUnoDTO(id);
        return ResponseEntity.ok(user);
    }

    
}

