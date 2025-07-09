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
import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.requests.CategoriaCrear;
import com.eduPlatform.apiCurso.models.requests.CategoriaEditar;
import com.eduPlatform.apiCurso.repositories.CategoriaRepository;





@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepo;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaCrear categoriaCrear;
    private CategoriaEditar categoriaEditar;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombreCategoria("Test Category");

        categoriaCrear = new CategoriaCrear();
        categoriaCrear.setNombreCategoria("New Category");

        categoriaEditar = new CategoriaEditar();
        categoriaEditar.setId(1);
        categoriaEditar.setNombreCategoria("Updated Category");
    }

    @Test
    void obtenerTodos_ReturnsAllCategorias() {
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepo.findAll()).thenReturn(categorias);

        List<Categoria> result = categoriaService.obtenerTodos();

        assertEquals(categorias, result);
        verify(categoriaRepo).findAll();
    }

    @Test
    void obtenerCategoriaPorId_ExistingId_ReturnsCategoria() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));

        Categoria result = categoriaService.obtenerCategoriaPorId(1);

        assertEquals(categoria, result);
        verify(categoriaRepo).findById(1);
    }

    @Test
    void obtenerCategoriaPorId_NonExistingId_ThrowsNotFoundException() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoriaService.obtenerCategoriaPorId(1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("categoria no encontrada", exception.getReason());
    }

    @Test
    void crearCategoria_ValidData_ReturnsCreatedCategoria() {
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("New Category")).thenReturn(null);
        when(categoriaRepo.save(any(Categoria.class))).thenReturn(categoria);

        Categoria result = categoriaService.crearCategoria(categoriaCrear);

        assertNotNull(result);
        verify(categoriaRepo).findByNombreCategoriaIgnoreCase("New Category");
        verify(categoriaRepo).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_NullData_ThrowsBadRequestException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoriaService.crearCategoria(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Datos de categoría inválidos", exception.getReason());
    }

    @Test
    void crearCategoria_NullNombre_ThrowsBadRequestException() {
        categoriaCrear.setNombreCategoria(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoriaService.crearCategoria(categoriaCrear));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Datos de categoría inválidos", exception.getReason());
    }

    @Test
    void crearCategoria_ExistingName_ThrowsConflictException() {
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("New Category")).thenReturn(categoria);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoriaService.crearCategoria(categoriaCrear));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("La categoría ya existe", exception.getReason());
    }

    @Test
    void modificarCategoria_ValidData_ReturnsUpdatedCategoria() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("Updated Category")).thenReturn(null);
        when(categoriaRepo.save(categoria)).thenReturn(categoria);

        Categoria result = categoriaService.modificarCategoria(categoriaEditar);

        assertEquals(categoria, result);
        verify(categoriaRepo).findById(1);
        verify(categoriaRepo).findByNombreCategoriaIgnoreCase("Updated Category");
        verify(categoriaRepo).save(categoria);
    }

    @Test
    void modificarCategoria_NonExistingId_ThrowsNotFoundException() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoriaService.modificarCategoria(categoriaEditar));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Categoría no encontrada", exception.getReason());
    }

    @Test
    void modificarCategoria_DuplicateName_ThrowsConflictException() {
        Categoria otraCategoria = new Categoria();
        otraCategoria.setId(2);
        otraCategoria.setNombreCategoria("Updated Category");

        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("Updated Category")).thenReturn(otraCategoria);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> categoriaService.modificarCategoria(categoriaEditar));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Ya existe otra categoría con ese nombre", exception.getReason());
    }

    @Test
    void modificarCategoria_SameName_AllowsUpdate() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("Updated Category")).thenReturn(categoria);
        when(categoriaRepo.save(categoria)).thenReturn(categoria);

        Categoria result = categoriaService.modificarCategoria(categoriaEditar);

        assertEquals(categoria, result);
        verify(categoriaRepo).save(categoria);
    }
}