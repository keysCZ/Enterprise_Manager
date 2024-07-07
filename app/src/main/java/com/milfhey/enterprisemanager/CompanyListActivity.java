package com.milfhey.enterprisemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompanyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton addCompanyButton;
    private Button exportButton;
    private CompanyAdapter companyAdapter;
    private DatabaseReference databaseReference;
    private List<Company> companyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        recyclerView = findViewById(R.id.recyclerView);
        addCompanyButton = findViewById(R.id.addCompanyButton);
        exportButton = findViewById(R.id.exportButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyList = new ArrayList<>();
        companyAdapter = new CompanyAdapter(companyList, this);
        recyclerView.setAdapter(companyAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        addCompanyButton.setOnClickListener(v -> startActivity(new Intent(this, CompanyDetailActivity.class)));
        exportButton.setOnClickListener(v -> startActivity(new Intent(this, ExportActivity.class)));

        loadCompanies();
    }

    private void loadCompanies() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                companyList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Company company = snapshot.getValue(Company.class);
                    if (company != null) {
                        companyList.add(company);
                    }
                }
                companyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CompanyListActivity.this, "Erreur de chargement des données", Toast.LENGTH_SHORT).show();
            }
        });
    }
}