package com.eduplatform.apiUsuario.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusUpdate {
    private int id;
    private boolean active;
}
