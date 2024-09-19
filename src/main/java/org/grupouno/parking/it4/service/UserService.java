package org.grupouno.parking.it4.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.grupouno.parking.it4.dto.UserDto;
import org.grupouno.parking.it4.exceptions.UserDeletionException;
import org.grupouno.parking.it4.model.User;
import org.grupouno.parking.it4.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.Optional;
@AllArgsConstructor
@Service
public class UserService implements IUserService {
    final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String USER_WITH = "User with id ";
    private static final String DONT_EXIST = "Don't exist";

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null ) {
            throw new IllegalArgumentException("Id could not be null");
        }
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("email")));
        return userRepository.findAll(pageable);
    }

    @Override
    public void delete(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new IllegalArgumentException(USER_WITH + idUser + DONT_EXIST);
        }
        try {
            userRepository.deleteById(idUser);
        } catch (DataAccessException e) {
            throw new UserDeletionException("Error deleting user with ID " + idUser, e);
        }
    }

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
            userRepository.save(user);
        }
    }

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
        userRepository.save(user);
    }

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
    }

}
