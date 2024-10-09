package org.grupouno.parking.it4.service;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.model.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);

    void delete(Long id);

    Page<User> getAllUsers(int page, int size, String email);

    void updateUser(UserDto userDto, Long idUser);

    void patchUser(UserDto userDto, Long idUser);

    void updatePassword(Long idUser, String pastPassword, String newPassword, String confirmPassword);

    void changePassword(Long idUser, String newPassword, String confirmPassword);

    void saveVerificationCode(User user, String code);

    boolean isVerificationCodeValid(User user, String code);

}