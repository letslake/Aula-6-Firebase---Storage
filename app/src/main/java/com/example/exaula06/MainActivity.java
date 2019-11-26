package com.example.exaula06;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText senhaEditText;
    private FirebaseAuth mAuth;

    private void setupFirebase (){
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupViews(){
        loginEditText = findViewById(R.id.loginEditText);
        senhaEditText = findViewById(R.id.senhaEditText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        setupFirebase();
    }

    public void irParaCadastro(View view) {
        Intent intent = new Intent (this, NovoUsuarioActivity.class);
        startActivity(intent);
    }

    public void fazerLogin (View v){
        String email =
                loginEditText.getText().toString();
        String senha =
                senhaEditText.getText().toString();
        mAuth.signInWithEmailAndPassword(email, senha).
                addOnSuccessListener((result) -> {
                    startActivity(new Intent(this, ChatActivity.class));

                }).
                addOnFailureListener((exception) -> {
                    exception.printStackTrace();
                    Toast.makeText(
                            this,
                            getString(R.string.cant_login),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

    }
}
