package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.entity.DirecteurEtudes;
import model.entity.Enseignant;
import model.entity.Etudiant;
import model.entity.Utilisateur;
import view.App;

/**
 * Controller for the login view.
 * Handles user authentication for students, teachers and directors.
 * 
 * @author Kevin Boeffard
 */
public class LoginController {

    /** Text field for the user identifier input. */
    @FXML private TextField identifiantField;

    /** Password field for the user password input. */
    @FXML private PasswordField mdpField;

    /** Label displaying the error message for the identifier field. */
    @FXML private Label identifiantErreur;

    /** Label displaying the error message for the password field. */
    @FXML private Label mdpErreur;

    @FXML private Button btnConnexion;

    /** CSS style applied to fields with invalid input. */
    private static final String ERROR_STYLE =
        "-fx-background-color: #fecaca, #fff5f5;" +
        "-fx-background-insets: 0, 1.5;" +
        "-fx-background-radius: 6px;" +
        "-fx-padding: 10px 15px;" +
        "-fx-font-size: 14px;";

    /**
     * Handles the login button action.
     * Validates the identifier and password fields, searches for the user
     * in the application's lists, and redirects to the appropriate main view
     * based on the user type (student, teacher or director).
     * Displays error messages with red highlighting on invalid fields.
     */
    @FXML
    public void handleConnexion() {
        reinitialiserErreurs();

        if(identifiantField.getText().trim().isEmpty()) {
            afficherErreurIdentifiant("Veuillez saisir votre identifiant.");
        } else if(mdpField.getText().trim().isEmpty()) {
            afficherErreurMdp("Veuillez saisir votre mot de passe.");
        } else {
            Utilisateur utilisateur = rechercherUtilisateur(identifiantField.getText());

            if(utilisateur == null) {
                afficherErreurIdentifiant("Identifiant inexistant.");
            } else if(!utilisateur.getMdp().equals(mdpField.getText())) {
                afficherErreurMdp("Mot de passe incorrect.");
            } else {
                ouvrirSession(utilisateur);
            }
        }
    }

    /**
     * Resets the styles and hides the error messages of both fields.
     */
    private void reinitialiserErreurs() {
        identifiantField.setStyle("");
        mdpField.setStyle("");
        identifiantErreur.setVisible(false);
        mdpErreur.setVisible(false);
    }

    /**
     * Highlights the identifier field and shows the given error message.
     * @param message the error message to display
     */
    private void afficherErreurIdentifiant(String message) {
        identifiantField.setStyle(ERROR_STYLE);
        identifiantErreur.setText(message);
        identifiantErreur.setVisible(true);
    }

    /**
     * Highlights the password field and shows the given error message.
     * @param message the error message to display
     */
    private void afficherErreurMdp(String message) {
        mdpField.setStyle(ERROR_STYLE);
        mdpErreur.setText(message);
        mdpErreur.setVisible(true);
    }

    /**
     * Searches for a user by their identifier among students and teachers.
     *
     * @param identifiant the identifier entered by the user
     * @return the matching user, or null if none is found
     */
    private Utilisateur rechercherUtilisateur(String identifiant) {
        Utilisateur ret = null;

        for(Etudiant etudiant : App.listEtudiants) {
            if(etudiant.getNumEtudiant().equalsIgnoreCase(identifiant)) ret = etudiant;
        }
        for(Enseignant enseignant : App.listEnseignants) {
            if(enseignant.getIdent().equalsIgnoreCase(identifiant)) ret = enseignant;
        }

        return ret;
    }

    /**
     * Sets the connected user and switches to the main view matching their type.
     * @param utilisateur the user that just logged in
     */
    private void ouvrirSession(Utilisateur utilisateur) {
        String fxml = (utilisateur instanceof Etudiant) ? "main.fxml" : "main_enseignant.fxml";

        App.utilisateurConnecte = utilisateur;
        chargerScene(fxml);
    }

    /**
     * Loads the given FXML file and sets it as the current scene of the window.
     * @param fxml the FXML file of the main view to open
     */
    private void chargerScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/" + fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnConnexion.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}