package org.grupouno.parking.it4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    private String name;
    private String surname;
    private long age;
    private String dpi;
    private String email;

}