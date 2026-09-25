package model.entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a student of the application.
 * Extends Utilisateur with a student number and a tutorial group.
 * @author Kevin Boeffard
 */
public class Etudiant extends Utilisateur {

    /** Student number of this student. */
    private String numEtudiant;

    /** Tutorial group this student belongs to. */
    private GroupeTD groupe;

    /**
     * Creates a new Etudiant with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param nom    the last name of the student
     * @param prenom the first name of the student
     * @param email  the email address of the student
     * @param mdp    the password of the student
     * @param num    the student number
     */
    public Etudiant(String nom, String prenom, String email, String mdp, String num) {
        super(nom, prenom, email, mdp);

        if(num.length() <= 0) throw new IllegalArgumentException("Le numéro étudiant ne peut pas être négatif ou nul");
        if(String.valueOf(num).length() > 50) throw new IllegalArgumentException("Le numéro étudiant ne peut pas dépasser 50 caractères");

        this.numEtudiant = num;
        
    }

    /**
     * Returns a string representation of the student.
     * Contains the student number, personal information and tutorial group.
     *
     * @return a string containing the student number, personal information and tutorial group
     */
    public String toString() {
        String ret = "Numéro étudiant = " + this.numEtudiant + "\n";
        ret += super.toString() + "\n";
        if(groupe != null) ret += "Groupe TD = " + this.groupe.getPromotion().getNomPromo() + " " + this.groupe.getNom() + "\n";

        return ret;
    }
    

    /**
     * Calculates and returns the overall average grade of the student across all followed modules.
     * Returns 0 if no modules are followed.
     *
     * @return the overall average grade of the student
     */
    public double calculerMoyenneGenerale() {
        double moyenne = 0;
        ArrayList<Module> modules = this.groupe.getModulesSuivis();

        for(Module module : modules) {
            moyenne += this.calculerMoyenne(module);
        }

        if(!modules.isEmpty()) moyenne = moyenne / modules.size();

        return moyenne;
    }


    /**
     * Calculates and returns the weighted average grade of the student for the given module.
     * Returns 0 if no grades have been added.
     *
     * @throws IllegalArgumentException if m is null.
     *
     * @param m the module to calculate the average for
     * @return the weighted average grade of the student for the given module
     */
    public double calculerMoyenne(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");

        double moyenne = 0;
        double coeff = 0;

        for(Evaluation evaluation : m.getListeEval()) {
            HashMap<Etudiant, Double> notes = evaluation.getNotes();
            if(notes.containsKey(this)) {
                moyenne += (notes.get(this) * evaluation.getCoeff());
                coeff += evaluation.getCoeff();
            }
        }

        if(coeff != 0) moyenne = moyenne / coeff;

        return moyenne;
    }

    /**
     * Returns the list of sessions attended by this student based on their tutorial group.
     *
     * @return the list of sessions attended by this student
     */
    public ArrayList<Seance> seancesSuivies() {
        ArrayList<Seance> seances = new ArrayList<>();

        for(Module module : this.groupe.getModulesSuivis()) {
            for(Seance s : module.getSeances()) {
                if(s.getGroupe().equals(this.groupe)) seances.add(s);
            }
        }

        return seances;
    }

    /**
     * Checks whether the given module is part of the student's promotion.
     *
     * @throws IllegalArgumentException if m is null.
     *
     * @param m the module to check
     * @return true if the module is part of the student's promotion, false otherwise
     */
    public boolean moduleValide(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        return this.groupe.getPromotion().getModules().contains(m);
    }

    /**
     * Returns a map associating each session of the given module to the attendance status of this student.
     *
     * @throws IllegalArgumentException if m is null.
     *
     * @param m the module to retrieve the attendance for
     * @return a map associating each session to the attendance status of this student
     */
    public HashMap<Seance, EnumPresence> recupererPresence(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        HashMap<Seance, EnumPresence> presence = new HashMap<>();
        for(Seance seance : m.getSeances()) {
            HashMap<Etudiant, EnumPresence> appel = seance.getAppel();
            if(appel.containsKey(this) && seance.getGroupe().equals(groupe)) presence.put(seance, appel.get(this));
        }
        return presence;
    }

    /**
     * Returns the student number of this student.
     * @return the student number
     */
    public String getNumEtudiant() {
        return this.numEtudiant;
    }

    /**
     * Returns the tutorial group this student belongs to.
     * @return the tutorial group of the student
     */
    public GroupeTD getGroupe() {
        return this.groupe;
    }

    /**
     * Sets the student number of this student.
     * @throws IllegalArgumentException if num is negative or zero.
     * @param num the new student number
     */
    public void setNumEtudiant(String num) {
        if(num.length() <= 0) throw new IllegalArgumentException("Le numéro étudiant ne peut pas être négatif ou nul");
        if(String.valueOf(num).length() > 50) throw new IllegalArgumentException("Le numéro étudiant ne peut pas dépasser 50 caractères");
        this.numEtudiant = num;
    }

    /**
     * Sets the tutorial group of this student.
     * @throws IllegalArgumentException if groupe is null.
     * @param groupe the new tutorial group of the student
     */
    public void setGroupe(GroupeTD groupe) {
        if(groupe == null) throw new IllegalArgumentException("Le groupe TD ne peut pas être null");
        this.groupe = groupe;
    }
}
