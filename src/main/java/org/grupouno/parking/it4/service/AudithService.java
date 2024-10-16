package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.AudithDTO;
import org.grupouno.parking.it4.model.Audith;
import org.grupouno.parking.it4.repository.AudithRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AudithService {

    private final AudithRepository audithRepository;
    @Autowired
    public AudithService(AudithRepository audithRepository) {
        this.audithRepository = audithRepository;
    }

    public Audith createAudit(String entity, String description, String operation, Map<String, Object> request, Map<String, Object> response, String result) {
        Audith audit = new Audith();
        audit.setEntity(entity);
        audit.setStartDate(LocalDateTime.now());
        audit.setDescription(description);
        audit.setOperation(operation);
        audit.setRequest(request);
        audit.setResponse(response);
        audit.setResult(result);
        return audithRepository.save(audit);
    }

    public List<Audith> getAllAudits() {
        return audithRepository.findAll();
    }

    public Optional<Audith> getAuditById(long id) {
        return audithRepository.findById(id);
    }

    public List<Audith> getAuditsByEntity(String entity) {
        return audithRepository.findAll().stream()
                .filter(audit -> audit.getEntity().equalsIgnoreCase(entity))
                .toList();
    }

    public AudithDTO convertToDTO(Audith audit) {
        if (audit == null) {
            return null;
        }
        AudithDTO dto = new AudithDTO();
        dto.setAuditId(audit.getAuditId());
        dto.setEntity(audit.getEntity());
        dto.setStartDate(OffsetDateTime.from(audit.getStartDate()));
        dto.setDescription(audit.getDescription());
        dto.setOperation(audit.getOperation());
        dto.setResult(audit.getResult());
        dto.setRequest(audit.getRequest() != null ? audit.getRequest() : null);
        dto.setResponse(audit.getResponse() != null ? audit.getResponse() : null);
        return dto;
    }



}
