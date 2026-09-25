package model.entity;

import java.util.ArrayList;

/**
 * Represents a group associated with a promotion.
 * Stores the students and modules associated with the group.
 * @author Kevin Boeffard
 */
public class GroupeTD {

    /** Name of the group. */
    private String nom;

    /** Promotion this group belongs to. */
    private Promotion promotion;

    /** List of students in this group. */
    private ArrayList<Etudiant> etudiants;

    /** List of modules followed by this group. */
    private ArrayList<Module> modulesSuivis;
    
    /**
     * Creates a new GroupeTD with the given attributes.
     *
     * @throws IllegalArgumentException if nom is null or empty, or if promo is null.
     *
     * @param nom   the name of the group
     * @param promo the promotion this group belongs to
     */
    public GroupeTD(String nom, Promotion promo) {
        if(nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom du groupe TD ne peut pas être vide ou null");
        if(promo == null) throw new IllegalArgumentException("La promotion du groupe TD ne peut pas être null");

        this.nom = nom;
        this.promotion = promo;
        this.etudiants = new ArrayList<>();
        this.modulesSuivis = new ArrayList<>();
    }

    /**
     * Returns a string representation of the group.
     * Contains the promotion, name and list of students.
     *
     * @return a string containing the promotion, name and students of the group
     */
    public String toString() {
        String ret = "Groupe TD = " + this.promotion.getNomPromo() + " " + this.nom + "\n";
        ret += "Etudiants : \n";

        for(Etudiant etudiant : this.etudiants) {
            ret += "| -> " + etudiant.getPrenom() + " " + etudiant.getNom() + "\n";
        } 

        return ret;
    }

    /**
     * Adds a student to this group.
     *
     * @throws IllegalArgumentException if e is null or already in the list.
     *
     * @param e the student to add
     */
    public void ajouterEtudiant(Etudiant e) {
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        if(this.etudiants.contains(e)) throw new IllegalArgumentException("L'étudiant est déjà dans la liste");

        this.etudiants.add(e);
    }

    /**
     * Adds a module to the list of modules followed by this group.
     *
     * @throws IllegalArgumentException if m is null or already in the list.
     *
     * @param m the module to add
     */
    public void ajouterModule(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        if(this.modulesSuivis.contains(m)) throw new IllegalArgumentException("Le module est déjà dans la liste");

        this.modulesSuivis.add(m);
    }

    /**
     * Removes a module from the list of modules followed by this group.
     *
     * @throws IllegalArgumentException if m is null or not in the list.
     *
     * @param m the module to remove
     */
    public void retirerModule(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        if(!this.modulesSuivis.contains(m)) throw new IllegalArgumentException("Le module n'est pas dans la liste");

        this.modulesSuivis.remove(m);
    }

    /**
     * Checks whether this group is equal to the given object.
     * Two groups are equal if they have the same name and promotion.
     *
     * @param gr the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    public boolean equals(GroupeTD gr) {
        return this.nom.equals(gr.nom) && this.promotion.getNomPromo().equals(gr.promotion.getNomPromo());
    }

    /**
     * Returns the name of the group.
     * @return the name of the group
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Returns the promotion this group belongs to.
     * @return the promotion of the group
     */
    public Promotion getPromotion() {
        return this.promotion;
    }

    /**
     * Returns a copy of the list of students in this group.
     * @return a copy of the list of students
     */
    public ArrayList<Etudiant> getEtudiants() {
        return new ArrayList<>(this.etudiants);
    }

    /**
     * Returns a copy of the list of modules followed by this group.
     * @return a copy of the list of modules
     */
    public ArrayList<Module> getModulesSuivis() {
        return new ArrayList<>(this.modulesSuivis);
    }

    /**
     * Sets the name of the group.
     * @throws IllegalArgumentException if nom is null or empty.
     * @param nom the new name of the group
     */
    public void setNom(String nom) {
        if(nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom du groupe TD ne peut pas être vide ou null");
        this.nom = nom;
    }

    /**
     * Sets the promotion of the group.
     * @throws IllegalArgumentException if promotion is null.
     * @param promotion the new promotion of the group
     */
    public void setPromotion(Promotion promotion) {
        if(promotion == null) throw new IllegalArgumentException("La promotion ne peut pas être null");
        this.promotion = promotion;
    }
}