package com.eduplatform.apiUsuario.services;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.models.request.UserUpdate;
import com.eduplatform.apiUsuario.repositories.UserRepository;

@Service
public class UserService {
   
    @Autowired
    private UserRepository userRepository;
    
    public List<User> obtenerTodos(){
        return userRepository.findAll();
    }

    public User obtenerUno(int id){
        return userRepository.findById(id).orElse(null);
    }

    public User obtenerPorEmail(String email){
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public List<User> obtenerActivos(){
        return userRepository.findByActive(true);
    }

    public User registrar(UserCrear user){
        try {
            User newUser = new User();
            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail()); 
            newUser.setPhone(user.getPhone());
            newUser.setPassword(generateHash(user.getPassword()));
            newUser.setActive(true);
            newUser.setDateCreated(new Date());
            newUser.setRol(user.getRol());
            return userRepository.save(newUser);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar el usuario");
        }

    }

    private String generateHash(String password){
        PasswordEncoder hasheador = new BCryptPasswordEncoder();
        return hasheador.encode(password);
    }

    public User modificar(UserUpdate modiicado){
        User user = userRepository.findById(modiicado.getId()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        try {
            user.setName(modiicado.getName());
            user.setPhone(modiicado.getPhone());
            if (modiicado.getPassword() != null) {
                user.setPassword(generateHash(modiicado.getPassword()));
            }
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al modificar el usuario");
        }
    }

    public void cambiarEstado(int id, boolean activo) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        try {
            user.setActive(activo);
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al cambiar el estado del usuario");
        }
    }

}
