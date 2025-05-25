package com.eduPlatform.apiCurso.models.user;

import lombok.Data;

@Data
public class Usuario {
    private int id;
    private String name;
    private String email;
    private Rol rol;
}
