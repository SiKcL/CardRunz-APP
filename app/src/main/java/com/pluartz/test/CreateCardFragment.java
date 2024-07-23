package com.pluartz.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CreateCardFragment extends DialogFragment {

    String id_card;
    Button btn_add;
    EditText name, category, color, precio_carta;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            id_card = getArguments().getString("id_card");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_pet, container, false);
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        name = v.findViewById(R.id.nombre);
        category = v.findViewById(R.id.categoria);
        color = v.findViewById(R.id.color);
        precio_carta = v.findViewById(R.id.precio_carta);
        btn_add = v.findViewById(R.id.btn_add);

        if (id_card==null || id_card==""){
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String namecard = name.getText().toString().trim();
                    String catcard = category.getText().toString().trim();
                    String colorcard = color.getText().toString().trim();
                    Double precio_cartacard = Double.parseDouble(precio_carta.getText().toString().trim());

                    if(namecard.isEmpty() && catcard.isEmpty() && colorcard.isEmpty()){
                        Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    }else{
                        postCard(namecard, catcard, colorcard, precio_cartacard);
                    }
                }
            });
        }else {
            getCard();
            btn_add.setText("update");
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String namecard = name.getText().toString().trim();
                    String catcard = category.getText().toString().trim();
                    String colorcard = color.getText().toString().trim();
                    Double precio_cartacard = Double.parseDouble(precio_carta.getText().toString().trim());

                    if(namecard.isEmpty() && catcard.isEmpty() && colorcard.isEmpty()){
                        Toast.makeText(getContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    }else{
                        updateCard(namecard, catcard, colorcard, precio_cartacard);
                    }
                }
            });
        }
        return v;
    }

    private void updateCard(String namecard, String catcard, String colorcard, Double precio_cartacard) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", namecard);
        map.put("category", catcard);
        map.put("color", colorcard);
        map.put("card_price", precio_cartacard);

        mfirestore.collection("card").document(id_card).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCard(){
        mfirestore.collection("card").document(id_card).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DecimalFormat format = new DecimalFormat("0.00");
//            format.setMaximumFractionDigits(2);
                String nameCard = documentSnapshot.getString("name");
                String catCard = documentSnapshot.getString("category");
                String colorCard = documentSnapshot.getString("color");
                Double precio_cartacard = documentSnapshot.getDouble("card_price");
                name.setText(nameCard);
                category.setText(catCard);
                color.setText(colorCard);
                precio_carta.setText(format.format(precio_cartacard));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
