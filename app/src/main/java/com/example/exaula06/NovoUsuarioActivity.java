package com.example.exaula06;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NovoUsuarioActivity extends AppCompatActivity {

    private EditText loginNovoUsuarioEditText;
    private EditText senhaNovoUsuarioEditText;
    private StorageReference pictureStorageReference;
    private ImageView pictureImageView;
    private FirebaseAuth mAuth;
    private static final int REQ_CODE_CAMERA = 1001;

    private void setupViews (){
        loginNovoUsuarioEditText =
                findViewById(R.id.loginNovoUsuarioEditText);
        senhaNovoUsuarioEditText =
                findViewById(R.id.senhaNovoUsuarioEditText);
        pictureImageView = findViewById(R.id.pictureImageView);
    }

    private void setupFirebase (){
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);
        setupViews();
        setupFirebase();
    }

    public void criarNovoUsuario(View view) {
        String email = loginNovoUsuarioEditText.getText().toString();
        String senha = senhaNovoUsuarioEditText.getText().toString();
        mAuth.createUserWithEmailAndPassword(
                email,
                senha
        ).addOnSuccessListener((result) -> {
            Toast.makeText(
                    this,
                    getString(R.string.usuario_criado),
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        }).addOnFailureListener((exception) ->{
            Toast.makeText(
                    this,
                    getString(R.string.usuario_nao_criado),
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    public void tirarFoto (View v){
        if (loginNovoUsuarioEditText.getText() != null
                && !loginNovoUsuarioEditText.getText().toString().isEmpty()){
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQ_CODE_CAMERA);
            }else{
                Toast.makeText(
                        this,

                        getString(R.string.no_camera),//defina um texto apropriado
                        Toast.LENGTH_SHORT

                ).show();
            }
        }
        else{
            Toast.makeText(
                    this,
                    getString(R.string.empty_email),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void uploadPicture (Bitmap picture){
        pictureStorageReference = FirebaseStorage.getInstance().getReference(
                String.format(
                        Locale.getDefault(),
                        "images/%s/profilePic.jpg",
                        loginNovoUsuarioEditText.getText().toString().replace ( "@", "")
        ));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        pictureStorageReference.putBytes(bytes); //upload acontece aqui
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        switch (requestCode){
            case REQ_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK){
                    Bitmap picture = (Bitmap) data.getExtras().get
                            ("data");
                    pictureImageView.setImageBitmap(picture);
                    uploadPicture(picture);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
