package com.eduPlatform.apiCurso.services;

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
import org.springframework.web.server.ResponseStatusException;
import com.eduPlatform.apiCurso.models.entities.Contenido;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ContenidoCrear;
import com.eduPlatform.apiCurso.models.requests.ContenidoEditar;
import com.eduPlatform.apiCurso.repositories.ContenidoRepository;



@ExtendWith(MockitoExtension.class)
class ContenidoServiceTest {

    @Mock
    private ContenidoRepository contenidoRepo;

    @Mock
    private CursoService cursoService;

    @InjectMocks
    private ContenidoService contenidoService;

    private Contenido contenido;
    private Curso curso;
    private ContenidoCrear contenidoCrear;
    private ContenidoEditar contenidoEditar;

    @BeforeEach
    void setUp() {
        curso = new Curso();
        curso.setId(1);

        contenido = new Contenido();
        contenido.setIdContenido(1);
        contenido.setTituloContenido("Test Title");
        contenido.setContenido("Test Content");
        contenido.setCurso(curso);

        contenidoCrear = new ContenidoCrear();
        contenidoCrear.setContenido("New Content");

        contenidoEditar = new ContenidoEditar();
        contenidoEditar.setIdContenido(1);
        contenidoEditar.setTituloContenido("Updated Title");
        contenidoEditar.setContenido("Updated Content");
    }

    @Test
    void testObtenerTodos() {
        List<Contenido> contenidos = Arrays.asList(contenido);
        when(contenidoRepo.findAll()).thenReturn(contenidos);

        List<Contenido> result = contenidoService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals(contenido, result.get(0));
        verify(contenidoRepo).findAll();
    }

    @Test
    void testObtenerContenidoPorId_Success() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));

        Contenido result = contenidoService.obtenerContenidoPorId(1);

        assertEquals(contenido, result);
        verify(contenidoRepo).findById(1);
    }

    @Test
    void testObtenerContenidoPorId_NotFound() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> contenidoService.obtenerContenidoPorId(1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Contenido de este curso no encontrado", exception.getReason());
    }

    @Test
    void testRegistrar_Success() {
        when(cursoService.obtenerCursoPorId(1)).thenReturn(curso);
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenido);

        Contenido result = contenidoService.registrar(contenidoCrear, 1);

        assertNotNull(result);
        verify(cursoService).obtenerCursoPorId(1);
        verify(contenidoRepo).save(any(Contenido.class));
    }

    @Test
    void testRegistrar_Error() {
        when(cursoService.obtenerCursoPorId(1)).thenThrow(new RuntimeException("Error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> contenidoService.registrar(contenidoCrear, 1));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Error al registrar contenido", exception.getReason());
    }

    @Test
    void testModificar_Success() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenido);

        Contenido result = contenidoService.modificar(contenidoEditar);

        assertNotNull(result);
        verify(contenidoRepo).findById(1);
        verify(contenidoRepo).save(contenido);
    }

    @Test
    void testModificar_NotFound() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> contenidoService.modificar(contenidoEditar));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Contenido de este curso no encontrado", exception.getReason());
    }

    @Test
    void testModificar_PartialUpdate() {
        ContenidoEditar parcialEditar = new ContenidoEditar();
        parcialEditar.setIdContenido(1);
        parcialEditar.setTituloContenido("Only Title Updated");

        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenido);

        Contenido result = contenidoService.modificar(parcialEditar);

        assertNotNull(result);
        verify(contenidoRepo).save(contenido);
    }

    @Test
    void testEliminarContenido_Success() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));

        assertDoesNotThrow(() -> contenidoService.eliminarContenido(1));

        verify(contenidoRepo).findById(1);
        verify(contenidoRepo).delete(contenido);
    }

    @Test
    void testEliminarContenido_NotFound() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> contenidoService.eliminarContenido(1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Contenido de este curso no encontrado", exception.getReason());
        verify(contenidoRepo, never()).delete(any());
    }
}