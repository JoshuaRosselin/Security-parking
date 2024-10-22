package org.grupouno.parking.it4.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AudithService audithService;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("No se encontró el email: " + userEmail));
            Collection<GrantedAuthority> authorities = getAuthorities(user.getIdProfile().getProfileId());
            user.setAuthorities(authorities);
            auditAction("User", "Successfully loaded user by email: " + userEmail, "LOAD_USER", null, null, "Success");

            return user;
        } catch (UsernameNotFoundException e) {
            auditAction("User", "Failed to load user by email: " + userEmail, "LOAD_USER", null, null, "Failure");
            throw e;
        }
    }

    public Collection<GrantedAuthority> getAuthorities(long profileId) {
        List<Rol> roles = roleRepository.findRolesByProfileId(profileId);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    private void auditAction(String entity, String description, String operation, Map<String, Object> request, Map<String, Object> response, String result) {
        try {
            audithService.createAudit(entity, description, operation, request, response, result);
        } catch (Exception e) {
            logger.error("Error saving audit record: {}", e.getMessage());
        }
    }
}
