package com.eduPlatform.apiCurso.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.eduPlatform.apiCurso.models.entities.Contenido;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ContenidoCrear;
import com.eduPlatform.apiCurso.models.requests.ContenidoEditar;
import com.eduPlatform.apiCurso.repositories.ContenidoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    void listarTodosLosContenidos() {
        List<Contenido> lista = Arrays.asList(contenido);
        when(contenidoRepo.findAll()).thenReturn(lista);

        List<Contenido> resultado = contenidoService.obtenerTodos();

        assertEquals(lista, resultado);
        verify(contenidoRepo).findAll();
    }

    @Test
    void buscarContenidoPorIdExistente() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));

        Contenido resultado = contenidoService.obtenerContenidoPorId(1);

        assertEquals(contenido, resultado);
        verify(contenidoRepo).findById(1);
    }

    @Test
    void errorAlBuscarContenidoPorIdInexistente() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> contenidoService.obtenerContenidoPorId(1));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Contenido de este curso no encontrado", ex.getReason());
    }

    @Test
    void registrarContenidoCorrectamente() {
        when(cursoService.obtenerCursoPorId(1)).thenReturn(curso);
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenido);

        Contenido resultado = contenidoService.registrar(contenidoCrear, 1);

        assertNotNull(resultado);
        verify(cursoService).obtenerCursoPorId(1);
        verify(contenidoRepo).save(any(Contenido.class));
    }

    @Test
    void errorAlRegistrarContenidoPorCursoFallido() {
        when(cursoService.obtenerCursoPorId(1)).thenThrow(new RuntimeException("Error"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> contenidoService.registrar(contenidoCrear, 1));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Error al registrar contenido", ex.getReason());
    }

    @Test
    void modificarContenidoCorrectamente() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenido);

        Contenido resultado = contenidoService.modificar(contenidoEditar);

        assertEquals(contenido, resultado);
        verify(contenidoRepo).save(contenido);
    }

    @Test
    void errorAlModificarContenidoInexistente() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> contenidoService.modificar(contenidoEditar));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Contenido de este curso no encontrado", ex.getReason());
    }

    @Test
    void eliminarContenidoCorrectamente() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.of(contenido));

        assertDoesNotThrow(() -> contenidoService.eliminarContenido(1));

        verify(contenidoRepo).delete(contenido);
    }

    @Test
    void errorAlEliminarContenidoInexistente() {
        when(contenidoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> contenidoService.eliminarContenido(1));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Contenido de este curso no encontrado", ex.getReason());
        verify(contenidoRepo, never()).delete(any());
    }
}
