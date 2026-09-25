package model.entity;

import java.time.LocalTime;
import java.util.HashMap;

/**
 * Represents an evaluation associated with a module.
 * Stores the grades of each student and allows computing their average.
 * @author Kevin Boeffard
 */
public class Evaluation {

    /** Identifier of the resource. */
    private String ident;

    /** Coefficient applied to this resource. */
    private double coeff;

    /** Module this resource belongs to. */
    private Module module;

    /** Unique identifier of the evaluation in the database. */
    private int numero;

    /** Map associating each student to their grade for this resource. */
    private HashMap<Etudiant, Double> notes;

    /** Duration of the evaluation. */
    private LocalTime duree;

    /**
     * Creates a new Evaluation with the given attributes.
     *
     * @throws IllegalArgumentException if type is null or empty, if coeff is negative or zero, or if m is null.
     *
     * @param type  the identifier of the evaluation
     * @param coeff the coefficient applied to this evaluation
     * @param m     the module this evaluation belongs to
     */
    public Evaluation(String type, double coeff, Module m, LocalTime duree) {
        if(type == null || type.trim().isEmpty()) throw new IllegalArgumentException("Le type de l'évaluation ne peut pas être vide ou null");
        if(coeff <= 0) throw new IllegalArgumentException("Le coefficient de l'évaluation ne peut pas être négatif ou null");
        if(m == null) throw new IllegalArgumentException("Le module de l'évaluation ne peut pas être null");
        if(duree != null && duree.equals(LocalTime.MIDNIGHT)) throw new IllegalArgumentException("La durée ne peut pas être nulle (00:00:00)");

        this.coeff = coeff;
        this.module = m;
        this.ident = type;
        this.duree = duree;
        this.notes = new HashMap<>();
    }

    /**
     * Returns a string representation of the evaluation.
     * Contains the identifier, coefficient, module name and the grade of each student.
     *
     * @return a string containing the identifier, coefficient, module name and grades of the evaluation
     */
    public String toString() {
        String ret = "ident = " + this.ident + "\n";
        ret += "coeff = " + this.coeff + "\n";
        ret += "Module = " + this.module.getIntitule() + "\n";
        ret += "Notes : \n";

        for(Etudiant etudiant : this.notes.keySet()) {
            ret += "| -> " + etudiant.getPrenom() + " " + etudiant.getNom() + " : " + this.notes.get(etudiant);
        }

        return ret;
    }

    /**
     * Adds a grade for the given student.
     *
     * @param e      the student to add a grade for
     * @param valeur the grade to assign to the student
     */
    public void ajouterNote(Etudiant e, double valeur) {
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        this.notes.put(e, valeur);
    }

    /**
     * Calculates and returns the average grade of all students for this evaluation.
     * Returns 0 if no grades have been added.
     *
     * @return the average grade of all students
     */
    public double calculerMoyenne() {
        double moyenne = 0;
        for(Etudiant etudiant : notes.keySet()) {
            moyenne += this.notes.get(etudiant);
        }

        if(this.notes.size() != 0) moyenne /= this.notes.size();

        return moyenne;
    }

    /**
     * Returns the grade of the given student for this evaluation.
     *
     * @throws IllegalArgumentException if the student has no grade for this evaluation.
     *
     * @param e the student to get the grade of
     * @return the grade of the given student
     */
    public Double getNote(Etudiant e) {
        if(!this.notes.containsKey(e)) throw new IllegalArgumentException("L'etudiant ne possède pas de note pour l'évaluation");
        return this.notes.get(e);
    }

    /**
     * Returns the identifier of the evaluation.
     * @return the identifier of the evaluation
     */
    public String getIdent() {
        return this.ident;
    }

    /**
     * Returns the coefficient of the evaluation.
     * @return the coefficient of the evaluation
     */
    public double getCoeff() {
        return this.coeff;
    }

    /**
     * Returns the module this evaluation belongs to.
     * @return the module of the evaluation
     */
    public Module getModule() {
        return this.module;
    }

    /**
     * Returns the duration of the evaluation.
     * @return the duration of the evaluation, or null if undefined
     */
    public LocalTime getDuree() {
        return this.duree;
    }

    /**
     * Returns a copy of the grades map of this evaluation.
     * @return a copy of the map associating each student to their grade
     */
    public HashMap<Etudiant, Double> getNotes() {
        return new HashMap<Etudiant, Double>(this.notes);
    }

    /**
     * Sets the identifier of the evaluation.
     * @throws IllegalArgumentException if type is null or empty.
     * @param type the new identifier of the evaluation
     */
    public void setIdent(String type) {
        if(type == null || type.trim().isEmpty()) throw new IllegalArgumentException("Le type de l'évaluation ne peut pas être vide ou null");
        this.ident = type;
    }

    /**
     * Sets the coefficient of the evaluation.
     * @throws IllegalArgumentException if coeff is negative or zero.
     * @param coeff the new coefficient of the evaluation
     */
    public void setCoeff(double coeff) {
        if(coeff <= 0) throw new IllegalArgumentException("Le coefficient de l'évaluation ne peut pas être négatif ou null");
        this.coeff = coeff;
    }

    /**
     * Sets the module of the evaluation.
     * @throws IllegalArgumentException if m is null.
     * @param m the new module of the evaluation
     */
    public void setModule(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module de l'évaluation ne peut pas être null");
        this.module = m;
    }

    /**
     * Returns the unique identifier of the evaluation.
     * @return the unique identifier of the evaluation
     */
    public int getNumero() {
        return this.numero;
    }

    /**
     * Sets the unique identifier of the evaluation.
     * @param numero the new unique identifier of the evaluation
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * Sets the duration of the evaluation.
     * @param duree the new duration of the evaluation, or null if undefined
     */
    public void setDuree(LocalTime duree) {
    if(duree != null && duree.equals(LocalTime.MIDNIGHT)) throw new IllegalArgumentException("La durée ne peut pas être nulle (00:00:00)");
    this.duree = duree;
}

}
