package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.entity.Enseignant;
import model.entity.EnumPromo;
import model.entity.Etudiant;
import model.entity.Evaluation;
import model.entity.GroupeTD;
import model.entity.Module;
import model.entity.Note;
import model.entity.Promotion;
import view.App;
import model.dao.EvaluationDAO;

/**
 * Controller for the teacher's grade entry dashboard.
 * Displays the teacher's modules grouped by promotion, each evaluation showing
 * its grading progress, and lets the teacher create new evaluations.
 * @author Kevin Boeffard
 */
public class SaisirNotesEnseignantController {

    @FXML private VBox formNouvelleEval;
    @FXML private ComboBox<Module> comboModuleNouvelleEval;
    @FXML private TextField champNomEval;
    @FXML private TextField champCoeffEval;
    @FXML private Label lblMessage;
    @FXML private HBox conteneurStats;
    @FXML private VBox conteneurPromotions;

    /** The connected teacher. */
    private Enseignant enseignant;

    /**
     * Initializes the view: configures the module combo box and builds the dashboard.
     */
    @FXML
    public void initialize() {
        enseignant = (Enseignant) App.utilisateurConnecte;

        comboModuleNouvelleEval.setCellFactory(col -> creerCelluleModule());
        comboModuleNouvelleEval.setButtonCell(creerCelluleModule());
        comboModuleNouvelleEval.setItems(FXCollections.observableArrayList(enseignant.getModules()));

        afficherStatistiques();
        afficherPromotions();
    }

    /**
     * Builds a list cell displaying a module's code and title.
     * @return the list cell
     */
    private ListCell<Module> creerCelluleModule() {
        return new ListCell<Module>() {
            @Override
            protected void updateItem(Module module, boolean empty) {
                super.updateItem(module, empty);
                setText(empty || module == null ? null : module.getCode() + " - " + module.getIntitule());
            }
        };
    }

    /**
     * Groups the teacher's modules by promotion name.
     * @return a map associating each promotion name to the teacher's modules in it
     */
    private Map<EnumPromo, List<Module>> regrouperModulesParPromotion() {
        Map<EnumPromo, List<Module>> regroupement = new LinkedHashMap<>();

        for(Promotion promo : App.listPromotions) {
            regroupement.put(promo.getNomPromo(), new ArrayList<>());
        }

        for(Module module : enseignant.getModules()) {
            List<Module> liste = regroupement.get(module.getPromo().getNomPromo());
            if(liste != null) liste.add(module);
        }

        return regroupement;
    }

    /**
     * Builds and displays the stats cards (total modules + one per promotion with modules).
     */
    private void afficherStatistiques() {
        conteneurStats.getChildren().clear();
        conteneurStats.getChildren().add(creerCarteStat(String.valueOf(enseignant.getModules().size()), "MODULES ENSEIGNÉS"));

        Map<EnumPromo, List<Module>> regroupement = regrouperModulesParPromotion();

        for(Promotion promo : App.listPromotions) {
            List<Module> modules = regroupement.get(promo.getNomPromo());
            if(modules != null && !modules.isEmpty()) {
                conteneurStats.getChildren().add(creerCarteStat(String.valueOf(modules.size()), promo.getNomPromo().name()));
            }
        }
    }

    /**
     * Builds a small statistic card with a value and its label.
     *
     * @param valeur the value to display
     * @param label  the label describing the value
     * @return the stat card as a VBox
     */
    private VBox creerCarteStat(String valeur, String label) {
        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");

        VBox carte = new VBox(4, lblValeur, lblLabel);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 16px;");
        HBox.setHgrow(carte, Priority.ALWAYS);

        return carte;
    }

    /**
     * Rebuilds the list of promotion cards.
     */
    private void afficherPromotions() {
        conteneurPromotions.getChildren().clear();

        Map<EnumPromo, List<Module>> regroupement = regrouperModulesParPromotion();

        for(Promotion promo : App.listPromotions) {
            List<Module> modules = regroupement.get(promo.getNomPromo());
            if(modules != null && !modules.isEmpty()) {
                conteneurPromotions.getChildren().add(creerCartePromotion(promo, modules));
            }
        }

        if(conteneurPromotions.getChildren().isEmpty()) {
            Label vide = new Label("Aucun module ne vous est assigné.");
            vide.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20px;");
            conteneurPromotions.getChildren().add(vide);
        }
    }

