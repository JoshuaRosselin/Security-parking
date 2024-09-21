package org.grupouno.parking.it4.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "role")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private long id;
    private String role;
    private String description;

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}