package com.eduplatform.apiSoporte.controllers;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduplatform.apiSoporte.Services.TicketService;
import com.eduplatform.apiSoporte.models.EstadoTicket;
import com.eduplatform.apiSoporte.models.entities.Ticket;
import com.eduplatform.apiSoporte.models.request.EstadoRequest;
import com.eduplatform.apiSoporte.models.request.TicketCrear;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'PROFESOR', 'ADMIN', 'COORDINADOR')")
    @PostMapping("/crearTicket")
    @Operation(summary = "Crear un nuevo ticket", description = "Permite a un usuario crear un nuevo ticket de soporte.")
    public ResponseEntity<Ticket> crearTicket(@Valid @RequestBody TicketCrear ticketCrear) {
        Ticket ticket = ticketService.crearTicket(ticketCrear);
        return ResponseEntity.ok(ticket);
    }

    @PreAuthorize("hasRole('ADMIN', 'SOPORTE')")
    @GetMapping
    @Operation(summary = "Obtener todos los tickets", description = "Devuelve una lista de todos los tickets de soporte.")
    public ResponseEntity<List<Ticket>> obtenerTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket por ID", description = "Devuelve los detalles de un ticket específico basado en su ID.")
    public ResponseEntity<Ticket> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.obtenerPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener tickets por estado (ABIERTO, EN_PROCESO, CERRADO)", 
               description = "Devuelve una lista de tickets filtrados por su estado.")
    public ResponseEntity<List<Ticket>> obtenerPorEstado(@PathVariable EstadoTicket estado) {
        return ResponseEntity.ok(ticketService.obtenerPorEstado(estado));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SOPORTE')")
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener tickets por email del usuario",   
               description = "Devuelve una lista de tickets asociados al email del usuario.")
    public ResponseEntity<List<Ticket>> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(ticketService.obtenerPorEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN','SOPORTE')")
    @PutMapping("/{idTicket}/estado")
    @Operation(summary = "Cambiar estado de un ticket", 
               description = "Permite a un usuario con rol ADMIN o SOPORTE cambiar el estado de un ticket.")
    public ResponseEntity<Ticket> cambiarEstado(@PathVariable int idTicket, @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(ticketService.cambiarEstado(idTicket, request.getNuevoEstado()));
    }

    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'PROFESOR', 'ADMIN', 'COORDINADOR')")
    @GetMapping("/mis-tickets")
    @Operation(summary = "Obtener mis tickets", description = "Devuelve los tickets del usuario autenticado.")
    public ResponseEntity<List<Ticket>> obtenerMisTickets() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Ticket> tickets = ticketService.obtenerPorEmail(email);
        return ResponseEntity.ok(tickets);
    }
}
