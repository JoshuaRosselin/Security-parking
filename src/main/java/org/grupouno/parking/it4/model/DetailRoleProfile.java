package org.grupouno.parking.it4.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "detail_role_profile")
public class DetailRoleProfile {

    @EmbeddedId
    private DetailDTO id;  // Aquí usamos la clave compuesta

    @ManyToOne
    @MapsId("idProfile")  // Mapeamos idProfile desde el embebido
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    private Profile profile;

    @ManyToOne
    @MapsId("idRole")  // Mapeamos idRole desde el embebido
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Rol role;
}

