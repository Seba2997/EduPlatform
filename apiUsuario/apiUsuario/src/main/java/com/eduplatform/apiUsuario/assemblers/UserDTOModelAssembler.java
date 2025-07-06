package com.eduplatform.apiUsuario.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.eduplatform.apiUsuario.controllers.UserControllers;
import com.eduplatform.apiUsuario.models.response.UserDTO;


@Component
public class UserDTOModelAssembler implements RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    @Override
    public @NonNull EntityModel<UserDTO> toModel(@NonNull UserDTO userDTO) {
        return generarModeloGeneral(userDTO);
    }

    public EntityModel<UserDTO> generarModeloGeneral(UserDTO userDTO) {
        return EntityModel.of(userDTO,
            linkTo(methodOn(UserControllers.class).obtenerUno(userDTO.getId())).withSelfRel(),
            linkTo(methodOn(UserControllers.class).modificar(userDTO.getId(), null)).withRel("modificar"),
            linkTo(methodOn(UserControllers.class).cambiarEstado(userDTO.getId())).withRel("cambiarEstado")
        );
    }

    public EntityModel<UserDTO> modeloSoloLectura(UserDTO userDTO) {
        return EntityModel.of(userDTO,
            linkTo(methodOn(UserControllers.class).obtenerUno(userDTO.getId())).withSelfRel()
        );
    }

    public EntityModel<UserDTO> modeloPerfil(UserDTO userDTO) {
        return EntityModel.of(userDTO,
            linkTo(methodOn(UserControllers.class).miPerfil()).withSelfRel(),
            linkTo(methodOn(UserControllers.class).modificar(userDTO.getId(), null)).withRel("modificar")
        );
    }

    public EntityModel<UserDTO> modeloRegistro(UserDTO userDTO) {
        return EntityModel.of(userDTO,
            linkTo(methodOn(UserControllers.class).miPerfil()).withRel("miPerfil")
        );
    }
}