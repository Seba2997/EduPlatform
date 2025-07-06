package com.eduplatform.apiSoporte.controllers;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/crearTicket")
    @Operation(summary = "Crear un nuevo ticket", description = "Permite a un usuario crear un nuevo ticket de soporte.")
    public ResponseEntity<Ticket> crearTicket(@Valid @RequestBody TicketCrear ticketCrear) {
        Ticket ticket = ticketService.crearTicket(ticketCrear);
        return ResponseEntity.ok(ticket);
    }

    

     @GetMapping
    @Operation(summary = "Obtener todos los tickets")
    public ResponseEntity<List<Ticket>> obtenerTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket por ID")
    public ResponseEntity<Ticket> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(ticketService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener tickets por estado (ABIERTO, EN_PROCESO, CERRADO)")
    public ResponseEntity<List<Ticket>> obtenerPorEstado(@PathVariable EstadoTicket estado) {
        return ResponseEntity.ok(ticketService.obtenerPorEstado(estado));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener tickets por email del usuario")
    public ResponseEntity<List<Ticket>> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(ticketService.obtenerPorEmail(email));
    }

    @PutMapping("/{idTicket}/estado")
    @Operation(summary = "Cambiar estado de un ticket")
    public ResponseEntity<Ticket> cambiarEstado(@PathVariable int idTicket, @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(ticketService.cambiarEstado(idTicket, request.getNuevoEstado()));
    }

    
}





