package controller;

import model.entity.Utilisateur;

/**
 * Represents one row of the users table for the Directeur des Études view.
 * @author Kevin Boeffard
 */
public class LigneUtilisateur {

    /** Reference to the underlying user, used for deletion. */
    private Utilisateur utilisateur;

    /** Display identifier (student number or teacher identifier). */
    private String identifiant;

    /** Last name. */
    private String nom;

    /** First name. */
    private String prenom;

    /** Email address. */
    private String email;

    /** Display type (Étudiant / Enseignant / Directeur des Études). */
    private String type;

    /** Additional detail (group for students, modules count for teachers). */
    private String detail;

    /**
     * Creates a new row with the given values.
     *
     * @param utilisateur the underlying user
     * @param identifiant the display identifier
     * @param nom         the last name
     * @param prenom      the first name
     * @param email       the email address
     * @param type        the display type
     * @param detail      the additional detail
     */
    public LigneUtilisateur(Utilisateur utilisateur, String identifiant, String nom, String prenom, String email, String type, String detail) {
        this.utilisateur = utilisateur;
        this.identifiant = identifiant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.type = type;
        this.detail = detail;
    }

    /**
     * Returns the underlying user.
     * @return the underlying user
     */
    public Utilisateur getUtilisateur() {
        return this.utilisateur;
    }

    /**
     * Returns the display identifier.
     * @return the display identifier
     */
    public String getIdentifiant() {
        return this.identifiant;
    }

    /**
     * Returns the last name.
     * @return the last name
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Returns the first name.
     * @return the first name
     */
    public String getPrenom() {
        return this.prenom;
    }

    /**
     * Returns the email address.
     * @return the email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the display type.
     * @return the display type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the additional detail.
     * @return the additional detail
     */
    public String getDetail() {
        return this.detail;
    }
}