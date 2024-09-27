package org.grupouno.parking.it4.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.Optional;


@AllArgsConstructor
@Service
public class UserService implements IUserService {
    final UserRepository userRepository;

    private final AudithService audithService; // Inyección del servicio de auditoría
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper; // Para convertir a JSON

    private static final String USER_WITH = "User with id ";
    private static final String DONT_EXIST = "Don't exist";

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        String responseMessage = user.map(User::toString).orElse("Not Found");

        auditAction("User", "Fetching user by Email", "Read",
                Map.of("email", email),
                Map.of("user", responseMessage),
                user.isPresent() ? "Success" : "Not Found");
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null ) {
            throw new IllegalArgumentException("Id is necessary");
        }

        Optional<User> user = userRepository.findById(id);
        String responseMessage = user.map(User::toString).orElse("Not Found");

        auditAction("User", "Fetching user by ID", "Read",
                Map.of("id", id),
                Map.of("user", responseMessage),
                user.isPresent() ? "Success" : "Not Found");
        return user;


    }

    @Override
    public User save(User user) {
        User saveUser = userRepository.save(user);

        // Registro de auditoría
        auditAction("User", "Saving user", "Create",
                convertToMap(user),
                convertToMap(saveUser),
                "Success");
        return saveUser;


    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("email")));
        Page<User> users = userRepository.findAll(pageable);

        // Registro de auditoría
        auditAction("User", "Fetching all users", "Read",
                Map.of(),
                Map.of("usersCount", users.getTotalElements()),
                "Success");
        return users;
    }

    @Override
    public void delete(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new IllegalArgumentException(USER_WITH + idUser + DONT_EXIST);
        }
        try {
            userRepository.deleteById(idUser);
            auditAction("User", "Deleting user", "Delete",
                    Map.of("userId", idUser),
                    null,
                    "Success");
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting user with ID " + idUser, e);
        }
    }

    @Override
    public void updateUser(UserDto userDto, Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw  new EntityNotFoundException (USER_WITH + idUser + DONT_EXIST);
        }
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (userDto.getName() != null) user.setName(userDto.getName());
            if (userDto.getSurname() != null) user.setSurname(userDto.getSurname());
            if (userDto.getAge() > 0) user.setAge(userDto.getAge());
            if (userDto.getDpi() != null) user.setDpi(userDto.getDpi());
            if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
            if(userDto.getProfileId() > 0 ){
                Profile profile = profileRepository.findById(userDto.getProfileId())
                        .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
                user.setIdProfile(profile);
            }
            userRepository.save(user);
            // Registro de auditoría
            auditAction("User", "Updating user", "Update",
                    Map.of("userId", idUser, "userUpdates", userDto),
                    convertToMap(user),
                    "Success");
        }
    }

    @Override
    public void patchUser(UserDto userDto, Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new EntityNotFoundException(USER_WITH + idUser + DONT_EXIST);
        }
        User user = userRepository.findById(idUser).orElseThrow(() ->
                new EntityNotFoundException(USER_WITH + idUser + DONT_EXIST));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getSurname() != null) {
            user.setSurname(userDto.getSurname());
        }
        if (userDto.getAge() > 0 ) {
            user.setAge(userDto.getAge());
        }
        if (userDto.getDpi() != null) {
            user.setDpi(userDto.getDpi());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if(!userDto.isStatus()){
            user.setStatus(userDto.isStatus());
        }
        if(userDto.isStatus() ){
            user.setStatus(userDto.isStatus());
        }
        if(userDto.getProfileId() > 0 ){
            Profile profile = profileRepository.findById(userDto.getProfileId())
                    .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
            user.setIdProfile(profile);
        }
        userRepository.save(user);

        // Registro de auditoría
        auditAction("User", "Patching user", "Update",
                Map.of("userId", idUser, "userUpdates", userDto),
                convertToMap(user),
                "Success");
    }

    @Override
    public void updatePassword(Long idUser, String pastPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(pastPassword, user.getPassword())) {
            throw new IllegalArgumentException("Password incorrect");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("The new password and confirm password do not match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Registro de auditoría
        auditAction("User", "update password", "Update",
                Map.of("userId", idUser, "paswordUpdate", pastPassword),
                convertToMap(user),
                "Success");
    }

    @Override
    public void changePassword(Long idUser, String newPassword) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Registro de auditoría
        auditAction("User", "Change password", "Update",
                Map.of("UserId", idUser, "Update Password", newPassword),
                convertToMap(user),
                "Success");

    }

    @Override
    public void saveVerificationCode(User user, String code) {
        verificationCodeService.saveVerificationCode(user.getEmail(), code);
    }

    @Override
    public boolean isVerificationCodeValid(User user, String code) {
        return  verificationCodeService.isVerificationCodeValid(user.getEmail(), code);
    }

    private Map<String, Object> convertToMap(User user) {
        try {
            return objectMapper.convertValue(user, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting User to Map", e);
        }
    }

    private void auditAction(String entity, String description, String operation,
                             Map<String, Object> request, Map<String, Object> response, String result) {
        // Llamar al método de auditoría
        audithService.createAudit(entity, description, operation, request, response, result);
    }

}