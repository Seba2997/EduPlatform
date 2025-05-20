package com.eduplatform.apiUsuario.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCrear {
    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String phone;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min=8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
