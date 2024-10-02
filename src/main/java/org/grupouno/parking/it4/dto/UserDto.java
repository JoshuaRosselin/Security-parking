package org.grupouno.parking.it4.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private String name;
    private String surname;
    private long age;
    private String dpi;
    private String email;
    private String password;
    private boolean status;
    private Long profileId;

}