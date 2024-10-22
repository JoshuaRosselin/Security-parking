package org.grupouno.parking.it4.service;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AudithService audithService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private static final String USER_EMAIL = "user@example.com";
    private static final long PROFILE_ID = 1L;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail(USER_EMAIL);
        user.setIdProfile(new Profile());  // Asegúrate de tener la clase Profile
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(roleRepository.findRolesByProfileId(PROFILE_ID)).thenReturn(List.of(new Rol()));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(USER_EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(USER_EMAIL, result.getUsername());
        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(audithService, times(1)).createAudit(anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        // Arrange
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(USER_EMAIL);
        });
        assertEquals("No se encontró el email: " + USER_EMAIL, thrown.getMessage());

        verify(audithService, times(1)).createAudit(anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void testGetAuthorities() {
        // Arrange
        when(roleRepository.findRolesByProfileId(PROFILE_ID)).thenReturn(List.of(new Rol(), new Rol()));

        // Act
        Collection<GrantedAuthority> authorities = userDetailsService.getAuthorities(PROFILE_ID);

        // Assert
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

}
