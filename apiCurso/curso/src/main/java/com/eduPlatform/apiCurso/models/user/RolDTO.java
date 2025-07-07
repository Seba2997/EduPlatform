package com.eduPlatform.apiCurso.models.user;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Data;

@Data
public class RolDTO {
    private int id;
    private String nombre;

    @JsonCreator
    public RolDTO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}