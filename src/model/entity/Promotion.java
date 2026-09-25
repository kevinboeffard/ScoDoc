package model.entity;

import java.util.ArrayList;

/**
 * Represents a promotion in the BUT Informatique program.
 * Stores the modules and groups associated with the promotion.
 * @author Kevin Boeffard
 */
public class Promotion {

    /** Name of the promotion. */
    private EnumPromo nomPromo;

    /** List of modules associated with this promotion. */
    private ArrayList<Module> modules;

    /** List of groups associated with this promotion. */
    private ArrayList<GroupeTD> groupes;

    /**
     * Creates a new Promotion with the given name.
     *
     * @throws IllegalArgumentException if nom is null.
     *
     * @param nom the name of the promotion
     */
    public Promotion(EnumPromo nom) {
        if(nom == null) throw new IllegalArgumentException("Le nom de la promotion ne peut pas être null");
        if(nom.name().length() > 5) throw new IllegalArgumentException("Le nom de la promotion ne peut pas dépasser 50 caractères");

        this.nomPromo = nom;
        this.modules = new ArrayList<>();
        this.groupes = new ArrayList<>();
    }

    /**
     * Returns a string representation of the promotion.
     * Contains the name, modules and groups of the promotion.
     *
     * @return a string containing the name, modules and groups of the promotion
     */
    public String toString() {
        String ret = "Nom = " + this.nomPromo + "\n";

        ret += "Module : \n";

        for(Module module : this.modules) {
            ret += "| -> " + module.getCode() + "\n";
        }

        ret += "Groupes : \n";

        for(GroupeTD groupeTD : groupes) {
            ret += "| -> " + groupeTD.getNom() + "\n";
        }

        return ret;
    }

    /**
     * Adds a group to this promotion.
     *
     * @throws IllegalArgumentException if gr is null or already in the list.
     *
     * @param gr the group to add
     */
    public void ajouterGroupe(GroupeTD gr) {
        if(gr == null) throw new IllegalArgumentException("Le nom du groupe ne peut pas être null");
        if(this.groupes.contains(gr)) throw new IllegalArgumentException("Le groupe est déjà dans la liste");

        this.groupes.add(gr);
    }

    /**
     * Adds a module to this promotion.
     *
     * @throws IllegalArgumentException if m is null or already in the list.
     *
     * @param m the module to add
     */
    public void ajouterModule(Module m) {
        if(m  == null) throw new IllegalArgumentException("Le module ne peut pas être vide");
        if(this.modules.contains(m)) throw new IllegalArgumentException("Le module est déjà dans la liste");

        this.modules.add(m);
    }

    /**
     * Removes a module from this promotion.
     *
     * @throws IllegalArgumentException if m is null or not in the list.
     *
     * @param m the module to remove
     */
    public void retirerModule(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        if(!this.modules.contains(m)) throw new IllegalArgumentException("Le module n'est pas dans la liste");

        this.modules.remove(m);
    }

    /**
     * Returns the name of the promotion.
     * @return the name of the promotion
     */
    public EnumPromo getNomPromo() {
        return this.nomPromo;
    }

    /**
     * Returns a copy of the list of modules associated with this promotion.
     * @return a copy of the list of modules
     */
    public ArrayList<Module> getModules() {
        return new ArrayList<>(this.modules);
    }

    /**
     * Returns a copy of the list of groups associated with this promotion.
     * @return a copy of the list of groups
     */
    public ArrayList<GroupeTD> getGroupes() {
        return new ArrayList<>(this.groupes);
    }

    /**
     * Sets the name of the promotion.
     * @throws IllegalArgumentException if nomPromo is null.
     * @param nomPromo the new name of the promotion
     */
    public void setNomPromo(EnumPromo nomPromo) {
        if(nomPromo == null) throw new IllegalArgumentException("Le nom de la promotion ne peut pas être null");
        if(nomPromo.name().length() > 5) throw new IllegalArgumentException("Le nom de la promotion ne peut pas dépasser 50 caractères");
        this.nomPromo = nomPromo;
    }

}