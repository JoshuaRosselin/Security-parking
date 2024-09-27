package org.grupouno.parking.it4.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Data
@Getter
@Setter
@Embeddable
public class DetailDTO implements Serializable {
    private long idProfile;
    private long idRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailDTO detailDTO = (DetailDTO) o;
        return idProfile == detailDTO.idProfile && idRole == detailDTO.idRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProfile, idRole);
    }
}

