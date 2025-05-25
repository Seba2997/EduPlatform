package com.eduPlatform.apiCurso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduPlatform.apiCurso.models.entities.Profesor;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {  
    
}
