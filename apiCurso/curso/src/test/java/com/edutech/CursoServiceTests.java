package com.edutech;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.repositories.CursoRepository;
import com.eduPlatform.apiCurso.services.CursoService;


@SpringBootTest
public class CursoServiceTests {
    
    @Autowired
    private CursoService cursoService;

    @Autowired
    private CursoRepository cursoRepo;

    @Test
void testCambiarEstado_Exitoso() {
    Curso curso = new Curso();
    curso.setId(1);
    curso.setEstado(true);

    when(cursoRepo.findById(1)).thenReturn(Optional.of(curso));
    when(cursoRepo.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Curso actualizado = cursoService.cambiarEstado(1);

    assertFalse(actualizado.getEstado());
}

    @Test
    void testCambiarEstado_NoExiste() {
    when(cursoRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(ResponseStatusException.class, () -> cursoService.cambiarEstado(1));
}

}
