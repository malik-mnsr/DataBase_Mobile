package com.hai811i.tp3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment {

    private TextView textViewLogin, textViewNom, textViewPrenom, textViewDateNaissance, textViewTelephone, textViewEmail, textViewCentresInteret;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);


        textViewLogin = view.findViewById(R.id.textViewLogin);
        textViewNom = view.findViewById(R.id.textViewNom);
        textViewPrenom = view.findViewById(R.id.textViewPrenom);
        textViewDateNaissance = view.findViewById(R.id.textViewDateNaissance);
        textViewTelephone = view.findViewById(R.id.textViewTelephone);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewCentresInteret = view.findViewById(R.id.textViewCentresInteret);


        Bundle bundle = getArguments();
        if (bundle != null) {

            textViewLogin.setText("Login: " + bundle.getString("login"));
            textViewNom.setText("Nom: " + bundle.getString("nom"));
            textViewPrenom.setText("Prénom: " + bundle.getString("prenom"));
            textViewDateNaissance.setText("Date de naissance: " + bundle.getString("dateNaissance"));
            textViewTelephone.setText("Téléphone: " + bundle.getString("telephone"));
            textViewEmail.setText("Email: " + bundle.getString("email"));


            StringBuilder centresInteret = new StringBuilder("Centres d'intérêt: ");
            if (bundle.getBoolean("sport")) centresInteret.append("Sport, ");
            if (bundle.getBoolean("musique")) centresInteret.append("Musique, ");
            if (bundle.getBoolean("lecture")) centresInteret.append("Lecture, ");
            textViewCentresInteret.setText(centresInteret.toString());
        }

        return view;
    }
}