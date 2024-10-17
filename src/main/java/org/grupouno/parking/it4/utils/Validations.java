package org.grupouno.parking.it4.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
@Component
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

    private static final List<String> validCodes  = Arrays.asList(
            // Departamento de Guatemala
            "0101", "0102", "0103", "0104", "0105", "0106", "0107", "0108", "0109", "0110",
            "0111", "0112", "0113", "0114", "0115", "0116", "0117",
            // Departamento de El Progreso
            "0201", "0202", "0203", "0204", "0205", "0206", "0207", "0208",
            // Departamento de Sacatepéquez
            "0301", "0302", "0303", "0304", "0305", "0306", "0307", "0308", "0309", "0310",
            "0311", "0312", "0313", "0314", "0315", "0316",
            // Departamento de Chimaltenango
            "0401", "0402", "0403", "0404", "0405", "0406", "0407", "0408", "0409", "0410",
            "0411", "0412", "0413", "0414", "0415", "0416",
            // Departamento de Escuintla
            "0501", "0502", "0503", "0504", "0505", "0506", "0507", "0508", "0509", "0510",
            "0511", "0512", "0513",
            // Departamento de Santa Rosa
            "0601", "0602", "0603", "0604", "0605", "0606", "0607", "0608", "0609", "0610",
            "0611", "0612", "0613", "0614",
            // Departamento de Sololá
            "0701", "0702", "0703", "0704", "0705", "0706", "0707", "0708", "0709", "0710",
            "0711", "0712", "0713", "0714", "0715", "0716", "0717", "0718", "0719",
            // Departamento de Totonicapán
            "0801", "0802", "0803", "0804", "0805", "0806", "0807", "0808",
            // Departamento de Quetzaltenango
            "0901", "0902", "0903", "0904", "0905", "0906", "0907", "0908", "0909", "0910",
            "0911", "0912", "0913", "0914", "0915", "0916", "0917", "0918", "0919", "0920",
            "0921", "0922", "0923", "0924",
            // Departamento de Suchitepéquez
            "1001", "1002", "1003", "1004", "1005", "1006", "1007", "1008", "1009", "1010",
            "1011", "1012", "1013", "1014", "1015", "1016", "1017", "1018", "1019", "1020",
            // Departamento de Retalhuleu
            "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109",
            // Departamento de San Marcos
            "1201", "1202", "1203", "1204", "1205", "1206", "1207", "1208", "1209", "1210",
            "1211", "1212", "1213", "1214", "1215", "1216", "1217", "1218", "1219", "1220",
            "1221", "1222", "1223", "1224", "1225", "1226", "1227", "1228", "1229",
            // Departamento de Huehuetenango
            "1301", "1302", "1303", "1304", "1305", "1306", "1307", "1308", "1309", "1310",
            "1311", "1312", "1313", "1314", "1315", "1316", "1317", "1318", "1319", "1320",
            "1321", "1322", "1323", "1324", "1325", "1326", "1327", "1328", "1329", "1330",
            "1331", "1332", "1333", "1334", "1335", "1336",
            // Departamento de Quiché
            "1401", "1402", "1403", "1404", "1405", "1406", "1407", "1408", "1409", "1410",
            "1411", "1412", "1413", "1414", "1415", "1416", "1417", "1418", "1419", "1420",
            // Departamento de Baja Verapaz
            "1501", "1502", "1503", "1504", "1505", "1506", "1507", "1508",
            // Departamento de Alta Verapaz
            "1601", "1602", "1603", "1604", "1605", "1606", "1607", "1608", "1609", "1610",
            "1611", "1612", "1613", "1614", "1615", "1616", "1617",
            // Departamento de Petén
            "1701", "1702", "1703", "1704", "1705", "1706", "1707", "1708", "1709", "1710",
            "1711", "1712", "1713", "1714", "1715",
            // Departamento de Izabal
            "1801", "1802", "1803", "1804", "1805",
            // Departamento de Zacapa
            "1901", "1902", "1903", "1904", "1905", "1906", "1907", "1908", "1909", "1910",
            // Departamento de Chiquimula
            "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
            // Departamento de Jalapa
            "2101", "2102", "2103", "2104", "2105", "2106", "2107",
            // Departamento de Jutiapa
            "2201", "2202", "2203", "2204", "2205", "2206", "2207", "2208", "2209", "2210",
            "2211", "2212", "2213", "2214", "2215", "2216", "2217"
    );

    public static boolean isValidDpi(String dpi) {
        if (dpi == null || dpi.length() != 13) {
            return false;
        }
        String dpiSuffix = dpi.substring(dpi.length() - 4);

        return validCodes.contains(dpiSuffix);
    }

}
