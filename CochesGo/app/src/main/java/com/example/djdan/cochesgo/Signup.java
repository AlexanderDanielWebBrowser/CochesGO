package com.example.djdan.cochesgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Signup extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private EditText etEmail;
    private EditText etUsuario;
    private EditText etContra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = (EditText) findViewById(R.id.email);
        etUsuario = (EditText) findViewById(R.id.usuario);
        etContra = (EditText) findViewById(R.id.passwd);

        Button btnRegister = (Button) findViewById(R.id.signup);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usuario = etUsuario.getText().toString();
                final String email = etEmail.getText().toString();
                String passwd = etContra.getText().toString();

                if (Validaciones.validarUsuario(usuario)) {
                    if (Validaciones.validarEmail(email)) {
                        if (Validaciones.validarPassword(passwd)) {
                            passwd = Validaciones.psswdSHA256(passwd);
                            // Initialize  AsyncLogin() class with email and password
                            new AsyncRegister().execute(usuario, email, passwd);
                        } else {
                            Toast.makeText(Signup.this, "Contraseña inválida", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Signup.this, "Email inválido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Signup.this, "Usuario inválido", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private class AsyncRegister extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Signup.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tConectando...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://pruebacochesgo.000webhostapp.com/register.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Imposible conectar con el servidor";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("usuario", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("passwd", params[2]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "Error IO";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("Servidor no responde");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "Error IO";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if (result.equalsIgnoreCase("true")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Toast.makeText(Signup.this, "Comprueba tu email para verificar la cuenta", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
                Signup.this.finish();

            } else if (result.equalsIgnoreCase("false")) {

                Toast.makeText(Signup.this, "Usuario o Email ya existen.", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(Signup.this, result, Toast.LENGTH_LONG).show();

            }
        }

    }
}
