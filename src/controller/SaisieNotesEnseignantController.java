package controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.entity.EnumStatutNote;
import model.entity.Etudiant;
import model.entity.Evaluation;
import model.entity.GroupeTD;
import model.entity.Module;
import model.entity.Note;
import view.App;

import model.dao.NoteDAO;

/**
 * Controller for the grading view.
 * Displays every student following the selected module and lets the teacher
 * enter or edit their grade for the selected evaluation.
 * @author Kevin Boeffard
 */
public class SaisieNotesEnseignantController {

    /** Style applied to a valid grade field. */
    private static final String STYLE_CHAMP_NOTE =
        "-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6px 10px; -fx-font-size: 13px;";

    /** Style applied to an invalid grade field. */
    private static final String STYLE_CHAMP_NOTE_ERREUR =
        "-fx-background-color: #fef2f2; -fx-border-color: #dc2626; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 6px 10px; -fx-font-size: 13px;";

    @FXML private Label lblTitre;
    @FXML private Label lblSousTitre;
    @FXML private VBox conteneurEtudiants;
    @FXML private Label lblMessage;

    /** The module being graded. */
    private Module module;

    /** The evaluation being graded. */
    private Evaluation evaluation;

    /** Students currently displayed. */
    private List<Etudiant> etudiantsAffiches;

    /** Grade fields, indexed by student. */
    private Map<Etudiant, TextField> champsNotes;

    /** Status combo boxes, indexed by student. */
    private Map<Etudiant, ComboBox<EnumStatutNote>> combosStatuts;

    /**
     * Initializes the view with the module and evaluation selected on the dashboard.
     */
    @FXML
    public void initialize() {
        module = NavigationContext.getModuleSelectionne();
        evaluation = NavigationContext.getEvaluationSelectionnee();
        etudiantsAffiches = new ArrayList<>();
        champsNotes = new HashMap<>();
        combosStatuts = new HashMap<>();

        if(module == null || evaluation == null) {
            lblTitre.setText("Aucune évaluation sélectionnée");
            lblSousTitre.setText("Retournez au tableau de bord et choisissez une évaluation.");
        } else {
            lblTitre.setText(module.getCode() + " — " + evaluation.getIdent());
            lblSousTitre.setText(module.getIntitule() + " · Coefficient " + formatCoeff(evaluation.getCoeff()));

            afficherEtudiants();
        }
    }

    /**
     * Builds the students list for the module and evaluation being graded.
     */
    private void afficherEtudiants() {
        etudiantsAffiches.addAll(recupererEtudiantsModule(module));

        if(etudiantsAffiches.isEmpty()) {
            conteneurEtudiants.getChildren().add(creerMessageVide("Aucun élève ne suit ce module."));
        } else {
            conteneurEtudiants.getChildren().add(creerEnteteListe());

            for(Etudiant etudiant : etudiantsAffiches) {
                conteneurEtudiants.getChildren().add(creerLigneEtudiant(etudiant));
            }
        }
    }

