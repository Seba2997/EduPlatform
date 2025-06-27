package com.eduplatform.apiUsuario.service;

import static org.junit.jupiter.api.Assertions.*;


import com.eduplatform.apiUsuario.models.Rol;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.repositories.UserRepository;
import com.eduplatform.apiUsuario.services.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.web.server.ResponseStatusException;


@SpringBootTest
public class UserServiceTest {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void probraHash() {
        String passsword = "password123";
        String hash = userService.generateHash(passsword);
        boolean coincide = userService.validarPassword(passsword, hash);
        assertEquals(true, coincide);
    }

    
    @Test
    public void testRegistrarUsuario() {
        
        //Crear solicitud de usuario
        UserCrear nuevo = new UserCrear();
        nuevo.setName("Juan");
        nuevo.setEmail("juan@example.com");
        nuevo.setPhone("12345678");
        nuevo.setPassword("password123");
        nuevo.setRol(Rol.ESTUDIANTE);

        //Ejecutar
        User creado = userService.registrar(nuevo);

        //Verificar
        assertNotNull(creado.getId());
        assertEquals("Juan", creado.getName());
        assertEquals("juan@example.com", creado.getEmail());
        assertEquals("12345678", creado.getPhone());
        assertNotNull(creado.getPassword());
        assertNotEquals("password123", creado.getPassword());
        assertEquals(Rol.ESTUDIANTE, creado.getRol());
        assertTrue(creado.getActive());
        assertNotNull(creado.getDateCreated());

        
        userRepository.deleteById(creado.getId());
    }

    
    @Test
    public void testRegistrarUsuarioConDatosInvalidos() {
        //Email faltante
        UserCrear sinEmail = new UserCrear();
        sinEmail.setName("Usuario");
        sinEmail.setPhone("12345678");
        sinEmail.setPassword("password");
        sinEmail.setRol(Rol.ESTUDIANTE);
        
        assertThrows(ResponseStatusException.class, () -> userService.registrar(sinEmail));

        //Contraseña faltante
        UserCrear sinPassword = new UserCrear();
        sinPassword.setName("Usuario");
        sinPassword.setEmail("usuario@example.com");
        sinPassword.setPhone("12345678");
        sinPassword.setRol(Rol.ESTUDIANTE);
        
        assertThrows(ResponseStatusException.class, () -> userService.registrar(sinPassword));
    }
}