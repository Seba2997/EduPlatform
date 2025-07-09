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
import com.eduPlatform.apiCurso.models.entities.Curso;
import com.eduPlatform.apiCurso.models.entities.Evaluacion;
import com.eduPlatform.apiCurso.models.entities.EvaluacionEstudiante;
import com.eduPlatform.apiCurso.models.requests.EvaluacionCrear;
import com.eduPlatform.apiCurso.models.requests.EvaluacionEditar;
import com.eduPlatform.apiCurso.models.responses.EvaluacionEstudianteRespuesta;
import com.eduPlatform.apiCurso.repositories.EvaluacionEstudianteRepository;
import com.eduPlatform.apiCurso.repositories.EvaluacionRepository;





@ExtendWith(MockitoExtension.class)
class EvaluacionServiceTest {

    @Mock
    private CursoService cursoService;

    @Mock
    private EvaluacionRepository evaluacionRepo;

    @Mock
    private EvaluacionEstudianteRepository evaluacionEstudianteRepo;

    @InjectMocks
    private EvaluacionService evaluacionService;

    private Curso curso;
    private Evaluacion evaluacion;
    private EvaluacionCrear evaluacionCrear;
    private EvaluacionEditar evaluacionEditar;
    private EvaluacionEstudiante evaluacionEstudiante;

    @BeforeEach
    void setUp() {
        curso = new Curso();
        curso.setId(1);
        curso.setNombreCurso("Curso Test");

        evaluacion = new Evaluacion();
        evaluacion.setId(1);
        evaluacion.setTitulo("Test Evaluacion");
        evaluacion.setPregunta("¿Pregunta test?");
        evaluacion.setPuntajeMaximo(100);
        evaluacion.setCurso(curso);

        evaluacionCrear = new EvaluacionCrear();
        evaluacionCrear.setTitulo("Nueva Evaluacion");
        evaluacionCrear.setPregunta("¿Nueva pregunta?");
        evaluacionCrear.setPuntajeMaximo(50);

        evaluacionEditar = new EvaluacionEditar();
        evaluacionEditar.setTitulo("Evaluacion Editada");
        evaluacionEditar.setPregunta("¿Pregunta editada?");
        evaluacionEditar.setPuntajeMaximo(75);

        evaluacionEstudiante = new EvaluacionEstudiante();
        evaluacionEstudiante.setEvaluacionEstudianteid(1);
        evaluacionEstudiante.setNombreEstudiante("Juan Perez");
        evaluacionEstudiante.setEmailEstudiante("juan@test.com");
        evaluacionEstudiante.setRespuesta("Respuesta test");
        evaluacionEstudiante.setEvaluacion(evaluacion);
    }

    @Test
    void registrar_DeberiaCrearEvaluacion_CuandoDatosValidos() {
        when(cursoService.obtenerCursoPorId(1)).thenReturn(curso);
        when(evaluacionRepo.save(any(Evaluacion.class))).thenReturn(evaluacion);

        Evaluacion resultado = evaluacionService.registrar(evaluacionCrear, 1);

        assertNotNull(resultado);
        assertEquals("Test Evaluacion", resultado.getTitulo());
        verify(cursoService).obtenerCursoPorId(1);
        verify(evaluacionRepo).save(any(Evaluacion.class));
    }

    @Test
    void registrar_DeberiaLanzarExcepcion_CuandoOcurreError() {
        when(cursoService.obtenerCursoPorId(1)).thenThrow(new RuntimeException("Error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> evaluacionService.registrar(evaluacionCrear, 1));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Error al registrar evaluación", exception.getReason());
    }

    @Test
    void listarEvaluacionPorCurso_DeberiaRetornarEvaluaciones() {
        List<Evaluacion> evaluaciones = Arrays.asList(evaluacion);
        curso.setEvaluaciones(evaluaciones);
        when(cursoService.obtenerCursoPorId(1)).thenReturn(curso);

        List<Evaluacion> resultado = evaluacionService.listarEvaluacionPorCurso(1);

        assertEquals(1, resultado.size());
        assertEquals(evaluacion, resultado.get(0));
        verify(cursoService).obtenerCursoPorId(1);
    }

    @Test
    void modificarEvaluacion_DeberiaActualizarEvaluacion_CuandoExiste() {
        when(evaluacionRepo.findById(1)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepo.save(any(Evaluacion.class))).thenReturn(evaluacion);

        Evaluacion resultado = evaluacionService.modificarEvaluacion(evaluacionEditar, 1);

        assertNotNull(resultado);
        verify(evaluacionRepo).findById(1);
        verify(evaluacionRepo).save(evaluacion);
    }

    @Test
    void modificarEvaluacion_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(evaluacionRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> evaluacionService.modificarEvaluacion(evaluacionEditar, 1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Evaluación no encontrada", exception.getReason());
    }

    @Test
    void eliminarEvaluacion_DeberiaEliminar_CuandoExiste() {
        when(evaluacionRepo.findById(1)).thenReturn(Optional.of(evaluacion));

        evaluacionService.eliminarEvaluacion(1);

        verify(evaluacionRepo).findById(1);
        verify(evaluacionRepo).delete(evaluacion);
    }

    @Test
    void eliminarEvaluacion_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(evaluacionRepo.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> evaluacionService.eliminarEvaluacion(1));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Evaluación no encontrada", exception.getReason());
    }

    @Test
    void calificarRespuesta_DeberiaCalcularNotaCorrectamente() {
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.of(evaluacionEstudiante));
        when(evaluacionEstudianteRepo.save(any(EvaluacionEstudiante.class))).thenReturn(evaluacionEstudiante);

        EvaluacionEstudianteRespuesta resultado = evaluacionService.calificarRespuesta(1, 80);

        assertNotNull(resultado);
        assertEquals(80, evaluacionEstudiante.getPuntajeObtenido());
        assertEquals(5.8, evaluacionEstudiante.getNota(), 0.01); // 1 + (80/100) * 6 = 5.8
        verify(evaluacionEstudianteRepo).save(evaluacionEstudiante);
    }

    @Test
    void calificarRespuesta_DeberiaLanzarExcepcion_CuandoPuntajeExcedeLimite() {
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.of(evaluacionEstudiante));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> evaluacionService.calificarRespuesta(1, 120));

        assertEquals("Puntaje obtenido no puede ser mayor que el puntaje máximo.", exception.getMessage());
    }

    @Test
    void calificarRespuesta_DeberiaLanzarExcepcion_CuandoEvaluacionEstudianteNoExiste() {
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> evaluacionService.calificarRespuesta(1, 80));

        assertEquals("Evaluación del estudiante no encontrada", exception.getMessage());
    }

    @Test
    void verRespuestaEstudiantePorId_DeberiaRetornarEvaluacionEstudiante() {
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.of(evaluacionEstudiante));

        EvaluacionEstudiante resultado = evaluacionService.verRespuestaEstudiantePorId(1);

        assertEquals(evaluacionEstudiante, resultado);
        verify(evaluacionEstudianteRepo).findById(1);
    }

    @Test
    void verRespuestaEstudiantePorId_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(evaluacionEstudianteRepo.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> evaluacionService.verRespuestaEstudiantePorId(1));

        assertTrue(exception.getMessage().contains("Respuesta de estudiante no encontrada con ID: 1"));
    }
}