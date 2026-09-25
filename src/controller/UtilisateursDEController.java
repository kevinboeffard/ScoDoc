package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.dao.EnseignantDAO;
import model.dao.EtudiantDAO;
import model.entity.DirecteurEtudes;
import model.entity.Enseignant;
import model.entity.Etudiant;
import model.entity.GroupeTD;
import model.entity.Promotion;
import model.entity.Utilisateur;
import view.App;

/**
 * Controller for the Directeur des Études users management view.
 * Lists every student, teacher and director, with search, filters,
 * creation and deletion of accounts.
 * @author Kevin Boeffard
 */
public class UtilisateursDEController {

    @FXML private Label lblTotal;
    @FXML private Label lblNbEtudiants;
    @FXML private Label lblNbEnseignants;
    @FXML private Label lblNbDirecteurs;

    @FXML private Button btnFiltreTous;
    @FXML private Button btnFiltreEtudiants;
    @FXML private Button btnFiltreEnseignants;
    @FXML private Button btnFiltreDirecteurs;
    @FXML private TextField champRecherche;

    @FXML private VBox formNouvelUtilisateur;
    @FXML private ComboBox<String> comboTypeUtilisateur;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private TextField champEmail;
    @FXML private PasswordField champMdp;
    @FXML private HBox blocEtudiant;
    @FXML private TextField champNumEtudiant;
    @FXML private ComboBox<GroupeTD> comboGroupeNouvelUtilisateur;
    @FXML private HBox blocEnseignant;
    @FXML private TextField champIdentifiant;
    @FXML private Label lblMessage;

    @FXML private TableView<LigneUtilisateur> table;
    @FXML private TableColumn<LigneUtilisateur, String> colIdentifiant;
    @FXML private TableColumn<LigneUtilisateur, String> colNom;
    @FXML private TableColumn<LigneUtilisateur, String> colPrenom;
    @FXML private TableColumn<LigneUtilisateur, String> colEmail;
    @FXML private TableColumn<LigneUtilisateur, String> colType;
    @FXML private TableColumn<LigneUtilisateur, String> colDetail;
    @FXML private TableColumn<LigneUtilisateur, Void> colAction;

    /** The connected Directeur des Études. */
    private DirecteurEtudes directeur;

    /** Currently active filter: "TOUS", "ETUDIANT", "ENSEIGNANT" or "DE". */
    private String filtreActif;

    /**
     * Initializes the view: configures columns, combo boxes, stats and table.
     */
    @FXML
    public void initialize() {
        directeur = (DirecteurEtudes) App.utilisateurConnecte;
        filtreActif = "TOUS";

        configurerColonnes();
        configurerComboType();
        configurerComboGroupe();

        champRecherche.textProperty().addListener((obs, ancien, nouveau) -> afficherTable());

        afficherStatistiques();
        afficherTable();
        setFiltreActif(btnFiltreTous);
    }

