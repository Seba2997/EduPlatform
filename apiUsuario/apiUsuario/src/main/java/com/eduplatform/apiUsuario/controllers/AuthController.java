package com.eduplatform.apiUsuario.controllers;

import com.eduplatform.apiUsuario.models.entities.User;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiUsuario.models.request.LoginDTO;
import com.eduplatform.apiUsuario.models.response.JwtResponseDTO;
import com.eduplatform.apiUsuario.repositories.UserRepository;
import com.eduplatform.apiUsuario.security.JwtUtil;
import com.eduplatform.apiUsuario.services.UserService;


@RestController
@RequestMapping("/auth")
public class AuthController {
 
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null || !user.getActive()) {
            return ResponseEntity.status(401).body("Usuario no encontrado o inactivo");
        }

        if (!userService.validarPassword(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getNombre().name())
                .toList();

        String token = jwtUtil.generarToker(user.getEmail(), roles);

        return ResponseEntity.ok(new JwtResponseDTO(token, user.getEmail(), roles));
    }


}
