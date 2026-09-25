package model.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a module associated with a promotion.
 * Stores the evaluations, sessions and responsible teachers of the module.
 * @author Kevin Boeffard
 */
public class Module {
    
    /** Unique code identifying the module (e.g. R2.01). */
    private String code;

    /** Full name of the module. */
    private String intitule;

    /** Type of the module (SAE or resource). */
    private EnumModule type;

    /** Promotion this module belongs to. */
    private Promotion promo;

    /** List of teachers responsible for this module. */
    private ArrayList<Enseignant> responsables;

    /** List of evaluations associated with this module. */
    private ArrayList<Evaluation> listeEval;

    /** List of sessions associated with this module. */
    private ArrayList<Seance> seances;

    /**
     * Creates a new Module with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is null, or if code or intitule is empty.
     *
     * @param code     the unique code identifying the module
     * @param intitule the full name of the module
     * @param type     the type of the module
     * @param promo    the promotion this module belongs to
     */
    public Module(String code, String intitule, EnumModule type, Promotion promo) {
        if(code == null || code.trim().isEmpty()) throw new IllegalArgumentException("Le code du module ne peut pas être vide ou null");
        if(intitule == null || intitule.trim().isEmpty()) throw new IllegalArgumentException("L'intitulé du module ne peut pas être vide ou null");
        if(type == null) throw new IllegalArgumentException("Le type de module ne peut pas être null");
        if(promo == null) throw new IllegalArgumentException("La promotion ne peut pas être null");

        this.code = code;
        this.intitule = intitule;
        this.type = type;
        this.promo = promo;

        this.responsables = new ArrayList<>();
        this.listeEval = new ArrayList<>();
        this.seances = new ArrayList<>();
    }

    /**
     * Returns a string representation of the module.
     * Contains the code, full name, type, promotion, responsible teachers, evaluations and sessions.
     *
     * @return a string containing the code, full name, type, promotion, teachers, evaluations and sessions
     */
    public String toString() {
        String ret = "Code = " + this.code + "\n";
        ret += "Intitule = " + this.intitule + "\n";
        ret += "Type = " + this.type.toString() + "\n";
        ret += "Promotion = " + this.promo.getNomPromo() + "\n";

        ret += "Responsables : \n";
        for(Enseignant enseignant : this.responsables) {
            ret += "| -> " + enseignant.getPrenom() + " " + enseignant.getNom() + "\n";
        }

        ret += "Evaluations : \n";
        for(Evaluation eval : this.listeEval) {
            ret += "| -> " + eval.getIdent() + " (coeff : " + eval.getCoeff() + ")\n";
        }

        ret += "Seances : \n";
        for(Seance seance : this.seances) {
            ret += "| -> " + seance.toString() + "\n";
        }

        return ret;
    }

    /**
     * Creates and adds a new session to this module.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param date     the date of the session
     * @param heureDeb the start time of the session
     * @param heureFin the end time of the session
     * @param grTD     the group attending the session
     */
    public void ajouterSeance(Date date, LocalTime heureDeb, LocalTime heureFin, GroupeTD grTD) throws IllegalArgumentException {
        Seance seance = new Seance(date, heureDeb, heureFin, this, grTD);
        this.seances.add(seance);
    }

    /**
     * Adds an existing session to this module.
     *
     * @throws IllegalArgumentException if seance is null.
     *
     * @param seance the session to add
     */
    public void ajouterSeance(Seance seance) {
        if(seance == null) throw new IllegalArgumentException("La séance ne peut pas être null");
        this.seances.add(seance);
    }

    /**
     * Adds a teacher as responsible for this module.
     *
     * @throws IllegalArgumentException if e is null or already responsible for this module.
     *
     * @param e the teacher to add as responsible
     */
    public void ajouterResponsable(Enseignant e) {
        if(e == null) throw new IllegalArgumentException("Le responsable ne peut pas être null");
        if(this.responsables.contains(e)) throw new IllegalArgumentException("L'enseignant est déja responsable");
        this.responsables.add(e);
    }

    /**
     * Removes a teacher from the list of responsible teachers of this module.
     *
     * @throws IllegalArgumentException if e is null or not responsible for this module.
     *
     * @param e the teacher to remove from the responsible list
     */
    public void retirerResponsable(Enseignant e) {
        if(e == null) throw new IllegalArgumentException("Le responsable ne peut pas être null");
        if(!this.responsables.contains(e)) throw new IllegalArgumentException("L'enseignant n'est pas responsable");
        this.responsables.remove(e);
    }

