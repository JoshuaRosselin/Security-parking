package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository repository;
    @Override
    public List<String> findRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        return roles.stream()
                .map(Rol::getRole)
                .collect(Collectors.toList());
    }
    public List<GrantedAuthority> getRolesByProfileId(Long profileId) {
        List<Rol> roles = repository.findRolesByProfileId(profileId);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
}
