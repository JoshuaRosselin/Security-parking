package org.grupouno.parking.it4.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private long profileId;
    @NotNull(message = "El [description] no puede ser nulo")
    @NotBlank(message = "[description] No debe estar en blanco")
    @Size(max = 50, message = "[description] tiene máximo de 50" )
    private String description;
    private boolean status;
}