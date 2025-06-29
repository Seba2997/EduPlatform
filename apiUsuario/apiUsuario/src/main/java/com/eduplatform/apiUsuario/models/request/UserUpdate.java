package com.eduplatform.apiUsuario.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdate {
   
   
    private int id;

    private String name;

    private String password;

    private String phone;
}
