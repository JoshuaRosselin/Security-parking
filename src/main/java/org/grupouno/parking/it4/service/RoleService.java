package org.grupouno.parking.it4.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RoleService implements IRoleService {

    private final RoleRepository repository;
    private final AudithService audithService;
    private final ObjectMapper objectMapper;

    @Override
    public List<String> findRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        auditAction("Role", "Retrieved roles for profile ID: " + profileId, "GET", null, null, "Success");
        return roles.stream()
                .map(Rol::getRole)
                .toList();
    }

    @Override
    public List<GrantedAuthority> getRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        auditAction("Role", "Retrieved roles for profile ID: " + profileId, "GET", null, null, "Success");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public Rol saveRole(RoleDto roleDto) {
        Rol rol = new Rol();
        String roleValue = roleDto.getRole();

        if (!roleValue.startsWith("ROLE_")) {
            roleValue = "ROLE_" + roleValue;
        }
        rol.setRole(roleValue.toUpperCase());
        rol.setDescription(roleDto.getDescription());

        Rol savedRole = repository.save(rol);
        auditAction("Role", "Created a new role", "CREATE", convertToMap(savedRole), null, "Success");

        return savedRole;
    }

    @Override
    public void updateRol(RoleDto roleDto, Long idRol) {
        Optional<Rol> optionalRol = repository.findById(idRol);
        if (optionalRol.isEmpty()) {
            throw new EntityNotFoundException("This role doesn't exist");
        }

        Rol role = optionalRol.get();
        String previousRoleState = null;
        try {
            previousRoleState = objectMapper.writeValueAsString(role);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing role to JSON", e);
        }

        if (roleDto.getRole() != null) {
            role.setRole(roleDto.getRole());
        }
        if (roleDto.getDescription() != null) {
            role.setDescription(roleDto.getDescription());
        }
        Rol updatedRole = repository.save(role);

        try {
            auditAction("Role", "Updated role information", "UPDATE", convertToMap(updatedRole),
                    objectMapper.readValue(previousRoleState, Map.class), "Success");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing previous role state to JSON", e);
        }
    }

    @Override
    public void delete(Long idRole) {
        if (!repository.existsById(idRole)) {
            throw new IllegalArgumentException("This role doesn't exist");
        }

        try {
            Rol roleToDelete = repository.findById(idRole).orElseThrow(() ->
                    new EntityNotFoundException("This role doesn't exist"));

            auditAction("Role", "Deleted a role", "DELETE", convertToMap(roleToDelete), null, "Success");

            repository.deleteById(idRole);
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting role", e);
        }
    }

    @Override
    public Optional<Rol> findRolById(Long idRole) {
        if (idRole == null) {
            throw new IllegalArgumentException("Id is necessary");
        }
        Optional<Rol> roleOptional = repository.findById(idRole);
        if (roleOptional.isPresent()) {
            auditAction("Role", "Retrieved role with ID: " + idRole, "GET", null, convertToMap(roleOptional.get()), "Success");
        } else {
            auditAction("Role", "Failed to retrieve role with ID: " + idRole, "GET", null, null, "Not Found");
        }
        return roleOptional;
    }

    @Override
    public List<Rol> getAllRol() {
        List<Rol> roles = repository.findAll();
        auditAction("Role", "Retrieved all roles", "GET", null, null, "Success");
        return roles;
    }

    private Map<String, Object> convertToMap(Rol rol) {
        try {
            return objectMapper.convertValue(rol, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Rol to Map", e);
        }
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        try {
            audithService.createAudit(entity, description, operation, request, response, result);
        } catch (Exception e) {
            System.err.println("Error saving audit record: " + e.getMessage());
        }
    }
}
