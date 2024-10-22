package org.grupouno.parking.it4.service;

import org.grupouno.parking.it4.dto.AudithDTO;
import org.grupouno.parking.it4.model.Audith;
import org.grupouno.parking.it4.repository.AudithRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AudithServiceTest {

    @Mock
    private AudithRepository audithRepository;

    @InjectMocks
    private AudithService audithService;
    private Audith audit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAuditTest() {
        Audith audit = new Audith();
        when(audithRepository.save(any(Audith.class))).thenReturn(audit);

        Audith result = audithService.createAudit(
                "TestEntity", "TestDescription", "TestOperation",
                new HashMap<>(), new HashMap<>(), "Success");

        assertNotNull(result);
        verify(audithRepository, times(1)).save(any(Audith.class));
    }

    @Test
    void getAllAuditsTest() {
        List<Audith> auditList = Arrays.asList(new Audith(), new Audith());
        when(audithRepository.findAll()).thenReturn(auditList);

        List<Audith> result = audithService.getAllAudits();

        assertEquals(2, result.size());
        verify(audithRepository, times(1)).findAll();
    }

    @Test
    void getAuditByIdTest() {
        Audith audit = new Audith();
        when(audithRepository.findById(anyLong())).thenReturn(Optional.of(audit));

        Optional<Audith> result = audithService.getAuditById(1L);

        assertTrue(result.isPresent());
        verify(audithRepository, times(1)).findById(1L);
    }

    @Test
    void getAuditsByEntityTest() {
        Audith audit1 = new Audith();
        audit1.setEntity("Entity1");
        Audith audit2 = new Audith();
        audit2.setEntity("Entity1");

        List<Audith> auditList = Arrays.asList(audit1, audit2);
        when(audithRepository.findAll()).thenReturn(auditList);

        List<Audith> result = audithService.getAuditsByEntity("Entity1");

        assertEquals(2, result.size());
        verify(audithRepository, times(1)).findAll();
    }


    @Test
    void convertToDTO_success() {
        AudithDTO dto = audithService.convertToDTO(audit);

        assertNotNull(dto);
        assertEquals(audit.getAuditId(), dto.getAuditId());
        assertEquals(audit.getEntity(), dto.getEntity());
        assertEquals(audit.getDescription(), dto.getDescription());
        verify(audithRepository, never()).findById(any());
    }

    @Test
    void convertToDTO_nullAudit() {
        AudithDTO dto = audithService.convertToDTO(null);

        assertNull(dto);
    }
}
