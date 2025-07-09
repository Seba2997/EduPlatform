package com.eduplatform.apiUsuario.services;

import com.eduplatform.apiUsuario.models.RolNombre;
import com.eduplatform.apiUsuario.models.entities.Rol;
import com.eduplatform.apiUsuario.models.entities.User;
import com.eduplatform.apiUsuario.models.request.UserCrear;
import com.eduplatform.apiUsuario.models.request.UserUpdate;
import com.eduplatform.apiUsuario.models.response.UserDTO;
import com.eduplatform.apiUsuario.repositories.RolRepository;
import com.eduplatform.apiUsuario.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private Rol mockRol;

    @BeforeEach
    void setUp() {
        mockRol = new Rol();
        mockRol.setId(1L);
        mockRol.setNombre(RolNombre.ESTUDIANTE);

        mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPhone("123456789");
        mockUser.setActive(true);
        mockUser.setRoles(Set.of(mockRol));
    }

    @Test
    void obtenerTodosDTO_ShouldReturnAllUsersAsDTO() {
        List<User> users = Arrays.asList(mockUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.obtenerTodosDTO();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void obtenerUnoDTO_WhenUserExists_ShouldReturnUserDTO() {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        UserDTO result = userService.obtenerUnoDTO(1);

        assertNotNull(result);
        verify(userRepository).findById(1);
    }

    @Test
    void obtenerUnoDTO_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> userService.obtenerUnoDTO(1));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuario no encontrado", exception.getReason());
    }

    @Test
    void obtenerPorEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        User result = userService.obtenerPorEmail("test@example.com");

        assertNotNull(result);
        assertEquals(mockUser, result);
    }

    @Test
    void obtenerPorEmail_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.obtenerPorEmail("test@example.com"));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void obtenerPorRol_ShouldReturnUsersWithSpecificRole() {
        List<User> users = Arrays.asList(mockUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.obtenerPorRol(RolNombre.ESTUDIANTE);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void obtenerActivos_ShouldReturnActiveUsers() {
        List<User> activeUsers = Arrays.asList(mockUser);
        when(userRepository.findByActive(true)).thenReturn(activeUsers);

        List<User> result = userService.obtenerActivos();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByActive(true);
    }

    @Test
    void registrarComo_WhenValidUser_ShouldCreateUser() {
        UserCrear userCrear = new UserCrear();
        userCrear.setName("New User");
        userCrear.setEmail("new@example.com");
        userCrear.setPhone("987654321");
        userCrear.setPassword("password");

        when(rolRepository.findByNombre(RolNombre.ESTUDIANTE)).thenReturn(Optional.of(mockRol));
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.registrarComo(userCrear, RolNombre.ESTUDIANTE);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registrarComo_WhenEmailExists_ShouldThrowConflictException() {
        UserCrear userCrear = new UserCrear();
        userCrear.setEmail("test@example.com");

        when(rolRepository.findByNombre(RolNombre.ESTUDIANTE)).thenReturn(Optional.of(mockRol));
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.registrarComo(userCrear, RolNombre.ESTUDIANTE));
        
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void registrarComo_WhenRolNotFound_ShouldThrowNotFoundException() {
        UserCrear userCrear = new UserCrear();
        when(rolRepository.findByNombre(RolNombre.ESTUDIANTE)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.registrarComo(userCrear, RolNombre.ESTUDIANTE));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void validarPassword_ShouldReturnTrueForValidPassword() {
        String password = "password";
        String hash = userService.generateHash(password);

        boolean result = userService.validarPassword(password, hash);

        assertTrue(result);
    }

    @Test
    void modificar_WhenUserExists_ShouldUpdateUser() {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setId(1);
        userUpdate.setName("Updated Name");

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.modificar(userUpdate);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void modificar_WhenUserNotExists_ShouldThrowException() {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.modificar(userUpdate));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void cambiarEstado_WhenUserExists_ShouldToggleActiveStatus() {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.cambiarEstado(1);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void cambiarEstado_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> userService.cambiarEstado(1));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}