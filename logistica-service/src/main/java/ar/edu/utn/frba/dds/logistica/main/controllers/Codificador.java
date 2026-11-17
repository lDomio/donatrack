package ar.edu.utn.frba.dds.logistica.main.controllers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Codificador {


    public static String calculateHmac256(String data, String secret) {
        try {
            // Inicializar el algoritmo HMAC-SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            // Generar el hash en un array de bytes
            byte[] rawHmac = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convertir los bytes a una cadena Hexadecimal (Formato más común)
            StringBuilder hexString = new StringBuilder();
            for (byte b : rawHmac) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();


        } catch (Exception e) {
            throw new RuntimeException("Error calculando la firma HMAC", e);
        }
    }

}

