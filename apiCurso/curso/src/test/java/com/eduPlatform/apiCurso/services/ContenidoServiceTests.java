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
        
        ContenidoCrear crear = new ContenidoCrear();
        crear.setContenido("Programación orientada a objetos");

        Curso cursoSimulado = new Curso();
        cursoSimulado.setId(1); 
        cursoSimulado.setNombreCurso("Java Básico");

        Contenido contenidoGuardado = new Contenido();
        contenidoGuardado.setContenido("Programación orientada a objetos");
        contenidoGuardado.setTituloContenido("Programación orientada a objetos");
        contenidoGuardado.setCurso(cursoSimulado);

        when(cursoService.obtenerCursoPorId(1)).thenReturn(cursoSimulado);
        when(contenidoRepo.save(any(Contenido.class))).thenReturn(contenidoGuardado);

        Contenido resultado = contenidoService.registrar(crear, 1);

        assertNotNull(resultado);
        assertEquals("Programación orientada a objetos", resultado.getContenido());
        assertEquals("Programación orientada a objetos", resultado.getTituloContenido());
        assertEquals("Java Básico", resultado.getCurso().getNombreCurso());

        verify(cursoService).obtenerCursoPorId(1);
        verify(contenidoRepo).save(any(Contenido.class));
    }

    
}
