package com.example.dentalmobile;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalmobile.adapter.booking_adapter;
import com.example.dentalmobile.model.booking_model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class admin_dashboard extends AppCompatActivity {

    RecyclerView present_list;
    Button scan, log_out, export;
    DatabaseReference regDatabase = FirebaseDatabase.getInstance().getReference("Patient Bookings");
    DatabaseReference patient_Database = FirebaseDatabase.getInstance().getReference("Attendance List");
    FirebaseRecyclerOptions<booking_model> listQueue;
    AlertDialog.Builder build;
    FirebaseAuth auth;
    ProgressDialog pd;
    String count, formatedDate;

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
        setContentView(R.layout.activity_admin_dashboard);

        //THIS IS FOR EXCEL STUFF LIBRARIES
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        //UI INSTANTIATION
        present_list = findViewById(R.id.patient_list);
        scan = findViewById(R.id.scan);
        log_out = findViewById(R.id.log_out);
        export = findViewById(R.id.export);

        //AUTHENTICATION INSTANTIATION
        auth = FirebaseAuth.getInstance();

        //date fetcher
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd yyyy", Locale.getDefault());
        formatedDate = dateFormat.format(date);

        //dashboard queuing
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(admin_dashboard.this, RecyclerView.VERTICAL, false);
        present_list.setLayoutManager(layoutManager);
        listQueue = new FirebaseRecyclerOptions.Builder<booking_model>().setQuery(patient_Database.child(formatedDate), booking_model.class)
                    .build();
        booking_adapter detailsAdapter = new booking_adapter(listQueue);
        present_list.setAdapter(detailsAdapter);
        detailsAdapter.startListening();

        //FOR LOG-OUT
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent z = new Intent(admin_dashboard.this, MainActivity.class);
                admin_dashboard.this.startActivity(z);
                admin_dashboard.this.finish();
            }
        });

        //FOR SCAN
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        //FOR EXPORT TO EXCEL
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        createXL();
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 1000);
                    }
                }
            }
        });
    }

    //for creating excel file
    private void createXL() {
        pd = new ProgressDialog(admin_dashboard.this);
        pd.setTitle("Creating Excel file");
        pd.setMessage("Application is now creating workbook. Please wait...");
        pd.show();

        Workbook excel = new HSSFWorkbook();

        Cell cell;
        Sheet sheet = excel.createSheet("Scanned Patient List");

        //now for the row
        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Full Name");
        cell = row.createCell(1);
        cell.setCellValue("Address");
        cell = row.createCell(2);
        cell.setCellValue("Gender");
        cell = row.createCell(3);
        cell.setCellValue("Email Address");
        cell = row.createCell(4);
        cell.setCellValue("Contact Number");
        cell = row.createCell(5);
        cell.setCellValue("Schedule");
        cell = row.createCell(6);
        cell.setCellValue("Service Type");
        cell = row.createCell(7);
        cell.setCellValue("Rescheduled?");


        sheet.setColumnWidth(0, (20 * 200));
        sheet.setColumnWidth(1, (30 * 200));
        sheet.setColumnWidth(2, (30 * 200));
        sheet.setColumnWidth(3, (30 * 200));
        sheet.setColumnWidth(4, (30 * 200));
        sheet.setColumnWidth(5, (30 * 200));
        sheet.setColumnWidth(6, (30 * 200));
        sheet.setColumnWidth(7, (30 * 200));


        count = String.valueOf(Objects.requireNonNull(present_list.getAdapter()).getItemCount());

        for(int i = 0; i < Integer.parseInt(count) ; i++){
            Row row1 = sheet.createRow(i+1);

            cell = row1.createCell(0);
            cell.setCellValue(listQueue.getSnapshots().get(i).getFullName());
            cell = row1.createCell(1);
            cell.setCellValue(listQueue.getSnapshots().get(i).getAddress());
            cell = row1.createCell(2);
            cell.setCellValue(listQueue.getSnapshots().get(i).getGender());
            cell = row1.createCell(3);
            cell.setCellValue(listQueue.getSnapshots().get(i).getEmail());
            cell = row1.createCell(4);
            cell.setCellValue(listQueue.getSnapshots().get(i).getContact_num());
            cell = row1.createCell(5);
            cell.setCellValue(listQueue.getSnapshots().get(i).getSched());
            cell = row1.createCell(6);

            cell = row1.createCell(7);
            cell.setCellValue(listQueue.getSnapshots().get(i).getChanged());


            sheet.setColumnWidth(0, (20 * 200));
            sheet.setColumnWidth(1, (30 * 200));
            sheet.setColumnWidth(2, (30 * 200));
            sheet.setColumnWidth(3, (30 * 200));
            sheet.setColumnWidth(4, (30 * 200));
            sheet.setColumnWidth(5, (30 * 200));
            sheet.setColumnWidth(6, (30 * 200));
            sheet.setColumnWidth(7, (30 * 200));

        }
        String folderName = "Exported Data";
        String fileName = folderName + System.currentTimeMillis() + ".xlsx";
        String path = Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator + fileName;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
        if(!file.exists()){
            file.mkdirs();
        }
        FileOutputStream output = null;

        try{
            pd.dismiss();
            build  = new AlertDialog.Builder(admin_dashboard.this);
            build.setTitle("Excel file created successfully");
            build.setMessage("Excel file has been created at:"+ path);
            build.setCancelable(true);
            build.setIcon(R.drawable.confirm);

            output = new FileOutputStream(path);
            excel.write(output);
        } catch (FileNotFoundException e) {
            build.setTitle("File not found");
            build.setMessage("Error caused by: "+ e.getMessage());
            build.setIcon(R.drawable.error);
        } catch (IOException e) {
            build.setTitle("File input error");
            build.setMessage("Error detected due to: "+ e.getMessage());
            build.setIcon(R.drawable.error);
        }
        build.show();
    }

    //for qr scanning button
    private void scanCode() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Press volume up to turn on flash\nPress volume down to turn it off");
        scanOptions.setBeepEnabled(true);

        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(Capture_Class.class);
        scanLauncher.launch(scanOptions);
    }
    ActivityResultLauncher<ScanOptions> scanLauncher = registerForActivityResult(new ScanContract(), result -> copyData(regDatabase.child(result.getContents()).child(result.getContents()), patient_Database.child(formatedDate).child(result.getContents())));
    
    private void copyData(DatabaseReference regDatabase, DatabaseReference scanDatabase) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scanDatabase.setValue(dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(admin_dashboard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        regDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
                    // The user granted permission to manage all files on the device
                } else {
                    // The user denied permission to manage all files on the device
                }
            }
        }
    }

}