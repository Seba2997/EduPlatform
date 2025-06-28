package com.eduplatform.apiUsuario.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.controllers.UserControllers;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public @NonNull EntityModel<User> toModel(@NonNull User user) {
        return EntityModel.of(user,
            linkTo(methodOn(UserControllers.class).obtenerUno(user.getId())).withSelfRel()
            
        );
    }

    public @NonNull EntityModel<User> toModelSoloModificar(@NonNull User user) {
        return EntityModel.of(user,
            linkTo(methodOn(UserControllers.class).obtenerUno(user.getId())).withSelfRel(),
            linkTo(methodOn(UserControllers.class).modificar(user.getId(), null)).withRel("actualizarUsuario")
        );
    }

    

}
