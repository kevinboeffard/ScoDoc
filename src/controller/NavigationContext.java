package controller;

import model.entity.Evaluation;
import model.entity.Module;
import model.entity.Seance;

/**
 * Holds shared navigation state for the teacher's "Saisir des notes" feature.
 * Used to pass the selected module and evaluation between the dashboard view
 * and the grading view, and to trigger navigation between the two.
 * @author Kevin Boeffard
 */
public class NavigationContext {

    /** Reference to the main controller, used to switch the central view. */
    private static MainEnseignantController mainController;

    /** The module selected on the dashboard, currently being graded. */
    private static Module moduleSelectionne;

    /** The evaluation selected on the dashboard, currently being graded. */
    private static Evaluation evaluationSelectionnee;

    

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private NavigationContext() {}

    /**
     * Sets the main controller reference.
     * @param controller the main controller of the application
     */
    public static void setMainController(MainEnseignantController controller) {
        mainController = controller;
    }

    /**
     * Returns the main controller reference.
     * @return the main controller of the application
     */
    public static MainEnseignantController getMainController() {
        return mainController;
    }

    /**
     * Sets the module and evaluation to be graded on the grading view.
     *
     * @param module     the selected module
     * @param evaluation the selected evaluation
     */
    public static void setSelection(Module module, Evaluation evaluation) {
        moduleSelectionne = module;
        evaluationSelectionnee = evaluation;
    }

    /**
     * Returns the module selected on the dashboard.
     * @return the selected module, or null if none
     */
    public static Module getModuleSelectionne() {
        return moduleSelectionne;
    }

    /**
     * Returns the evaluation selected on the dashboard.
     * @return the selected evaluation, or null if none
     */
    public static Evaluation getEvaluationSelectionnee() {
        return evaluationSelectionnee;
    }

    /** The session selected on the absences dashboard. */
    private static Seance seanceSelectionnee;

    /**
     * Sets the session to be marked on the attendance sheet.
     * @param seance the selected session
     */
    public static void setSeanceSelectionnee(Seance seance) {
        seanceSelectionnee = seance;
    }

    /**
     * Returns the session selected on the absences dashboard.
     * @return the selected session, or null if none
     */
    public static Seance getSeanceSelectionnee() {
        return seanceSelectionnee;
    }

    /** Whether the connected Directeur des Études is currently in "DE" mode (vs "Prof" mode). */
    private static boolean modeDE = false;

    /**
     * Returns whether the connected Directeur des Études is currently in "DE" mode.
     * @return true if in DE mode, false if in normal teacher mode
     */
    public static boolean isModeDE() {
        return modeDE;
    }

    /**
     * Sets whether the connected Directeur des Études is in "DE" mode.
     * @param mode true for DE mode, false for normal teacher mode
     */
    public static void setModeDE(boolean mode) {
        modeDE = mode;
    }
}