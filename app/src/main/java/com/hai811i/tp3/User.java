package com.hai811i.tp3;


public class User {
    private String login;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String telephone;
    private String email;
    private boolean sport;
    private boolean musique;
    private boolean lecture;

    public User(String login, String nom, String prenom, String dateNaissance, String telephone, String email, boolean sport, boolean musique, boolean lecture) {
        this.login = login;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.email = email;
        this.sport = sport;
        this.musique = musique;
        this.lecture = lecture;
    }


    public String getLogin() { return login; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getDateNaissance() { return dateNaissance; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public boolean isSport() { return sport; }
    public boolean isMusique() { return musique; }
    public boolean isLecture() { return lecture; }
}