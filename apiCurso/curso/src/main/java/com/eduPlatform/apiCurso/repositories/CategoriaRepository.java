package com.eduPlatform.apiCurso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduPlatform.apiCurso.models.entities.Categoria;
import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Integer>{
    
    List<Categoria> findByNombreCategoria(String nombreCategoria);

    Categoria findByNombreCategoriaIgnoreCase(String nombreCategoria);
}
