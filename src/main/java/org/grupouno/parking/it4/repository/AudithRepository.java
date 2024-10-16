package org.grupouno.parking.it4.repository;

import org.grupouno.parking.it4.model.Audith;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AudithRepository extends JpaRepository<Audith, Long> {

    List<Audith> findByEntity(String entity);

    List<Audith> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<Audith> findById(Long id);

    List<Audith> findByOperation(String operation);
}
