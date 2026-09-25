package controller;

/**
 * Represents one row of the students table for the teacher view.
 * @author Kevin Boeffard
 */
public class LigneEleve {

    /** Student number. */
    private String numEtudiant;

    /** Last name of the student. */
    private String nom;

    /** First name of the student. */
    private String prenom;

    /** Display name of the student's promotion and group. */
    private String groupe;

    /** Formatted overall average to display. */
    private String moyenne;

    /**
     * Creates a new row with the given values.
     *
     * @param numEtudiant the student number
     * @param nom         the last name of the student
     * @param prenom      the first name of the student
     * @param groupe      the display name of the promotion and group
     * @param moyenne     the formatted overall average
     */
    public LigneEleve(String numEtudiant, String nom, String prenom, String groupe, String moyenne) {
        this.numEtudiant = numEtudiant;
        this.nom = nom;
        this.prenom = prenom;
        this.groupe = groupe;
        this.moyenne = moyenne;
    }

    /**
     * Returns the student number.
     * @return the student number
     */
    public String getNumEtudiant() {
        return this.numEtudiant;
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
     * Returns the display name of the promotion and group.
     * @return the promotion and group
     */
    public String getGroupe() {
        return this.groupe;
    }

    /**
     * Returns the formatted overall average.
     * @return the formatted overall average
     */
    public String getMoyenne() {
        return this.moyenne;
    }
}