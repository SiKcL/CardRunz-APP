package com.pluartz.test.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pluartz.test.CreatePetActivity;
import com.pluartz.test.R;
import com.pluartz.test.model.Card;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class CardAdapter extends FirestoreRecyclerAdapter<Card, CardAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CardAdapter(@NonNull FirestoreRecyclerOptions<Card> options, Activity activity, FragmentManager fm) {
        super(options);
        this.activity = activity;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardAdapter.ViewHolder viewHolder, int i, @NonNull Card Card) {
        DecimalFormat format = new DecimalFormat("0.00");
//      format.setMaximumFractionDigits(2);
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.name.setText(Card.getName());
        viewHolder.category.setText(Card.getCategory());
        viewHolder.color.setText(Card.getColor());
        viewHolder.card_price.setText( format.format(Card.getCard_price()));
        String photoCard = Card.getPhoto();
        try {
            if (!photoCard.equals(""))
                Picasso.with(activity.getApplicationContext())
                        .load(photoCard)
                        .resize(150, 150)
                        .into(viewHolder.photo_card);
        }catch (Exception e){
            Log.d("Exception", "e: "+e);
        }

        viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//          SEND DATA ACTIVITY
                Intent i = new Intent(activity, CreatePetActivity.class);
                i.putExtra("id_card", id);
                activity.startActivity(i);

//          SEND DATA FRAGMENT
//            CreateCardFragment createCardFragment = new CreateCardFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("id_card", id);
//            createCardFragment.setArguments(bundle);
//            createCardFragment.show(fm, "open fragment");
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCard(id);
            }
        });
    }

    private void deleteCard(String id) {
        mFirestore.collection("card").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pet_single, parent, false);
        return new CardAdapter.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, color, card_price;
        ImageView btn_delete, btn_edit, photo_card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nombre);
            category = itemView.findViewById(R.id.categoria);
            color = itemView.findViewById(R.id.color);
            card_price = itemView.findViewById(R.id.precio_carta);
            photo_card = itemView.findViewById(R.id.photo);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editar);
        }
    }
}