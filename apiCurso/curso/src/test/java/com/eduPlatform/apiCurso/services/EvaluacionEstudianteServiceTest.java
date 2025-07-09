package com.eduPlatform.apiCurso.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEstudianteCrear;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.models.user.UsuarioDTO;
import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRepository;
import com.eduPlatform.apiCurso.repositories.EvaluacionRepository;
import reactor.core.publisher.Mono;




@ExtendWith(MockitoExtension.class)
class EvaluacionEstudianteServiceTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private EvaluacionEstudianteRepository evaluacionEstudianteRepo;

    @Mock
    private EvaluacionRepository evaluacionRepo;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private EvaluacionEstudianteService service;

    private UsuarioDTO usuarioEstudiante;
    private EvaluacionEstudianteCrear crear;
    private Evaluacion evaluacion;
    private EvaluacionEstudiante evaluacionEstudiante;

    @BeforeEach
    void setUp() {
        usuarioEstudiante = new UsuarioDTO();
        usuarioEstudiante.setName("Juan Perez");
        usuarioEstudiante.setEmail("juan@test.com");
        usuarioEstudiante.setRoles(List.of("ESTUDIANTE"));

        crear = new EvaluacionEstudianteCrear();
        crear.setEvaluacionId(1);
        crear.setRespuesta("Mi respuesta");

        evaluacion = new Evaluacion();
        evaluacion.setTitulo("Evaluación Test");

        evaluacionEstudiante = new EvaluacionEstudiante();
        evaluacionEstudiante.setEvaluacionEstudianteid(1);
        evaluacionEstudiante.setNombreEstudiante("Juan Perez");
        evaluacionEstudiante.setEmailEstudiante("juan@test.com");
        evaluacionEstudiante.setRespuesta("Mi respuesta");
        evaluacionEstudiante.setEvaluacion(evaluacion);
    }

    @SuppressWarnings("unchecked")
    @Test
    void responder_Success() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            // Arrange
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-jwt");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(UsuarioDTO.class)).thenReturn(Mono.just(usuarioEstudiante));

            when(evaluacionRepo.findById(1)).thenReturn(Optional.of(evaluacion));
            when(evaluacionEstudianteRepo.save(any(EvaluacionEstudiante.class))).thenReturn(evaluacionEstudiante);

            // Act
            EvaluacionEstudiante result = service.responder(crear);

            // Assert
            assertNotNull(result);
            assertEquals("Juan Perez", result.getNombreEstudiante());
            assertEquals("juan@test.com", result.getEmailEstudiante());
            assertEquals("Mi respuesta", result.getRespuesta());
            verify(evaluacionEstudianteRepo).save(any(EvaluacionEstudiante.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void responder_UserNotFound_ThrowsForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            // Arrange
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-jwt");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(UsuarioDTO.class)).thenReturn(Mono.just((UsuarioDTO) null));

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                () -> service.responder(crear));
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
            assertEquals("No tienes permiso para responder evaluaciones", exception.getReason());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void responder_UserNotStudent_ThrowsForbidden() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            // Arrange
            UsuarioDTO profesor = new UsuarioDTO();
            profesor.setRoles(List.of("PROFESOR"));

            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-jwt");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(UsuarioDTO.class)).thenReturn(Mono.just(profesor));

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                () -> service.responder(crear));
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void responder_EvaluacionNotFound_ThrowsNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            // Arrange
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-jwt");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(UsuarioDTO.class)).thenReturn(Mono.just(usuarioEstudiante));

            when(evaluacionRepo.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                () -> service.responder(crear));
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Evaluación no encontrada", exception.getReason());
        }
    }

    @Test
    void obtenerCalificacionPorId_Success() {
        // Arrange
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.of(evaluacionEstudiante));

        // Act
        EvaluacionEstudianteRespuesta result = service.obtenerCalificacionPorId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Juan Perez", result.getNombreEstudiante());
        assertEquals("juan@test.com", result.getEmailEstudiante());
        assertEquals("Mi respuesta", result.getRespuesta());
    }

    @Test
    void obtenerCalificacionPorId_NotFound_ThrowsException() {
        // Arrange
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.obtenerCalificacionPorId(1));
        assertEquals("Evaluación no encontrada", exception.getMessage());
    }

    @Test
    void obtenerEvaluacionPorId_Success() {
        // Arrange
        when(evaluacionRepo.findById(1)).thenReturn(Optional.of(evaluacion));

        // Act
        Evaluacion result = service.obtenerEvaluacionPorId(1);

        // Assert
        assertNotNull(result);
        assertEquals(evaluacion, result);
    }

    @Test
    void obtenerEvaluacionPorId_NotFound_ThrowsException() {
        // Arrange
        when(evaluacionRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.obtenerEvaluacionPorId(1));
        assertEquals("Evaluación no encontrada", exception.getMessage());
    }
}