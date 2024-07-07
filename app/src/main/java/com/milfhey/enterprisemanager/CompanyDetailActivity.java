package com.milfhey.enterprisemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyDetailActivity extends AppCompatActivity {
    private TextView nameTextView, addressTextView, phoneTextView;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        nameTextView = findViewById(R.id.nameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        saveButton = findViewById(R.id.saveButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Intent intent = getIntent();
        companyId = intent.getStringExtra("companyId");
        if (companyId != null) {
            loadCompanyDetails();
        }

        saveButton.setOnClickListener(v -> saveCompany());
    }

    private void loadCompanyDetails() {
        databaseReference.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                if (company != null) {
                    nameTextView.setText(company.getName());
                    addressTextView.setText(company.getAddress());
                    phoneTextView.setText(company.getPhone());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CompanyDetailActivity.this, "Erreur de chargement des données", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCompany() {
        String name = nameTextView.getText().toString().trim();
        String address = addressTextView.getText().toString().trim();
        String phone = phoneTextView.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (companyId == null) {
            companyId = databaseReference.push().getKey();
        }

        Company company = new Company(companyId, name, address, phone);
        databaseReference.child(companyId).setValue(company)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Entreprise enregistrée avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void editName(View view) {
        showEditDialog(nameTextView, "Nom");
    }

    public void editAddress(View view) {
        showEditDialog(addressTextView, "Adresse");
    }

    public void editPhone(View view) {
        showEditDialog(phoneTextView, "Téléphone");
    }

    private void shareCompany(String userIdToShare) {
        if (companyId != null) {
            databaseReference.child(companyId).child("sharedWith").push().setValue(userIdToShare)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Entreprise partagée avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Erreur de partage", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addComment(String commentText) {
        String commentId = databaseReference.child(companyId).child("comments").push().getKey();
        Comment comment = new Comment(commentId, FirebaseAuth.getInstance().getCurrentUser().getUid(), commentText, System.currentTimeMillis());
        databaseReference.child(companyId).child("comments").child(commentId).setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Commentaire ajouté avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur d'ajout de commentaire", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditDialog(final TextView textView, String fieldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier " + fieldName);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(textView.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> textView.setText(input.getText().toString()));
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
