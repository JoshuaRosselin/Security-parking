package org.grupouno.parking.it4.security;

import org.grupouno.parking.it4.dto.LoginUserDto;
import org.grupouno.parking.it4.dto.RegisterUserDto;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.grupouno.parking.it4.service.MailService;
import org.grupouno.parking.it4.service.RoleService;
import org.grupouno.parking.it4.utils.Validations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final ProfileRepository profileRepository;
    private Validations validations = new Validations();
    private final MailService mailService;


    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleService roleService,
            ProfileRepository profileRepository,
            MailService mailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.profileRepository = profileRepository;
        this.mailService = mailService;
    }

    public User signup(RegisterUserDto input) {
        if (input.getEmail() == null || !input.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email is not valid");
        }
        if (input.getDpi() == null || input.getDpi().length() > 13) {
            throw new IllegalArgumentException("DPI must not exceed 13 digits");
        }
        if(!validations.isValidDpi(input.getDpi())){
            throw new IllegalArgumentException("DPI IS NOT VALID");
        }
        String passwordUser = validations.generatePassword();
        Boolean isValid = validations.isValidPassword(passwordUser);
        if (Boolean.FALSE.equals(isValid)) {
            throw new IllegalArgumentException("The password is invalid");
        }
        Boolean isNotRepeatData = userRepository.findByEmail(input.getEmail()).isPresent();
        Boolean isNotRepeatDPI = userRepository.findByDPI(input.getDpi()).isPresent();
        if (Boolean.TRUE.equals(isNotRepeatData) || Boolean.TRUE.equals(isNotRepeatDPI)) {
            throw new IllegalArgumentException("You have already a account with this DPI or Email");
        }
        User user = new User();
        user.setName(input.getName());
        user.setSurname(input.getSurname());
        user.setAge(input.getAge());
        user.setDpi(input.getDpi());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(passwordUser));
        user.setStatus(true);
        Profile profile = profileRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        user.setIdProfile(profile);
        mailService.sendPasswordAndUser(input.getEmail(), passwordUser);
        return userRepository.save(user);

    }

    public User authenticate(LoginUserDto input) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();
        Profile profile = user.getIdProfile();

        List<GrantedAuthority> authorities;
        if (profile != null) {
            Long profileId = profile.getProfileId();
            authorities = roleService.getRolesByProfileId(profileId);
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        user.setAuthorities(authorities);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return user;
    }

}