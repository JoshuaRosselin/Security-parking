package org.grupouno.parking.it4.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class AudithDTO {
    private long auditId;
    private String entity;
    private OffsetDateTime startDate;
    private String description;
    private String operation;
    private String result;
    private Map<String, Object> request;
    private Map<String, Object> response;
}
