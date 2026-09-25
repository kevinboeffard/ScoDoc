package model.entity;

/**
 * Represents the director of studies of the application.
 * Extends Enseignant with administrative capabilities.
 * @author Kevin Boeffard
 */
public class DirecteurEtudes extends Enseignant {

    /**
     * Creates a new DirecteurEtudes with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param nom    the last name of the director
     * @param prenom the first name of the director
     * @param email  the email address of the director
     * @param mdp    the password of the director
     * @param ident  the identifier of the director
     */
    public DirecteurEtudes(String nom, String prenom, String email, String mdp, String ident) {
        super(nom, prenom, email, mdp, ident);
    }

    /**
     * Creates and returns a new Enseignant with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param nom    the last name of the teacher
     * @param prenom the first name of the teacher
     * @param email  the email address of the teacher
     * @param mdp    the password of the teacher
     * @param ident  the identifier of the teacher
     * @return the created teacher
     */
    public Enseignant creerEnseignant(String nom, String prenom, String email, String mdp, String ident) {
        return new Enseignant(nom, prenom, email, mdp, ident);
    }

    /**
     * Creates and returns a new Etudiant with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param nom    the last name of the student
     * @param prenom the first name of the student
     * @param email  the email address of the student
     * @param mdp    the password of the student
     * @param num    the student number
     * @return the created student
     */
    public Etudiant creerEtudiant(String nom, String prenom, String email, String mdp, String num) {
        return new Etudiant(nom, prenom, email, mdp, num);
    }

    /**
     * Creates and returns a new Module with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param code     the unique code of the module
     * @param intitule the full name of the module
     * @param type     the type of the module
     * @param promo    the promotion this module belongs to
     * @return the created module
     */
    public Module creerModule(String code, String intitule, EnumModule type, Promotion promo) {
        Module m = new Module(code, intitule, type, promo);
        promo.ajouterModule(m);
        return m;
    }

    /**
     * Creates and returns a new Promotion with the given name.
     *
     * @throws IllegalArgumentException if promotion is null.
     *
     * @param promotion the name of the promotion
     * @return the created promotion
     */
    public Promotion creerPromotion(EnumPromo promotion) {
        return new Promotion(promotion);
    }

    /**
     * Creates and returns a new GroupeTD with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param nom   the name of the tutorial group
     * @param promo the promotion this tutorial group belongs to
     * @return the created tutorial group
     */
    public GroupeTD creerGroupeTD(String nom, Promotion promo) {
        GroupeTD gr = new GroupeTD(nom, promo);
        promo.ajouterGroupe(gr);
        return gr;
    }

    /**
     * Assigns a teacher as responsible for the given module.
     *
     * @throws IllegalArgumentException if m or e is null.
     *
     * @param m the module to assign the teacher to
     * @param e the teacher to assign as responsible
     */
    public void definirResponsable(Module m, Enseignant e) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        if(e == null) throw new IllegalArgumentException("L'enseignant ne peut pas être null");
        m.ajouterResponsable(e);
        e.ajouterModule(m);
    }

    /**
     * Adds a student to the given tutorial group.
     *
     * @throws IllegalArgumentException if gr or e is null.
     *
     * @param gr the tutorial group to add the student to
     * @param e  the student to add
     */
    public void ajouteEtudiantGroupe(GroupeTD gr, Etudiant e) {
        if(gr == null) throw new IllegalArgumentException("Le groupe TD ne peut pas être null");
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        gr.ajouterEtudiant(e);
        e.setGroupe(gr);
    }

    /**
     * Calculates and returns the overall average grade of the given student.
     *
     * @throws IllegalArgumentException if e is null.
     *
     * @param e the student to calculate the average for
     * @return the overall average grade of the student
     */
    public double calculerMoyenneEtudiant(Etudiant e) {
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        return e.calculerMoyenneGenerale();
    }

    /**
     * Checks whether the given student has passed all their modules.
     * A student passes if their overall average is at least 10.
     *
     * @throws IllegalArgumentException if e is null.
     *
     * @param e the student to deliberate
     * @return true if the student has passed, false otherwise
     */
    public boolean deliberer(Etudiant e) {
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        return e.calculerMoyenneGenerale() >= 10;
    }
}