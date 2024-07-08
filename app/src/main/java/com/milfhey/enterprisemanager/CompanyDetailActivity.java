package com.milfhey.enterprisemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CompanyDetailActivity extends AppCompatActivity {
    private EditText companyNameEditText, companyAddressEditText, companyPhoneEditText;
    private EditText commentEditText;
    private Button addCommentButton, saveButton;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private DatabaseReference databaseReference;
    private List<Comment> commentList;
    private String companyId;
    private boolean isEditing = false;

    @Override/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        companyNameEditText = findViewById(R.id.nameEditText);
        companyAddressEditText = findViewById(R.id.addressEditText);
        companyPhoneEditText = findViewById(R.id.phoneEditText);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);
        saveButton = findViewById(R.id.saveButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setAdapter(commentsAdapter);

        companyId = getIntent().getStringExtra("companyId");
        isEditing = companyId != null;

        if (isEditing) {
            databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(companyId);
            loadCompanyDetails();
            loadComments();
            saveButton.setText("Mettre à jour");
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            saveButton.setText("Ajouter");
        }

        addCommentButton.setOnClickListener(v -> addComment());
        saveButton.setOnClickListener(v -> saveCompany());
    }

    private void loadCompanyDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null) {
                    companyNameEditText.setText(company.getName());
                    companyAddressEditText.setText(company.getAddress());
                    companyPhoneEditText.setText(company.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompanyDetailActivity.this, "Erreur de chargement des détails de l'entreprise", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCompany() {
        String name = companyNameEditText.getText().toString().trim();
        String address = companyAddressEditText.getText().toString().trim();
        String phone = companyPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Veuillez entrer le nom de l'entreprise", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Veuillez entrer l'adresse de l'entreprise", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Veuillez entrer le téléphone de l'entreprise", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditing) {
            updateCompanyField("name", name);
            updateCompanyField("address", address);
            updateCompanyField("phone", phone);
        } else {
            String companyId = databaseReference.push().getKey();
            Company company = new Company(companyId, name, address, phone);

            if (companyId != null) {
                databaseReference.child(companyId).setValue(company)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Entreprise ajoutée avec succès", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Erreur lors de l'ajout de l'entreprise", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    public void editPhone(View view) {
        showEditDialog("Téléphone de l'entreprise", companyPhoneEditText, "phone");
    }

    public void editName(View view) {
        showEditDialog("Nom de l'entreprise", companyNameEditText, "name");
    }

    public void editAddress(View view) {
        showEditDialog("Adresse de l'entreprise", companyAddressEditText, "address");
    }

    private void showEditDialog(String title, final EditText editText, final String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier " + title);

        final EditText input = new EditText(this);
        input.setText(editText.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newValue = input.getText().toString();
                if (!newValue.isEmpty()) {
                    editText.setText(newValue);
                    updateCompanyField(field, newValue);
                } else {
                    Toast.makeText(CompanyDetailActivity.this, "Le champ ne peut pas être vide", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateCompanyField(String field, String value) {
        databaseReference.child(field).setValue(value)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CompanyDetailActivity.this, "Mise à jour réussie", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompanyDetailActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Veuillez entrer un commentaire", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = databaseReference.child("comments").push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Comment comment = new Comment(commentId, userId, commentText, System.currentTimeMillis());

        if (commentId != null) {
            databaseReference.child("comments").child(commentId).setValue(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                            commentEditText.setText("");
                        } else {
                            Toast.makeText(this, "Erreur d'ajout du commentaire", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadComments() {
        databaseReference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompanyDetailActivity.this, "Erreur de chargement des commentaires", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
