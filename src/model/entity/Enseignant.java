package model.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a teacher of the application.
 * Extends Utilisateur with an identifier and a list of taught modules.
 * @author Kevin Boeffard
 */
public class Enseignant extends Utilisateur {

    /** Identifier of the teacher. */
    private String ident;

    /** List of modules taught by this teacher. */
    private ArrayList<Module> modules;

    /**
     * Creates a new Enseignant with the given attributes.
     *
     * @throws IllegalArgumentException if ident is null or empty.
     *
     * @param nom    the last name of the teacher
     * @param prenom the first name of the teacher
     * @param email  the email address of the teacher
     * @param mdp    the password of the teacher
     * @param ident  the identifier of the teacher
     */
    public Enseignant(String nom, String prenom, String email, String mdp, String ident) {
        super(nom, prenom, email, mdp);

        if(ident == null || ident.trim().isEmpty()) throw new IllegalArgumentException("L'identifiant de l'enseignant ne peux pas être vide ou null");
        
        this.ident = ident;
        this.modules = new ArrayList<>();
    }

    /**
     * Returns a string representation of the teacher.
     * Contains the identifier, personal information and list of taught modules.
     *
     * @return a string containing the identifier, personal information and modules of the teacher
     */
    public String toString() {
        String ret = "Identifiant = " + this.ident + "\n";
        ret += super.toString() + "\n";

        ret += "Modules : \n";
        for(Module module : this.modules) {
            ret += "| -> " + module.getCode() + " " + module.getIntitule() + "\n";
        }

        return ret;
    }

    /**
     * Adds a module to the list of modules taught by this teacher.
     *
     * @throws IllegalArgumentException if m is null or already in the list.
     *
     * @param m the module to add
     */
    public void ajouterModule(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être vide ou null");
        if(this.modules.contains(m)) throw new IllegalArgumentException("Le module est déjà dans la liste");
        this.modules.add(m);
    }

