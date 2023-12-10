package com.trabajo.ferreteriacartier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Firebase.ListaProductosFB;

public class Login extends AppCompatActivity {

    //Declaracion de objetos
    private FirebaseAuth firebaseAuth;
    private Button buttonIniciar;
    private EditText editTextCorreo,editTextContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonIniciar = findViewById(R.id.btnIniciar);
        editTextCorreo = findViewById(R.id.etCorreo);
        editTextContrasena = findViewById(R.id.etContrasena);
        firebaseAuth = FirebaseAuth.getInstance();

        //Programacion de boton para inciar
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarInternet()) {
                    if (!editTextCorreo.getText().toString().isEmpty() || !editTextContrasena.getText().toString().isEmpty()) {
                        if (editTextCorreo.getText().toString().matches(".*@.*")) {
                            iniciarSesion(editTextCorreo.getText().toString(), editTextContrasena.getText().toString());
                        } else {
                            Toast.makeText(Login.this, "Formato de correo invalido", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(Login.this, "Llen todos los campos solicitados", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Login.this, "No cuentas con internet para poder entrar a la aplicacion", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Funcion para iniciar sesion con firebase
    private void iniciarSesion(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Inicio de sesión exitosa", Toast.LENGTH_SHORT).show();
                            Intent intentListaP = new Intent(Login.this, ListaProductosFB.class);
                            startActivity(intentListaP);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Credenciales invalidas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //funcion para comprobar conexion a internet
    private boolean verificarInternet () {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //programacion del boton regresar de android
    @Override
    public void onBackPressed() {
        AlertDialog.Builder confirmar = new AlertDialog.Builder(this);
        confirmar.setTitle("Salir");
        confirmar.setMessage("¿Desea salir de la aplicación?");
        confirmar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        confirmar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        confirmar.show();
    }
}