package com.hai811i.tp3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConnexionActivity extends AppCompatActivity {

    private EditText editTextLogin, editTextPassword;
    private Button buttonConnexion;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonConnexion = findViewById(R.id.buttonConnexion);
        dbHelper = new DatabaseHelper(this);

        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();

                if (dbHelper.checkUser(login, password)) {
                    // Récupérer les informations de l'utilisateur
                    User user = dbHelper.getUser(login);

                    if (user != null) {

                        Intent intent = new Intent(ConnexionActivity.this, PlanningActivity.class);
                        intent.putExtra("login", user.getLogin());
                        intent.putExtra("nom", user.getNom());
                        intent.putExtra("prenom", user.getPrenom());
                        startActivity(intent);
                    } else {
                        Toast.makeText(ConnexionActivity.this, "Erreur : utilisateur non trouvé.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConnexionActivity.this, "Login ou mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}