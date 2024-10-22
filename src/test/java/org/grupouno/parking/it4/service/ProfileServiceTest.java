package org.grupouno.parking.it4.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.grupouno.parking.it4.dto.ProfileDto;
import org.grupouno.parking.it4.exceptions.RoleExistingException;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private DetailRoleProfileRepository detailRoleProfileRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AudithService audithService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRolesByProfileId() {
        // Arrange
        Long profileId = 1L;
        when(detailRoleProfileRepository.findRolesByProfileId(profileId)).thenReturn(List.of(new Rol()));

        // Act
        List<Rol> result = profileService.getRolesByProfileId(profileId);

        // Assert
        assertNotNull(result);
        verify(detailRoleProfileRepository, times(1)).findRolesByProfileId(profileId);
    }

    @Test
    void testGetAllProfilesWithDescription() {
        // Arrange
        int page = 0;
        int size = 10;
        String description = "test";
        Page<Profile> mockPage = mock(Page.class);
        when(profileRepository.findByDescriptionContainingIgnoreCase(description, PageRequest.of(page, size))).thenReturn(mockPage);

        // Act
        Page<Profile> result = profileService.getAllProfiles(page, size, description);

        // Assert
        assertNotNull(result);
        verify(profileRepository, times(1)).findByDescriptionContainingIgnoreCase(description, PageRequest.of(page, size));
    }

    @Test
    void testGetAllProfilesNoDescription() {
        // Arrange
        int page = 0;
        int size = 10;
        Page<Profile> mockPage = mock(Page.class);
        when(profileRepository.findAll(PageRequest.of(page, size))).thenReturn(mockPage);

        // Act
        Page<Profile> result = profileService.getAllProfiles(page, size, null);

        // Assert
        assertNotNull(result);
        verify(profileRepository, times(1)).findAll(PageRequest.of(page, size));
    }

    @Test
    void testGetAllProfilesForUser() {
        // Arrange
        List<Profile> mockProfiles = List.of(new Profile());
        when(profileRepository.findAll()).thenReturn(mockProfiles);

        // Act
        List<Profile> result = profileService.getAllProfilesForUser();

        // Assert
        assertNotNull(result);
        assertEquals(mockProfiles.size(), result.size());
        verify(profileRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExists() {
        // Arrange
        Long profileId = 1L;
        Profile profile = new Profile();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        Optional<Profile> result = profileService.findById(profileId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }

    @Test
    void testFindByIdNotExists() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            profileService.findById(profileId);
        });
        assertEquals("Profile ID 1 does not exist", thrown.getMessage());
    }

    @Test
    void testSaveProfile() {
        // Arrange
        Profile profile = new Profile();
        when(profileRepository.save(profile)).thenReturn(profile);

        // Act
        Profile result = profileService.saveProfile(profile);

        // Assert
        assertNotNull(result);
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testSaveProfileNull() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            profileService.saveProfile(null);
        });
        assertEquals("Profile cannot be null", thrown.getMessage());
    }

    @Test
    void testSaveProfileWithRoles() {
        // Arrange
        Profile profile = new Profile();
        profile.setDescription("New Profile");
        List<Long> roleIds = List.of(1L, 2L);
        when(profileRepository.findByDescription(profile.getDescription())).thenReturn(Optional.empty());
        when(profileRepository.save(profile)).thenReturn(profile);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Rol()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(new Rol()));

        // Act
        Profile result = profileService.saveProfileWithRoles(profile, roleIds);

        // Assert
        assertNotNull(result);
        verify(profileRepository, times(1)).save(profile);
        verify(detailRoleProfileRepository, times(2)).save(any(DetailRoleProfile.class));
    }

    @Test
    void testSaveProfileWithRolesProfileAlreadyExists() {
        // Arrange
        Profile profile = new Profile();
        profile.setDescription("Existing Profile");
        when(profileRepository.findByDescription(profile.getDescription())).thenReturn(Optional.of(profile));

        // Act & Assert
        RoleExistingException thrown = assertThrows(RoleExistingException.class, () -> {
            profileService.saveProfileWithRoles(profile, List.of(1L));
        });
        assertEquals("Profile already exists with the name: Existing Profile", thrown.getMessage());
    }

    @Test
    void testUpdateProfile() {
        // Arrange
        Long profileId = 1L;
        ProfileDto profileDto = new ProfileDto();
        profileDto.setDescription("Updated Description");

        Profile profile = new Profile();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        profileService.updateProfile(profileDto, profileId);

        // Assert
        assertEquals("Updated Description", profile.getDescription());
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testUpdateProfileNotFound() {
        // Arrange
        Long profileId = 1L;
        ProfileDto profileDto = new ProfileDto();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            profileService.updateProfile(profileDto, profileId);
        });
        assertEquals("Profile ID 1 does not exist", thrown.getMessage());
    }

    @Test
    void testUpdateProfileRoles() {
        // Arrange
        Long profileId = 1L;
        List<Long> newRoleIds = List.of(1L, 2L);
        Profile profile = new Profile();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Rol()));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(new Rol()));

        // Act
        Profile result = profileService.updateProfileRoles(profileId, newRoleIds);

        // Assert
        assertNotNull(result);
        verify(detailRoleProfileRepository, times(2)).save(any(DetailRoleProfile.class));
    }

    @Test
    void testUpdateProfileRolesProfileNotFound() {
        // Arrange
        Long profileId = 1L;
        List<Long> newRoleIds = List.of(1L, 2L);
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            profileService.updateProfileRoles(profileId, newRoleIds);
        });
        assertEquals("Profile not found with id 1", thrown.getMessage());
    }

    @Test
    void testPatchProfile() {
        // Arrange
        Long profileId = 1L;
        ProfileDto profileDto = new ProfileDto();
        profileDto.setDescription("Patched Description");

        Profile profile = new Profile();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Act
        profileService.patchProfile(profileDto, profileId);

        // Assert
        assertEquals("Patched Description", profile.getDescription());
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testPatchProfileNotFound() {
        // Arrange
        Long profileId = 1L;
        ProfileDto profileDto = new ProfileDto();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            profileService.patchProfile(profileDto, profileId);
        });
        assertEquals("Profile with ID 1 does not exist", thrown.getMessage());
    }

    @Test
    void testDeleteProfile() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(true);

        // Act
        profileService.deleteProfile(profileId);

        // Assert
        verify(profileRepository, times(1)).deleteById(profileId);
    }

    @Test
    void testDeleteProfileNotFound() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            profileService.deleteProfile(profileId);
        });
        assertEquals("Profile with ID 1 does not exist", thrown.getMessage());
    }

    @Test
    void testDeleteProfileThrowsDataAccessException() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.existsById(profileId)).thenReturn(true);
        doThrow(new DataAccessException("Database error") {}).when(profileRepository).deleteById(profileId);

        // Act & Assert
        UserDeletionException thrown = assertThrows(UserDeletionException.class, () -> {
            profileService.deleteProfile(profileId);
        });
        assertEquals("Error deleting profile", thrown.getMessage());
    }

    @Test
    void testDeleteProfileAndDetail() {
        // Arrange
        Long profileId = 1L;
        Profile profile = new Profile();
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(detailRoleProfileRepository.findAllByIdIdProfile(profileId)).thenReturn(List.of(new DetailRoleProfile()));

        // Act
        profileService.deleteProfileAndDetail(profileId);

        // Assert
        verify(detailRoleProfileRepository, times(1)).deleteByProfile(profile);
        verify(profileRepository, times(1)).deleteById(profileId);
    }

    @Test
    void testDeleteProfileAndDetailProfileNotFound() {
        // Arrange
        Long profileId = 1L;
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            profileService.deleteProfileAndDetail(profileId);
        });
        assertEquals("Profile not found", thrown.getMessage());
    }


}
