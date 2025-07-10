package com.eduPlatform.apiCurso.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.eduPlatform.apiCurso.models.entities.Categoria;
import com.eduPlatform.apiCurso.models.requests.CategoriaCrear;
import com.eduPlatform.apiCurso.models.requests.CategoriaEditar;
import com.eduPlatform.apiCurso.repositories.CategoriaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    void listarTodasLasCategorias() {
        List<Categoria> categorias = Arrays.asList(categoria);
        when(categoriaRepo.findAll()).thenReturn(categorias);

        List<Categoria> resultado = categoriaService.obtenerTodos();

        assertEquals(categorias, resultado);
        verify(categoriaRepo).findAll();
    }

    @Test
    void obtenerCategoriaPorIdExistente() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.obtenerCategoriaPorId(1);

        assertEquals(categoria, resultado);
        verify(categoriaRepo).findById(1);
    }

    @Test
    void errorAlBuscarCategoriaPorIdInexistente() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoriaService.obtenerCategoriaPorId(1));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("categoria no encontrada", ex.getReason());
    }

    @Test
    void crearCategoriaCorrectamente() {
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("New Category")).thenReturn(null);
        when(categoriaRepo.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.crearCategoria(categoriaCrear);

        assertNotNull(resultado);
        verify(categoriaRepo).save(any(Categoria.class));
    }

    @Test
    void errorAlCrearCategoriaDuplicada() {
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("New Category")).thenReturn(categoria);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoriaService.crearCategoria(categoriaCrear));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("La categoría ya existe", ex.getReason());
    }

    @Test
    void modificarCategoriaCorrectamente() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("Updated Category")).thenReturn(null);
        when(categoriaRepo.save(categoria)).thenReturn(categoria);

        Categoria resultado = categoriaService.modificarCategoria(categoriaEditar);

        assertEquals(categoria, resultado);
        verify(categoriaRepo).save(categoria);
    }

    @Test
    void errorAlModificarCategoriaInexistente() {
        when(categoriaRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoriaService.modificarCategoria(categoriaEditar));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Categoría no encontrada", ex.getReason());
    }

    @Test
    void errorAlModificarCategoriaConNombreRepetido() {
        Categoria otra = new Categoria();
        otra.setId(2);
        otra.setNombreCategoria("Updated Category");

        when(categoriaRepo.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepo.findByNombreCategoriaIgnoreCase("Updated Category")).thenReturn(otra);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoriaService.modificarCategoria(categoriaEditar));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Ya existe otra categoría con ese nombre", ex.getReason());
    }
}
