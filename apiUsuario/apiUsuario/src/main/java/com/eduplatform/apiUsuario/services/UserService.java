package com.eduplatform.apiUsuario.services;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduplatform.apiUsuario.models.RolNombre;
import com.eduplatform.apiUsuario.models.entities.Rol;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.models.request.UserUpdate;
import com.eduplatform.apiUsuario.models.response.UserDTO;
import com.eduplatform.apiUsuario.repositories.RolRepository;
import com.eduplatform.apiUsuario.repositories.UserRepository;

@Service
public class UserService {
   
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;
    
    public List<UserDTO> obtenerTodosDTO() {
        return userRepository.findAll().stream()
            .map(UserDTO::fromEntity)
            .toList();
    }

    public UserDTO obtenerUnoDTO(int id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return UserDTO.fromEntity(user);
    }

    public User obtenerPorEmail(String email){
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public List<UserDTO> obtenerPorRol(RolNombre rolNombre) {
        return userRepository.findAll().stream()
            .filter(u -> u.getRoles().stream()
            .anyMatch(r -> r.getNombre().equals(rolNombre)))
            .map(UserDTO::fromEntity)
            .toList();
}

    public List<User> obtenerActivos(){
        return userRepository.findByActive(true);
    }

    public User registrarComo(UserCrear user, RolNombre rolNombre) {
    try {
        Rol rol = rolRepository.findByNombre(rolNombre)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setPassword(generateHash(user.getPassword()));
        newUser.setActive(true);
        newUser.setDateCreated(new Date());
        newUser.setRoles(Set.of(rol));

        return userRepository.save(newUser);
    } catch (ResponseStatusException e) {
        throw e;
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar usuario: " + e.getMessage());
    }
}



    public String generateHash(String password){
        PasswordEncoder hasheador = new BCryptPasswordEncoder();
        return hasheador.encode(password);
    }

    public boolean validarPassword(String password, String hash) {
        BCryptPasswordEncoder passsPasswordEncoder = new BCryptPasswordEncoder();
        return passsPasswordEncoder.matches(password, hash);
    }

    

    public User modificar(UserUpdate modificado){
        User user = userRepository.findById(modificado.getId()).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        try {
            if (modificado.getName() != null) {
                user.setName(modificado.getName());
            }
            if (modificado.getPhone() != null) {
                user.setPhone(modificado.getPhone());
                }
            if (modificado.getPassword() != null) {
                user.setPassword(generateHash(modificado.getPassword()));
            }
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al modificar el usuario");
        }
    }

    public User cambiarEstado(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        if (user.getActive() == true) {
            user.setActive(false);
        } else {
            user.setActive(true);
        }
        return userRepository.save(user);
    }

}
