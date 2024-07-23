package com.pluartz.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePetActivity extends AppCompatActivity {

   ImageView photo_card;
   Button btn_add;
   Button btn_cu_photo, btn_r_photo;
   LinearLayout linearLayout_image_btn;
   EditText name, category, color, precio_carta;
   private FirebaseFirestore mfirestore;
   private FirebaseAuth mAuth;

   StorageReference storageReference;
   String storage_path = "card/*";

   private static final int COD_SEL_STORAGE = 200;
   private static final int COD_SEL_IMAGE = 300;

   private Uri image_url;
   String photo = "photo";
   String idd;

   ProgressDialog progressDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_create_pet);
      this.setTitle("Actualizar Carta");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      progressDialog = new ProgressDialog(this);
      String id = getIntent().getStringExtra("id_card");
      mfirestore = FirebaseFirestore.getInstance();
      mAuth = FirebaseAuth.getInstance();
      storageReference = FirebaseStorage.getInstance().getReference();

      linearLayout_image_btn = findViewById(R.id.images_btn);
      name = findViewById(R.id.nombre);
      category = findViewById(R.id.edad);
      color = findViewById(R.id.color);
      precio_carta = findViewById(R.id.precio_vacuna);
      photo_card = findViewById(R.id.pet_photo);

      btn_add = findViewById(R.id.btn_add);



      if (id == null || id == ""){
         linearLayout_image_btn.setVisibility(View.GONE);
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String namecard = name.getText().toString().trim();
               String catcard = category.getText().toString().trim();
               String colorcard = color.getText().toString().trim();
               Double precio_cartacard = Double.parseDouble(precio_carta.getText().toString().trim());

               if(namecard.isEmpty() && catcard.isEmpty() && colorcard.isEmpty()){
                  Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
               }else{
                  postCard(namecard, catcard, colorcard, precio_cartacard);
               }
            }
         });
      }else{
         idd = id;
         btn_add.setText("Update");
         getCard(id);
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String namecard = name.getText().toString().trim();
               String catcard = category.getText().toString().trim();
               String colorcard = color.getText().toString().trim();
               Double precio_cartacard = Double.parseDouble(precio_carta.getText().toString().trim());

               if(namecard.isEmpty() && catcard.isEmpty() && colorcard.isEmpty()){
                  Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
               }else{
                  updateCard(namecard, catcard, colorcard, precio_cartacard, id);
               }
            }
         });
      }
   }

   private void uploadPhoto() {
      Intent i = new Intent(Intent.ACTION_PICK);
      i.setType("image/*");

      startActivityForResult(i, COD_SEL_IMAGE);
   }


   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if(resultCode == RESULT_OK){
         if (requestCode == COD_SEL_IMAGE){
            image_url = data.getData();
            subirPhoto(image_url);
         }
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   private void subirPhoto(Uri image_url) {
      progressDialog.setMessage("Actualizando foto");
      progressDialog.show();
      String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
      StorageReference reference = storageReference.child(rute_storage_photo);
      reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
               if (uriTask.isSuccessful()){
                  uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                        String download_uri = uri.toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("photo", download_uri);
                        mfirestore.collection("card").document(idd).update(map);
                        Toast.makeText(CreatePetActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                     }
                  });
               }
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(CreatePetActivity.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void updateCard(String namecard, String catcard, String colorcard, Double precio_cartacard, String id) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", namecard);
      map.put("category", catcard);
      map.put("color", colorcard);
      map.put("card_price", precio_cartacard);

      mfirestore.collection("card").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getApplicationContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void postCard(String namecard, String catcard, String colorcard, Double precio_cartacard) {
      String idUser = mAuth.getCurrentUser().getUid();
      DocumentReference id = mfirestore.collection("card").document();

      Map<String, Object> map = new HashMap<>();
      map.put("id_user", idUser);
      map.put("id", id.getId());
      map.put("name", namecard);
      map.put("category", catcard);
      map.put("color", colorcard);
      map.put("card_price", precio_cartacard);

      mfirestore.collection("card").document(id.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getApplicationContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void getCard(String id){
      mfirestore.collection("card").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {
            DecimalFormat format = new DecimalFormat("0.00");
            String nameCard = documentSnapshot.getString("name");
            String catCard = documentSnapshot.getString("age");
            String colorCard = documentSnapshot.getString("color");
            Double precio_cartacard = documentSnapshot.getDouble("card_price");
            String photoCard = documentSnapshot.getString("photo");

            name.setText(nameCard);
            category.setText(catCard);
            color.setText(colorCard);
            precio_carta.setText(format.format(precio_cartacard));
            try {
               if(!photoCard.equals("")){
                  Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
                  toast.setGravity(Gravity.TOP,0,200);
                  toast.show();
                  Picasso.with(CreatePetActivity.this)
                          .load(photoCard)
                          .resize(150, 150)
                          .into(photo_card);
               }
            }catch (Exception e){
               Log.v("Error", "e: " + e);
            }
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
         }
      });
   }

   @Override
   public boolean onSupportNavigateUp() {
      onBackPressed();
      return false;
   }
}