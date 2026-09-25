package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import view.App;

/**
 * Controller for the Directeur des Études dashboard view.
 * Displays global statistics about the institution.
 * @author Kevin Boeffard
 */
public class DashboardDEController {

    @FXML private Label lblBienvenue;
    @FXML private Label lblNbEtudiants;
    @FXML private Label lblNbEnseignants;
    @FXML private Label lblNbModules;
    @FXML private Label lblNbPromotions;
    @FXML private Label lblNbGroupes;

    /**
     * Initializes the view with the connected user's name and global statistics.
     */
    @FXML
    public void initialize() {
        lblBienvenue.setText("Bonjour " + App.utilisateurConnecte.getPrenom() + " " + App.utilisateurConnecte.getNom());

        lblNbEtudiants.setText(String.valueOf(App.listEtudiants.size()));
        lblNbEnseignants.setText(String.valueOf(App.listEnseignants.size()));
        lblNbModules.setText(String.valueOf(App.listModules.size()));
        lblNbPromotions.setText(String.valueOf(App.listPromotions.size()));
        lblNbGroupes.setText(String.valueOf(App.listGroupesTDs.size()));
    }
}