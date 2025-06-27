package com.eduplatform;

import static org.junit.jupiter.api.Assertions.*;

import com.eduplatform.apiUsuario.ApiUsuarioApplication;
import com.eduplatform.apiUsuario.models.Rol;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.repositories.UserRepository;
import com.eduplatform.apiUsuario.services.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.web.server.ResponseStatusException;


@SpringBootTest(classes = ApiUsuarioApplication.class)
public class UserServiceTest {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegistrarUsuario() {
        //Crear solicitud de usuario
        UserCrear nuevo = new UserCrear();
        nuevo.setName("Juan");
        nuevo.setEmail("juan@example.com");
        nuevo.setPhone("12345678");
        nuevo.setPassword("password123");
        nuevo.setRol(Rol.ESTUDIANTE);

        // Ejecutar
        User creado = userService.registrar(nuevo);

        // Verificar
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
        // Caso 1: Email faltante
        UserCrear sinEmail = new UserCrear();
        sinEmail.setName("Usuario");
        sinEmail.setPhone("12345678");
        sinEmail.setPassword("password");
        sinEmail.setRol(Rol.ESTUDIANTE);
        
        assertThrows(ResponseStatusException.class, () -> userService.registrar(sinEmail));

        // Caso 2: Contraseña faltante
        UserCrear sinPassword = new UserCrear();
        sinPassword.setName("Usuario");
        sinPassword.setEmail("usuario@example.com");
        sinPassword.setPhone("12345678");
        sinPassword.setRol(Rol.ESTUDIANTE);
        
        assertThrows(ResponseStatusException.class, () -> userService.registrar(sinPassword));
    }
}