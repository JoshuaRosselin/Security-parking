package org.grupouno.parking.it4.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.exceptions.SerializingRolException;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    ObjectMapper objectMapper;
    private RoleRepository repository;
    AudithService audithService;
    private static final String SUCCES = "Success";

    @Override
    public List<String> findRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        auditAction("Role", "Retrieved roles for profile ID: " + profileId, "GET", null, null, SUCCES);
        return roles.stream()
                .map(Rol::getRole)
                .toList();
    }

    @Override
    public List<GrantedAuthority> getRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        auditAction("Role", "Retrieved roles for profile ID: " + profileId, "GET", null, null, SUCCES);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public Rol saveRole(RoleDto role) {
        String roleValue = role.getRole().toUpperCase();
        Optional<Rol> existingRole = repository.findByRole(roleValue);
        if (existingRole.isPresent()) {
            logger.info("Rol {} ya existe en la base de datos, no se guardará.", roleValue);
            return null;
        }
        Rol rol = new Rol();
        if (!roleValue.contains("ROLE_")) {
            roleValue = "ROLE_" + roleValue;
        }
        rol.setRole(roleValue);
        rol.setDescription(role.getDescription());

        logger.info("Rol guardado {}", rol.getRole());
        return repository.save(rol);
    }

    @Override
    public void updateRol(RoleDto roleDto, Long idRol){
        if (!repository.existsById(idRol)) {
            throw  new EntityNotFoundException("This rol don't exist");
        }
        Optional<Rol> optionalRol = repository.findById(idRol);
        if (optionalRol.isEmpty()) {
            throw new EntityNotFoundException("This role doesn't exist");
        }

        Rol role = optionalRol.get();
        String previousRoleState = null;
        try {
            previousRoleState = objectMapper.writeValueAsString(role);
        } catch (JsonProcessingException e) {
            throw new SerializingRolException("Error serializing role to JSON", e);
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
                    objectMapper.readValue(previousRoleState, Map.class), SUCCES);
        } catch (JsonProcessingException e) {
            throw new SerializingRolException("Error serializing previous role state to JSON", e);
        }
    }

    @Override
    public void delete(Long idRole) {
        if (!repository.existsById(idRole)) {
            throw new IllegalArgumentException("This rol don't exist");
        }

        try {
            Rol roleToDelete = repository.findById(idRole).orElseThrow(() ->
                    new EntityNotFoundException("This role doesn't exist"));

            auditAction("Role", "Deleted a role", "DELETE", convertToMap(roleToDelete), null, SUCCES);

            repository.deleteById(idRole);
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting rol ", e);
        }
    }

    @Override
    public Optional<Rol> findRolById(Long idRole) {
        if (idRole == null ) {
            throw new IllegalArgumentException("Id is necessary");
        }
        Optional<Rol> roleOptional = repository.findById(idRole);
        if (roleOptional.isPresent()) {
            auditAction("Role", "Retrieved role with ID: " + idRole, "GET", null, convertToMap(roleOptional.get()), SUCCES);
        } else {
            auditAction("Role", "Failed to retrieve role with ID: " + idRole, "GET", null, null, "Not Found");
        }
        return roleOptional;
    }

    @Override
    public List<Rol> getAllRol() {
        List<Rol> roles = repository.findAll();
        auditAction("Role", "Retrieved all roles", "GET", null, null, SUCCES);
        return roles;
    }

    private Map<String, Object> convertToMap(Rol rol) {
        try {
            return objectMapper.convertValue(rol, Map.class);
        } catch (Exception e) {
            throw new SerializingRolException("Error converting Rol to Map", e);
        }
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        try {
            audithService.createAudit(entity, description, operation, request, response, result);
        } catch (Exception e) {
            logger.error("Error saving audit record: {}", e.getMessage(), e);
        }
    }
}
