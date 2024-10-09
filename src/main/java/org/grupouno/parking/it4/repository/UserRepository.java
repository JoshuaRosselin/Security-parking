package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.dpi = :dpi")
    Optional<User> findByDPI(@Param("dpi") String dpi);

    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

}