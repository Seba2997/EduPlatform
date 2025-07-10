package com.eduplatform.apiSoporte.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.eduplatform.apiSoporte.models.EstadoTicket;
import com.eduplatform.apiSoporte.models.entities.Ticket;
import com.eduplatform.apiSoporte.models.request.TicketCrear;
import com.eduplatform.apiSoporte.repositories.TicketRepository;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void crearTicket_DeberiaCrearTicketCorrectamente() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            TicketCrear ticketCrear = new TicketCrear();
            ticketCrear.setAsunto("Test Subject");
            ticketCrear.setDescripcion("Test Description");

            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");

            Ticket savedTicket = new Ticket();
            savedTicket.setAsunto("Test Subject");
            savedTicket.setDescripcion("Test Description");
            savedTicket.setEmailUsuario("test@example.com");

            when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

            Ticket result = ticketService.crearTicket(ticketCrear);

            assertNotNull(result);
            assertEquals("Test Subject", result.getAsunto());
            assertEquals("Test Description", result.getDescripcion());
            assertEquals("test@example.com", result.getEmailUsuario());
            verify(ticketRepository).save(any(Ticket.class));
        }
    }

    @Test
    void obtenerTodos_DeberiaRetornarTodosLosTickets() {
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.obtenerTodos();

        assertEquals(2, result.size());
        verify(ticketRepository).findAll();
    }

    @Test
    void obtenerPorId_ConIdExistente_DeberiaRetornarTicket() {
        int id = 1;
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));

        Ticket result = ticketService.obtenerPorId(id);

        assertNotNull(result);
        assertEquals(ticket, result);
        verify(ticketRepository).findById(id);
    }

    @Test
    void obtenerPorId_ConIdInexistente_DeberiaLanzarExcepcion() {
        int id = 1;
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> ticketService.obtenerPorId(id));

        assertEquals("Ticket no encontrado con ID: " + id, exception.getMessage());
        verify(ticketRepository).findById(id);
    }

    @Test
    void cambiarEstado_DeberiaActualizarEstadoCorrectamente() {
        int id = 1;
        EstadoTicket nuevoEstado = EstadoTicket.EN_PROCESO;
        Ticket ticket = new Ticket();

        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.cambiarEstado(id, nuevoEstado);

        assertNotNull(result);
        assertEquals(nuevoEstado, result.getEstado());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void obtenerPorEstado_DeberiaRetornarTicketsPorEstado() {
        EstadoTicket estado = EstadoTicket.ABIERTO;
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findByEstado(estado)).thenReturn(tickets);

        List<Ticket> result = ticketService.obtenerPorEstado(estado);

        assertEquals(2, result.size());
        verify(ticketRepository).findByEstado(estado);
    }

    @Test
    void obtenerPorEmail_DeberiaFiltrarTicketsPorEmail() {
        String email = "test@example.com";

        Ticket ticket1 = new Ticket();
        ticket1.setEmailUsuario("test@example.com");

        Ticket ticket2 = new Ticket();
        ticket2.setEmailUsuario("other@example.com");

        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket1, ticket2));

        List<Ticket> result = ticketService.obtenerPorEmail(email);

        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmailUsuario());
        verify(ticketRepository).findAll();
    }

    @Test
    void responderTicket_ConCerrarTrue_DeberiaResponderYCerrar() {
        int id = 1;
        String respuesta = "Test response";
        String emailSoporte = "support@example.com";
        boolean cerrar = true;

        Ticket ticket = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.responderTicket(id, respuesta, emailSoporte, cerrar);

        assertNotNull(result);
        assertEquals(respuesta, result.getRespuesta());
        assertEquals(emailSoporte, result.getRespondidoPor());
        assertEquals(EstadoTicket.CERRADO, result.getEstado());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void responderTicket_ConCerrarFalse_DeberiaResponderYMantenerEnProceso() {
        int id = 1;
        String respuesta = "Test response";
        String emailSoporte = "support@example.com";
        boolean cerrar = false;

        Ticket ticket = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.responderTicket(id, respuesta, emailSoporte, cerrar);

        assertNotNull(result);
        assertEquals(respuesta, result.getRespuesta());
        assertEquals(emailSoporte, result.getRespondidoPor());
        assertEquals(EstadoTicket.EN_PROCESO, result.getEstado());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void responderTicket_ConIdInexistente_DeberiaLanzarExcepcion() {
        int id = 1;
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> ticketService.responderTicket(id, "response", "support@example.com", false));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Ticket no encontrado", exception.getReason());
    }
}
