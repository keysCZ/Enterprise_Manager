package com.milfhey.enterprisemanager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private List<Company> filteredList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        addCompanyButton = findViewById(R.id.addCompanyButton);
        exportButton = findViewById(R.id.exportButton);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyList = new ArrayList<>();
        filteredList = new ArrayList<>();
        companyAdapter = new CompanyAdapter(filteredList, this);
        recyclerView.setAdapter(companyAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("companies")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        addCompanyButton.setOnClickListener(v -> startActivity(new Intent(this, CompanyDetailActivity.class)));
        exportButton.setOnClickListener(v -> startActivity(new Intent(this, ExportActivity.class)));

        loadCompanies();
        setupSearchView();
        setupItemTouchHelper();
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
                filteredList.clear();
                filteredList.addAll(companyList);
                companyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CompanyListActivity.this, "Erreur de chargement des données", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(companyList);
        } else {
            text = text.toLowerCase();
            for (Company company : companyList) {
                if (company.getName().toLowerCase().contains(text)) {
                    filteredList.add(company);
                }
            }
        }
        companyAdapter.notifyDataSetChanged();
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                // Dessinez une couleur rouge et une icône de suppression
                View itemView = viewHolder.itemView;
                Paint p = new Paint();
                p.setColor(Color.RED);
                RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                c.drawRect(background, p);
                Drawable deleteIcon = ContextCompat.getDrawable(CompanyListActivity.this, R.drawable.ic_delete);
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer l'entreprise")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette entreprise ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteCompany(position))
                .setNegativeButton("Non", (dialog, which) -> companyAdapter.notifyItemChanged(position))
                .show();
    }

    private void deleteCompany(int position) {
        Company companyToDelete = filteredList.get(position);
        databaseReference.child(companyToDelete.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Entreprise supprimée avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur de suppression", Toast.LENGTH_SHORT).show();
                    }
                });
        filteredList.remove(position);
        companyAdapter.notifyItemRemoved(position);
        companyList.remove(companyToDelete);
    }
}
