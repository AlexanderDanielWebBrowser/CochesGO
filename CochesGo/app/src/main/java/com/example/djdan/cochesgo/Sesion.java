package com.example.djdan.cochesgo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by djdan on 08/02/2018.
 */

public class Sesion {
    private static JSONObject user;

    public static JSONObject getUser() {
        return user;
    }

    public static void setUser(JSONObject user) {
        Sesion.user = user;
    }

    public static String getUsername() {
        try {
            String userName = user.getString("username");
            return userName;
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public static String getEmail() {
        try {
            String email = user.getString("email");
            return email;
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public static void guardaSesion(){
        //TODO escribir en un fichero binario el objeto json
    }

    public static void borrarSesion(){
        //TODO borrar el fichero binario del json con el usuario
    }
}


