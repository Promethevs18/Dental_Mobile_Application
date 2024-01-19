package com.example.dentalmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class patient_booking extends AppCompatActivity {

    CircleImageView profile;
    EditText full_Name, email_Nya, sched_day, phone_Num, address, sched_time;
    Spinner gendr;
    ChipGroup service;
    Button accept;
    DatabaseReference userReference;
    String sched, gender_selected;
    String changed = "no", imageUrl;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    AlertDialog.Builder builder;
    String service_selected;
    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    ProgressDialog pd;
    ArrayList<String> services_selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.Theme_DentalAppointmentDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
            default:
                setTheme(R.style.Theme_DentalAppointment);
                break;
        }

        setContentView(R.layout.activity_patient_booking);

        //profilepic
        profile = findViewById(R.id.user_profile);

        //edittexts
        full_Name = findViewById(R.id.user_fullName);
        email_Nya = findViewById(R.id.email_user);
        sched_day = findViewById(R.id.user_sched);
        gendr = findViewById(R.id.gender);
        phone_Num = findViewById(R.id.user_phone);
        address = findViewById(R.id.address_user);
        sched_time = findViewById(R.id.time);

        //spinner
        service = findViewById(R.id.services);

        //buttons
        accept = findViewById(R.id.accept);


        //database reference
        userReference = FirebaseDatabase.getInstance().getReference("Patient Bookings");

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            full_Name.setText(account.getDisplayName());
            email_Nya.setText(account.getEmail());
            if(account.getPhotoUrl() != null){
               imageUrl = String.valueOf(account.getPhotoUrl());
            }
            else{
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/dental-management-system-2dccb.appspot.com/o/tooth_w_title.png?alt=media&token=1eeb9a95-8964-4fc3-b199-40eb261f8c5f";
            }
            Picasso.get().load(imageUrl).into(profile);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //DATE PICKER
        sched_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(view.getContext(), (datePicker, i, i1, i2) -> {
                    calendar.set(Calendar.YEAR, i);
                    calendar.set(Calendar.MONTH, i1);
                    calendar.set(Calendar.DAY_OF_MONTH, i2);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
                    sched = dateFormat.format(calendar.getTime());
                    sched_day.setText(sched);
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        //TIME PICKER
        sched_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timeDialog = new TimePickerDialog(patient_booking.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        sched_time.setText(String.format("%02d",i) +":"+String.format("%02d",i1));
                    }
                }, hour, minute, false);
                timeDialog.setTitle("Select your time");
                timeDialog.show();
            }
        });

        //this is responsible for services eme
        services_selected = new ArrayList<>();
        DatabaseReference service_ref = FirebaseDatabase.getInstance().getReference("Services");
        service_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                service.removeAllViews();
                for (DataSnapshot snappy : snapshot.getChildren()) {
                    Chip chippy = new Chip(patient_booking.this);
                    chippy.setText(Objects.requireNonNull(snappy.getValue()).toString());

                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(5,5,5,5);
                    chippy.setLayoutParams(params);
                    chippy.setId(View.generateViewId());
                    ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(patient_booking.this, null, com.google.android.material.R.attr.checkedChip, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter);
                    chippy.setChipDrawable(chipDrawable);
                    service.addView(chippy);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(patient_booking.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        service.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                int chipCount = group.getChildCount();
                for(int i = 0; i < chipCount; i++){
                    Chip piniling_Chip = (Chip) group.getChildAt(i);
                    if(piniling_Chip.isChecked()){
                        if(!services_selected.contains(String.valueOf(piniling_Chip.getText()))){
                            services_selected.add(String.valueOf(piniling_Chip.getText()));
                        }

                    }
                    else{
                        services_selected.remove(String.valueOf(piniling_Chip.getText()));
                    }

                }

            }
        });



        //this is responsible for gender eme
        ArrayList<String> genders = new ArrayList<>();
        DatabaseReference gender_ref = FirebaseDatabase.getInstance().getReference("Genders");

        ArrayAdapter<String> gender_adapter = new ArrayAdapter<>(patient_booking.this, android.R.layout.simple_spinner_dropdown_item, genders);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gendr.setAdapter(gender_adapter);

        gender_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snappy : snapshot.getChildren()) {
                    genders.add(snappy.getValue().toString());
                    gender_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(patient_booking.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        gendr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender_selected = gender_adapter.getItem(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                service_selected = "";
            }
        });



        //THIS IS FOR THE ACCEPT BUTTON
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    pd = new ProgressDialog(patient_booking.this);
                    pd.setTitle("Creating booking...");
                    pd.setMessage("Data is uploading, this might take a while...");
                    pd.show();

                    builder = new AlertDialog.Builder(patient_booking.this);

                    HashMap <String, Object> mapa = new HashMap<>();
                    mapa.put("fullName", full_Name.getText().toString());
                    mapa.put("gender", gender_selected);
                    mapa.put("sched", sched);
                    mapa.put("contact_num", phone_Num.getText().toString());
                    mapa.put("address", address.getText().toString());
                    mapa.put("email", email_Nya.getText().toString());
                    mapa.put("service", services_selected);
                    mapa.put("changed", "No");
                    mapa.put("imageUrl", imageUrl);
                    mapa.put("sched_date", sched);
                    mapa.put("sched_time", sched_time.getText().toString());

                    //this is the code for sharedPreferences
                    SharedPreferences time_prefer = getSharedPreferences("Time", MODE_PRIVATE);
                    SharedPreferences.Editor edit_time = time_prefer.edit();
                    edit_time.putString("time", sched_time.getText().toString());
                    edit_time.commit();


                userReference.child(full_Name.getText().toString() + " " + sched_time.getText().toString()).child((full_Name.getText().toString() + " " + sched_time.getText().toString())).updateChildren(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();
                                builder.setTitle("Booking has been added");
                                builder.setMessage("Your information has been uploaded to the database. \nPresent the QR in the next page to the receptionist to confirm your appointment");
                                builder.setIcon(R.drawable.approved);
                                builder.setPositiveButton("Generate", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent a = new Intent(patient_booking.this, QR_maker.class);
                                        a.putExtra("code", full_Name.getText().toString() + " " + sched_time.getText().toString());
                                        startActivity(a);
                                        patient_booking.this.finish();
                                    }
                                });
                                builder.show();
                            }
                            else {
                                pd.dismiss();
                                builder.setTitle("Procedure terminated");
                                builder.setMessage("Error occurred. Try again later");
                                builder.setIcon(R.drawable.error);
                                builder.show();
                            }
                        }
                    });


                }

        });
    }
}