package com.example.dentalmobile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button log, sign_on;
    SignInButton reg;
    EditText email, pass;
    FirebaseAuth auth;
    ProgressDialog pd;
    AlertDialog.Builder build;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

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

        setContentView(R.layout.activity_main);


        //FOR THE PERMISSION SHIT
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        }, PackageManager.PERMISSION_GRANTED);


        reg = findViewById(R.id.register);
        log = findViewById(R.id.login);
        sign_on = findViewById(R.id.sign_on);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.passkey);


        //THIS IS FOR THE CHANGING OF DESCRIPTION NG SIGN IN BUTTON
        View view = reg.getChildAt(0);
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Set the text of the button
            textView.setText("Sign in using Google");
        }


        //THIS IS PARA SA GOOGLE ACCOUNT SIGNING IN
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);


        //FOR LOGOUT BUTTON
        log.setOnClickListener(v -> {
            email.setVisibility(View.VISIBLE);
            pass.setVisibility(View.VISIBLE);
            sign_on.setVisibility(View.VISIBLE);
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        sign_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAuth = email.getText().toString();
                String passAuth = pass.getText().toString();
                if (emailAuth.isEmpty() || passAuth.isEmpty()) {
                    if (emailAuth.isEmpty()) {
                        email.setError("Requires an input of valid email");
                    }
                    if (passAuth.isEmpty()) {
                        pass.setError("Please provide a password");
                    }
                } else {
                    authenticate(emailAuth, passAuth);
                }

            }
        });
    }

    private void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                gotoBooking();

            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            }
        }
    }

    private void gotoBooking() {
        Log.d("Success", "Log Success");
        Intent a = new Intent(MainActivity.this, user_dashboard.class);
        startActivity(a);
    }

    private void authenticate(String email, String passkey) {
        auth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(MainActivity.this);
        build = new AlertDialog.Builder(MainActivity.this);

        pd.setTitle("Signing on");
        pd.setMessage("Signing on, kindly wait for a while...");
        pd.show();

        auth.signInWithEmailAndPassword(email, passkey).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pd.dismiss();
                build.setTitle("Sign on successful");
                build.setMessage("Welcome: " + Objects.requireNonNull(authResult.getUser()).getEmail());
                build.setPositiveButton("Continue", (dialogInterface, i) -> {
                    if (email.contains("admin@dentalappoint.system")) {
                        dialogInterface.dismiss();
                        Intent a = new Intent(MainActivity.this, admin_dashboard.class);
                        MainActivity.this.startActivity(a);
                        MainActivity.this.finish();
                    } else {
                        dialogInterface.dismiss();
                        FirebaseUser userInfo = authResult.getUser();
                        if(userInfo != null){
                            Toast.makeText(MainActivity.this, userInfo.getDisplayName(), Toast.LENGTH_SHORT).show();
                            Intent a = new Intent(MainActivity.this, patient_booking.class);
                            a.putExtra("code", userInfo.getDisplayName());
                           MainActivity.this.startActivity(a);
                        }



                    }
                });
                build.setIcon(R.drawable.approved);
                build.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                build.setTitle("Sign in failed");
                build.setMessage("Access is incomplete due to: " + e.getMessage());
                build.setCancelable(true);
                build.setIcon(R.drawable.error);
                build.show();
            }
        });
    }
}