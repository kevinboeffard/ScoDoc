package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import view.App;

/**
 * Controller for the student sidebar.
 * Handles navigation between the different student views.
 * @author Kevin Boeffard
 */
public class SidebarController {

    @FXML private HBox btnDashboard;
    @FXML private HBox btnNotes;
    @FXML private HBox btnAbsences;
    @FXML private HBox btnEmploi;

    /** Reference to the main controller, used to switch the central view. */
    private MainController mainController;

    /**
     * Sets the main controller reference.
     * @param mainController the main controller of the application
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleDashboard() {
        mainController.chargerVue("dashboard_etudiant.fxml");
        setActif(btnDashboard);
    }

    @FXML
    public void handleNotes() {
        mainController.chargerVue("notes_etudiant.fxml");
        setActif(btnNotes);
    }

    @FXML
    public void handleAbsences() {
        mainController.chargerVue("absences_etudiant.fxml");
        setActif(btnAbsences);
    }

    @FXML
    public void handleEmploi() {
        mainController.chargerVue("emploi_du_temps_etudiant.fxml");
        setActif(btnEmploi);
    }

    /**
     * Logs the user out and returns to the login page.
     * @param event the mouse click event, used to retrieve the current stage
     */
    @FXML
    public void handleDeconnexion(MouseEvent event) {
        App.utilisateurConnecte = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/login_page.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Highlights the given menu item as active and resets the others to their default style.
     * @param actif the menu item to mark as active
     */
    private void setActif(HBox actif) {
        HBox[] boutons = { btnDashboard, btnNotes, btnAbsences, btnEmploi };

        for(HBox bouton : boutons) {
            boolean estActif = (bouton == actif);

            bouton.getStyleClass().removeAll("sidebar-item", "sidebar-item-active");
            bouton.getStyleClass().add(estActif ? "sidebar-item-active" : "sidebar-item");

            Label icone = (Label) bouton.getChildren().get(0);
            Label texte = (Label) bouton.getChildren().get(1);
            String couleur = estActif ? "#3b82f6" : "#8a9ab0";

            icone.setStyle("-fx-font-size: 12px; -fx-text-fill: " + couleur + ";");
            texte.setStyle("-fx-font-size: 13px; -fx-text-fill: " + couleur + ";" + (estActif ? " -fx-font-weight: bold;" : ""));
        }
    }
}