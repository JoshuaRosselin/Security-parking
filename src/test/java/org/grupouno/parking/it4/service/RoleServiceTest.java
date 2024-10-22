package org.grupouno.parking.it4.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.exceptions.SerializingRolException;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @Mock
    private AudithService audithService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RoleService roleService;

    private static final Long VALID_ROLE_ID = 1L;
    private static final Long INVALID_ROLE_ID = 999L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindRolesByProfileId() {
        // Arrange
        Long profileId = 1L;
        List<Rol> roles = List.of(new Rol(), new Rol());
        when(repository.findRolesByProfileId(profileId)).thenReturn(roles);

        // Act
        List<String> result = roleService.findRolesByProfileId(profileId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findRolesByProfileId(profileId);
    }

    @Test
    void testGetRolesByProfileId() {
        // Arrange
        Long profileId = 1L;
        List<Rol> roles = List.of(new Rol(), new Rol());
        when(repository.findRolesByProfileId(profileId)).thenReturn(roles);

        // Act
        List<GrantedAuthority> result = roleService.getRolesByProfileId(profileId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).findRolesByProfileId(profileId);
    }

    @Test
    void testSaveRole() {
        // Arrange
        RoleDto roleDto = new RoleDto("USER", "User role");
        when(repository.findByRole("ROLE_USER")).thenReturn(Optional.empty());
        when(repository.save(any(Rol.class))).thenReturn(new Rol());

        // Act
        Rol result = roleService.saveRole(roleDto);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_USER", result.getRole());
        verify(repository, times(1)).save(any(Rol.class));
    }

    @Test
    void testSaveRoleAlreadyExists() {
        // Arrange
        RoleDto roleDto = new RoleDto("USER", "User role");
        when(repository.findByRole("ROLE_USER")).thenReturn(Optional.of(new Rol()));

        // Act
        Rol result = roleService.saveRole(roleDto);

        // Assert
        assertNull(result);
        verify(repository, never()).save(any(Rol.class));
    }

    @Test
    void testUpdateRol() {
        // Arrange
        RoleDto roleDto = new RoleDto("ADMIN", "Admin role");
        Rol existingRole = new Rol();
        when(repository.existsById(VALID_ROLE_ID)).thenReturn(true);
        when(repository.findById(VALID_ROLE_ID)).thenReturn(Optional.of(existingRole));
        when(repository.save(any(Rol.class))).thenReturn(existingRole);

        // Act
        roleService.updateRol(roleDto, VALID_ROLE_ID);

        // Assert
        assertEquals("ADMIN", existingRole.getRole());
        verify(repository, times(1)).save(existingRole);
    }

    @Test
    void testUpdateRolNotFound() {
        // Arrange
        RoleDto roleDto = new RoleDto("ADMIN", "Admin role");
        when(repository.existsById(INVALID_ROLE_ID)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            roleService.updateRol(roleDto, INVALID_ROLE_ID);
        });
        assertEquals("This rol don't exist", thrown.getMessage());
    }

    @Test
    void testDeleteRole() {
        // Arrange
        when(repository.existsById(VALID_ROLE_ID)).thenReturn(true);
        when(repository.findById(VALID_ROLE_ID)).thenReturn(Optional.of(new Rol()));

        // Act
        roleService.delete(VALID_ROLE_ID);

        // Assert
        verify(repository, times(1)).deleteById(VALID_ROLE_ID);
    }

    @Test
    void testDeleteRoleNotFound() {
        // Arrange
        when(repository.existsById(INVALID_ROLE_ID)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            roleService.delete(INVALID_ROLE_ID);
        });
        assertEquals("This rol don't exist", thrown.getMessage());
    }

    @Test
    void testDeleteRoleDataAccessException() {
        // Arrange
        when(repository.existsById(VALID_ROLE_ID)).thenReturn(true);
        when(repository.findById(VALID_ROLE_ID)).thenReturn(Optional.of(new Rol()));
        doThrow(new DataAccessException("Database error") {}).when(repository).deleteById(VALID_ROLE_ID);

        // Act & Assert
        UserDeletionException thrown = assertThrows(UserDeletionException.class, () -> {
            roleService.delete(VALID_ROLE_ID);
        });
        assertEquals("Error deleting rol ", thrown.getMessage());
    }

    @Test
    void testFindRolById() {
        // Arrange
        when(repository.findById(VALID_ROLE_ID)).thenReturn(Optional.of(new Rol()));

        // Act
        Optional<Rol> result = roleService.findRolById(VALID_ROLE_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ROLE_USER", result.get().getRole());
        verify(audithService, times(1)).createAudit(anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void testFindRolByIdNotFound() {
        // Arrange
        when(repository.findById(INVALID_ROLE_ID)).thenReturn(Optional.empty());

        // Act
        Optional<Rol> result = roleService.findRolById(INVALID_ROLE_ID);

        // Assert
        assertFalse(result.isPresent());
        verify(audithService, times(1)).createAudit(anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void testGetAllRol() {
        // Arrange
        List<Rol> roles = List.of(new Rol(), new Rol());
        when(repository.findAll()).thenReturn(roles);

        // Act
        List<Rol> result = roleService.getAllRol();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(audithService, times(1)).createAudit(anyString(), anyString(), anyString(), any(), any(), any());
    }


}

