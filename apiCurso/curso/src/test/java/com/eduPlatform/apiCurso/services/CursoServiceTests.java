package com.eduPlatform.apiCurso.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.repositories.CursoRepository;

@SpringBootTest
public class CursoServiceTests {

    @Test
    void cambiarEstado_CambiaDeTrueAFalso() {
        // Preparar mocks manualmente
        CursoRepository cursoRepo = mock(CursoRepository.class);
        CursoService cursoService = new CursoService();
        // Usar reflexión o constructor si no tienes setter
        setField(cursoService, "cursoRepo", cursoRepo);

        // Curso de prueba
        Curso curso = new Curso();
        curso.setId(1);
        curso.setEstado(true);

        when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
        when(cursoRepo.save(any(Curso.class))).thenReturn(curso);

        // Ejecutar
        Curso actualizado = cursoService.cambiarEstado(1);

        // Verificar
        assertFalse(actualizado.getEstado());
    }

    // Método auxiliar para setear campos privados (sin Spring)
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
}
