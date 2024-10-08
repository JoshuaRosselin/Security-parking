package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findById(Long profileId);
    Optional<Profile> findByDescription(String description);
    Page<Profile> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
}