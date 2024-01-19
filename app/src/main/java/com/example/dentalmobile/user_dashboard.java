package com.example.dentalmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dentalmobile.adapter.user_adapter;
import com.example.dentalmobile.model.booking_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_dashboard extends AppCompatActivity {

    RecyclerView user_booking;
    DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference("Patient Bookings");
    FirebaseRecyclerOptions<booking_model> queue;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button create, cancel;
    String getTime;

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
        setContentView(R.layout.activity_user_dashboard);

        user_booking = findViewById(R.id.patient_record);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        create = findViewById(R.id.create_booking);
        cancel = findViewById(R.id.cancelBooking);
        gsc = GoogleSignIn.getClient(this, gso);

        //Google Sign-in stuff
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //SharedPreferences Getter
        SharedPreferences time_getter = getSharedPreferences("Time", MODE_PRIVATE);
        getTime = time_getter.getString("time", "");


        //RECYCLER POPULATOR
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(user_dashboard.this,RecyclerView.VERTICAL, false);
        user_booking.setLayoutManager(layoutManager);
        queue = new FirebaseRecyclerOptions.Builder<booking_model>().setQuery(patientRef.child(account.getDisplayName() +" "+getTime), booking_model.class).build();
        user_adapter user_adapter = new user_adapter(queue);
        user_booking.setAdapter(user_adapter);
        user_adapter.startListening();

        user_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(user_adapter.getItemCount() >0){
                    create.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                }
                else{
                    create.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if(user_adapter.getItemCount() >0){
                    create.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                }
                else{
                    create.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if(user_adapter.getItemCount() >0){
                    create.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                }
                else{
                    create.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder build = new AlertDialog.Builder(user_dashboard.this);
                build.setTitle("Confirm Cancellation");
                build.setMessage("Are you sure you want to proceed with your booking cancellation?");
                build.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressDialog pd = new ProgressDialog(user_dashboard.this);
                        pd.setTitle("Processing Cancellation");
                        pd.setMessage("Request processing. Please wait...");
                        patientRef.child(account.getDisplayName()+" "+getTime).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(user_dashboard.this);
                                builder.setTitle("Cancellation completed");
                                builder.setMessage("Your booking cancellation has been successful.");
                                builder.setIcon(R.drawable.approved);
                                builder.setCancelable(true);
                                builder.show();
                            }
                        });
                    }
                });
                build.show();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_booking = new Intent(user_dashboard.this, patient_booking.class);
                startActivity(goto_booking);
            }
        });

    }

}