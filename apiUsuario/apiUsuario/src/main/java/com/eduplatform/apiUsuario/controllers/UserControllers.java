package com.eduplatform.apiUsuario.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.models.request.UserUpdate;
import com.eduplatform.apiUsuario.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserControllers {
    
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List<User> obtenerTodo(){
        return userService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public User obtenerUno(@PathVariable int id){
        return userService.obtenerUno(id);
    }

    @GetMapping("/email/{email}")
    public User obtenerPorEmail(@PathVariable String email){
        return userService.obtenerPorEmail(email);
    }

    @GetMapping("/activos")
    public List<User> obtenerActivos(){
        return userService.obtenerActivos();
    }

    @PostMapping("/registrar")
    public User registrar(@Valid @RequestBody UserCrear user){
        return userService.registrar(user);
    }

    @PutMapping("/modificar")
    public User modificar(@Valid @RequestBody UserUpdate body){
        return userService.modificar(body);
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<String> cambiarEstado(@PathVariable int id) {
        User usuarioModificado = userService.cambiarEstado(id);
        String mensaje;
        if (usuarioModificado.getActive()) {
            mensaje = "Usuario activado correctamente";
        } else {
            mensaje = "Usuario desactivado correctamente";
        }
        return ResponseEntity.ok(mensaje);
    }   

}
