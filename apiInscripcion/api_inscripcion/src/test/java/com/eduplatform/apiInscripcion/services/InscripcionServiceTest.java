package com.eduplatform.apiInscripcion.services;

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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import com.eduplatform.apiInscripcion.models.Curso;
import com.eduplatform.apiInscripcion.models.Usuario;
import com.eduplatform.apiInscripcion.models.entities.Boleta;
import com.eduplatform.apiInscripcion.models.entities.Inscripcion;
import com.eduplatform.apiInscripcion.models.request.CompraRequest;
import com.eduplatform.apiInscripcion.models.responses.CompraResponse;
import com.eduplatform.apiInscripcion.repositories.BoletaRepository;
import com.eduplatform.apiInscripcion.repositories.InscripcionRepository;
import reactor.core.publisher.Mono;






@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepo;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private BoletaRepository boletaRepository;

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
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Usuario usuario;
    private Curso curso;
    private CompraRequest compraRequest;
    private Inscripcion inscripcion;
    private Boleta boleta;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setName("Test User");
        usuario.setEmail("test@example.com");

        curso = new Curso();
        curso.setId(1);
        curso.setNombreCurso("Test Course");
        curso.setPrecio(100);
        curso.setEstado(true);

        compraRequest = new CompraRequest();
        compraRequest.setNombreTarjeta("Test User");
        compraRequest.setNumeroTarjeta("12345678");
        compraRequest.setCodigoTarjeta("123");

        inscripcion = new Inscripcion();
        inscripcion.setId(1);
        inscripcion.setIdEstudiante(1);
        inscripcion.setNombreEstudiante("Test User");
        inscripcion.setEmailEstudiante("test@example.com");

        boleta = new Boleta();
        boleta.setNumeroBoleta(123456);
        boleta.setPrecio(100);
    }

    @SuppressWarnings("unchecked")
    @Test
    void inscribirUsuarioAutenticado_Success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            // Setup mocks
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("token");
            
            when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
            when(webClientBuilder.build()).thenReturn(webClient);
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(Usuario.class)).thenReturn(Mono.just(usuario));
            when(responseSpec.bodyToMono(Curso.class)).thenReturn(Mono.just(curso));

            when(inscripcionRepo.existsByIdEstudianteAndIdCurso(1, 1)).thenReturn(false);
            when(inscripcionRepo.save(any(Inscripcion.class))).thenReturn(inscripcion);
            when(boletaRepository.existsByNumeroBoleta(anyInt())).thenReturn(false);
            when(boletaRepository.save(any(Boleta.class))).thenReturn(boleta);

            CompraResponse result = inscripcionService.inscribirUsuarioAutenticado(1, compraRequest);

            assertNotNull(result);
            assertEquals("Test User", result.getNombreUsuario());
            assertEquals("Test Course", result.getNombreCurso());
            verify(inscripcionRepo).save(any(Inscripcion.class));
            verify(boletaRepository).save(any(Boleta.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void inscribirUsuarioAutenticado_UsuarioYaInscrito() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("token");
            
            when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
            when(webClientBuilder.build()).thenReturn(webClient);
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(Usuario.class)).thenReturn(Mono.just(usuario));
            when(responseSpec.bodyToMono(Curso.class)).thenReturn(Mono.just(curso));

            when(inscripcionRepo.existsByIdEstudianteAndIdCurso(1, 1)).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> inscripcionService.inscribirUsuarioAutenticado(1, compraRequest));
            
            assertEquals("Usuario ya inscrito", exception.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void inscribirUsuarioAutenticado_CursoInactivo() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            curso.setEstado(false);
            
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("token");
            
            when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
            when(webClientBuilder.build()).thenReturn(webClient);
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(Usuario.class)).thenReturn(Mono.just(usuario));
            when(responseSpec.bodyToMono(Curso.class)).thenReturn(Mono.just(curso));

            when(inscripcionRepo.existsByIdEstudianteAndIdCurso(1, 1)).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> inscripcionService.inscribirUsuarioAutenticado(1, compraRequest));
            
            assertEquals("El curso no está activo y no permite inscripciones.", exception.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void inscribirUsuarioAutenticado_NombreTarjetaIncorrecto() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            compraRequest.setNombreTarjeta("Wrong Name");
            
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("token");
            
            when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
            when(webClientBuilder.build()).thenReturn(webClient);
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(Usuario.class)).thenReturn(Mono.just(usuario));
            when(responseSpec.bodyToMono(Curso.class)).thenReturn(Mono.just(curso));

            when(inscripcionRepo.existsByIdEstudianteAndIdCurso(1, 1)).thenReturn(false);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                () -> inscripcionService.inscribirUsuarioAutenticado(1, compraRequest));
            
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void inscribirUsuarioAutenticado_NumeroTarjetaIncorrecto() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            compraRequest.setNumeroTarjeta("123");
            
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("token");
            
            when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
            when(webClientBuilder.build()).thenReturn(webClient);
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(Usuario.class)).thenReturn(Mono.just(usuario));
            when(responseSpec.bodyToMono(Curso.class)).thenReturn(Mono.just(curso));

            when(inscripcionRepo.existsByIdEstudianteAndIdCurso(1, 1)).thenReturn(false);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
                () -> inscripcionService.inscribirUsuarioAutenticado(1, compraRequest));
            
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        }
    }

    @Test
    void obtenerTodos_Success() {
        List<Inscripcion> inscripciones = Arrays.asList(inscripcion);
        when(inscripcionRepo.findAll()).thenReturn(inscripciones);

        List<Inscripcion> result = inscripcionService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals(inscripcion, result.get(0));
    }

    @Test
    void obtenerInscripcionId_Success() {
        when(inscripcionRepo.findById(1)).thenReturn(Optional.of(inscripcion));

        Inscripcion result = inscripcionService.obtenerInscripcionId(1);

        assertEquals(inscripcion, result);
    }

    @Test
    void obtenerInscripcionId_NotFound() {
        when(inscripcionRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> inscripcionService.obtenerInscripcionId(1));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void obtenerPorEmail_Success() {
        List<Inscripcion> inscripciones = Arrays.asList(inscripcion);
        when(inscripcionRepo.findByEmailEstudiante("test@example.com")).thenReturn(inscripciones);

        List<Inscripcion> result = inscripcionService.obtenerPorEmail("test@example.com");

        assertEquals(1, result.size());
        assertEquals(inscripcion, result.get(0));
    }
}