package org.grupouno.parking.it4.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.RoleDto;
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
import java.util.Optional;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class RoleService implements IRoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private RoleRepository repository;

    @Override
    public List<String> findRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        return roles.stream()
                .map(Rol::getRole)
                .toList();
    }

    @Override
    public List<GrantedAuthority> getRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public Rol saveRole(RoleDto role){
        Rol rol = new Rol();
        String roleValue = role.getRole();
        if (roleValue.contains("ROLE_")) {
            rol.setRole(role.getRole().toUpperCase());
            rol.setDescription(role.getDescription());
        } else {
            roleValue = "ROLE_" + roleValue;
            rol.setRole(roleValue);
            rol.setDescription(role.getDescription());
        }
        logger.info("Rol saved {}", rol.getRole() );
        return repository.save(rol);
    }

    @Override
    public void updateRol(RoleDto roleDto, Long idRol){
        if (!repository.existsById(idRol)) {
            logger.error("Rol not exist");
            throw  new EntityNotFoundException("This rol don't exist");
        }
        Optional<Rol> optionalRol = repository.findById(idRol);
        if (optionalRol.isPresent()) {
            Rol role = optionalRol.get();
            if (roleDto.getRole() != null) role.setRole(roleDto.getRole());
            if (roleDto.getDescription() != null) role.setDescription(roleDto.getDescription());
            repository.save(role);
        }
    }

    @Override
    public void delete(Long idRole) {
        if (!repository.existsById(idRole)) {
            logger.warn("Role with id {} not exist", idRole);
            throw new IllegalArgumentException("This rol don't exist");
        }
        try {
            repository.deleteById(idRole);
        } catch (DataAccessException e) {
            logger.error("Error deleting with id {}", idRole);
            throw new UserDeletionException("Error deleting rol ", e);
        }
    }

    @Override
    public Optional<Rol> findRolById(Long idRole) {
        if (idRole == null ) {
            logger.warn("Id is necessary ");
            throw new IllegalArgumentException("Id is necessary");
        }
        return repository.findById(idRole);
    }

    @Override
    public List<Rol> getAllRol(){
        return repository.findAll();

    }


}
