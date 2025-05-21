package com.eduplatform.apiUsuario.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
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

    public User registrar(UserCrear userCrear){
        try {
            User newUser = new User();
            newUser.setName(userCrear.getName());
            newUser.setEmail(userCrear.getEmail()); 
            newUser.setPhone(userCrear.getPhone());
            newUser.setPassword(userCrear.getPassword());
            newUser.setActive(true);
            newUser.setDateCreated(new Date());
            return userRepository.save(newUser);
            
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar el usuario");
        }

    }

    

}
