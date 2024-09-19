package org.grupouno.parking.it4.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.grupouno.parking.it4.model.Rol;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.RoleRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el email: " + userEmail));

        Collection<GrantedAuthority> authorities = getAuthorities(user.getIdProfile().getProfileId());

        user.setAuthorities(authorities);

        return user;
    }

    private Collection<GrantedAuthority> getAuthorities(long profileId) {
        List<Rol> roles = roleRepository.findRolesByProfileId(profileId);

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
    

}
