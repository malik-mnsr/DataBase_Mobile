package com.hai811i.tp3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Fragment1 extends Fragment {

    private EditText editTextLogin, editTextPassword, editTextNom, editTextPrenom, editTextDateNaissance, editTextTelephone, editTextEmail;
    private CheckBox checkBoxSport, checkBoxMusique, checkBoxLecture;
    private Button buttonSoumettre;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);


        editTextLogin = view.findViewById(R.id.editTextLogin);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextNom = view.findViewById(R.id.editTextNom);
        editTextPrenom = view.findViewById(R.id.editTextPrenom);
        editTextDateNaissance = view.findViewById(R.id.editTextDateNaissance);
        editTextTelephone = view.findViewById(R.id.editTextTelephone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        checkBoxSport = view.findViewById(R.id.checkBoxSport);
        checkBoxMusique = view.findViewById(R.id.checkBoxMusique);
        checkBoxLecture = view.findViewById(R.id.checkBoxLecture);
        buttonSoumettre = view.findViewById(R.id.signup_button);
        dbHelper = new DatabaseHelper(getActivity());

        buttonSoumettre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String login = editTextLogin.getText().toString();
                String password = editTextPassword.getText().toString();
                String nom = editTextNom.getText().toString();
                String prenom = editTextPrenom.getText().toString();
                String dateNaissance = editTextDateNaissance.getText().toString();
                String telephone = editTextTelephone.getText().toString();
                String email = editTextEmail.getText().toString();
                boolean sport = checkBoxSport.isChecked();
                boolean musique = checkBoxMusique.isChecked();
                boolean lecture = checkBoxLecture.isChecked();


                if (!validateLogin(login)) {
                    Toast.makeText(getActivity(), "Login invalide : doit commencer par une lettre et ne pas dépasser 10 caractères.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validatePassword(password)) {
                    Toast.makeText(getActivity(), "Mot de passe invalide : doit inclure au moins 6 caractères.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.checkLoginExists(login)) {
                    Toast.makeText(getActivity(), "Login déjà utilisé.", Toast.LENGTH_SHORT).show();
                    return;
                }


                dbHelper.addUser(login, password, nom, prenom, dateNaissance, telephone, email, sport, musique, lecture);


                Bundle bundle = new Bundle();
                bundle.putString("login", login);
                bundle.putString("password", password);
                bundle.putString("nom", nom);
                bundle.putString("prenom", prenom);
                bundle.putString("dateNaissance", dateNaissance);
                bundle.putString("telephone", telephone);
                bundle.putString("email", email);
                bundle.putBoolean("sport", sport);
                bundle.putBoolean("musique", musique);
                bundle.putBoolean("lecture", lecture);


                Fragment2 fragment2 = new Fragment2();
                fragment2.setArguments(bundle);


                ((MainActivity) getActivity()).navigateToFragment2(fragment2);
            }
        });

        return view;
    }

    private boolean validateLogin(String login) {
        return login.matches("[a-zA-Z][a-zA-Z0-9]{0,9}");
    }

    private boolean validatePassword(String password) {
        return password.length() >= 6;
    }
}