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
 * Controller for the teacher / Directeur des Études sidebar.
 * Handles navigation between the different "Prof" and "DE" views.
 * @author Kevin Boeffard
 */
public class SidebarEnseignantController {

    @FXML private HBox btnDashboard;
    @FXML private HBox btnEleves;
    @FXML private HBox btnSaisirNotes;
    @FXML private HBox btnAbsences;
    @FXML private HBox btnPlanning;

    @FXML private HBox btnDashboardDE;
    @FXML private HBox btnUtilisateurs;
    @FXML private HBox btnModulesAdmin;

    /** Reference to the main controller, used to switch the central view. */
    private MainEnseignantController mainController;

    /**
     * Sets the main controller reference.
     * @param mainController the main controller of the application
     */
    public void setMainController(MainEnseignantController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleDashboard() {
        mainController.chargerVue("dashboard_enseignant.fxml");
        setActif(btnDashboard);
    }

    @FXML
    public void handleEleves() {
        mainController.chargerVue("eleves_enseignant.fxml");
        setActif(btnEleves);
    }

    @FXML
    public void handleSaisirNotes() {
        mainController.chargerVue("saisir_notes_enseignant.fxml");
        setActif(btnSaisirNotes);
    }

    @FXML
    public void handleAbsences() {
        mainController.chargerVue("absences_enseignant.fxml");
        setActif(btnAbsences);
    }

    @FXML
    public void handlePlanning() {
        mainController.chargerVue("planning_enseignant.fxml");
        setActif(btnPlanning);
    }

    @FXML
    public void handleDashboardDE() {
        mainController.chargerVue("dashboard_de.fxml");
        setActif(btnDashboardDE);
    }

    @FXML
    public void handleUtilisateurs() {
        mainController.chargerVue("utilisateurs_de.fxml");
        setActif(btnUtilisateurs);
    }

    @FXML
    public void handleModulesAdmin() {
        mainController.chargerVue("modules_de.fxml");
        setActif(btnModulesAdmin);
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
     * Switches the sidebar between "Prof" and "DE" mode: shows the matching menu items
     * and loads the corresponding dashboard.
     */
    public void actualiserModeDE() {
        boolean modeDE = NavigationContext.isModeDE();

        HBox[] itemsProf = { btnDashboard, btnEleves, btnSaisirNotes, btnAbsences, btnPlanning };
        HBox[] itemsDE = { btnDashboardDE, btnUtilisateurs, btnModulesAdmin };

        for(HBox item : itemsProf) basculerAffichage(item, !modeDE);
        for(HBox item : itemsDE) basculerAffichage(item, modeDE);

        if(modeDE) {
            handleDashboardDE();
        } else {
            handleDashboard();
        }
    }

    /**
     * Shows or hides a menu item.
     *
     * @param item    the menu item to show or hide
     * @param visible true to show the item, false to hide it
     */
    private void basculerAffichage(HBox item, boolean visible) {
        item.setVisible(visible);
        item.setManaged(visible);
    }

    /**
     * Highlights the given menu item as active and resets the others to their default style.
     * @param actif the menu item to mark as active
     */
    private void setActif(HBox actif) {
        HBox[] boutons = {
            btnDashboard, btnEleves, btnSaisirNotes, btnAbsences, btnPlanning,
            btnDashboardDE, btnUtilisateurs, btnModulesAdmin
        };

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