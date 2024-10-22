package org.grupouno.parking.it4.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.grupouno.parking.it4.dto.AudithDTO;
import org.grupouno.parking.it4.model.Audith;
import org.grupouno.parking.it4.service.AudithService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class AudithControllerTest {

    @InjectMocks
    private AudithController audithController;

    @Mock
    private AudithService audithService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAudits_Success() {
        Audith audit = new Audith();
        audit.setAuditId(1L);
        audit.setEntity("TestEntity");
        audit.setDescription("Test description");
        audit.setStartDate(LocalDateTime.now());
        audit.setOperation("CREATE");
        audit.setResult("SUCCESS");
        audit.setRequest(Collections.singletonMap("key", "value"));
        audit.setResponse(Collections.singletonMap("key", "value"));

        when(audithService.getAllAudits()).thenReturn(List.of(audit));

        ResponseEntity<List<AudithDTO>> response = audithController.getAllAudits();

        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().size() == 1;
        assert "TestEntity".equals(response.getBody().get(0).getEntity());
    }

    @Test
    void testGetAuditById_Success() {
        Audith audit = new Audith();
        audit.setAuditId(1L);
        audit.setEntity("TestEntity");
        audit.setDescription("Test description");
        audit.setStartDate(LocalDateTime.now());
        audit.setOperation("CREATE");
        audit.setResult("SUCCESS");
        audit.setRequest(Collections.singletonMap("key", "value"));
        audit.setResponse(Collections.singletonMap("key", "value"));

        when(audithService.getAuditById(anyLong())).thenReturn(Optional.of(audit));

        ResponseEntity<AudithDTO> response = audithController.getAuditById(1L);

        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert "TestEntity".equals(response.getBody().getEntity());
    }

    @Test
    void testGetAuditById_NotFound() {
        when(audithService.getAuditById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<AudithDTO> response = audithController.getAuditById(1L);

        assert response.getStatusCode().is4xxClientError();
    }

    @Test
    void testGetAuditsByEntity_Success() {
        Audith audit = new Audith();
        audit.setAuditId(1L);
        audit.setEntity("TestEntity");
        audit.setDescription("Test description");
        audit.setStartDate(LocalDateTime.now());
        audit.setOperation("CREATE");
        audit.setResult("SUCCESS");
        audit.setRequest(Collections.singletonMap("key", "value"));
        audit.setResponse(Collections.singletonMap("key", "value"));

        when(audithService.getAuditsByEntity(anyString())).thenReturn(List.of(audit));

        ResponseEntity<List<AudithDTO>> response = audithController.getAuditsByEntity("TestEntity");

        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().size() == 1;
    }

    @Test
    void testCreateManualAudit_Success() {
        Audith auditRequest = new Audith();
        auditRequest.setEntity("TestEntity");
        auditRequest.setDescription("Test description");
        auditRequest.setOperation("CREATE");
        auditRequest.setRequest(Collections.singletonMap("key", "value"));
        auditRequest.setResponse(Collections.singletonMap("key", "value"));
        auditRequest.setResult("SUCCESS");

        ResponseEntity<String> response = audithController.createManualAudit(auditRequest);

        assert response.getStatusCode().is2xxSuccessful();
        assert "Auditoría creada exitosamente".equals(response.getBody());
    }

    @Test
    void testCreateManualAudit_Error() {
        Audith auditRequest = new Audith();
        auditRequest.setEntity("TestEntity");
        auditRequest.setDescription("Test description");
        auditRequest.setOperation("CREATE");
        auditRequest.setRequest(Collections.singletonMap("key", "value"));
        auditRequest.setResponse(Collections.singletonMap("key", "value"));
        auditRequest.setResult("SUCCESS");

        doThrow(new RuntimeException("Error")).when(audithService).createAudit(anyString(), anyString(), anyString(), any(), any(), any());

        ResponseEntity<String> response = audithController.createManualAudit(auditRequest);

        assert response.getStatusCode().is5xxServerError();
        assert response.getBody().contains("Error al crear la auditoría:");
    }
}
