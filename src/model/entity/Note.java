package model.entity;

/**
 * Represents a grade assigned to a student for a given evaluation.
 * @author Kevin Boeffard
 */
public class Note {

    /** The student. */
    private Etudiant etudiant;

    /** The evaluation. */
    private Evaluation evaluation;

    /** The grade value. */
    private Double valeur;

    /** The status of the grade. */
    private EnumStatutNote statut;

    /**
     * Creates a new Note with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is null, or if valeur is negative.
     *
     * @param etudiant   the student
     * @param evaluation the evaluation
     * @param valeur     the grade value, or null if not yet assigned
     * @param statut     the status of the grade
     */
    public Note(Etudiant etudiant, Evaluation evaluation, Double valeur, EnumStatutNote statut) {
        if(etudiant == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        if(evaluation == null) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        if(statut == null) throw new IllegalArgumentException("Le statut ne peut pas être null");
        if(valeur != null && valeur < 0) throw new IllegalArgumentException("La valeur ne peut pas être négative");

        this.etudiant = etudiant;
        this.evaluation = evaluation;
        this.valeur = valeur;
        this.statut = statut;
    }

    /**
     * Returns the student.
     * @return the student
     */
    public Etudiant getEtudiant() { return this.etudiant; }

    /**
     * Returns the evaluation.
     * @return the evaluation
     */
    public Evaluation getEvaluation() { return this.evaluation; }

    /**
     * Returns the grade value.
     * @return the grade value, or null if not yet assigned
     */
    public Double getValeur() { return this.valeur; }

    /**
     * Returns the status of the grade.
     * @return the status of the grade
     */
    public EnumStatutNote getStatut() { return this.statut; }

    /**
     * Sets the grade value.
     * @throws IllegalArgumentException if valeur is negative.
     * @param valeur the new grade value, or null if not yet assigned
     */
    public void setValeur(Double valeur) {
        if(valeur != null && valeur < 0) throw new IllegalArgumentException("La valeur ne peut pas être négative");
        this.valeur = valeur;
    }

    /**
     * Sets the status of the grade.
     * @throws IllegalArgumentException if statut is null.
     * @param statut the new status of the grade
     */
    public void setStatut(EnumStatutNote statut) {
        if(statut == null) throw new IllegalArgumentException("Le statut ne peut pas être null");
        this.statut = statut;
    }
}