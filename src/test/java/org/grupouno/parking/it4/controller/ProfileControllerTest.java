package org.grupouno.parking.it4.controller;

import jakarta.persistence.EntityNotFoundException;
import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    public ProfileControllerTest() {
        initMocks(this);
    }

    @Test
    void testListProfilesSuccess() {
        Page<Profile> profilePage = new PageImpl<>(Collections.singletonList(new Profile()));
        when(profileService.getAllProfiles(anyInt(), anyInt(), anyString())).thenReturn(profilePage);

        ResponseEntity<Map<String, Object>> response = profileController.listProfiles(0, 10, "test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("profiles"));
    }

    @Test
    void testListProfilesException() {
        when(profileService.getAllProfiles(anyInt(), anyInt(), anyString())).thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<Map<String, Object>> response = profileController.listProfiles(0, 10, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error fetching profiles", response.getBody().get("Message"));
    }

    @Test
    void testSaveProfileWithRolesSuccess() {
        Profile profile = new Profile();
        when(profileService.saveProfileWithRoles(any(Profile.class), anyList())).thenReturn(profile);

        ResponseEntity<Map<String, Object>> response = profileController.saveProfileWithRoles(profile, List.of(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile and roles saved successfully", response.getBody().get("Message"));
    }

    @Test
    void testSaveProfileWithRolesException() {
        when(profileService.saveProfileWithRoles(any(Profile.class), anyList())).thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<Map<String, Object>> response = profileController.saveProfileWithRoles(new Profile(), List.of(1L));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error saving profile with roles", response.getBody().get("Message"));
    }

    @Test
    void testUpdateProfileRolesSuccess() {
        Profile profile = new Profile();
        when(profileService.updateProfileRoles(anyLong(), anyList())).thenReturn(profile);

        ResponseEntity<Map<String, Object>> response = profileController.updateProfileRoles(1L, List.of(1L), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile and roles saved successfully", response.getBody().get("Message"));
    }

    @Test
    void testUpdateProfileRolesEntityNotFoundException() {
        when(profileService.updateProfileRoles(anyLong(), anyList())).thenThrow(new EntityNotFoundException("Profile not found"));

        ResponseEntity<Map<String, Object>> response = profileController.updateProfileRoles(1L, List.of(1L), null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Profile or roles not found", response.getBody().get("Message"));
    }

    @Test
    void testDeleteProfileSuccess() {
        doNothing().when(profileService).deleteProfileAndDetail(anyLong());

        ResponseEntity<Map<String, Object>> response = profileController.deleteProfileAndDetail(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile: 1 Deleted", response.getBody().get("Message"));
    }

    @Test
    void testDeleteProfileEntityNotFoundException() {
        doThrow(new EntityNotFoundException("Profile not found")).when(profileService).deleteProfileAndDetail(anyLong());

        ResponseEntity<Map<String, Object>> response = profileController.deleteProfileAndDetail(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Profile not found: Profile not found", response.getBody().get("Error"));
    }

    @Test
    void testDeleteProfileUserDeletionException() {
//doThrow(new UserDeletionException("Profile is referenced")).when(profileService).deleteProfileAndDetail(anyLong());

        ResponseEntity<Map<String, Object>> response = profileController.deleteProfileAndDetail(1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot delete profile. It may be referenced by another entity: Profile is referenced", response.getBody().get("Error"));
    }

    @Test
    void testFindProfileByIdSuccess() {
        Profile profile = new Profile();
        when(profileService.findById(anyLong())).thenReturn(Optional.of(profile));
        when(profileService.getRolesByProfileId(anyLong())).thenReturn(List.of(new Rol()));

        ResponseEntity<Map<String, Object>> response = profileController.findProfileById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("profile"));
        assertTrue(response.getBody().containsKey("roles"));
    }

    @Test
    void testFindProfileByIdNotFound() {
        when(profileService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = profileController.findProfileById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
