package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.AudithDTO;
import org.grupouno.parking.it4.model.Audith;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IAudithService {
    void recordAudit(String entity, String description, String operation, Map<String, Object> request, Map<String, Object> response, String result);

    List<AudithDTO> getAllAudits();

    Optional<AudithDTO> getAuditById(long id);

    AudithDTO convertToDTO(Audith audit);
}
