package com.eduplatform.apiInscripcion.services;

import com.eduplatform.apiInscripcion.models.Curso;
import com.eduplatform.apiInscripcion.models.Usuario;
import com.eduplatform.apiInscripcion.models.entities.Boleta;
import com.eduplatform.apiInscripcion.models.entities.Inscripcion;
import com.eduplatform.apiInscripcion.models.request.CompraRequest;
import com.eduplatform.apiInscripcion.models.responses.CompraResponse;
import com.eduplatform.apiInscripcion.repositories.BoletaRepository;
import com.eduplatform.apiInscripcion.repositories.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




class InscripcionServiceTest {

    @InjectMocks
    private InscripcionService inscripcionService;

    @Mock
    private InscripcionRepository inscripcionRepo;
    
    @Mock
    private WebClient webClient;
    
    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Usuario mockUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setName("Juan");
        usuario.setEmail("juan@email.com");
        return usuario;
    }

    private Curso mockCurso() {
        Curso curso = new Curso();
        curso.setId(2);
        curso.setNombreCurso("Java Básico");
        curso.setPrecio(100);
        curso.setEstado(true);
        return curso;
    }

    private CompraRequest mockCompraRequest() {
        CompraRequest req = new CompraRequest();
        req.setNombreTarjeta("Juan");
        req.setNumeroTarjeta("12345678");
        req.setCodigoTarjeta("123");
        return req;
    }

    @Test
    void testObtenerTodos() {
        List<Inscripcion> lista = Arrays.asList(new Inscripcion(), new Inscripcion());
        when(inscripcionRepo.findAll()).thenReturn(lista);

        List<Inscripcion> result = inscripcionService.obtenerTodos();
        assertEquals(2, result.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testInscribirUsuario_Success() {
        Usuario usuario = mockUsuario();
        Curso curso = mockCurso();
        CompraRequest compraRequest = mockCompraRequest();

        // Mock WebClient para Usuario
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://localhost:8082/user/{id}", usuario.getId())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Usuario.class)).thenReturn(Mono.just(usuario));

        // Mock WebClient para Curso
        when(requestHeadersUriSpec.uri("http://localhost:8081/cursos/{id}", curso.getId())).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(Curso.class)).thenReturn(Mono.just(curso));
        when(inscripcionRepo.existsByIdEstudianteAndIdCurso(usuario.getId(), curso.getId())).thenReturn(false);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(10);
        when(inscripcionRepo.save(any(Inscripcion.class))).thenReturn(inscripcion);

        when(boletaRepository.existsByNumeroBoleta(anyInt())).thenReturn(false);

        // Mock Boleta
        Boleta boleta = new Boleta();
        boleta.setNumeroBoleta(123456);
        boleta.setPrecio(curso.getPrecio());
        boleta.setFechaCompra(LocalDate.now().toString());
        boleta.setInscripcion(inscripcion);
        when(boletaRepository.save(any(Boleta.class))).thenReturn(boleta);

        CompraResponse response = inscripcionService.inscribirUsuario(usuario.getId(), curso.getId(), compraRequest);

        // Verificar que se llamaron los métodos correctos
        assertNotNull(response);
        assertEquals("Juan", response.getNombreUsuario());
        assertEquals("Java Básico", response.getNombreCurso());
        assertEquals(100, response.getPrecio());
        assertEquals("juan@email.com", response.getEmail());
        assertTrue(response.getMensaje().contains("exitosa"));
    }

    
}