package com.eduplatform.apiUsuario;

import java.util.Date;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.eduplatform.apiUsuario.models.RolNombre;
import com.eduplatform.apiUsuario.models.entities.Rol;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.repositories.RolRepository;
import com.eduplatform.apiUsuario.repositories.UserRepository;
import com.eduplatform.apiUsuario.services.UserService;

@SpringBootApplication
public class ApiUsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiUsuarioApplication.class, args);
	}

    //Usar para crear roles y usuario admin al iniciar la aplicación solo en desarrollo
	
    @Bean
	CommandLineRunner initRolesYAdmin(RolRepository rolRepo, UserRepository userRepo, UserService userService) {
    	return args -> {
        	// Crear roles si no existen
        for (RolNombre r : RolNombre.values()) {
            rolRepo.findByNombre(r).orElseGet(() -> {
    		Rol nuevo = new Rol(r);
    		return rolRepo.save(nuevo);
				});
        }

        // Crear admin si no existe
        if (userRepo.findByEmail("admin@eduplatform.cl") == null) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@eduplatform.cl");
            admin.setPhone("999999999");
            admin.setPassword(userService.generateHash("admin123")); 
            admin.setActive(true);
            admin.setDateCreated(new Date());

            Rol rolAdmin = rolRepo.findByNombre(RolNombre.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            admin.setRoles(Set.of(rolAdmin));
            userRepo.save(admin);
        }
    };
}

}
