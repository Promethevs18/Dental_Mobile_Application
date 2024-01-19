package com.example.dentalmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QR_maker extends AppCompatActivity {

    ImageView qr;
    Button exit;
    String code;
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

        setContentView(R.layout.activity_qr_maker);

        qr = findViewById(R.id.qr);
        exit = findViewById(R.id.returnNow);

        Intent z = getIntent();
        code = z.getStringExtra("code");

        if(code != null){
            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                //initialize bit matrix
                BitMatrix matrix = writer.encode(code, BarcodeFormat.QR_CODE,
                        450, 450);
                //enable barcode encoder
                BarcodeEncoder encoder = new BarcodeEncoder();
                //initialize bitmap
                Bitmap bitmap = encoder.createBitmap(matrix);
                //set bitmap to image
                qr.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(QR_maker.this, MainActivity.class);
                startActivity(k);
                QR_maker.this.finish();
            }
        });

    }
}