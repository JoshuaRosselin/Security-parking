package org.grupouno.parking.it4.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private String pastPassword;
    private String newPassword;
    private String confirmPassword;
}