    /**
     * Returns the distinct students following the given module, sorted by name.
     *
     * @param module the module to search for
     * @return the sorted list of students
     */
    private List<Etudiant> recupererEtudiantsModule(Module module) {
        Set<Etudiant> etudiants = new LinkedHashSet<>();

        for(GroupeTD groupe : App.listGroupesTDs) {
            if(moduleDansListe(module, groupe.getModulesSuivis())) {
                etudiants.addAll(groupe.getEtudiants());
            }
        }

        List<Etudiant> liste = new ArrayList<>(etudiants);
        liste.sort(Comparator.comparing(Etudiant::getNom).thenComparing(Etudiant::getPrenom));

        return liste;
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
     * Builds the placeholder label shown when there is nothing to display.
     *
     * @param texte the message to display
     * @return the placeholder label
     */
    private Label creerMessageVide(String texte) {
        Label label = new Label(texte);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20px;");

        return label;
    }

    /**
     * Builds the header row of the students list.
     * @return the header as an HBox
     */
    private HBox creerEnteteListe() {
        Label colEtudiant = new Label("Étudiant");
        colEtudiant.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
        colEtudiant.setPrefWidth(280.0);

        Label colNote = new Label("Note / 20");
        colNote.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
        colNote.setPrefWidth(80.0);

        Label colStatut = new Label("Statut");
        colStatut.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

        HBox entete = new HBox(16, colEtudiant, colNote, colStatut);
        entete.setStyle("-fx-padding: 0 14px 4px 14px;");

        return entete;
    }

    /**
     * Builds the row for a given student: name, grade field and status combo box.
     *
     * @param etudiant the student to build the row for
     * @return the row as an HBox
     */
    private HBox creerLigneEtudiant(Etudiant etudiant) {
        Note noteExistante = trouverNote(etudiant);

        Label nom = new Label(etudiant.getNumEtudiant() + " — " + etudiant.getNom() + " " + etudiant.getPrenom());
        nom.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");
        nom.setPrefWidth(280.0);

        TextField champNote = new TextField();
        champNote.setPrefWidth(80.0);
        champNote.setStyle(STYLE_CHAMP_NOTE);

        ComboBox<EnumStatutNote> comboStatut = creerComboStatut();

        EnumStatutNote statutInitial = noteExistante == null ? EnumStatutNote.ATT : noteExistante.getStatut();
        comboStatut.getSelectionModel().select(statutInitial);

        if(noteExistante != null && noteExistante.getValeur() != null) {
            champNote.setText(formatDecimal(noteExistante.getValeur()));
        }

        champNote.setDisable(statutInitial != EnumStatutNote.OK);

        comboStatut.getSelectionModel().selectedItemProperty().addListener((obs, ancien, nouveau) -> {
            boolean estNote = nouveau == EnumStatutNote.OK;
            champNote.setDisable(!estNote);
            if(!estNote) champNote.clear();
        });

        champsNotes.put(etudiant, champNote);
        combosStatuts.put(etudiant, comboStatut);

        HBox ligne = new HBox(16, nom, champNote, comboStatut);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-padding: 10px 14px;");

        return ligne;
    }

    /**
     * Builds a combo box listing every grade status with French labels.
     * @return the configured combo box
     */
    private ComboBox<EnumStatutNote> creerComboStatut() {
        ComboBox<EnumStatutNote> combo = new ComboBox<>();
        combo.setItems(FXCollections.observableArrayList(EnumStatutNote.values()));
        combo.setPrefWidth(140.0);
        combo.setCellFactory(col -> creerCelluleStatut());
        combo.setButtonCell(creerCelluleStatut());

        return combo;
    }

    /**
     * Builds a list cell displaying the French label of a grade status.
     * @return the list cell
     */
    private ListCell<EnumStatutNote> creerCelluleStatut() {
        return new ListCell<EnumStatutNote>() {
            @Override
            protected void updateItem(EnumStatutNote statut, boolean empty) {
                super.updateItem(statut, empty);
                setText(empty || statut == null ? null : libelleStatut(statut));
            }
        };
    }

    /**
     * Returns the French label of a grade status.
     *
     * @param statut the status to translate
     * @return the French label
     */
    private String libelleStatut(EnumStatutNote statut) {
        String libelle;

        if(statut == EnumStatutNote.OK) {
            libelle = "Noté";
        } else if(statut == EnumStatutNote.ABS) {
            libelle = "Absent";
        } else if(statut == EnumStatutNote.EXC) {
            libelle = "Excusé";
        } else {
            libelle = "En attente";
        }

        return libelle;
    }

    /**
     * Searches for the existing grade of a student for the evaluation being graded.
     *
     * @param etudiant the student to search for
     * @return the matching note, or null if none exists
     */
    private Note trouverNote(Etudiant etudiant) {
        Note trouvee = null;

        for(Note note : App.listNotes) {
            if(note.getEtudiant().equals(etudiant) && note.getEvaluation() == evaluation) {
                trouvee = note;
            }
        }

        return trouvee;
    }

    /**
     * Returns to the grade entry dashboard.
     */
    @FXML
    public void handleRetour() {
        NavigationContext.getMainController().chargerVue("saisir_notes_enseignant.fxml");
    }

    /**
     * Validates and saves the grade of every displayed student.
     */
    @FXML
    public void handleEnregistrer() {
        if(evaluation == null) {
            afficherErreur("Aucune évaluation sélectionnée.");
        } else {
            int erreurs = 0;

            for(Etudiant etudiant : etudiantsAffiches) {
                if(!enregistrerNoteEtudiant(etudiant)) erreurs++;
            }

            if(erreurs > 0) {
                afficherErreur(erreurs + " note(s) invalide(s) — vérifiez les champs en rouge.");
            } else {
                afficherSucces("Notes enregistrées avec succès.");
            }
        }
    }

    /**
     * Validates and saves the grade of a single student, creating or updating its Note.
     *
     * @param etudiant the student to save the grade for
     * @return true if the grade was valid and saved, false otherwise
     */
    private boolean enregistrerNoteEtudiant(Etudiant etudiant) {
        ComboBox<EnumStatutNote> comboStatut = combosStatuts.get(etudiant);
        TextField champNote = champsNotes.get(etudiant);
        EnumStatutNote statut = comboStatut.getSelectionModel().getSelectedItem();

        Double valeur = null;
        boolean succes = true;

        if(statut == EnumStatutNote.OK) {
            try {
                valeur = Double.parseDouble(champNote.getText().trim().replace(",", "."));
                succes = valeur >= 0 && valeur <= 20;
            } catch(NumberFormatException e) {
                succes = false;
            }
        }

        champNote.setStyle(succes ? STYLE_CHAMP_NOTE : STYLE_CHAMP_NOTE_ERREUR);

        if(succes) {
            Note note = trouverNote(etudiant);

            if(note == null) {
                note = new Note(etudiant, evaluation, valeur, statut);
                if(new NoteDAO().create(note) > 0) App.listNotes.add(note);
                else succes = false;
            } else {
                note.setValeur(valeur);
                note.setStatut(statut);
                if(new NoteDAO().update(note) <= 0) succes = false;
            }
        }

        return succes;
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

    /**
     * Formats a coefficient, removing the decimal part if it's a whole number.
     *
     * @param coeff the coefficient to format
     * @return the text to display
     */
    private String formatCoeff(double coeff) {
        return coeff == Math.floor(coeff) ? String.valueOf((int) coeff) : String.valueOf(coeff);
    }

    /**
     * Formats a double with one decimal, French locale (comma separator).
     *
     * @param valeur the value to format
     * @return the formatted value
     */
    private String formatDecimal(double valeur) {
        return String.format(Locale.FRANCE, "%.1f", valeur);
    }
}