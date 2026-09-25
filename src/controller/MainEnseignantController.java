package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.entity.DirecteurEtudes;
import model.entity.Enseignant;
import view.App;

/**
 * Main controller of the application for a connected teacher or Directeur des Études.
 * Manages the central content area and delegates navigation to it.
 * @author Kevin Boeffard
 */
public class MainEnseignantController {

    @FXML private AnchorPane contenuPrincipal;

    @FXML private SidebarEnseignantController sidebarController;

    @FXML private TopbarController topbarController;

    /**
     * Initializes the main view: links the sidebar and topbar to this controller,
     * resets the DE toggle state, and loads the default dashboard.
     */
    @FXML
    public void initialize() {
        sidebarController.setMainController(this);
        topbarController.setMainController(this);
        NavigationContext.setMainController(this);
        NavigationContext.setModeDE(false);

        if(App.utilisateurConnecte instanceof Enseignant || App.utilisateurConnecte instanceof DirecteurEtudes) {
            chargerVue("dashboard_enseignant.fxml");
        }
    }

    /**
     * Loads the given FXML view into the central content area.
     * @param fxml the name of the FXML file to load
     */
    public void chargerVue(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/" + fxml));
            AnchorPane vue = loader.load();
            vue.prefWidthProperty().bind(contenuPrincipal.widthProperty());
            vue.prefHeightProperty().bind(contenuPrincipal.heightProperty());
            contenuPrincipal.getChildren().setAll(vue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the "Prof / DE" mode and refreshes the sidebar accordingly.
     * Does nothing if the requested mode is already active.
     *
     * @param modeDE true to switch to DE mode, false to switch to Prof mode
     */
    public void definirModeDE(boolean modeDE) {
        if(NavigationContext.isModeDE() != modeDE) {
            NavigationContext.setModeDE(modeDE);
            sidebarController.actualiserModeDE();
        }
    }

    /**
     * Returns to the dashboard view matching the current mode and highlights it in the sidebar.
     */
    public void retourDashboard() {
        if(NavigationContext.isModeDE()) {
            sidebarController.handleDashboardDE();
        } else {
            sidebarController.handleDashboard();
        }
    }
}