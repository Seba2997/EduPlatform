package com.edutech.curso.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edutech.curso.models.entities.Contenido;


@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Integer>{

}
