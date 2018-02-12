package com.example.djdan.cochesgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    //Parametros para el servidor
    public static final int CONNECTION_TIMEOUT = 10000; //Tiempo de espera para el servidor
    public static final int READ_TIMEOUT = 15000; //Tiempo de espera de respuesta para el servidor
    //Variables para guardar la sesion
    public static SharedPreferences pref; // 0 - for private mode
    public  static SharedPreferences.Editor editor;
    private EditText etUsuario; //Campo usuario
    private EditText etPasswd; //Campo password
    private String usuario, email, passwd; //String con el texto de los campos
    private CheckBox guardarSesion;
    private JSONObject userJSON; //Objeto JSON que nos devuelve PHP para pasarlo a la sesion y poder utilizarlo en la app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("Sesion", 0);
        editor = pref.edit();

        //Declaramos los campos de texto
        etUsuario = (EditText) findViewById(R.id.usuario);
        etPasswd = (EditText) findViewById(R.id.passwd);

        guardarSesion = (CheckBox) findViewById(R.id.recordar);
        String savedUser = pref.getString("savedUser", "0");

        if (savedUser != null) {
            try {
                userJSON = new JSONObject(savedUser);

                new AsyncLogin().execute(userJSON.getString("username"), userJSON.getString("email"), userJSON.getString("passwd"));

            } catch (JSONException e) {
                //Esta excepcion salta al no haber guardado el usuario, no nos interesa que se diga nada en ella
            }

        }

        //Declaramos el boton de login con su OnClick
        Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Rellenamos las variables con los campos
                usuario = etUsuario.getText().toString();
                email = etUsuario.getText().toString();
                passwd = etPasswd.getText().toString();

                //Validamos usuario y correo, si pasa
                if (Validaciones.validarUsuario(usuario) || Validaciones.validarEmail(usuario)) {
                    //Validamos la password
                    if (Validaciones.validarPassword(passwd)) {
                        //Encriptamos la password con SHA256 antes de mandarla al servidor
                        passwd = Validaciones.psswdSHA256(passwd);
                        // Iniciamos el Login como Asincrono pasando user, email y password
                        new AsyncLogin().execute(usuario, email, passwd);
                    } else {
                        //Si no valida password
                        Toast.makeText(Login.this, "Contraseña inválida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Si no valida email o usuario
                    Toast.makeText(Login.this, "Usuario o Email inválido", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Declaramos el boton de signup con su OnClick
        Button btnSignup = (Button) findViewById(R.id.signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lanza la actividad del SignUp
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

    }

    //TAREA ASINCRONA DEL LOGIN
    private class AsyncLogin extends AsyncTask<String, String, String> {

        //Crea un dialogo de progreso
        ProgressDialog pdLoading = new ProgressDialog(Login.this);

        //Declaramos la conexion y la url
        HttpURLConnection conn;
        URL url = null;

        //Metodo que se ejecuta ANTES que el hilo en background y se ejecuta en el mismo hilo que la Interfaz
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Pone un mensaje al dialogo del progreso
            pdLoading.setMessage("\tConectando...");
            //Hace que no se pueda quitar
            pdLoading.setCancelable(false);
            //y lo muestra
            pdLoading.show();

        }

        //Funcion que se ejecuta en un hilo en Background
        @Override
        protected String doInBackground(String... params) {
            try {
                // URL del servidor donde se aloja el php
                url = new URL("http://192.168.1.108/pruebacochesgo/login.inc.php");

                //Si no conecta salta excepcion y retorna el error para el Toast final
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Imposible conectar con el servidor";
            }
            try {
                // Establecemos la conexino abriendo la URL
                conn = (HttpURLConnection) url.openConnection();
                //Establecemos el tiempo de respuesta maximo
                conn.setReadTimeout(READ_TIMEOUT);
                //Establedemos el tiempo de espera maximo
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                //Establecemos el metodo POST
                conn.setRequestMethod("POST");

                // Establecemos a true la lectura y escritura al servidor
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Añadimos los parametros a una Uri
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("usuario", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("passwd", params[2]);
                //Pasamos a String la Uri con los parametros
                String query = builder.build().getEncodedQuery();

                // Cogemos el canal de la conexion por donde irán los datos
                OutputStream os = conn.getOutputStream();
                //Creamos el escritor pasando el canal donde escribirá y la codificacion
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //Escribimos los parametros en la conexion
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                //Finalmente conectamos
                conn.connect();

                //Si no puede escribir salta excepcion y retornamos el error para el Toast final
            } catch (IOException e1) {
                e1.printStackTrace();
                return "Error IOW";
            }

            try {
                //Si fue bien, recogemos el codigo de respuesta del servidor (200 es OK cualquier otro es error)
                int response_code = conn.getResponseCode();

                // Comprobamos que el codigo de respuesta es OK, 200
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Cogemos el canal de la conexion por donde vendrán los datos
                    InputStream input = conn.getInputStream();
                    // Creamos el lector que se pondra en ese canal
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    //Creamos la cadena del resultado, StringBuilder es un String que nos deja hacer mas cosas, en este caso concatenar lo que necesitamos
                    StringBuilder result = new StringBuilder();
                    //Creamos el String para el while
                    String line;

                    //Mientras el lector lea algo, se lo pasa a la linea
                    while ((line = reader.readLine()) != null) {
                        // y lo concatena al resultado
                        result.append(line);
                    }
                    // Retornamos el resultado como String al PostExecute
                    return (result.toString());

                } else {
                    // Si el servidor nos responde algo distinto de OK, retornamos error para el Toast final
                    return ("Servidor no responde, Error: " + response_code);
                }

                //Si no puede leer salta excepcion y retornamos el error para el Toast final
            } catch (IOException e) {
                e.printStackTrace();
                return "Error IOR";
            } finally {
                //Nos desconectamos del servidor
                conn.disconnect();
            }


        }

        //Este metodo se ejecuta DESPUES del hilo en background y corre en el mismo hilo que la Interfaz
        @Override
        protected void onPostExecute(String result) {

            //Quita el progressDialog de la pantalla
            pdLoading.dismiss();

            //Cuando el result es true, se ha logueado correctamente
            if (result.charAt(0) == '{') {

                //Lanza la activity success
                Intent intent = new Intent(Login.this, Success.class);
                startActivity(intent);
                //Se pasa el username a la sesion estatica
                try {
                    userJSON = new JSONObject(result);
                } catch (JSONException e) {
                    //Se muestra el error ocurrido en caso de que lo haya
                    Toast.makeText(Login.this, e.toString(), Toast.LENGTH_LONG).show();
                }
                Sesion.setUser(userJSON);

                if (guardarSesion.isChecked()) {
                    editor.putString("savedUser", userJSON.toString());
                    editor.commit();
                }

                //y finaliza esta activity
                Login.this.finish();

                //Cuando el result es false, no se ha logueado
            } else if (result.equalsIgnoreCase("false")) {
                // Mostramos un toast conforme no se ha podido loguear porque el user o pass son incorrectos
                Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();

                //Esto solo pasa cuando se ha lanzado una excepcion o error, no tiene que ver con el login
            } else {
                //Se muestra el error ocurrido en caso de que lo haya
                Toast.makeText(Login.this, result, Toast.LENGTH_LONG).show();

            }
        }

    }
}
