package org.grupouno.parking.it4.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DetailDTO implements Serializable {
    private Long idProfile;
    private Long idRole;
}

