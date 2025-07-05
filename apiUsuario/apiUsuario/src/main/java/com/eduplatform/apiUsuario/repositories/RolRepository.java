package com.eduplatform.apiUsuario.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduplatform.apiUsuario.models.RolNombre;
import com.eduplatform.apiUsuario.models.entities.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByNombre(RolNombre nombre);
    
}
