package org.grupouno.parking.it4.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Validations {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "@$!%*?&";
    private static final String ALL_ALLOWED = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS;
    private static final SecureRandom random = new SecureRandom();
    private Random rand = new Random();


    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    public String generateVerificationCode() {
        return String.valueOf(rand.nextInt(999999));
    }


    public String generatePassword() {
        //ver que la contraseña tenga minimo un caracter necesario
        StringBuilder password = new StringBuilder();
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        //meter más dijitos
        for (int i = 4; i < 8; i++) {
            password.append(ALL_ALLOWED.charAt(random.nextInt(ALL_ALLOWED.length())));
        }
        return mezclaCaracteres(password.toString());
    }

    // mezclar los caracteres
    private String mezclaCaracteres(String input) {
        StringBuilder mezcla = new StringBuilder(input.length());
        int[] indices = random.ints(0, input.length()).distinct().limit(input.length()).toArray();
        for (int i : indices) {
            mezcla.append(input.charAt(i));
        }
        return mezcla.toString();
    }


}