package org.grupouno.parking.it4.controller;

import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.DetailRoleProfileService;
import org.grupouno.parking.it4.service.ProfileService;
import org.grupouno.parking.it4.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DetailRoleProfileControllerTest {

    @Mock
    private DetailRoleProfileService detailRoleProfileService;

    @Mock
    private ProfileService profileService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private DetailRoleProfileController detailRoleProfileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDetailRoleProfile_Success() {
        long profileId = 1L;
        long roleId = 2L;

        Profile profile = new Profile();
        profile.setProfileId(profileId);

        Rol role = new Rol();
        role.setRole(String.valueOf(roleId));

        DetailRoleProfile detailRoleProfile = new DetailRoleProfile();
        detailRoleProfile.setProfile(profile);
        detailRoleProfile.setRole(role);

        when(profileService.findById(profileId)).thenReturn(Optional.of(profile));
        when(roleService.findRolById(roleId)).thenReturn(Optional.of(role));
        when(detailRoleProfileService.saveDetailRoleProfile(any(DetailRoleProfile.class))).thenReturn(detailRoleProfile);

        ResponseEntity<DetailRoleProfile> response = detailRoleProfileController.saveDetailRoleProfile(profileId, roleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(detailRoleProfile, response.getBody());
        verify(detailRoleProfileService).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

    @Test
    void testSaveDetailRoleProfile_ProfileNotFound() {
        long profileId = 1L;
        long roleId = 2L;

        when(profileService.findById(profileId)).thenReturn(Optional.empty());
        when(roleService.findRolById(roleId)).thenReturn(Optional.of(new Rol()));

        ResponseEntity<DetailRoleProfile> response = detailRoleProfileController.saveDetailRoleProfile(profileId, roleId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(detailRoleProfileService, never()).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

    @Test
    void testSaveDetailRoleProfile_RoleNotFound() {
        long profileId = 1L;
        long roleId = 2L;

        when(profileService.findById(profileId)).thenReturn(Optional.of(new Profile()));
        when(roleService.findRolById(roleId)).thenReturn(Optional.empty());

        ResponseEntity<DetailRoleProfile> response = detailRoleProfileController.saveDetailRoleProfile(profileId, roleId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(detailRoleProfileService, never()).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

    @Test
    void testGetAllDetailRoleProfiles() {
        List<DetailRoleProfile> detailRoleProfiles = Arrays.asList(new DetailRoleProfile(), new DetailRoleProfile());
        when(detailRoleProfileService.getAllDetailRoleProfiles()).thenReturn(detailRoleProfiles);

        List<DetailRoleProfile> result = detailRoleProfileController.getAllDetailRoleProfiles();

        assertEquals(detailRoleProfiles.size(), result.size());
        verify(detailRoleProfileService).getAllDetailRoleProfiles();
    }

    @Test
    void testGetRolesByProfileId() {
        long profileId = 1L;
        List<Rol> roles = Arrays.asList(new Rol(), new Rol());

        when(detailRoleProfileService.getRolesByProfileId(profileId)).thenReturn(roles);

        ResponseEntity<List<Rol>> response = detailRoleProfileController.getRolesByProfileId(profileId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roles, response.getBody());
        verify(detailRoleProfileService).getRolesByProfileId(profileId);
    }

    @Test
    void testGetProfilesByRoleId() {
        long roleId = 2L;
        List<Profile> profiles = Arrays.asList(new Profile(), new Profile());

        when(detailRoleProfileService.getProfilesByRoleId(roleId)).thenReturn(profiles);

        ResponseEntity<List<Profile>> response = detailRoleProfileController.getProfilesByRoleId(roleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profiles, response.getBody());
        verify(detailRoleProfileService).getProfilesByRoleId(roleId);
    }

    @Test
    void testUpdateRolesForProfile_Success() {
        long profileId = 1L;
        List<Long> roleIds = Arrays.asList(2L, 3L);

        Rol role1 = new Rol();
        role1.setRole("Role1");

        Rol role2 = new Rol();
        role2.setRole("Role2");

        when(roleService.findRolById(2L)).thenReturn(Optional.of(role1));
        when(roleService.findRolById(3L)).thenReturn(Optional.of(role2));

        ResponseEntity<Void> response = detailRoleProfileController.updateRolesForProfile(profileId, roleIds);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(detailRoleProfileService).deleteRolesFromProfile(profileId);
        verify(detailRoleProfileService, times(2)).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

    @Test
    void testUpdateRolesForProfile_RoleNotFound() {
        long profileId = 1L;
        List<Long> roleIds = Arrays.asList(2L);

        when(roleService.findRolById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = detailRoleProfileController.updateRolesForProfile(profileId, roleIds);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(detailRoleProfileService, never()).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

    @Test
    void testUpdateRolesForProfile_EmptyRoleIds() {
        long profileId = 1L;
        List<Long> roleIds = Collections.emptyList();

        ResponseEntity<Void> response = detailRoleProfileController.updateRolesForProfile(profileId, roleIds);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(detailRoleProfileService).deleteRolesFromProfile(profileId);
        verify(detailRoleProfileService, never()).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

    @Test
    void testUpdateRolesForProfile_MultipleRoles_SomeNotFound() {
        long profileId = 1L;
        List<Long> roleIds = Arrays.asList(2L, 3L);

        Rol role1 = new Rol();
        role1.setRole(String.valueOf(2L));

        // Simulamos que solo el primer rol está disponible.
        when(roleService.findRolById(2L)).thenReturn(Optional.of(role1));
        when(roleService.findRolById(3L)).thenReturn(Optional.empty());

        // Ejecutamos la actualización de roles para el perfil.
        ResponseEntity<Void> response = detailRoleProfileController.updateRolesForProfile(profileId, roleIds);

        // Verificamos que la respuesta sea un BAD_REQUEST, ya que uno de los roles no fue encontrado.
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verificamos que el método para eliminar roles del perfil fue llamado una vez.
        verify(detailRoleProfileService, times(1)).deleteRolesFromProfile(profileId);
        // Verificamos que no se intentó guardar ningún DetailRoleProfile, ya que uno de los roles no fue encontrado.
        verify(detailRoleProfileService, never()).saveDetailRoleProfile(any(DetailRoleProfile.class));
    }

}
