package com.eduplatform.apiUsuario.models.response;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.eduplatform.apiUsuario.models.entities.User;

import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private String name;
    private String email;
    private String phone;
    private Date dateCreated;
    private Boolean active;
    private List<String> roles;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDateCreated(user.getDateCreated());
        dto.setActive(user.getActive());

        List<String> nombresRoles = user.getRoles().stream()
        .map(rol -> rol.getNombre().name())  // Accede al enum y lo convierte a String
        .collect(Collectors.toList());
        dto.setRoles(nombresRoles);
        return dto;
    }
}
