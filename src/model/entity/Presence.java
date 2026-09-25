package model.entity;

/**
 * Represents the attendance of a student for a given session.
 * @author Kevin Boeffard
 */
public class Presence {

    /** The student. */
    private Etudiant etudiant;

    /** The session. */
    private Seance seance;

    /** The attendance status. */
    private EnumPresence statut;

    /**
     * Creates a new Presence with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is null.
     *
     * @param etudiant the student
     * @param seance   the session
     * @param statut   the attendance status
     */
    public Presence(Etudiant etudiant, Seance seance, EnumPresence statut) {
        if(etudiant == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        if(seance == null) throw new IllegalArgumentException("La séance ne peut pas être null");
        if(statut == null) throw new IllegalArgumentException("Le statut ne peut pas être null");
        this.etudiant = etudiant;
        this.seance = seance;
        this.statut = statut;
    }

    /**
     * Returns the student.
     * @return the student
     */
    public Etudiant getEtudiant() { return this.etudiant; }

    /**
     * Returns the session.
     * @return the session
     */
    public Seance getSeance() { return this.seance; }

    /**
     * Returns the attendance status.
     * @return the attendance status
     */
    public EnumPresence getStatut() { return this.statut; }

    /**
     * Sets the attendance status.
     * @throws IllegalArgumentException if statut is null.
     * @param statut the new attendance status
     */
    public void setStatut(EnumPresence statut) {
        if(statut == null) throw new IllegalArgumentException("Le statut ne peut pas être null");
        this.statut = statut;
    }
}