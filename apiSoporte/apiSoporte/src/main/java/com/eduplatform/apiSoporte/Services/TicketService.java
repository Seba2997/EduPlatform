package com.eduplatform.apiSoporte.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduplatform.apiSoporte.models.EstadoTicket;
import com.eduplatform.apiSoporte.models.entities.Ticket;
import com.eduplatform.apiSoporte.models.request.TicketCrear;
import com.eduplatform.apiSoporte.repositories.TicketRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;

    public Ticket crearTicket(TicketCrear ticketCrear) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Ticket ticket = new Ticket();
        ticket.setAsunto(ticketCrear.getAsunto());
        ticket.setDescripcion(ticketCrear.getDescripcion());
        ticket.setEmailUsuario(email);
        return ticketRepository.save(ticket);
    }

    
    public List<Ticket> obtenerTodos() {
        return ticketRepository.findAll();
    }


    public Ticket obtenerPorId(int id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            return ticket.get();
        } else {
            throw new EntityNotFoundException("Ticket no encontrado con ID: " + id);
        }
    }

    public Ticket cambiarEstado(int id, EstadoTicket nuevoEstado) {
        Ticket ticket = obtenerPorId(id);
        ticket.setEstado(nuevoEstado);
        return ticketRepository.save(ticket);
    }

    public List<Ticket> obtenerPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    public List<Ticket> obtenerPorEmail(String emailUsuario) {
        return ticketRepository.findAll().stream()
            .filter(t -> t.getEmailUsuario().equalsIgnoreCase(emailUsuario))
            .toList();
    }

    public Ticket responderTicket(int id, String respuesta, String emailSoporte, boolean cerrar) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket no encontrado"));

        ticket.setRespuesta(respuesta);
        ticket.setRespondidoPor(emailSoporte);
        ticket.setEstado(cerrar ? EstadoTicket.CERRADO : EstadoTicket.EN_PROCESO);

        return ticketRepository.save(ticket);
    }

}