    /**
     * Builds the card for a given promotion: a clickable header and a hidden modules pane.
     *
     * @param promo   the promotion to build the card for
     * @param modules the teacher's modules in this promotion
     * @return the card as a VBox
     */
    private VBox creerCartePromotion(Promotion promo, List<Module> modules) {
        Label titre = new Label(promo.getNomPromo().name());
        titre.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label nb = new Label(modules.size() + " module(s) affecté(s) · " + compterEtudiantsPromotion(promo) + " élèves");
        nb.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        Label fleche = new Label("▾");
        fleche.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox entete = new HBox(10, titre, espace, nb, fleche);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setStyle("-fx-cursor: hand;");

        FlowPane modulesPane = new FlowPane();
        modulesPane.setHgap(16.0);
        modulesPane.setVgap(16.0);
        modulesPane.setStyle("-fx-padding: 14px 0 0 0;");
        modulesPane.setVisible(false);
        modulesPane.setManaged(false);

        for(Module module : modules) {
            modulesPane.getChildren().add(creerCarteModule(module));
        }

        entete.setOnMouseClicked(e -> basculerVisibilite(modulesPane, fleche));

        VBox carte = new VBox(0, entete, modulesPane);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 18px;");

        return carte;
    }

    /**
     * Toggles the visibility of a node and updates the arrow icon accordingly.
     *
     * @param pane   the node to show or hide
     * @param fleche the arrow label to update
     */
    private void basculerVisibilite(Region pane, Label fleche) {
        boolean visible = !pane.isVisible();

        pane.setVisible(visible);
        pane.setManaged(visible);
        fleche.setText(visible ? "▴" : "▾");
    }

    /**
     * Returns the total number of students in the given promotion.
     *
     * @param promo the promotion to count students for
     * @return the number of students in the promotion
     */
    private int compterEtudiantsPromotion(Promotion promo) {
        int compteur = 0;

        for(GroupeTD groupe : promo.getGroupes()) {
            compteur += groupe.getEtudiants().size();
        }

        return compteur;
    }

    /**
     * Builds the card for a given module: header, group names and evaluation rows.
     *
     * @param module the module to build the card for
     * @return the card as a VBox
     */
    private VBox creerCarteModule(Module module) {
        Label code = new Label(module.getCode());
        code.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label intitule = new Label(module.getIntitule());
        intitule.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox titre = new HBox(8, code, intitule);
        titre.setAlignment(Pos.CENTER_LEFT);

        Label groupes = new Label(listerGroupes(module));
        groupes.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        VBox evaluations = new VBox(8);
        if(module.getListeEval().isEmpty()) {
            Label vide = new Label("Aucune évaluation créée");
            vide.setStyle("-fx-font-size: 12px; -fx-text-fill: #ca8a04; -fx-background-color: #fef9c3; -fx-background-radius: 6px; -fx-padding: 8px 12px;");
            evaluations.getChildren().add(vide);
        } else {
            for(Evaluation evaluation : module.getListeEval()) {
                evaluations.getChildren().add(creerLigneEvaluation(module, evaluation));
            }
        }

        VBox carte = new VBox(10, titre, groupes, evaluations);
        carte.setPrefWidth(360.0);
        carte.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-padding: 14px;");

        return carte;
    }

    /**
     * Returns a list of the group names following the given module, separated by " · ".
     *
     * @param module the module to search groups for
     * @return the list of group names, or "Aucun groupe" if none
     */
    private String listerGroupes(Module module) {
        Set<String> noms = new LinkedHashSet<>();

        for(GroupeTD groupe : App.listGroupesTDs) {
            if(moduleDansListe(module, groupe.getModulesSuivis())) {
                noms.add("Groupe " + groupe.getNom());
            }
        }

        return noms.isEmpty() ? "Aucun groupe" : String.join(" · ", noms);
    }

    /**
     * Checks whether the given module is present in the given list, comparing by code.
     *
     * @param module the module to look for
     * @param liste  the list to search in
     * @return true if a module with the same code is in the list
     */
    private boolean moduleDansListe(Module module, List<Module> liste) {
        boolean trouve = false;

        for(Module m : liste) {
            if(m.getCode().equals(module.getCode())) trouve = true;
        }

        return trouve;
    }

    /**
     * Builds a clickable row for a given evaluation, showing its grading progress.
     *
     * @param module     the module the evaluation belongs to
     * @param evaluation the evaluation to display
     * @return the row as an HBox
     */
    private HBox creerLigneEvaluation(Module module, Evaluation evaluation) {
        int total = compterEtudiantsModule(module);
        int notes = compterNotesSaisies(evaluation);
        boolean complet = total > 0 && notes == total;

        Label point = new Label("●");
        point.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (complet ? "#16a34a" : "#f59e0b") + ";");

