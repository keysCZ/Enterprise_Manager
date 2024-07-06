package com.milfhey.enterprisemanager;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vérifier si l'utilisateur est connecté
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Utilisateur connecté, rediriger vers CompanyListActivity
            startActivity(new Intent(this, CompanyListActivity.class));
        } else {
            // Utilisateur non connecté, rediriger vers LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        }
        // Fermer MainActivity pour ne pas revenir en arrière
        finish();
    }
}
