package com.milfhey.enterprisemanager;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExportActivity extends AppCompatActivity {
    private TextView exportTextView;
    private Button exportButton;
    private DatabaseReference databaseReference;
    private static final int CREATE_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        exportTextView = findViewById(R.id.exportTextView);
        exportButton = findViewById(R.id.exportButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        exportButton.setOnClickListener(v -> exportCompanies());
    }

    private void exportCompanies() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder exportData = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Company company = snapshot.getValue(Company.class);
                    if (company != null) {
                        exportData.append("Nom: ").append(company.getName()).append("\n")
                                .append("Adresse: ").append(company.getAddress()).append("\n")
                                .append("Téléphone: ").append(company.getPhone()).append("\n\n");
                    }
                }
                exportTextView.setText(exportData.toString());
                createFile(exportData.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExportActivity.this, "Erreur de chargement des données", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createFile(String data) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "entreprises.txt");

        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                writeToFile(uri, exportTextView.getText().toString());
            }
        }
    }

    private void writeToFile(Uri uri, String data) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            outputStream.write(data.getBytes());
            outputStream.close();
            Toast.makeText(this, "Fichier exporté avec succès", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors de l'exportation du fichier", Toast.LENGTH_SHORT).show();
        }
    }
}
