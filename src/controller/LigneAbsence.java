package controller;

import java.time.LocalTime;
import java.util.Date;

import model.entity.EnumPresence;

/**
 * Represents one row of the attendance timeline for the student view.
 * @author Kevin Boeffard
 */
public class LigneAbsence {

    /** Date of the session. */
    private Date date;

    /** Code of the module. */
    private String moduleCode;

    /** Title of the module. */
    private String moduleIntitule;

    /** Start time of the session. */
    private LocalTime heureDeb;

    /** End time of the session. */
    private LocalTime heureFin;

    /** Attendance status of the student for this session. */
    private EnumPresence statut;

    /** Semester deduced from the module code (1 or 2). */
    private int semestre;

    /**
     * Creates a new row with the given values.
     *
     * @param date           the date of the session
     * @param moduleCode     the code of the module
     * @param moduleIntitule the title of the module
     * @param heureDeb       the start time of the session
     * @param heureFin       the end time of the session
     * @param statut         the attendance status
     * @param semestre       the semester deduced from the module code
     */
    public LigneAbsence(Date date, String moduleCode, String moduleIntitule, LocalTime heureDeb, LocalTime heureFin, EnumPresence statut, int semestre) {
        this.date = date;
        this.moduleCode = moduleCode;
        this.moduleIntitule = moduleIntitule;
        this.heureDeb = heureDeb;
        this.heureFin = heureFin;
        this.statut = statut;
        this.semestre = semestre;
    }

    /**
     * Returns the date of the session.
     * @return the date of the session
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Returns the code of the module.
     * @return the code of the module
     */
    public String getModuleCode() {
        return this.moduleCode;
    }

    /**
     * Returns the title of the module.
     * @return the title of the module
     */
    public String getModuleIntitule() {
        return this.moduleIntitule;
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
     * Returns the attendance status.
     * @return the attendance status
     */
    public EnumPresence getStatut() {
        return this.statut;
    }

    /**
     * Returns the semester deduced from the module code.
     * @return the semester (1 or 2)
     */
    public int getSemestre() {
        return this.semestre;
    }
}