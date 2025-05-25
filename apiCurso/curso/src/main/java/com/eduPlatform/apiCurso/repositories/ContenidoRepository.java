package com.eduPlatform.apiCurso.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduPlatform.apiCurso.models.entities.Contenido;


@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Integer>{

}
