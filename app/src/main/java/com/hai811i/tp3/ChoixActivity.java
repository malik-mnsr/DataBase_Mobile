package com.hai811i.tp3;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChoixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix);

        Button buttonNouvelleInscription = findViewById(R.id.buttonNouvelleInscription);
        Button buttonConnexion = findViewById(R.id.buttonConnexion);

        // Naviguer vers l'activité de nouvelle inscription
        buttonNouvelleInscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoixActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Naviguer vers l'activité de connexion
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoixActivity.this, ConnexionActivity.class);
                startActivity(intent);
            }
        });
    }
}