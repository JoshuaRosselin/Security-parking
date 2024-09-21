package org.grupouno.parking.it4.utils;

import java.util.Random;

public class Validations {
    private Random rand = new Random();
    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    public String generateVerificationCode() {
        return String.valueOf(rand.nextInt(999999));
    }


}