    /**
     * Adds an evaluation to this module.
     *
     * @throws IllegalArgumentException if e is null or already in the list.
     *
     * @param e the evaluation to add
     */
    public void ajouterEvaluation(Evaluation e) {
        if(e == null) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        if(this.listeEval.contains(e)) throw new IllegalArgumentException("L'évaluation est déjà dans la liste");

        this.listeEval.add(e);
    }

    /**
     * Removes an evaluation from this module.
     *
     * @throws IllegalArgumentException if e is null or not in the list.
     *
     * @param e the evaluation to remove
     */
    public void retirerEvaluation(Evaluation e) {
        if(e == null) throw new IllegalArgumentException("L'évaluation ne peut pas être null");
        if(!this.listeEval.contains(e)) throw new IllegalArgumentException("L'évaluation n'est pas dans la liste");

        this.listeEval.remove(e);
    }

    /**
     * Calculates and returns the weighted average grade of all evaluations for this module.
     * Returns 0 if no evaluations have been added.
     *
     * @return the weighted average grade of all evaluations
     */
    public double calculerMoyenne() {

        double moyenne = 0;
        double coeff = 0;

        for(Evaluation evaluation : this.listeEval) {
            moyenne += (evaluation.calculerMoyenne() * evaluation.getCoeff());
            coeff += evaluation.getCoeff();
        }
        if(coeff != 0) moyenne = moyenne / coeff;

        return moyenne;
    }

    /**
     * Searches for a module by its code in the given list.
     *
     * @throws IllegalArgumentException if no module with the given code is found.
     *
     * @param code  the code of the module to search for
     * @param liste the list of modules to search in
     * @return the module with the given code
     */
    public static Module chercherModuleParCode(String code, ArrayList<Module> liste) {
        Module ret = null;

        for(Module module : liste) {
            if(module.code.equals(code)) ret = module;
        }

        if(ret == null) throw new IllegalArgumentException("Le module n'existe pas");

        return ret;
    }

    /**
     * Returns the code of the module.
     * @return the code of the module
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Returns the full name of the module.
     * @return the full name of the module
     */
    public String getIntitule() {
        return this.intitule;
    }

    /**
     * Returns the type of the module.
     * @return the type of the module
     */
    public EnumModule getType() {
        return this.type;
    }

    /**
     * Returns the promotion this module belongs to.
     * @return the promotion of the module
     */
    public Promotion getPromo() {
        return this.promo;
    }

    /**
     * Returns a copy of the list of teachers responsible for this module.
     * @return a copy of the list of responsible teachers
     */
    public ArrayList<Enseignant> getResponsables() {
        return new ArrayList<>(this.responsables);
    }

    /**
     * Returns a copy of the list of evaluations associated with this module.
     * @return a copy of the list of evaluations
     */
    public ArrayList<Evaluation> getListeEval() {
        return new ArrayList<>(this.listeEval);
    }

    /**
     * Returns a copy of the list of sessions associated with this module.
     * @return a copy of the list of sessions
     */
    public ArrayList<Seance> getSeances() {
        return new ArrayList<>(this.seances);
    }

    /**
     * Sets the code of the module.
     * @throws IllegalArgumentException if code is null or empty.
     * @param code the new code of the module
     */
    public void setCode(String code) {
        if(code == null || code.trim().isEmpty()) throw new IllegalArgumentException("Le code du module ne peut pas être vide ou null");
        this.code = code;
    }

    /**
     * Sets the full name of the module.
     * @throws IllegalArgumentException if intitule is null or empty.
     * @param intitule the new full name of the module
     */
    public void setIntitule(String intitule) {
        if(intitule == null || intitule.trim().isEmpty()) throw new IllegalArgumentException("L'intitulé du module ne peut pas être vide ou null");
        this.intitule = intitule;
    }

    /**
     * Sets the type of the module.
     * @throws IllegalArgumentException if type is null.
     * @param type the new type of the module
     */
    public void setType(EnumModule type) {
        if(type == null) throw new IllegalArgumentException("Le type du module ne peut pas être null");
        this.type = type;
    }

    /**
     * Sets the promotion of the module.
     * @throws IllegalArgumentException if promo is null.
     * @param promo the new promotion of the module
     */
    public void setPromo(Promotion promo) {
        if(promo == null) throw new IllegalArgumentException("La promotion ne peut pas être null");
        this.promo = promo;
    }
}
