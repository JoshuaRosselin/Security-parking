package org.grupouno.parking.it4.controller;

import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles_Success() {
        List<Rol> roles = Arrays.asList(new Rol(), new Rol());
        when(roleService.getAllRol()).thenReturn(roles);

        ResponseEntity<Map<String, Object>> response = roleController.getAllRoles();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(roles, response.getBody().get("message"));
    }

    @Test
    void testGetAllRoles_Error() {
        when(roleService.getAllRol()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, Object>> response = roleController.getAllRoles();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().get("message"));
        assertEquals("An error get Roles Database error", response.getBody().get("err"));
    }

    @Test
    void testGetRolesId_Success() {
        Long id = 1L;
        Rol rol = new Rol();
        when(roleService.findRolById(id)).thenReturn(Optional.of(rol));

        ResponseEntity<Map<String, Object>> response = roleController.getRolesId(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Optional.of(rol), response.getBody().get("message"));
    }

    @Test
    void testGetRolesId_NotFound() {
        Long id = 1L;
        when(roleService.findRolById(id)).thenThrow(new IllegalArgumentException("Not found"));

        ResponseEntity<Map<String, Object>> response = roleController.getRolesId(id);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().get("message"));
        assertEquals("An error get Roles Not found", response.getBody().get("err"));
    }

    @Test
    void testAddRole_Success() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRole("NewRole");

        ResponseEntity<Map<String, String>> response = roleController.addRole(roleDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("NewRoleSaved", response.getBody().get("message"));
    }

    @Test
    void testAddRole_Error() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRole("NewRole");
        doThrow(new RuntimeException("Error")).when(roleService).saveRole(roleDto);

        ResponseEntity<Map<String, String>> response = roleController.addRole(roleDto);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().get("message"));
        assertEquals("An error save Role Error", response.getBody().get("err"));
    }

    @Test
    void testDeleteRole_Success() {
        Long id = 1L;

        ResponseEntity<Map<String, String>> response = roleController.deleteRole(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Role: 1 deleted", response.getBody().get("message"));
    }

    @Test
    void testDeleteRole_Error() {
        Long id = 1L;
        doThrow(new IllegalArgumentException("Not found")).when(roleService).delete(id);

        ResponseEntity<Map<String, String>> response = roleController.deleteRole(id);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().get("message"));
        assertEquals("Not found", response.getBody().get("Error"));
    }

    @Test
    void testUpdateRole_Success() {
        Long id = 1L;
        RoleDto roleDto = new RoleDto();
        roleDto.setRole("UpdatedRole");

        ResponseEntity<Map<String, String>> response = roleController.updateRole(id, roleDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Role Updated", response.getBody().get("message"));
    }

    @Test
    void testUpdateRole_NotFound() {
        Long id = 1L;
        RoleDto roleDto = new RoleDto();
        roleDto.setRole("UpdatedRole");
        doThrow(new EntityNotFoundException("Role not found")).when(roleService).updateRol(roleDto, id);

        ResponseEntity<Map<String, String>> response = roleController.updateRole(id, roleDto);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error", response.getBody().get("message"));
        assertEquals("An error update Role Role not found", response.getBody().get("err"));
    }
}
