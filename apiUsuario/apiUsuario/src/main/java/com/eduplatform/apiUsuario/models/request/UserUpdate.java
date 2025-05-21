package com.eduplatform.apiUsuario.models.request;
import lombok.Data;

@Data
public class UserUpdate {
   
    private int id;

    private String name;

    private String password;

    private String phone;
}
