package com.eduPlatform.apiCurso.services;

import com.eduPlatform.apiCurso.models.entities.Comentario;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ComentarioCrear;
import com.eduPlatform.apiCurso.models.requests.ComentarioEditar;
import com.eduPlatform.apiCurso.repositories.ComentarioRepository;
import com.eduPlatform.apiCurso.repositories.CursoRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepo;

    @Mock
    private CursoRepository cursoRepo;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ComentarioService comentarioService;

    @Test
    void listarPorCurso_DeberiaRetornarComentarios() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            // Arrange
            Integer cursoId = 1;
            List<Comentario> comentarios = Arrays.asList(new Comentario(), new Comentario());
            when(comentarioRepo.findByCursoId(cursoId)).thenReturn(comentarios);

            // Act
            List<Comentario> resultado = comentarioService.listarPorCurso(cursoId);

            // Assert
            assertEquals(comentarios, resultado);
            verify(comentarioRepo).findByCursoId(cursoId);
        }
    }

    @Test
    void crearComentario_CursoExiste_DeberiaCrearComentario() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@email.com");

            ComentarioCrear comentarioCrear = new ComentarioCrear();
            comentarioCrear.setCursoId(1);
            comentarioCrear.setDetalle("Test comment");

            Curso curso = new Curso();
            curso.setId(1);

            Comentario comentarioGuardado = new Comentario();
            comentarioGuardado.setId(1);

            when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
            when(comentarioRepo.save(any(Comentario.class))).thenReturn(comentarioGuardado);

            // Act
            Comentario resultado = comentarioService.crearComentario(comentarioCrear);

            // Assert
            assertEquals(comentarioGuardado, resultado);
            verify(cursoRepo).findById(1);
            verify(comentarioRepo).save(any(Comentario.class));
        }
    }

    @Test
    void crearComentario_CursoNoExiste_DeberiaLanzarExcepcion() {
        ComentarioCrear comentarioCrear = new ComentarioCrear();
        comentarioCrear.setCursoId(1);

        when(cursoRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> comentarioService.crearComentario(comentarioCrear));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Curso no encontrado", exception.getReason());
        verify(cursoRepo).findById(1);
        verify(comentarioRepo, never()).save(any());
    }

    @Test
    void editarComentario_ComentarioExisteYAutorizado_DeberiaEditarComentario() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@email.com");

            int idComentario = 1;
            ComentarioEditar comentarioEditar = new ComentarioEditar();
            comentarioEditar.setDetalle("Updated comment");

            Comentario comentarioExistente = new Comentario();
            comentarioExistente.setId(1);
            comentarioExistente.setEmailAutor("test@email.com");
            comentarioExistente.setDetalle("Original comment");

            Comentario comentarioActualizado = new Comentario();
            comentarioActualizado.setId(1);
            comentarioActualizado.setDetalle("Updated comment");

            when(comentarioRepo.findById(idComentario)).thenReturn(Optional.of(comentarioExistente));
            when(comentarioRepo.save(comentarioExistente)).thenReturn(comentarioActualizado);

            // Act
            Comentario resultado = comentarioService.editarComentario(comentarioEditar, idComentario);

            // Assert
            assertEquals(comentarioActualizado, resultado);
            assertEquals("Updated comment", comentarioExistente.getDetalle());
            verify(comentarioRepo).findById(idComentario);
            verify(comentarioRepo).save(comentarioExistente);
        }
    }

    @Test
    void editarComentario_ComentarioNoExiste_DeberiaLanzarExcepcion() {
        int idComentario = 1;
        ComentarioEditar comentarioEditar = new ComentarioEditar();

        when(comentarioRepo.findById(idComentario)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> comentarioService.editarComentario(comentarioEditar, idComentario));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Comentario no encontrado", exception.getReason());
        verify(comentarioRepo).findById(idComentario);
        verify(comentarioRepo, never()).save(any());
    }

    @Test
    void editarComentario_UsuarioNoAutorizado_DeberiaLanzarExcepcion() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@email.com");

            int idComentario = 1;
            ComentarioEditar comentarioEditar = new ComentarioEditar();

            Comentario comentarioExistente = new Comentario();
            comentarioExistente.setId(1);
            comentarioExistente.setEmailAutor("otro@email.com");

            when(comentarioRepo.findById(idComentario)).thenReturn(Optional.of(comentarioExistente));

            // Act & Assert
            ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                    () -> comentarioService.editarComentario(comentarioEditar, idComentario));

            assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
            assertEquals("No tienes permiso para editar este comentario", exception.getReason());
            verify(comentarioRepo).findById(idComentario);
            verify(comentarioRepo, never()).save(any());
        }
    }
}