    /**
     * Removes a module from the list of modules taught by this teacher.
     *
     * @throws IllegalArgumentException if m is null or not in the list.
     *
     * @param m the module to remove
     */
    public void retirerModule(Module m ) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être vide ou null");
        if(!this.modules.contains(m)) throw new IllegalArgumentException("Le module n'est pas dans la liste");
        this.modules.remove(m);
    }

    /**
     * Creates a new evaluation for the given module and adds it to the module.
     *
     * @throws IllegalArgumentException if any parameter is invalid or the module is not found.
     *
     * @param codeModule the code of the module to create the evaluation for
     * @param typeEval   the identifier of the evaluation
     * @param coeffEval  the coefficient of the evaluation
     * @return the created evaluation
     */
    public Evaluation creerEval(String codeModule, String typeEval, Double coeffEval, LocalTime duree) throws IllegalArgumentException {
        Module module = Module.chercherModuleParCode(codeModule, this.modules);
        Evaluation evaluation = new Evaluation(typeEval, coeffEval, module, duree);
        module.ajouterEvaluation(evaluation);

        return evaluation;
    }

    /**
     * Searches for an evaluation in the given module by its identifier and coefficient.
     *
     * @throws IllegalArgumentException if the module is not found or the evaluation does not exist.
     *
     * @param codeModule the code of the module to search in
     * @param typeEval   the identifier of the evaluation
     * @param coeffEval  the coefficient of the evaluation
     * @return the evaluation matching the given identifier and coefficient
     */
    public Evaluation chercherEval(String codeModule, String typeEval) throws IllegalArgumentException {
        Evaluation ret = null;
        
        Module module = Module.chercherModuleParCode(codeModule, modules);

        for(Evaluation evaluation : module.getListeEval()) {
            if(evaluation.getIdent().equals(typeEval)) ret = evaluation;
        }

        if(ret == null) throw new IllegalArgumentException("Evaluation Introuvable");

        return ret;
    }

    /**
     * Removes an evaluation from its module.
     *
     * @throws IllegalArgumentException if e is null or the teacher is not responsible for the module.
     *
     * @param e the evaluation to remove
     */
    public void supprimerEval(Evaluation e) {
        if((e == null)) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        if(!e.getModule().getResponsables().contains(this)) throw new IllegalArgumentException("Le professeur n'est pas responsable du module");
        e.getModule().retirerEvaluation(e);
    }

    /**
     * Updates the identifier of the given evaluation.
     *
     * @throws IllegalArgumentException if e is null or nvType is invalid.
     *
     * @param e      the evaluation to update
     * @param nvType the new identifier of the evaluation
     */
    public void modifierTypeEval(Evaluation e, String nvType) throws IllegalArgumentException {
        if(e == null) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        e.setIdent(nvType);
    }

    /**
     * Updates the coefficient of the given evaluation.
     *
     * @throws IllegalArgumentException if e is null or nvCoeff is invalid.
     *
     * @param e       the evaluation to update
     * @param nvCoeff the new coefficient of the evaluation
     */
    public void modifierCoeffEval(Evaluation e, double nvCoeff) {
        if(e == null) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        e.setCoeff(nvCoeff);
    }

    /**
     * Assigns a grade to a student for the given evaluation.
     *
     * @throws IllegalArgumentException if e is null.
     *
     * @param e      the evaluation to assign the grade to
     * @param etud   the student to assign the grade to
     * @param valeur the grade to assign
     */
    public void saisirNote(Evaluation e, Etudiant etud, double valeur) {
        if(e == null) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        e.ajouterNote(etud, valeur);
    }

    /**
     * Calculates and returns the weighted average grade of all evaluations for the given module.
     * Returns 0 if no evaluations have been added.
     *
     * @throws IllegalArgumentException if m is null.
     *
     * @param m the module to calculate the average for
     * @return the weighted average grade of all evaluations
     */
    public double calculerMoyenne(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");

        double moyenne = 0;
        double coeff = 0;

        for(Evaluation evaluation : m.getListeEval()) {
            moyenne += (evaluation.calculerMoyenne() * evaluation.getCoeff());
            coeff += evaluation.getCoeff();
        }

        if(coeff != 0) moyenne = moyenne / coeff;

        return moyenne;
    }

    /**
     * Creates and adds a new session to the given module.
     *
     * @throws IllegalArgumentException if m is null or the teacher is not responsible for the module.
     *
     * @param m        the module to add the session to
     * @param date     the date of the session
     * @param heureDeb the start time of the session
     * @param heureFin the end time of the session
     * @param gr       the tutorial group attending the session
     */
    public void ajouterSeance(Module m, Date date, LocalTime heureDeb, LocalTime heureFin, GroupeTD gr) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        if(!m.getResponsables().contains(this)) throw new IllegalArgumentException("L'enseignant n'est pas responsable de ce module");
        m.ajouterSeance(date, heureDeb, heureFin, gr);
    }

    /**
     * Marks the attendance status of a student for the given session.
     *
     * @throws IllegalArgumentException if e, s or pres is null.
     *
     * @param e    the student to mark the attendance for
     * @param s    the session to mark the attendance for
     * @param pres the attendance status to assign
     */
    public void marquerPresence(Etudiant e, Seance s, EnumPresence pres) {
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        if(s == null) throw new IllegalArgumentException("La séance ne peut pas être null");
        if(pres == null) throw new IllegalArgumentException("La présence ne peut pas être null");
        s.ajouterPresence(e, pres);
    }

    /**
     * Marks all students of the session's tutorial group as present.
     *
     * @throws IllegalArgumentException if s is null.
     *
     * @param s the session to mark all students as present for
     */
    public void tousPresents(Seance s) {
        if(s == null) throw new IllegalArgumentException("La séance ne peut pas être null");
        for(Etudiant etudiant : s.getGroupe().getEtudiants()) {
            s.ajouterPresence(etudiant, EnumPresence.PRESENT);
        }
    }

    /**
     * Prints the attendance of all sessions for the given module.
     *
     * @throws IllegalArgumentException if codeModule is null, empty or not found.
     *
     * @param codeModule the code of the module to display the attendance for
     */
    public void afficherPresenceModule(String codeModule) {
        if(codeModule == null || codeModule.trim().isEmpty()) throw new IllegalArgumentException("Le code du module ne peut pas être vide ou null");
        Module module = Module.chercherModuleParCode(codeModule, this.modules);

        for(Seance seance : module.getSeances()) {
            System.out.println(seance.toString());
        }
    }

    /**
     * Returns the identifier of the teacher.
     * @return the identifier of the teacher
     */
    public String getIdent() {
        return this.ident;
    }

    /**
     * Returns a copy of the list of modules taught by this teacher.
     * @return a copy of the list of modules
     */
    public ArrayList<Module> getModules() {
        return new ArrayList<>(this.modules);
    }

    /**
     * Sets the identifier of the teacher.
     * @throws IllegalArgumentException if ident is null or empty.
     * @param ident the new identifier of the teacher
     */
    public void setIdent(String ident) {
        if(ident == null || ident.trim().isEmpty()) throw new IllegalArgumentException("L'identifiant de l'enseignant ne peut pas être vide ou null");
        this.ident = ident;
    }
    
}
