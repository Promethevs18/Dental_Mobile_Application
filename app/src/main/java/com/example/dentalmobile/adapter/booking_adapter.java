package com.example.dentalmobile.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalmobile.R;
import com.example.dentalmobile.model.booking_model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class booking_adapter extends FirebaseRecyclerAdapter<booking_model, booking_adapter.myViewHolder> {


    public booking_adapter(@NonNull FirebaseRecyclerOptions<booking_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull booking_model model) {
        holder.setData(model);

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_lister, parent, false));
    }

    TextView full_name, contact_num, address;
    CircleImageView profile_image;
    ImageView logo;
    public class myViewHolder extends RecyclerView.ViewHolder {
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            full_name = itemView.findViewById(R.id.patient_full);
            contact_num = itemView.findViewById(R.id.contact_patient);
            address = itemView.findViewById(R.id.address_patient);

            logo = itemView.findViewById(R.id.clinic_logo);
            profile_image = itemView.findViewById(R.id.profile_image);

        }
        void setData(booking_model data){
            Picasso.get().load(data.getImageUrl()).into(profile_image);
            full_name.setText(data.getFullName());
            contact_num.setText(data.getContact_num());
            address.setText(data.getAddress());
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/dental-management-system-2dccb.appspot.com/o/1.png?alt=media&token=ec6d54ce-200d-4b42-9674-30c5f15fdc2b").into(logo);

        }
    }
}