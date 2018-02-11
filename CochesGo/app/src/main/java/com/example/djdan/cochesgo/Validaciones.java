package com.example.djdan.cochesgo;

import java.security.MessageDigest;

/**
 * Created by djdan on 07/02/2018.
 */

public class Validaciones {

    public static boolean validarUsuario(String usuario) {
        if (usuario.length() > 5 && usuario.length() <= 15 && !usuario.contains("=")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validarEmail(String email) {
        if (email.length() > 9 && email.contains("@") && !email.contains("=")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validarPassword(String pass) {
        if (pass.length() > 5 && pass.length() <= 20 && !pass.contains("=") && !pass.contains("'")) {
            return true;
        } else {
            return false;
        }
    }

    public static String psswdSHA256(String data) {
        String encryptedPass = "";
        String salt = "CochesGo";
        data += salt;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                encryptedPass += Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedPass.toString();
    }
}
