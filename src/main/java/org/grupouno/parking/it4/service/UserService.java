package org.grupouno.parking.it4.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.exceptions.ConverterMapException;
import org.grupouno.parking.it4.exceptions.DpiException;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.exceptions.UserNotFoundException;
import org.grupouno.parking.it4.model.Profile;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.ProfileRepository;
import org.grupouno.parking.it4.repository.UserRepository;
import org.grupouno.parking.it4.utils.Validations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.Map;
import java.util.Optional;


@AllArgsConstructor
@Service
public class UserService implements IUserService {
    final UserRepository userRepository;

    private final AudithService audithService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private final MailService mailService;

    private static final String USER_WITH = "User with id ";
    private static final String DONT_EXIST = "Don't exist";
    private static final String UPDATE = "Update";
    private static final String USERID = "userId";
    private static final String SUCCESS = "Success";
    private static final String PROFILENOTF = "Profile not found";
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        String responseMessage = user.map(User::toString).orElse("Not Found");

        auditAction("User", "Fetching user by Email", "Read",
                Map.of("email", email),
                Map.of("user", responseMessage),
                user.isPresent() ? SUCCESS : "Not Found");
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            logger.error("Id null");
            throw new IllegalArgumentException("Id is necessary");
        }

        Optional<User> optionalUser = userRepository.findById(id);

        // Verificar si el usuario existe
        if (optionalUser.isEmpty()) {
            logger.warn("User with ID: {} not found", id);
            throw new UserNotFoundException("User with ID " + id + " not found");
        }

        User user = optionalUser.get(); // Obtener el usuario ya que sabemos que existe
        String responseMessage = user.toString();

        auditAction("User", "Fetching user by ID", "Read",
                Map.of("id", id),
                Map.of("user", responseMessage),
                SUCCESS);

        return optionalUser;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Page<User> getAllUsers(int page, int size, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("email")));

        if (email != null && !email.isEmpty()) {
            return userRepository.findByEmailContainingIgnoreCase(email, pageable);
        }
        Page<User> users = userRepository.findAll(pageable);


        // Registro de auditoría
        auditAction("User", "Fetching all users", "Read",
                Map.of(),
                Map.of("usersCount", users.getTotalElements()),
                SUCCESS);
        return users;
    }

    @Override
    public void delete(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            logger.error("User not exist id: {}", idUser);
            throw new IllegalArgumentException(USER_WITH + idUser + DONT_EXIST);
        }
        try {
            userRepository.deleteById(idUser);
            auditAction("User", "Deleting user", "Delete",
                    Map.of(USERID, idUser),
                    null,
                    SUCCESS);
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting user with ID " + idUser, e);
        }
    }

    @Override
    public void updateUser(UserDto userDto, Long idUser) {
        validateUserDto(userDto, idUser);
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
                        .orElseThrow(() -> new IllegalArgumentException(PROFILENOTF));
                user.setIdProfile(profile);
            }
            logger.info("User {} updated",  userDto.getName());
            userRepository.save(user);
            // Registro de auditoría
            auditAction("User", "Updating user", UPDATE,
                    Map.of(USERID, idUser, "userUpdates", userDto),
                    convertToMap(user),
                    SUCCESS);
        }
    }

    public void validateUserDto(UserDto userDto, Long idUser) {
        if (userDto.getDpi() == null || userDto.getDpi().length() > 13) {
            throw new DpiException("DPI can not exceed 13 digits");
        }
        if (!userRepository.existsById(idUser)) {
            logger.error("User not exist id: {}", idUser);
            throw new EntityNotFoundException(USER_WITH + idUser + DONT_EXIST);
        }
    }



    @Override
    public void patchUser(UserDto userDto, Long idUser) {
        if (userDto.getDpi() == null || userDto.getDpi().length() > 13) {
            throw new IllegalArgumentException("DPI must not exceed 13 digits");
        }
        if (!userRepository.existsById(idUser)) {
            logger.error("User not exist with id" );
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
                    .orElseThrow(() -> new IllegalArgumentException(PROFILENOTF));
            user.setIdProfile(profile);
        }
        logger.info("User {} updated", userDto.getName() );
        userRepository.save(user);
        auditAction("User", "Patching user", UPDATE,
                Map.of(USERID, idUser, "userUpdates", userDto),
                convertToMap(user),
                SUCCESS);
    }

    @Override
    public void updatePassword(Long idUser, String pastPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(pastPassword, user.getPassword())) {
            logger.error("Password incorrect" );
            throw new IllegalArgumentException("Password incorrect");
        }
        if (!newPassword.equals(confirmPassword)) {
            logger.error("The password not matched" );
            throw new IllegalArgumentException("The new password and confirm password do not match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Registro de auditoría
        auditAction("User", "update password", UPDATE,
                Map.of(USERID, idUser, "paswordUpdate", pastPassword),
                convertToMap(user),
                SUCCESS);
    }

    @Override
    public void changePassword(Long idUser, String newPassword, String confirmPassword) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!newPassword.equals(confirmPassword)) {
            logger.error("The password not matched" );
            throw new IllegalArgumentException("The new password and confirm password do not match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Registro de auditoría
        auditAction("User", "Change password", UPDATE,
                Map.of("UserId", idUser, "Update Password", newPassword),
                convertToMap(user),
                SUCCESS);

    }

    @Override
    public void saveVerificationCode(User user, String code) {
        verificationCodeService.saveVerificationCode(user.getEmail(), code);
        logger.info("the code is {}", code );
    }

    @Override
    public boolean isVerificationCodeValid(User user, String code) {
        return  verificationCodeService.isVerificationCodeValid(user.getEmail(), code);
    }


    public User signup(UserDto input) {
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
        Profile profile = profileRepository.findById(input.getProfileId())
                .orElseThrow(() -> new IllegalArgumentException(PROFILENOTF));
        user.setIdProfile(profile);
        mailService.sendPasswordAndUser(input.getEmail(), passwordUser);
        return userRepository.save(user);

    }




    private Map<String, Object> convertToMap(User user) {
        try {
            return objectMapper.convertValue(user, Map.class);
        } catch (Exception e) {
            throw new ConverterMapException("Error converting User to Map", e);
        }
    }

    private void auditAction(String entity, String description, String operation, Map<String, Object> request, Map<String, Object> response, String result) {
        audithService.createAudit(entity, description, operation, request, response, result);
    }

}