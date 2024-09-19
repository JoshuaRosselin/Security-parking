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
@IdClass(DetailDTO.class)
public class DetailRoleProfile {
    @Id
    @Column(name = "profile_id")
    private long idProfile;
    @Id
    @Column(name = "role_id")
    private long idRole;
}
