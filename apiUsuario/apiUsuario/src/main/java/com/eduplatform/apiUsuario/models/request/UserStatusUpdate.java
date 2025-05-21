package com.eduplatform.apiUsuario.models.request;

import lombok.Data;

@Data
public class UserStatusUpdate {
    private int id;
    private boolean active;
}
