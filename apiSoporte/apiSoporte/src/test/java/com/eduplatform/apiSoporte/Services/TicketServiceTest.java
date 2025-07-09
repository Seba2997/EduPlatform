package com.eduplatform.apiSoporte.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import com.eduplatform.apiSoporte.models.EstadoTicket;
import com.eduplatform.apiSoporte.models.entities.Ticket;
import com.eduplatform.apiSoporte.models.request.TicketCrear;
import com.eduplatform.apiSoporte.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;






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

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void crearTicket_DeberiaCrearTicketCorrectamente() {
        // Arrange
        TicketCrear ticketCrear = new TicketCrear();
        ticketCrear.setAsunto("Test Subject");
        ticketCrear.setDescripcion("Test Description");
        
        when(authentication.getName()).thenReturn("test@example.com");
        
        Ticket savedTicket = new Ticket();
        savedTicket.setAsunto("Test Subject");
        savedTicket.setDescripcion("Test Description");
        savedTicket.setEmailUsuario("test@example.com");
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // Act
        Ticket result = ticketService.crearTicket(ticketCrear);

        // Assert
        assertNotNull(result);
        assertEquals("Test Subject", result.getAsunto());
        assertEquals("Test Description", result.getDescripcion());
        assertEquals("test@example.com", result.getEmailUsuario());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void obtenerTodos_DeberiaRetornarTodosLosTickets() {
        // Arrange
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findAll()).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.obtenerTodos();

        // Assert
        assertEquals(2, result.size());
        verify(ticketRepository).findAll();
    }

    @Test
    void obtenerPorId_ConIdExistente_DeberiaRetornarTicket() {
        // Arrange
        int id = 1;
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));

        // Act
        Ticket result = ticketService.obtenerPorId(id);

        // Assert
        assertNotNull(result);
        assertEquals(ticket, result);
        verify(ticketRepository).findById(id);
    }

    @Test
    void obtenerPorId_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        int id = 1;
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> ticketService.obtenerPorId(id));
        assertEquals("Ticket no encontrado con ID: " + id, exception.getMessage());
        verify(ticketRepository).findById(id);
    }

    @Test
    void cambiarEstado_DeberiaActualizarEstadoCorrectamente() {
        // Arrange
        int id = 1;
        EstadoTicket nuevoEstado = EstadoTicket.EN_PROCESO;
        Ticket ticket = new Ticket();
        
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        // Act
        Ticket result = ticketService.cambiarEstado(id, nuevoEstado);

        // Assert
        assertNotNull(result);
        assertEquals(nuevoEstado, result.getEstado());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void obtenerPorEstado_DeberiaRetornarTicketsPorEstado() {
        // Arrange
        EstadoTicket estado = EstadoTicket.ABIERTO;
        List<Ticket> tickets = Arrays.asList(new Ticket(), new Ticket());
        when(ticketRepository.findByEstado(estado)).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.obtenerPorEstado(estado);

        // Assert
        assertEquals(2, result.size());
        verify(ticketRepository).findByEstado(estado);
    }

    @Test
    void obtenerPorEmail_DeberiaFiltrarTicketsPorEmail() {
        // Arrange
        String email = "test@example.com";
        Ticket ticket1 = new Ticket();
        ticket1.setEmailUsuario("test@example.com");
        Ticket ticket2 = new Ticket();
        ticket2.setEmailUsuario("other@example.com");
        
        List<Ticket> allTickets = Arrays.asList(ticket1, ticket2);
        when(ticketRepository.findAll()).thenReturn(allTickets);

        // Act
        List<Ticket> result = ticketService.obtenerPorEmail(email);

        // Assert
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmailUsuario());
        verify(ticketRepository).findAll();
    }

    @Test
    void responderTicket_ConCerrarTrue_DeberiaResponderYCerrar() {
        // Arrange
        int id = 1;
        String respuesta = "Test response";
        String emailSoporte = "support@example.com";
        boolean cerrar = true;
        
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        // Act
        Ticket result = ticketService.responderTicket(id, respuesta, emailSoporte, cerrar);

        // Assert
        assertNotNull(result);
        assertEquals(respuesta, result.getRespuesta());
        assertEquals(emailSoporte, result.getRespondidoPor());
        assertEquals(EstadoTicket.CERRADO, result.getEstado());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void responderTicket_ConCerrarFalse_DeberiaResponderYMantenerEnProceso() {
        // Arrange
        int id = 1;
        String respuesta = "Test response";
        String emailSoporte = "support@example.com";
        boolean cerrar = false;
        
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        // Act
        Ticket result = ticketService.responderTicket(id, respuesta, emailSoporte, cerrar);

        // Assert
        assertNotNull(result);
        assertEquals(respuesta, result.getRespuesta());
        assertEquals(emailSoporte, result.getRespondidoPor());
        assertEquals(EstadoTicket.EN_PROCESO, result.getEstado());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void responderTicket_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        int id = 1;
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> ticketService.responderTicket(id, "response", "support@example.com", false));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Ticket no encontrado", exception.getReason());
    }
}