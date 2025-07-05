package com.eduplatform.apiUsuario.models.request;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}
