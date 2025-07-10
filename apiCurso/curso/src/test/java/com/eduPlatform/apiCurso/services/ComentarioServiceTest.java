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
    void listarComentariosPorCurso() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Integer cursoId = 1;
            List<Comentario> lista = Arrays.asList(new Comentario(), new Comentario());
            when(comentarioRepo.findByCursoId(cursoId)).thenReturn(lista);

            List<Comentario> resultado = comentarioService.listarPorCurso(cursoId);

            assertEquals(lista, resultado);
            verify(comentarioRepo).findByCursoId(cursoId);
        }
    }

    @Test
    void crearComentarioConCursoValido() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@email.com");

            ComentarioCrear nuevoComentario = new ComentarioCrear();
            nuevoComentario.setCursoId(1);
            nuevoComentario.setDetalle("Buen curso");

            Curso curso = new Curso();
            curso.setId(1);

            Comentario guardado = new Comentario();
            guardado.setId(1);

            when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
            when(comentarioRepo.save(any(Comentario.class))).thenReturn(guardado);

            Comentario resultado = comentarioService.crearComentario(nuevoComentario);

            assertEquals(guardado, resultado);
            verify(comentarioRepo).save(any(Comentario.class));
        }
    }

    @Test
    void errorAlCrearComentarioConCursoInexistente() {
        ComentarioCrear nuevoComentario = new ComentarioCrear();
        nuevoComentario.setCursoId(1);

        when(cursoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> comentarioService.crearComentario(nuevoComentario));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Curso no encontrado", ex.getReason());
        verify(comentarioRepo, never()).save(any());
    }

    @Test
    void editarComentarioCorrectamente() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@email.com");

            int id = 1;
            ComentarioEditar datos = new ComentarioEditar();
            datos.setDetalle("Actualizado");

            Comentario original = new Comentario();
            original.setId(1);
            original.setEmailAutor("test@email.com");
            original.setDetalle("Anterior");

            when(comentarioRepo.findById(id)).thenReturn(Optional.of(original));
            when(comentarioRepo.save(original)).thenReturn(original);

            Comentario resultado = comentarioService.editarComentario(datos, id);

            assertEquals("Actualizado", resultado.getDetalle());
            verify(comentarioRepo).save(original);
        }
    }

    @Test
    void errorAlEditarComentarioInexistente() {
        int id = 1;
        ComentarioEditar datos = new ComentarioEditar();

        when(comentarioRepo.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> comentarioService.editarComentario(datos, id));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Comentario no encontrado", ex.getReason());
        verify(comentarioRepo, never()).save(any());
    }

    @Test
    void errorAlEditarComentarioDeOtroUsuario() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("mi@email.com");

            int id = 1;
            ComentarioEditar datos = new ComentarioEditar();

            Comentario comentarioDeOtro = new Comentario();
            comentarioDeOtro.setId(1);
            comentarioDeOtro.setEmailAutor("otro@email.com");

            when(comentarioRepo.findById(id)).thenReturn(Optional.of(comentarioDeOtro));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> comentarioService.editarComentario(datos, id));

            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
            assertEquals("No tienes permiso para editar este comentario", ex.getReason());
            verify(comentarioRepo, never()).save(any());
        }
    }
}
