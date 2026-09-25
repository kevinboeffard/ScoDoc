package controller;

/**
 * Represents one row of the evaluations table inside a module card.
 * @author Kevin Boeffard
 */
public class LigneEvaluation {

    /** Identifier of the evaluation (its type). */
    private String type;

    /** Formatted grade to display. */
    private String note;

    /** Formatted coefficient to display. */
    private String coeff;

    /**
     * Creates a new row with the given values.
     *
     * @param type  the evaluation type
     * @param note  the formatted grade
     * @param coeff the formatted coefficient
     */
    public LigneEvaluation(String type, String note, String coeff) {
        this.type = type;
        this.note = note;
        this.coeff = coeff;
    }

    /**
     * Returns the evaluation type.
     * @return the evaluation type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the formatted grade.
     * @return the formatted grade
     */
    public String getNote() {
        return this.note;
    }

    /**
     * Returns the formatted coefficient.
     * @return the formatted coefficient
     */
    public String getCoeff() {
        return this.coeff;
    }
}