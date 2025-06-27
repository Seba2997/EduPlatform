package com.eduPlatform.apiCurso.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.eduPlatform.apiCurso.models.entities.Contenido;
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.requests.ContenidoCrear;
import com.eduPlatform.apiCurso.repositories.ContenidoRepository;

@SpringBootTest
public class ContenidoServiceTests {
    
    @Mock
    private ContenidoRepository contenidoRepo;

    @Mock
    private CursoService cursoService;

    @InjectMocks
    private ContenidoService contenidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrar_DeberiaGuardarContenidoCorrectamente() {
        // Arrange: crear el request que simula el formulario
        ContenidoCrear crear = new ContenidoCrear();
        crear.setContenido("Programación orientada a objetos");

        // Simular un curso ya existente
        Curso cursoSimulado = new Curso();
        cursoSimulado.setId(1); // este ID sí puede estar, es del curso, no autogenerado
        cursoSimulado.setNombreCurso("Java Básico");

        // Simular el contenido que se espera que se retorne (el repositorio lo "guarda")
        Contenido contenidoGuardado = new Contenido();
        contenidoGuardado.setContenido("Programación orientada a objetos");
        contenidoGuardado.setTituloContenido("Programación orientada a objetos");
        contenidoGuardado.setCurso(cursoSimulado);
        // NO seteamos el ID porque la base lo autogenera

        // Simular comportamiento del cursoService y del repositorio
        when(cursoService.obtenerCursoPorId(1)).thenReturn(cursoSimulado);
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenidoGuardado);

        // Act: ejecutar el método que estamos probando
        Contenido resultado = contenidoService.registrar(crear, 1);

        // Assert: verificar que el contenido fue creado correctamente
        assertNotNull(resultado);
        assertEquals("Programación orientada a objetos", resultado.getContenido());
        assertEquals("Programación orientada a objetos", resultado.getTituloContenido());
        assertEquals("Java Básico", resultado.getCurso().getNombreCurso());

        // Verificar que los mocks fueron llamados como esperábamos
        verify(cursoService).obtenerCursoPorId(1);
        verify(contenidoRepo).save(any(Contenido.class));
    }

    
}
