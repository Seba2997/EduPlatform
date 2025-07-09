package com.eduplatform.apiReporte.services;

import com.eduplatform.apiReporte.models.entities.Reporte;
import com.eduplatform.apiReporte.models.external.Boleta;
import com.eduplatform.apiReporte.models.external.Inscripcion;
import com.eduplatform.apiReporte.models.request.ReporteCrear;
import com.eduplatform.apiReporte.repositories.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepo;

    @Mock
    private WebClient.Builder webClientBuilder;

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
    private ReporteService reporteService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void crearReporteDesdeApi_ConContenidoValido_DeberiaCrearReporte() {
        // Arrange
        ReporteCrear dto = new ReporteCrear();
        dto.setTitulo("Test Reporte");
        dto.setContenido("Contenido de prueba");
        dto.setExportarPdf(false);

        Reporte reporteGuardado = new Reporte();
        reporteGuardado.setId(1);
        reporteGuardado.setTitulo("Test Reporte");

        when(reporteRepo.save(any(Reporte.class))).thenReturn(reporteGuardado);

        // Act
        Reporte resultado = reporteService.crearReporteDesdeApi(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Test Reporte", resultado.getTitulo());
        verify(reporteRepo).save(any(Reporte.class));
    }

    @Test
    void crearReporteDesdeApi_ConContenidoVacio_DeberiaLanzarExcepcion() {
        // Arrange
        ReporteCrear dto = new ReporteCrear();
        dto.setTitulo("Test Reporte");
        dto.setContenido("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reporteService.crearReporteDesdeApi(dto)
        );

        assertEquals("El contenido del reporte no puede estar vacío", exception.getMessage());
        verify(reporteRepo, never()).save(any(Reporte.class));
    }

    @Test
    void crearReporteDesdeApi_ConContenidoNull_DeberiaLanzarExcepcion() {
        // Arrange
        ReporteCrear dto = new ReporteCrear();
        dto.setTitulo("Test Reporte");
        dto.setContenido(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reporteService.crearReporteDesdeApi(dto)
        );

        assertEquals("El contenido del reporte no puede estar vacío", exception.getMessage());
    }

    @Test
    void generarReporteInscripciones_ConDatosValidos_DeberiaGenerarReporte() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-token");

            List<Inscripcion> inscripciones = Arrays.asList(
                createInscripcion(1L, "Juan Perez", "juan@test.com", "Java Course", "2023-01-01")
            );

            List<Boleta> boletas = Arrays.asList(
                createBoleta(1L, 1L, "BOL001", 1000, "2023-01-02")
            );

            when(responseSpec.bodyToFlux(Inscripcion.class))
                .thenReturn(Flux.fromIterable(inscripciones));
            when(responseSpec.bodyToFlux(Boleta.class))
                .thenReturn(Flux.fromIterable(boletas));

            Reporte reporteGuardado = new Reporte();
            reporteGuardado.setId(1);
            when(reporteRepo.save(any(Reporte.class))).thenReturn(reporteGuardado);

            // Act
            Reporte resultado = reporteService.generarReporteInscripciones();

            // Assert
            assertNotNull(resultado);
            verify(reporteRepo).save(any(Reporte.class));
            verify(webClient, times(2)).get();
        }
    }

    @Test
    void generarReporteInscripciones_SinInscripciones_DeberiaLanzarExcepcion() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-token");

            when(responseSpec.bodyToFlux(Inscripcion.class))
                .thenReturn(Flux.fromIterable(Collections.emptyList()));

            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reporteService.generarReporteInscripciones()
            );

            assertEquals("No se encontraron inscripciones para el reporte.", exception.getMessage());
        }
    }

    @Test
    void generarReporteInscripciones_ConBoletasNull_DeberiaGenerarReporteSinBoletas() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("test-token");

            List<Inscripcion> inscripciones = Arrays.asList(
                createInscripcion(1L, "Juan Perez", "juan@test.com", "Java Course", "2023-01-01")
            );

            when(responseSpec.bodyToFlux(Inscripcion.class))
                .thenReturn(Flux.fromIterable(inscripciones));
            when(responseSpec.bodyToFlux(Boleta.class))
                .thenReturn(Flux.fromIterable(Collections.emptyList()));

            Reporte reporteGuardado = new Reporte();
            reporteGuardado.setId(1);
            when(reporteRepo.save(any(Reporte.class))).thenReturn(reporteGuardado);

            // Act
            Reporte resultado = reporteService.generarReporteInscripciones();

            // Assert
            assertNotNull(resultado);
            verify(reporteRepo).save(any(Reporte.class));
        }
    }

    private Inscripcion createInscripcion(Long id, String nombre, String email, String curso, String fecha) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setNombreEstudiante(nombre);
        inscripcion.setEmailEstudiante(email);
        inscripcion.setNombreCurso(curso);
        inscripcion.setFechaInscripcion(LocalDate.parse(fecha));
        return inscripcion;

    }

    private Boleta createBoleta(Long id, Long inscripcionId, String numero, int precio, String fecha) {
        Boleta boleta = new Boleta();
        boleta.setId(id.intValue());
        boleta.setInscripcionId(inscripcionId.intValue());
        boleta.setNumeroBoleta(Integer.parseInt(numero));
        boleta.setPrecio(precio);
        boleta.setFechaCompra(fecha);
        return boleta;
    }
}