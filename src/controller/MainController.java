package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.entity.Etudiant;
import view.App;

public class MainController {
 
    @FXML private AnchorPane contenuPrincipal;

    @FXML private SidebarController sidebarController;

    @FXML private TopbarController topbarController;

    @FXML
    public void initialize() {
        sidebarController.setMainController(this);
        topbarController.setMainController(this);

        if(App.utilisateurConnecte instanceof Etudiant) {
            chargerVue("dashboard_etudiant.fxml");
        }
    }

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
     * Returns to the dashboard view and highlights it in the sidebar.
     */
    public void retourDashboard() {
        sidebarController.handleDashboard();
    }
}