        Label nom = new Label(evaluation.getIdent());
        nom.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e293b;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        Label progression = new Label(notes + "/" + total + (complet ? " ✓" : ""));
        progression.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (complet ? "#16a34a" : "#64748b") + ";");

        HBox ligne = new HBox(8, point, nom, espace, progression);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: white; -fx-background-radius: 6px; -fx-padding: 8px 10px; -fx-cursor: hand;");

        ligne.setOnMouseClicked(e -> ouvrirSaisie(module, evaluation));

        return ligne;
    }

    /**
     * Returns the number of distinct students following the given module.
     *
     * @param module the module to check
     * @return the number of distinct students
     */
    private int compterEtudiantsModule(Module module) {
        Set<Etudiant> etudiants = new LinkedHashSet<>();

        for(GroupeTD groupe : App.listGroupesTDs) {
            if(moduleDansListe(module, groupe.getModulesSuivis())) {
                etudiants.addAll(groupe.getEtudiants());
            }
        }

        return etudiants.size();
    }

    /**
     * Returns the number of grades already entered for the given evaluation.
     *
     * @param evaluation the evaluation to check
     * @return the number of grades entered
     */
    private int compterNotesSaisies(Evaluation evaluation) {
        int compteur = 0;

        for(Note note : App.listNotes) {
            if(note.getEvaluation() == evaluation) compteur++;
        }

        return compteur;
    }

    /**
     * Saves the selected module and evaluation, then opens the grading view.
     *
     * @param module     the module to grade
     * @param evaluation the evaluation to grade
     */
    private void ouvrirSaisie(Module module, Evaluation evaluation) {
        NavigationContext.setSelection(module, evaluation);
        NavigationContext.getMainController().chargerVue("saisie_notes_enseignant.fxml");
    }

    /**
     * Shows the new evaluation form, resetting its fields.
     */
    @FXML
    public void handleAfficherFormulaire() {
        formNouvelleEval.setVisible(true);
        formNouvelleEval.setManaged(true);
        champNomEval.clear();
        champCoeffEval.clear();
        lblMessage.setText("");

        if(!comboModuleNouvelleEval.getItems().isEmpty() && comboModuleNouvelleEval.getSelectionModel().isEmpty()) {
            comboModuleNouvelleEval.getSelectionModel().selectFirst();
        }
    }

    /**
     * Hides the new evaluation form.
     */
    @FXML
    public void handleAnnulerFormulaire() {
        formNouvelleEval.setVisible(false);
        formNouvelleEval.setManaged(false);
    }

    /** Sets the evaluation name field to the "DS" preset. */
    @FXML
    public void handleTypeDS() {
        appliquerPreset("DS — ");
    }

    /** Sets the evaluation name field to the "TP noté" preset. */
    @FXML
    public void handleTypeTP() {
        appliquerPreset("TP noté — ");
    }

    /** Sets the evaluation name field to the "CC" preset. */
    @FXML
    public void handleTypeCC() {
        appliquerPreset("CC — ");
    }

    /** Sets the evaluation name field to the "Projet" preset. */
    @FXML
    public void handleTypeProjet() {
        appliquerPreset("Projet — ");
    }

    /** Sets the evaluation name field to the "SAÉ" preset. */
    @FXML
    public void handleTypeSAE() {
        appliquerPreset("SAÉ — ");
    }

    /**
     * Sets the evaluation name field to the given prefix, keeping any text already typed after it.
     *
     * @param prefixe the prefix to apply
     */
    private void appliquerPreset(String prefixe) {
        String texte = champNomEval.getText();
        String suffixe = texte.contains("—") ? texte.substring(texte.indexOf("—") + 1).trim() : texte.trim();

        champNomEval.setText(suffixe.isEmpty() ? prefixe : prefixe + suffixe);
        champNomEval.positionCaret(champNomEval.getText().length());
        champNomEval.requestFocus();
    }

        /**
         * Creates a new evaluation for the selected module and refreshes the dashboard.
         */
        @FXML
        public void handleCreerEvaluation() {
            Module module = comboModuleNouvelleEval.getSelectionModel().getSelectedItem();
            String nom = champNomEval.getText().trim();
            String texteCoeff = champCoeffEval.getText().trim();

            if(module == null) {
                afficherErreur("Sélectionnez un module.");
            } else if(nom.isEmpty()) {
                afficherErreur("Le nom de l'évaluation ne peut pas être vide.");
            } else {
                try {
                    double coeff = texteCoeff.isEmpty() ? 1.0 : Double.parseDouble(texteCoeff.replace(",", "."));
                    Evaluation evaluation = new Evaluation(nom, coeff, module, null);

                    if(new EvaluationDAO().create(evaluation) > 0) {
                        App.listEvaluations.add(evaluation);
                        handleAnnulerFormulaire();
                        afficherStatistiques();
                        afficherPromotions();
                    } else {
                        afficherErreur("Erreur lors de l'enregistrement en base.");
                    }
                } catch(NumberFormatException e) {
                    afficherErreur("Le coefficient doit être un nombre.");
                } catch(IllegalArgumentException e) {
                    afficherErreur(e.getMessage());
                }
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
}