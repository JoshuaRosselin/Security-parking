package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.exceptions.CustomDataAccessException;
import org.grupouno.parking.it4.exceptions.CustomIllegalArgumentException;
import org.grupouno.parking.it4.model.DetailRoleProfile;
import org.grupouno.parking.it4.model.DetailDTO;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.DetailRoleProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DetailRoleProfileServiceTest {

    @Mock
    private DetailRoleProfileRepository repository;

    @Mock
    private AudithService audithService;

    @InjectMocks
    private DetailRoleProfileService service;

    private DetailRoleProfile detailRoleProfile;
    private Profile profile;
    private Rol role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profile = new Profile();
        profile.setProfileId(1L);
        role = new Rol();
        role.setId(1L);
        detailRoleProfile = new DetailRoleProfile();
        detailRoleProfile.setProfile(profile);
        detailRoleProfile.setRole(role);
    }

    @Test
    void testSaveDetailRoleProfileSuccess() {
        when(repository.save(any(DetailRoleProfile.class))).thenReturn(detailRoleProfile);

        DetailRoleProfile result = service.saveDetailRoleProfile(detailRoleProfile);

        assertNotNull(result);
        assertEquals(detailRoleProfile, result);
        verify(repository, times(1)).save(detailRoleProfile);
    }

    @Test
    void testSaveDetailRoleProfileDataAccessException() {
        when(repository.save(any(DetailRoleProfile.class))).thenThrow(new DataAccessException("...") {});

        CustomDataAccessException exception = assertThrows(CustomDataAccessException.class, () -> {
            service.saveDetailRoleProfile(detailRoleProfile);
        });

        assertEquals("Error al guardar el detalle del rol y perfil", exception.getMessage());
        verify(repository, times(1)).save(detailRoleProfile);
    }

    @Test
    void testSaveDetailRoleProfileIllegalArgumentException() {
        when(repository.save(any(DetailRoleProfile.class))).thenThrow(new IllegalArgumentException());

        CustomIllegalArgumentException exception = assertThrows(CustomIllegalArgumentException.class, () -> {
            service.saveDetailRoleProfile(detailRoleProfile);
        });

        assertEquals("Argumento inválido al guardar el detalle del rol y perfil", exception.getMessage());
        verify(repository, times(1)).save(detailRoleProfile);
    }

    @Test
    void testGetDetailRoleProfileById() {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(1L);
        id.setIdRole(1L);

        when(repository.findById(id)).thenReturn(Optional.of(detailRoleProfile));

        Optional<DetailRoleProfile> result = service.getDetailRoleProfileById(profile, role);

        assertTrue(result.isPresent());
        assertEquals(detailRoleProfile, result.get());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testGetAllDetailRoleProfiles() {
        when(repository.findAll()).thenReturn(List.of(detailRoleProfile));

        List<DetailRoleProfile> result = service.getAllDetailRoleProfiles();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testDeleteDetailRoleProfile() {
        DetailDTO id = new DetailDTO();
        id.setIdProfile(1L);
        id.setIdRole(1L);

        doNothing().when(repository).deleteById(id);

        service.deleteDetailRoleProfile(profile, role);

        verify(repository, times(1)).deleteById(id);
        verify(audithService, times(1)).createAudit(anyString(), anyString(), anyString(), anyMap(), anyMap(), anyString());
    }

    @Test
    void testGetRolesByProfileId() {
        when(repository.findByProfile_ProfileId(1L)).thenReturn(List.of(detailRoleProfile));

        List<Rol> result = service.getRolesByProfileId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0));
        verify(repository, times(1)).findByProfile_ProfileId(1L);
    }

    @Test
    void testGetProfilesByRoleId() {
        when(repository.findByRole_Id(1L)).thenReturn(List.of(detailRoleProfile));

        List<Profile> result = service.getProfilesByRoleId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(profile, result.get(0));
        verify(repository, times(1)).findByRole_Id(1L);
    }

    @Test
    void testDeleteRolesFromProfile() {
        when(repository.findByProfile_ProfileId(1L)).thenReturn(List.of(detailRoleProfile));

        service.deleteRolesFromProfile(1L);

        verify(repository, times(1)).delete(detailRoleProfile);
    }
}
