package com.milfhey.enterprisemanager;

import android.os.Bundle;
import android.text.TextUtils;
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
    private TextView companyNameTextView, companyAddressTextView, companyPhoneTextView;
    private EditText commentEditText;
    private Button addCommentButton;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private DatabaseReference databaseReference;
    private List<Comment> commentList;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        companyNameTextView = findViewById(R.id.nameTextView);
        companyAddressTextView = findViewById(R.id.addressTextView);
        companyPhoneTextView = findViewById(R.id.phoneTextView);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton = findViewById(R.id.addCommentButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setAdapter(commentsAdapter);

        companyId = getIntent().getStringExtra("companyId");
        databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(companyId);

        addCommentButton.setOnClickListener(v -> addComment());

        loadCompanyDetails();
        loadComments();
    }

    private void loadCompanyDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null) {
                    companyNameTextView.setText(company.getName());
                    companyAddressTextView.setText(company.getAddress());
                    companyPhoneTextView.setText(company.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CompanyDetailActivity.this, "Erreur de chargement des détails de l'entreprise", Toast.LENGTH_SHORT).show();
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
