package com.example.dentalmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalmobile.R;
import com.example.dentalmobile.model.booking_model;
import com.example.dentalmobile.patient_booking;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_adapter extends FirebaseRecyclerAdapter<booking_model, user_adapter.myViewHolder> {


    public user_adapter( @NonNull FirebaseRecyclerOptions<booking_model> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull booking_model model) {
        Log.d("Build", "Build complete");
        holder.setData(model);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_details, parent, false));
    }

    CircleImageView user_profile;
    ImageView user_qr;
    EditText user_name, user_email, user_sched, user_phone, user_address, user_gender;
    ChipGroup user_services;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Patient Bookings");

    public class myViewHolder extends RecyclerView.ViewHolder{

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile = itemView.findViewById(R.id.user_profile);
            user_qr = itemView.findViewById(R.id.user_qr);

            user_name = itemView.findViewById(R.id.user_fullName);
            user_email = itemView.findViewById(R.id.email_user);
            user_sched = itemView.findViewById(R.id.user_sched);
            user_phone = itemView.findViewById(R.id.user_phone);
            user_address = itemView.findViewById(R.id.address_user);
            user_gender = itemView.findViewById(R.id.user_gender);
            user_services = itemView.findViewById(R.id.user_services);

        }
        void setData(booking_model data){
            Picasso.get().load(data.getImageUrl()).into(user_profile);

            user_name.setText(data.getFullName());
            user_email.setText(data.getEmail());
            user_sched.setText(data.getSched());
            user_phone.setText(data.getContact_num());
            user_address.setText(data.getAddress());
            user_gender.setText(data.getGender());

            reference.child(data.getFullName()).child(data.getFullName()).child("service").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user_services.removeAllViews();
                    for(DataSnapshot snappy: snapshot.getChildren()){
                        Chip chip = new Chip(itemView.getContext());
                        chip.setText(Objects.requireNonNull(snappy.getValue()).toString());

                        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5,5,5,5);
                        chip.setLayoutParams(params);
                        chip.setId(View.generateViewId());
                        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(itemView.getContext(), null, com.google.android.material.R.attr.checkedChip, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter);
                        chip.setChipDrawable(chipDrawable);
                        user_services.addView(chip);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                //initialize bit matrix
                BitMatrix matrix = writer.encode(data.getFullName(), BarcodeFormat.QR_CODE,
                        450, 450);
                //enable barcode encoder
                BarcodeEncoder encoder = new BarcodeEncoder();
                //initialize bitmap
                Bitmap bitmap = encoder.createBitmap(matrix);
                //set bitmap to image
                user_qr.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }
}