package model.entity;

/**
 * Abstract class representing a user of the application.
 * Contains common attributes and behavior for all user types.
 * @author Kevin Boeffard
 */
public abstract class Utilisateur {

    /** Last name of the user.*/
    private String nom;

    /** First name of the user.*/
    private String prenom;

    /** Email address of the user.*/
    private String email;

    /** Password of the user.*/
    private String mdp;

    /**
     * Creates a new Utilisateur with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     *
     * @param nom    the last name of the user
     * @param prenom the first name of the user
     * @param email  the email address of the user
     * @param mdp    the password of the user
     */
    public Utilisateur(String nom, String prenom, String email, String mdp) {

        if(nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom ne peut pas être vide ou null");
        if(prenom == null || prenom.trim().isEmpty()) throw new IllegalArgumentException("Le prénom ne peut pas être vide ou null");
        if(email == null || email.trim().isEmpty()) throw new IllegalArgumentException("L'email ne peut pas être vide ou null");
        if(mdp == null || mdp.trim().isEmpty()) throw new IllegalArgumentException("Le mot de passe ne peut pas être vide ou null");

        if(!isValidEmail (email)) throw new IllegalArgumentException("Le format de l'email ne correspond pas au format : local@domain.extension");

        if(nom.length() > 50) throw new IllegalArgumentException("Le nom ne peut pas dépasser 50 caractères");
        if(prenom.length() > 50) throw new IllegalArgumentException("Le prénom ne peut pas dépasser 50 caractères");
        if(email.length() > 50) throw new IllegalArgumentException("L'email ne peut pas dépasser 50 caractères");
        if(mdp.length() > 50) throw new IllegalArgumentException("Le mot de passe ne peut pas dépasser 50 caractères");

        
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
    }

    /**
     * Checks that the email passed as parameter is valid.
     *
     * Expected format : local@domain.extension
     *
     * - local     : letters, digits, underscores, dots or dashes (e.g. kevin.b)
     * - @         : mandatory at sign
     * - domain    : letters, digits, underscores, dots or dashes (e.g. gmail)
     * - extension : at least 2 letters (e.g. com, fr)
     *
     * Valid examples   : kevin.b@gmail.com, user_01@iut.fr
     * Invalid examples : kevin, kevin@, @gmail.com
     *
     * Regex breakdown : ^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$
     *
     * ^           : start of the string
     * [\\w.-]+    : one or more of : letters/digits/underscores (\w), dot (.), dash (-)
     * "@"           : literal at sign
     * [\\w.-]+    : one or more of : letters/digits/underscores (\w), dot (.), dash (-)
     * \\.         : literal dot (escaped, because . alone means "any character" in regex)
     * [a-zA-Z]{2,}: at least 2 letters (a-z or A-Z) for the extension
     * $           : end of the string
     */
    private static boolean isValidEmail (String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Returns a string representation of the user.
     * Contains the first name, last name, email and password.
     * @return a string containing the first name, last name, email and password of the user
     */
    public String toString() {
        String ret = "Prenom = " + this.prenom + "\n";
        ret += "Nom = " + this.nom + "\n";
        ret += "Email = " + this.email + "\n";
        ret += "Mot de passe" + this.mdp;

        return ret;
    }

    /**
     * Returns the last name of the user.
     * @return the last name of the user
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Returns the first name of the user.
     * @return the first name of the user
     */
    public String getPrenom() {
        return this.prenom;
    }

    /**
     * Returns the email address of the user.
     * @return the email address of the user
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the password of the user.
     * @return the password of the user
     */
    public String getMdp() {
        return this.mdp;
    }

    /**
     * Sets the last name of the user.
     * @throws IllegalArgumentException if nom is null or empty.
     * @param nom the new last name of the user
     */
    public void setNom(String nom) {
        if(nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom ne peut pas être vide ou null");
        if(nom.length() > 50) throw new IllegalArgumentException("Le nom ne peut pas dépasser 50 caractères");
        this.nom = nom;
    }

    /**
     * Sets the first name of the user.
     * @throws IllegalArgumentException if prenom is null or empty.
     * @param prenom the new first name of the user
     */
    public void setPrenom(String prenom) {
        if(prenom == null || prenom.trim().isEmpty()) throw new IllegalArgumentException("Le prénom ne peut pas être vide ou null");
        if(prenom.length() > 50) throw new IllegalArgumentException("Le prénom ne peut pas dépasser 50 caractères");
        this.prenom = prenom;
    }

    /**
     * Sets the email address of the user.
     * @throws IllegalArgumentException if email is null, empty or does not match the format local@domain.extension.
     * @param email the new email address of the user
     */
    public void setEmail(String email) {
        if(email == null || email.trim().isEmpty()) throw new IllegalArgumentException("L'email ne peut pas être vide ou null");
        if(!isValidEmail(email)) throw new IllegalArgumentException("Le format de l'email ne correspond pas au format : local@domain.extension");
        if(email.length() > 50) throw new IllegalArgumentException("L'email ne peut pas dépasser 50 caractères");
        this.email = email;
    }

    /**
     * Sets the password of the user.
     * @throws IllegalArgumentException if mdp is null or empty.
     * @param mdp the new password of the user
     */
    public void setMdp(String mdp) {
        if(mdp == null || mdp.trim().isEmpty()) throw new IllegalArgumentException("Le mot de passe ne peut pas être vide ou null");
        if(mdp.length() > 50) throw new IllegalArgumentException("Le mot de passe ne peut pas dépasser 50 caractères");
        this.mdp = mdp;
    }
}