package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.entity.DirecteurEtudes;
import model.entity.Utilisateur;
import view.App;

/**
 * Controller for the top bar.
 * Displays the connected user, handles the back button, and the
 * "Prof / DE" toggle for a connected Directeur des Études.
 * @author Kevin Boeffard
 */
public class TopbarController {

    @FXML private Label nomUtilisateur;
    @FXML private Label avatarLabel;
    @FXML private VBox btnNotifs;
    @FXML private VBox btnRetour;
    @FXML private HBox toggleModeDE;
    @FXML private Label lblModeProf;
    @FXML private Label lblModeDE;

    /** Reference to the main controller when a student is connected. */
    private MainController mainController;

    /** Reference to the main controller when a teacher is connected. */
    private MainEnseignantController mainControllerEnseignant;

    /**
     * Sets the main controller reference for a student session.
     * @param mainController the main controller of the application
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Sets the main controller reference for a teacher session.
     * @param mainController the main controller of the application
     */
    public void setMainController(MainEnseignantController mainController) {
        this.mainControllerEnseignant = mainController;
    }

    /**
     * Initializes the view: displays the connected user's name and avatar,
     * and shows the "Prof / DE" toggle if the connected user is a Directeur des Études.
     */
    @FXML
    public void initialize() {
        Utilisateur u = App.utilisateurConnecte;
        if(u != null) {
            nomUtilisateur.setText(u.getPrenom() + " " + u.getNom());
            avatarLabel.setText(
                String.valueOf(u.getPrenom().charAt(0)) +
                String.valueOf(u.getNom().charAt(0))
            );
        }

        if(u instanceof DirecteurEtudes) {
            toggleModeDE.setVisible(true);
            toggleModeDE.setManaged(true);
            actualiserToggle();
        }
    }

    /**
     * Returns to the dashboard view, regardless of whether a student or teacher is connected.
     */
    @FXML
    public void handleRetour() {
        if(mainController != null) {
            mainController.retourDashboard();
        } else if(mainControllerEnseignant != null) {
            mainControllerEnseignant.retourDashboard();
        }
    }

    /**
     * Switches to "Prof" mode.
     */
    @FXML
    public void handleModeProf() {
        if(mainControllerEnseignant != null) {
            mainControllerEnseignant.definirModeDE(false);
            actualiserToggle();
        }
    }

    /**
     * Switches to "DE" mode.
     */
    @FXML
    public void handleModeDE() {
        if(mainControllerEnseignant != null) {
            mainControllerEnseignant.definirModeDE(true);
            actualiserToggle();
        }
    }

    /**
     * Restyles the two toggle segments to highlight the currently active mode.
     */
    private void actualiserToggle() {
        boolean modeDE = NavigationContext.isModeDE();

        String styleActif = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 17px; -fx-cursor: hand; -fx-background-color: #3b82f6; -fx-text-fill: white;";
        String styleInactif = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 17px; -fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: #64748b;";

        lblModeProf.setStyle(modeDE ? styleInactif : styleActif);
        lblModeDE.setStyle(modeDE ? styleActif : styleInactif);
    }

    @FXML public void handleNotifs() {}
}