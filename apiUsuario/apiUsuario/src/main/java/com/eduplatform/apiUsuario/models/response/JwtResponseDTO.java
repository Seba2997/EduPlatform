package com.eduplatform.apiUsuario.models.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String email;
    private List<String> roles;
}
