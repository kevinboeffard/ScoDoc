package model.entity;

import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents a session associated with a module and a group.
 * Stores the attendance status of each student.
 * @author Kevin Boeffard
 */
public class Seance {

    /** Date of the session. */
    private Date date;

    /** Start time of the session. */
    private LocalTime heureDeb;

    /** End time of the session. */
    private LocalTime heureFin;

    /** Module this session belongs to. */
    private Module module;

    /** Group attending this session. */
    private GroupeTD groupe;

    /** Unique identifier of the session in the database. */
    private int numero;

    /** Map associating each student to their attendance status for this session. */
    private HashMap<Etudiant, EnumPresence> appel;
    
    /**
     * Creates a new Seance with the given attributes.
     *
     * @throws IllegalArgumentException if any parameter is null, or if hFin is not strictly after hDeb.
     *
     * @param date  the date of the session
     * @param hDeb  the start time of the session
     * @param hFin  the end time of the session
     * @param m     the module this session belongs to
     * @param grTD  the group attending this session
     */
    public Seance(Date date, LocalTime hDeb, LocalTime hFin, Module m, GroupeTD grTD) {
        if(date == null) throw new IllegalArgumentException("La date ne peut pas être null");
        if(hDeb == null) throw new IllegalArgumentException("L'heure de début ne peut pas être null");
        if(hFin == null) throw new IllegalArgumentException("L'heure de fin ne peut pas être null");
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        if(grTD == null) throw new IllegalArgumentException("Le groupe TD ne peut pas être null");

        if(!hFin.isAfter(hDeb)) throw new IllegalArgumentException("L'heure de fin ne peux pas être avant ou égale à l'heure de début");

        this.date = new Date(date.getTime());
        this.heureDeb = hDeb;
        this.heureFin = hFin;
        this.module = m;
        this.groupe = grTD;
        this.appel = new HashMap<>();
    }

    /**
     * Returns a string representation of the session.
     * Contains the date, start time, end time, module, group and attendance of each student.
     *
     * @return a string containing the date, times, module, group and attendance of the session
     */
    public String toString() {
        String ret = "Date = " + this.date + "\n";
        ret += "Heure de début = " + this.heureDeb + "\n";
        ret += "Heure de fin = " + heureFin + "\n";
        ret += "Module = " + this.module.getCode() + "\n";
        ret += "Groupe TD = " + this.groupe.getPromotion().getNomPromo() + " " + this.groupe.getNom() + "\n";

        ret += "Appel : \n";
        for(Etudiant etudiant : this.appel.keySet()) {
            ret += "| -> " + etudiant.getPrenom() + " " + etudiant.getNom() + " : " + this.appel.get(etudiant).name() + "\n";
        }

        return ret;
    }

    /**
     * Adds an attendance status for the given student.
     *
     * @throws IllegalArgumentException if e or pres is null.
     *
     * @param e    the student to add an attendance status for
     * @param pres the attendance status to assign to the student
     */
    public void ajouterPresence(Etudiant e, EnumPresence pres) {
        if(e == null) throw new IllegalArgumentException("L'étudiant ne peut pas être null");
        if(pres == null) throw new IllegalArgumentException("La présence doit être : ABSENT, RETARD, PRESENT");

        this.appel.put(e, pres);
    }

    /**
     * Returns the date of the session.
     * @return the date of the session
     */
    public Date getDate() {
        return new Date(this.date.getTime());
    }

    /**
     * Returns the start time of the session.
     * @return the start time of the session
     */
    public LocalTime getHeureDeb() {
        return this.heureDeb;
    }

    /**
     * Returns the end time of the session.
     * @return the end time of the session
     */
    public LocalTime getHeureFin() {
        return this.heureFin;
    }

    /**
     * Returns the module this session belongs to.
     * @return the module of the session
     */
    public Module getModule() {
        return this.module;
    }

    /**
     * Returns the group attending this session.
     * @return the group of the session
     */
    public GroupeTD getGroupe() {
        return this.groupe;
    }

    /**
     * Returns a copy of the attendance map of this session.
     * @return a copy of the map associating each student to their attendance status
     */
    public HashMap<Etudiant, EnumPresence> getAppel() {
        return new HashMap<>(this.appel);
    }

    /**
     * Sets the date of the session.
     * @throws IllegalArgumentException if date is null.
     * @param date the new date of the session
     */
    public void setDate(Date date) {
        if(date == null) throw new IllegalArgumentException("La date ne peut pas être null");
        this.date = new Date(date.getTime());
    }

    /**
     * Sets the start time of the session.
     * @throws IllegalArgumentException if heureDeb is null or not strictly before heureFin.
     * @param heureDeb the new start time of the session
     */
    public void setHeureDeb(LocalTime heureDeb) {
        if(heureDeb == null) throw new IllegalArgumentException("L'heure de début ne peut pas être null");
        if(!this.heureFin.isAfter(heureDeb)) throw new IllegalArgumentException("L'heure de début ne peut pas être après ou égale à l'heure de fin");
        this.heureDeb = heureDeb;
    }

    /**
     * Sets the end time of the session.
     * @throws IllegalArgumentException if heureFin is null or not strictly after heureDeb.
     * @param heureFin the new end time of the session
     */
    public void setHeureFin(LocalTime heureFin) {
        if(heureFin == null) throw new IllegalArgumentException("L'heure de fin ne peut pas être null");
        if(!heureFin.isAfter(this.heureDeb)) throw new IllegalArgumentException("L'heure de fin ne peut pas être avant ou égale à l'heure de début");
        this.heureFin = heureFin;
    }

    /**
     * Sets the module of the session.
     * @throws IllegalArgumentException if m is null.
     * @param m the new module of the session
     */
    public void setModule(Module m) {
        if(m == null) throw new IllegalArgumentException("Le module ne peut pas être null");
        this.module = m;
    }

    /**
     * Sets the group of the session.
     * @throws IllegalArgumentException if groupe is null.
     * @param groupe the new group of the session
     */
    public void setGroupe(GroupeTD groupe) {
        if(groupe == null) throw new IllegalArgumentException("Le groupe TD ne peut pas être null");
        this.groupe = groupe;
    }

    /**
     * Returns the unique identifier of the session.
     * @return the unique identifier of the session
     */
    public int getNumero() {
        return this.numero;
    }

    /**
     * Sets the unique identifier of the session.
     * @param numero the new unique identifier of the session
     */
    public void setNumero(int numero) {
        this.numero = numero;

    }
}
