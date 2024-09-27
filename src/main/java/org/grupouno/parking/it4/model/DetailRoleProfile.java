package org.grupouno.parking.it4.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detail_role_profile")
public class DetailRoleProfile {

    @EmbeddedId
    private DetailDTO id;

    @ManyToOne
    @MapsId("idProfile")
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    private Profile profile;

    @ManyToOne
    @MapsId("idRole")
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Rol role;

    // Constructor vacío es necesario para JPA
    public DetailRoleProfile() {}
}
