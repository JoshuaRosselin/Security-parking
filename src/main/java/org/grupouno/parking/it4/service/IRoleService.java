package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.RoleDto;
import org.grupouno.parking.it4.model.Rol;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

     List<String> findRolesByProfileId(Long profileId);

     List<GrantedAuthority> getRolesByProfileId(Long profileId);

     Rol saveRole(RoleDto role);

     void updateRol(RoleDto roleDto, Long idRol);

     void delete(Long idRole);

     Optional<Rol> findRolById(Long idRole);

     List<Rol> getAllRol();
}