    /**
     * Configures the cell value factories and custom cell factories of the table columns.
     */
    private void configurerColonnes() {
        colIdentifiant.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));

        colType.setCellFactory(col -> creerCelluleType());
        colAction.setCellFactory(col -> creerCelluleSuppression());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Configures the "type" combo box of the new-user form and its visibility listener.
     */
    private void configurerComboType() {
        comboTypeUtilisateur.setItems(FXCollections.observableArrayList("Étudiant", "Enseignant", "Directeur des Études"));
        comboTypeUtilisateur.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> actualiserChampsFormulaire(nouveau));
    }

    /**
     * Configures the "group" combo box of the new-user form with every group of every promotion.
     */
    private void configurerComboGroupe() {
        List<GroupeTD> groupes = new ArrayList<>();

        for(Promotion promo : App.listPromotions) {
            groupes.addAll(promo.getGroupes());
        }

        comboGroupeNouvelUtilisateur.setCellFactory(col -> creerCelluleGroupe());
        comboGroupeNouvelUtilisateur.setButtonCell(creerCelluleGroupe());
        comboGroupeNouvelUtilisateur.setItems(FXCollections.observableArrayList(groupes));
    }

    /**
     * Builds a list cell displaying a group's promotion and name.
     * @return the list cell
     */
    private ListCell<GroupeTD> creerCelluleGroupe() {
        return new ListCell<GroupeTD>() {
            @Override
            protected void updateItem(GroupeTD groupe, boolean empty) {
                super.updateItem(groupe, empty);
                setText(empty || groupe == null ? null : groupe.getPromotion().getNomPromo().name() + " - " + groupe.getNom());
            }
        };
    }

    /**
     * Shows the relevant fields of the new-user form depending on the selected type.
     * @param type the selected user type
     */
    private void actualiserChampsFormulaire(String type) {
        boolean estEtudiant = "Étudiant".equals(type);

        blocEtudiant.setVisible(estEtudiant);
        blocEtudiant.setManaged(estEtudiant);
        blocEnseignant.setVisible(!estEtudiant);
        blocEnseignant.setManaged(!estEtudiant);
    }

    /**
     * Computes and displays the global statistics (total, students, teachers, directors).
     */
    private void afficherStatistiques() {
        int nbDirecteurs = 0;
        int nbEnseignants = 0;

        for(Enseignant enseignant : App.listEnseignants) {
            if(enseignant instanceof DirecteurEtudes) nbDirecteurs++;
            else nbEnseignants++;
        }

        int nbEtudiants = App.listEtudiants.size();

        lblTotal.setText(String.valueOf(nbEtudiants + nbEnseignants + nbDirecteurs));
        lblNbEtudiants.setText(String.valueOf(nbEtudiants));
        lblNbEnseignants.setText(String.valueOf(nbEnseignants));
        lblNbDirecteurs.setText(String.valueOf(nbDirecteurs));
    }

    /**
     * Shows every user, regardless of their type.
     */
    @FXML
    public void handleFiltreTous() {
        filtreActif = "TOUS";
        afficherTable();
        setFiltreActif(btnFiltreTous);
    }

    /**
     * Shows only the students.
     */
    @FXML
    public void handleFiltreEtudiants() {
        filtreActif = "ETUDIANT";
        afficherTable();
        setFiltreActif(btnFiltreEtudiants);
    }

    /**
     * Shows only the teachers (excluding directors).
     */
    @FXML
    public void handleFiltreEnseignants() {
        filtreActif = "ENSEIGNANT";
        afficherTable();
        setFiltreActif(btnFiltreEnseignants);
    }

    /**
     * Shows only the directors of studies.
     */
    @FXML
    public void handleFiltreDirecteurs() {
        filtreActif = "DE";
        afficherTable();
        setFiltreActif(btnFiltreDirecteurs);
    }

    /**
     * Highlights the active filter button and resets the others.
     * @param actif the filter button to mark as active
     */
    private void setFiltreActif(Button actif) {
        Button[] boutons = { btnFiltreTous, btnFiltreEtudiants, btnFiltreEnseignants, btnFiltreDirecteurs };

        for(Button bouton : boutons) {
            boolean estActif = (bouton == actif);
            String fond = estActif ? "#3b82f6" : "white";
            String texte = estActif ? "white" : "#64748b";
            String bordure = estActif ? "" : "-fx-border-color: #e2e8f0; -fx-border-radius: 8px;";

            bouton.setStyle("-fx-background-color: " + fond + "; -fx-text-fill: " + texte + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 16px; -fx-background-radius: 8px; -fx-cursor: hand;" + bordure);
        }
    }

    /**
     * Rebuilds the table rows according to the active filter and search text.
     */
    private void afficherTable() {
        String recherche = champRecherche.getText().trim().toLowerCase(Locale.FRENCH);
        List<LigneUtilisateur> lignes = new ArrayList<>();

        if(filtreActif.equals("TOUS") || filtreActif.equals("ETUDIANT")) {
            for(Etudiant etudiant : App.listEtudiants) {
                LigneUtilisateur ligne = construireLigneEtudiant(etudiant);
                if(correspond(ligne, recherche)) lignes.add(ligne);
            }
        }

        if(filtreActif.equals("TOUS") || filtreActif.equals("ENSEIGNANT") || filtreActif.equals("DE")) {
            for(Enseignant enseignant : App.listEnseignants) {
                boolean estDirecteur = enseignant instanceof DirecteurEtudes;
                boolean inclure = filtreActif.equals("TOUS")
                    || (filtreActif.equals("ENSEIGNANT") && !estDirecteur)
                    || (filtreActif.equals("DE") && estDirecteur);

                if(inclure) {
                    LigneUtilisateur ligne = construireLigneEnseignant(enseignant);
                    if(correspond(ligne, recherche)) lignes.add(ligne);
                }
            }
        }

        table.setItems(FXCollections.observableArrayList(lignes));
        table.setFixedCellSize(40);
        table.setPrefHeight(table.getFixedCellSize() * (lignes.size() + 1) + 2);
    }

    /**
     * Checks whether a row matches the given lowercase search text.
     *
     * @param ligne     the row to check
     * @param recherche the lowercase search text
     * @return true if the row matches (or if the search text is empty)
     */
    private boolean correspond(LigneUtilisateur ligne, String recherche) {
        return recherche.isEmpty()
            || ligne.getNom().toLowerCase(Locale.FRENCH).contains(recherche)
            || ligne.getPrenom().toLowerCase(Locale.FRENCH).contains(recherche)
            || ligne.getEmail().toLowerCase(Locale.FRENCH).contains(recherche)
            || ligne.getIdentifiant().toLowerCase(Locale.FRENCH).contains(recherche);
    }

    /**
     * Builds a table row for the given student.
     *
     * @param etudiant the student to build the row for
     * @return the row as a LigneUtilisateur
     */
    private LigneUtilisateur construireLigneEtudiant(Etudiant etudiant) {
        String detail = "--";

        if(etudiant.getGroupe() != null) {
            detail = etudiant.getGroupe().getPromotion().getNomPromo().name() + " - " + etudiant.getGroupe().getNom();
        }

        return new LigneUtilisateur(etudiant, etudiant.getNumEtudiant(), etudiant.getNom(), etudiant.getPrenom(), etudiant.getEmail(), "Étudiant", detail);
    }

    /**
     * Builds a table row for the given teacher (or director of studies).
     *
     * @param enseignant the teacher to build the row for
     * @return the row as a LigneUtilisateur
     */
    private LigneUtilisateur construireLigneEnseignant(Enseignant enseignant) {
        String type = (enseignant instanceof DirecteurEtudes) ? "Directeur des Études" : "Enseignant";
        String detail = enseignant.getModules().size() + " module(s)";

        return new LigneUtilisateur(enseignant, enseignant.getIdent(), enseignant.getNom(), enseignant.getPrenom(), enseignant.getEmail(), type, detail);
    }

    /**
     * Builds the cell factory used to display the "type" column as a colored badge.
     * @return the table cell
     */
    private TableCell<LigneUtilisateur, String> creerCelluleType() {
        return new TableCell<LigneUtilisateur, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);

                if(empty || type == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(creerBadgeType(type));
                    setText(null);
                }
            }
        };
    }

    /**
     * Builds a colored badge for a user type.
     *
     * @param type the type to display
     * @return the badge as a Label
     */
    private Label creerBadgeType(String type) {
        Label badge = new Label(type);
        badge.setStyle("-fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: bold;");

        if(type.equals("Étudiant")) {
            badge.setStyle(badge.getStyle() + "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6;");
        } else if(type.equals("Enseignant")) {
            badge.setStyle(badge.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;");
        } else {
            badge.setStyle(badge.getStyle() + "-fx-background-color: #f3e8ff; -fx-text-fill: #9333ea;");
        }

        return badge;
    }

    /**
     * Builds the cell factory used to display a "Supprimer" button in the "Action" column.
     * @return the table cell
     */
    private TableCell<LigneUtilisateur, Void> creerCelluleSuppression() {
        return new TableCell<LigneUtilisateur, Void>() {
            private final Button bouton = new Button("Supprimer");

            {
                bouton.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4px 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
                bouton.setOnAction(e -> confirmerSuppression(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : bouton);
            }
        };
    }

    /**
     * Shows the new-user form, resetting its fields to default values.
     */
    @FXML
    public void handleAfficherFormulaire() {
        formNouvelUtilisateur.setVisible(true);
        formNouvelUtilisateur.setManaged(true);

        champNom.clear();
        champPrenom.clear();
        champEmail.clear();
        champMdp.clear();
        champNumEtudiant.clear();
        champIdentifiant.clear();
        lblMessage.setText("");

        comboTypeUtilisateur.getSelectionModel().selectFirst();

        if(!comboGroupeNouvelUtilisateur.getItems().isEmpty()) {
            comboGroupeNouvelUtilisateur.getSelectionModel().selectFirst();
        }
    }

    /**
     * Hides the new-user form.
     */
    @FXML
    public void handleAnnulerFormulaire() {
        formNouvelUtilisateur.setVisible(false);
        formNouvelUtilisateur.setManaged(false);
    }

    /**
     * Validates the common fields and creates the user matching the selected type.
     */
    @FXML
    public void handleCreerUtilisateur() {
        String type = comboTypeUtilisateur.getSelectionModel().getSelectedItem();
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        String email = champEmail.getText().trim();
        String mdp = champMdp.getText().trim();

        if(type == null) {
            afficherErreur("Sélectionnez un type d'utilisateur.");
        } else if(nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            afficherErreur("Tous les champs sont obligatoires.");
        } else {
            try {
                if(type.equals("Étudiant")) {
                    creerEtudiant(nom, prenom, email, mdp);
                } else if(type.equals("Enseignant")) {
                    creerEnseignant(nom, prenom, email, mdp, false);
                } else {
                    creerEnseignant(nom, prenom, email, mdp, true);
                }
            } catch(IllegalArgumentException e) {
                afficherErreur(e.getMessage());
            }
        }
    }

    /**
     * Validates the student-specific fields, creates the student and persists it.
     *
     * @param nom    the last name
     * @param prenom the first name
     * @param email  the email address
     * @param mdp    the password
     */
    private void creerEtudiant(String nom, String prenom, String email, String mdp) {
        String numero = champNumEtudiant.getText().trim();
        GroupeTD groupe = comboGroupeNouvelUtilisateur.getSelectionModel().getSelectedItem();

        if(numero.isEmpty()) {
            afficherErreur("Le numéro étudiant ne peut pas être vide.");
        } else if(groupe == null) {
            afficherErreur("Sélectionnez un groupe.");
        } else {
            Etudiant etudiant = directeur.creerEtudiant(nom, prenom, email, mdp, numero);
            directeur.ajouteEtudiantGroupe(groupe, etudiant);

            if(new EtudiantDAO().create(etudiant) > 0) {
                App.listEtudiants.add(etudiant);
                handleAnnulerFormulaire();
                afficherStatistiques();
                afficherTable();
                afficherSucces("Étudiant créé avec succès.");
            } else {
                afficherErreur("Erreur lors de l'enregistrement en base.");
            }
        }
    }

    /**
     * Validates the teacher-specific fields, creates the teacher (or director) and persists it.
     *
     * @param nom               the last name
     * @param prenom            the first name
     * @param email             the email address
     * @param mdp               the password
     * @param directeurDesEtudes true to create a Directeur des Études instead of a regular teacher
     */
    private void creerEnseignant(String nom, String prenom, String email, String mdp, boolean directeurDesEtudes) {
        String identifiant = champIdentifiant.getText().trim();

        if(identifiant.isEmpty()) {
            afficherErreur("L'identifiant ne peut pas être vide.");
        } else {
            Enseignant nouvel;

            if(directeurDesEtudes) {
                nouvel = new DirecteurEtudes(nom, prenom, email, mdp, identifiant);
            } else {
                nouvel = directeur.creerEnseignant(nom, prenom, email, mdp, identifiant);
            }

            if(new EnseignantDAO().create(nouvel) > 0) {
                App.listEnseignants.add(nouvel);
                handleAnnulerFormulaire();
                afficherStatistiques();
                afficherTable();
                afficherSucces("Enseignant créé avec succès.");
            } else {
                afficherErreur("Erreur lors de l'enregistrement en base.");
            }
        }
    }

    /**
     * Asks for confirmation before deleting a user, blocking self-deletion.
     * @param ligne the row to delete
     */
    private void confirmerSuppression(LigneUtilisateur ligne) {
        Utilisateur utilisateur = ligne.getUtilisateur();

        if(utilisateur instanceof Enseignant && ((Enseignant) utilisateur).getIdent().equals(directeur.getIdent())) {
            afficherErreur("Vous ne pouvez pas supprimer votre propre compte.");
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmer la suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Supprimer définitivement " + ligne.getPrenom() + " " + ligne.getNom() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();

            if(resultat.isPresent() && resultat.get() == ButtonType.OK) {
                supprimerUtilisateur(utilisateur);
            }
        }
    }

    /**
     * Deletes a user from the database and refreshes the view.
     * @param utilisateur the user to delete
     */
    private void supprimerUtilisateur(Utilisateur utilisateur) {
        int resultat = -1;

        if(utilisateur instanceof Etudiant) {
            resultat = new EtudiantDAO().delete((Etudiant) utilisateur);
        } else if(utilisateur instanceof Enseignant) {
            resultat = new EnseignantDAO().delete((Enseignant) utilisateur);
        }

        if(resultat > 0) {
            afficherStatistiques();
            afficherTable();
            afficherSucces("Utilisateur supprimé avec succès.");
        } else {
            afficherErreur("Erreur lors de la suppression.");
        }
    }

    /**
     * Shows an error message in red.
     * @param message the message to display
     */
    private void afficherErreur(String message) {
        lblMessage.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        lblMessage.setText(message);
    }

    /**
     * Shows a success message in green.
     * @param message the message to display
     */
    private void afficherSucces(String message) {
        lblMessage.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
        lblMessage.setText(message);
    }
}