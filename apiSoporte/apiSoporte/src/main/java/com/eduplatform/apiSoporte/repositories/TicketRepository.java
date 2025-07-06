package com.eduplatform.apiSoporte.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduplatform.apiSoporte.models.EstadoTicket;
import com.eduplatform.apiSoporte.models.entities.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByEstado(EstadoTicket estado);
    List<Ticket> findByEmailUsuario(String email);
    
}
