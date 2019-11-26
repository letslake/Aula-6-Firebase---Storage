package com.example.exaula06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List <Mensagem> mensagens;
    private EditText mensagemEditText;;
    private StorageReference pictureStorageReference;
    private CollectionReference mMsgsReference;
    private ImageView pictureImageViewChat;
    private static final int REQ_CODE_CAMERA = 1001;
    private FirebaseUser fireUser;

    private void setupFirebase(){
        mMsgsReference =
                FirebaseFirestore.getInstance().collection(
                        "mensagens"
                );
        fireUser = FirebaseAuth.getInstance().getCurrentUser();
        mMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                mensagens.clear();
                for (DocumentSnapshot document :
                        queryDocumentSnapshots.getDocuments()){
                    Mensagem m = document.toObject(Mensagem.class);
                    mensagens.add(m);
                }
                Collections.sort(mensagens);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView (){
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter (this, mensagens);
        mensagensRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        mensagensRecyclerView.setAdapter(adapter);
    }

    private void setupViews (){
        mensagemEditText =
                findViewById(R.id.mensagemEditText);
        pictureImageViewChat =
                findViewById((R.id.pictureImageViewChat));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupRecyclerView();
        setupViews();
        setupFirebase();
    }

    public void enviarMensagem (View v){
        String texto = mensagemEditText.getText().toString();
        Mensagem m = new Mensagem (fireUser.getEmail(), new Date(), texto);
        mMsgsReference.add(m);
        mensagemEditText.setText("");
        Toast.makeText(
                this,
                getString(R.string.msg_enviada),
                Toast.LENGTH_SHORT
        ).show();
    }

    public void tirarFotoChat (View v){
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQ_CODE_CAMERA);
        }else{
            Toast.makeText(
                    this,
                    getString(R.string.no_camera),
                    Toast.LENGTH_SHORT

            ).show();
        }
    }

    private void uploadPicture (Bitmap picture){
        pictureStorageReference = FirebaseStorage.getInstance().getReference(
                String.format(
                        Locale.getDefault(),
                        "images/%s/profilePic.jpg",
                        fireUser.getEmail().toString().replace("@", "")
                ));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        pictureStorageReference.putBytes(bytes); //upload acontece aqui
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @androidx.annotation.Nullable Intent data) {
        switch (requestCode){
            case REQ_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK){
                    Bitmap picture = (Bitmap) data.getExtras().get
                            ("data");
                    pictureImageViewChat.setImageBitmap(picture);
                    uploadPicture(picture);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

class ChatAdapter extends RecyclerView.Adapter <ChatViewHolder>{

    private Context context;
    private List<Mensagem> mensagens;

    public ChatAdapter(Context context, List<Mensagem> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(
                R.layout.list_item,
                parent,
                false
        );
        return new ChatViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        holder.dataNomeTextView.setText(context.getString(R.string.data_nome,
                DateHelper.format(m.getData()), m.getUsuario()));
        holder.mensagemTextView.setText(m.getTexto());
        StorageReference profilePicReference =
                FirebaseStorage.getInstance().getReference(
                        String.format(
                                Locale.getDefault(),
                                "images/%s/profilePic.jpg",
                                m.getUsuario().replace("@", "")

                        )
                );
        //verifica se a pessoa tem foto
        profilePicReference.getDownloadUrl().addOnSuccessListener((result) -> {
            Glide.with(context).load(profilePicReference).into(holder.profilePicImageView
            );
        });
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }
}

class ChatViewHolder extends RecyclerView.ViewHolder{
    ImageView profilePicImageView;
    TextView dataNomeTextView;
    TextView mensagemTextView;
    ChatViewHolder (View v){

        super (v);
        this.dataNomeTextView =
                v.findViewById(R.id.dataNomeTextView);
        this.mensagemTextView =
                v.findViewById(R.id.mensagemTextView);
        this.profilePicImageView =
                v.findViewById(R.id.profilePicImageView);
    }
}