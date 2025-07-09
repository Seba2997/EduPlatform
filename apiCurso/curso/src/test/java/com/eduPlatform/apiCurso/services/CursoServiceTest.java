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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Profesor;
import com.eduPlatform.apiCurso.models.requests.CursoCrear;
import com.eduPlatform.apiCurso.models.requests.CursoEditar;
import com.eduPlatform.apiCurso.models.user.UsuarioDTO;
import com.eduPlatform.apiCurso.repositories.CategoriaRepository;
import com.eduPlatform.apiCurso.repositories.CursoRepository;
import com.eduPlatform.apiCurso.repositories.ProfesorRepository;
import reactor.core.publisher.Mono;




@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepo;

    @Mock
    private WebClient webClient;

    @Mock
    private ProfesorRepository profesorRepo;

    @Mock
    private CategoriaRepository categoriaRepo;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CursoService cursoService;

    private Curso curso;
    private Profesor profesor;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombreCategoria("Programación");

        profesor = new Profesor();
        profesor.setId(1);
        profesor.setNombre("Juan Pérez");
        profesor.setEmail("juan@test.com");

        curso = new Curso();
        curso.setId(1);
        curso.setNombreCurso("Java Básico");
        curso.setDescripcion("Curso de Java para principiantes");
        curso.setEstado(true);
        curso.setPrecio(100);
        curso.setProfesor(profesor);
        curso.setCategoria(categoria);
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaDeCursos() {
        List<Curso> cursos = Arrays.asList(curso);
        when(cursoRepo.findAll()).thenReturn(cursos);

        List<Curso> resultado = cursoService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals(curso, resultado.get(0));
        verify(cursoRepo).findAll();
    }

    @Test
    void obtenerActivos_DeberiaRetornarCursosActivos() {
        List<Curso> cursosActivos = Arrays.asList(curso);
        when(cursoRepo.findByEstado(true)).thenReturn(cursosActivos);

        List<Curso> resultado = cursoService.obtenerActivos();

        assertEquals(1, resultado.size());
        assertEquals(curso, resultado.get(0));
        verify(cursoRepo).findByEstado(true);
    }

    @Test
    void obtenerCursoPorId_CuandoExiste_DeberiaRetornarCurso() {
        when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));

        Curso resultado = cursoService.obtenerCursoPorId(1);

        assertEquals(curso, resultado);
        verify(cursoRepo).findById(1);
    }

    @Test
    void obtenerCursoPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(cursoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.obtenerCursoPorId(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Curso no encontrado", exception.getReason());
    }

    @Test
    void obtenerPorNombre_DeberiaRetornarCursosPorNombre() {
        List<Curso> cursos = Arrays.asList(curso);
        when(cursoRepo.findByNombreCurso("Java Básico")).thenReturn(cursos);

        List<Curso> resultado = cursoService.obtenerPorNombre("Java Básico");

        assertEquals(1, resultado.size());
        assertEquals(curso, resultado.get(0));
        verify(cursoRepo).findByNombreCurso("Java Básico");
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrar_CuandoEsExitoso_DeberiaCrearCurso() {
        try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
            // Arrange
            CursoCrear cursoCrear = new CursoCrear();
            cursoCrear.setIdProfesor("1");
            cursoCrear.setNombreCurso("Java Básico");
            cursoCrear.setDescripcion("Curso de Java");
            cursoCrear.setEstado(true);
            cursoCrear.setPrecio(100);
            cursoCrear.setCategoriaNombre("Programación");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            UsuarioDTO usuarioDTO = mock(UsuarioDTO.class);

            securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("jwt-token");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(UsuarioDTO.class)).thenReturn(Mono.just(usuarioDTO));

            when(usuarioDTO.getId()).thenReturn(1);
            when(usuarioDTO.getName()).thenReturn("Juan Pérez");
            when(usuarioDTO.getEmail()).thenReturn("juan@test.com");
            when(usuarioDTO.tieneRol("PROFESOR")).thenReturn(true);

            when(profesorRepo.findById(1)).thenReturn(Optional.of(profesor));
            when(categoriaRepo.findByNombreCategoriaIgnoreCase("Programación")).thenReturn(categoria);
            when(categoriaRepo.save(any(Categoria.class))).thenReturn(categoria);
            when(cursoRepo.save(any(Curso.class))).thenReturn(curso);

            // Act
            Curso resultado = cursoService.registrar(cursoCrear);

            // Assert
            assertNotNull(resultado);
            verify(cursoRepo).save(any(Curso.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrar_CuandoUsuarioNoEsProfesor_DeberiaLanzarExcepcion() {
        try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
            // Arrange
            CursoCrear cursoCrear = new CursoCrear();
            cursoCrear.setIdProfesor("1");
            cursoCrear.setCategoriaNombre("Programación");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            UsuarioDTO usuarioDTO = mock(UsuarioDTO.class);

            securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getCredentials()).thenReturn("jwt-token");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(UsuarioDTO.class)).thenReturn(Mono.just(usuarioDTO));

            when(usuarioDTO.tieneRol("PROFESOR")).thenReturn(false);

            // Act & Assert
            ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> cursoService.registrar(cursoCrear)
            );

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        }
    }

    @Test
    void modificar_CuandoExiste_DeberiaModificarCurso() {
        CursoEditar cursoEditar = new CursoEditar();
        cursoEditar.setId(1);
        cursoEditar.setNombreCurso("Java Avanzado");
        cursoEditar.setDescripcion("Nueva descripción");

        when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
        when(cursoRepo.save(any(Curso.class))).thenReturn(curso);

        Curso resultado = cursoService.modificar(cursoEditar);

        assertNotNull(resultado);
        verify(cursoRepo).findById(1);
        verify(cursoRepo).save(curso);
    }

    @Test
    void modificar_CuandoNoExiste_DeberiaLanzarExcepcion() {
        CursoEditar cursoEditar = new CursoEditar();
        cursoEditar.setId(1);

        when(cursoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.modificar(cursoEditar)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Curso no encontrado", exception.getReason());
    }

    @Test
    void cambiarEstado_CuandoEstaActivo_DeberiaCambiarAInactivo() {
        curso.setEstado(true);
        when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
        when(cursoRepo.save(any(Curso.class))).thenReturn(curso);

        Curso resultado = cursoService.cambiarEstado(1);

        assertNotNull(resultado);
        verify(cursoRepo).findById(1);
        verify(cursoRepo).save(curso);
    }

    @Test
    void cambiarEstado_CuandoEstaInactivo_DeberiaCambiarAActivo() {
        curso.setEstado(false);
        when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
        when(cursoRepo.save(any(Curso.class))).thenReturn(curso);

        Curso resultado = cursoService.cambiarEstado(1);

        assertNotNull(resultado);
        verify(cursoRepo).findById(1);
        verify(cursoRepo).save(curso);
    }

    @Test
    void cambiarEstado_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(cursoRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.cambiarEstado(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Curso no encontrado", exception.getReason());
    }
}