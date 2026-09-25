package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.entity.EnumStatutNote;
import model.entity.Etudiant;
import model.entity.Module;
import model.entity.Note;
import view.App;

/**
 * Controller for the student grades view.
 * Displays global statistics and a card per followed module, each containing
 * its evaluations table.
 * @author Kevin Boeffard
 */
public class NotesEtudiantController {

    @FXML private Label lblMoyenneGenerale;
    @FXML private Label lblMeilleureNote;
    @FXML private Label lblNotePlusBasse;
    @FXML private Label lblNbModules;
    @FXML private VBox conteneurModules;

    /** The connected student. */
    private Etudiant etudiant;

    /**
     * Initializes the view: loads the global stats and the module cards.
     */
    @FXML
    public void initialize() {
        etudiant = (Etudiant) App.utilisateurConnecte;

        afficherStatistiquesGenerales();
        afficherRessources();
    }

    /**
     * Computes and displays the global statistics (average, best/worst grade, modules count).
     */
    private void afficherStatistiquesGenerales() {
        List<Note> notesValides = recupererNotesValides(recupererNotesEtudiant());
        int nbModules = etudiant.getGroupe().getModulesSuivis().size();

        if(notesValides.isEmpty()) {
            lblMoyenneGenerale.setText("--");
            lblMeilleureNote.setText("--");
            lblNotePlusBasse.setText("--");
        } else {
            lblMoyenneGenerale.setText(formatDecimal(calculerMoyennePonderee(notesValides)));
            lblMeilleureNote.setText(formatDecimal(calculerNoteMax(notesValides)));
            lblNotePlusBasse.setText(formatDecimal(calculerNoteMin(notesValides)));
        }

        lblNbModules.setText(String.valueOf(nbModules));
    }

    /**
     * Builds and adds one card per module followed by the student.
     */
    private void afficherRessources() {
        for(Module module : etudiant.getGroupe().getModulesSuivis()) {
            conteneurModules.getChildren().add(creerCarteModule(module));
        }
    }

    /**
     * Returns every grade of the connected student.
     * @return the list of grades of the connected student
     */
    private List<Note> recupererNotesEtudiant() {
        List<Note> notes = new ArrayList<>();

        for(Note note : App.listNotes) {
            if(note.getEtudiant().equals(etudiant)) {
                notes.add(note);
            }
        }

        return notes;
    }

    /**
     * Returns every grade of the connected student for the given module.
     *
     * @param module the module to filter on
     * @return the list of grades for the given module
     */
    private List<Note> recupererNotesModule(Module module) {
        List<Note> notes = new ArrayList<>();

        for(Note note : App.listNotes) {
            if(note.getEtudiant().equals(etudiant) && note.getEvaluation().getModule().getCode().equals(module.getCode())) {
                notes.add(note);
            }
        }

        return notes;
    }

    /**
     * Filters out grades that are not valid (excused, absent, pending or null).
     *
     * @param notes the grades to filter
     * @return the list of valid grades
     */
    private List<Note> recupererNotesValides(List<Note> notes) {
        List<Note> notesValides = new ArrayList<>();

        for(Note note : notes) {
            if(note.getStatut() == EnumStatutNote.OK && note.getValeur() != null) {
                notesValides.add(note);
            }
        }

        return notesValides;
    }

    /**
     * Computes the weighted average of the given grades.
     *
     * @param notes the grades to average
     * @return the weighted average, or 0 if the sum of coefficients is 0
     */
    private double calculerMoyennePonderee(List<Note> notes) {
        double sommeNotes = 0;
        double sommeCoeffs = 0;

        for(Note note : notes) {
            double coeff = note.getEvaluation().getCoeff();
            sommeNotes += note.getValeur() * coeff;
            sommeCoeffs += coeff;
        }

        return sommeCoeffs == 0 ? 0 : sommeNotes / sommeCoeffs;
    }

    /**
     * Returns the highest grade among the given grades.
     *
     * @param notes the grades to inspect
     * @return the highest grade value
     */
    private double calculerNoteMax(List<Note> notes) {
        double max = 0;

        for(Note note : notes) {
            if(note.getValeur() > max) max = note.getValeur();
        }

        return max;
    }

    /**
     * Returns the lowest grade among the given grades.
     *
     * @param notes the grades to inspect
     * @return the lowest grade value
     */
    private double calculerNoteMin(List<Note> notes) {
        double min = 20;

        for(Note note : notes) {
            if(note.getValeur() < min) min = note.getValeur();
        }

        return min;
    }

    /**
     * Builds the card for a given module: header, mini-stats and evaluations table.
     *
     * @param module the module to build the card for
     * @return the card as a VBox
     */
    private VBox creerCarteModule(Module module) {
        List<Note> notesModule = recupererNotesModule(module);
        List<Note> notesValides = recupererNotesValides(notesModule);
        double moyenne = calculerMoyennePonderee(notesValides);

        VBox carte = new VBox(12);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 18px;");
        carte.getChildren().add(creerEnteteModule(module, moyenne, notesValides.isEmpty()));
        carte.getChildren().add(creerStatsModule(notesModule, notesValides, moyenne));
        carte.getChildren().add(creerTableEvaluations(notesModule));

        return carte;
    }

    /**
     * Builds the header row of a module card: code, title and status badge.
     *
     * @param module   the module to display
     * @param moyenne  the weighted average of the module
     * @param sansNote whether the module has no valid grade yet
     * @return the header as an HBox
     */
    private HBox creerEnteteModule(Module module, double moyenne, boolean sansNote) {
        Label code = new Label(module.getCode());
        code.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label intitule = new Label(module.getIntitule());
        intitule.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox titre = new HBox(10, code, intitule);
        titre.setAlignment(Pos.CENTER_LEFT);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox entete = new HBox(10, titre, espace, creerBadgeStatutModule(moyenne, sansNote));
        entete.setAlignment(Pos.CENTER_LEFT);

        return entete;
    }

    /**
     * Builds the status badge of a module (Validé / Non validé / En attente).
     *
     * @param moyenne  the weighted average of the module
     * @param sansNote whether the module has no valid grade yet
     * @return the badge as a Label
     */
    private Label creerBadgeStatutModule(double moyenne, boolean sansNote) {
        Label badge = new Label();
        badge.setStyle("-fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: bold;");

        if(sansNote) {
            badge.setText("En attente");
            badge.setStyle(badge.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;");
        } else if(moyenne >= 10) {
            badge.setText("Validé");
            badge.setStyle(badge.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;");
        } else {
            badge.setText("Non validé");
            badge.setStyle(badge.getStyle() + "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;");
        }

        return badge;
    }

    /**
     * Builds the mini-stats row of a module card (average, total coefficient, evaluations count).
     *
     * @param notesModule  every grade of the module
     * @param notesValides the valid grades of the module
     * @param moyenne      the weighted average of the module
     * @return the mini-stats row as an HBox
     */
    private HBox creerStatsModule(List<Note> notesModule, List<Note> notesValides, double moyenne) {
        double coeffTotal = 0;
        for(Note note : notesModule) {
            coeffTotal += note.getEvaluation().getCoeff();
        }

        String texteMoyenne = notesValides.isEmpty() ? "--" : formatDecimal(moyenne) + " / 20";
        String texteEvaluations = notesValides.size() + " / " + notesModule.size();

        return new HBox(10,
            creerMiniCarte(texteMoyenne, "MOYENNE"),
            creerMiniCarte(formatCoeff(coeffTotal), "COEFF."),
            creerMiniCarte(texteEvaluations, "ÉVAL. NOTÉES")
        );
    }

    /**
     * Builds a small statistic card with a value and its label.
     *
     * @param valeur the value to display
     * @param label  the label describing the value
     * @return the mini card as a VBox
     */
    private VBox creerMiniCarte(String valeur, String label) {
        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");

        VBox carte = new VBox(2, lblValeur, lblLabel);
        carte.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8px; -fx-padding: 10px 16px; -fx-alignment: center;");

        return carte;
    }

    /**
     * Builds the evaluations table of a module card.
     *
     * @param notesModule the grades of the module to display
     * @return the table as a TableView
     */
    private TableView<LigneEvaluation> creerTableEvaluations(List<Note> notesModule) {
        TableView<LigneEvaluation> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LigneEvaluation, String> colType = new TableColumn<>("Type d'épreuve");
        TableColumn<LigneEvaluation, String> colNote = new TableColumn<>("Note");
        TableColumn<LigneEvaluation, String> colCoeff = new TableColumn<>("Coeff");

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colCoeff.setCellValueFactory(new PropertyValueFactory<>("coeff"));
        colNote.setCellFactory(creerCellFactoryBadgeNote());

        table.getColumns().addAll(colType, colNote, colCoeff);

        List<LigneEvaluation> lignes = new ArrayList<>();
        for(Note note : notesModule) {
            lignes.add(new LigneEvaluation(note.getEvaluation().getIdent(), formatNoteAffichee(note), formatCoeff(note.getEvaluation().getCoeff())));
        }

        table.setItems(FXCollections.observableArrayList(lignes));
        table.setPrefHeight(40 + lignes.size() * 45);

        return table;
    }

    /**
     * Builds the cell factory used to display grades as colored badges.
     * @return the cell factory for the "Note" column
     */
    private Callback<TableColumn<LigneEvaluation, String>, TableCell<LigneEvaluation, String>> creerCellFactoryBadgeNote() {
        return col -> new TableCell<LigneEvaluation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(creerBadgeNote(item));
                    setText(null);
                }
            }
        };
    }

    /**
     * Builds a colored badge for a grade cell.
     *
     * @param texte the text to display in the badge
     * @return the badge as a Label
     */
    private Label creerBadgeNote(String texte) {
        Label label = new Label(texte);
        label.setStyle("-fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: bold;");

        try {
            double val = Double.parseDouble(texte.split(" ")[0].replace(",", "."));
            if(val >= 14) {
                label.setStyle(label.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;");
            } else if(val >= 10) {
                label.setStyle(label.getStyle() + "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04;");
            } else {
                label.setStyle(label.getStyle() + "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;");
            }
        } catch(NumberFormatException e) {
            if(texte.equals("ABS")) {
                label.setStyle(label.getStyle() + "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;");
            } else if(texte.equals("ATT")) {
                label.setStyle(label.getStyle() + "-fx-background-color: #dbeafe; -fx-text-fill: #2563eb;");
            } else {
                label.setStyle(label.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;");
            }
        }

        return label;
    }

    /**
     * Formats the grade of a note depending on its status, for the table display.
     *
     * @param note the note to format
     * @return the text to display in the table
     */
    private String formatNoteAffichee(Note note) {
        String texte;

        if(note.getStatut() == EnumStatutNote.EXC) {
            texte = "EXC";
        } else if(note.getStatut() == EnumStatutNote.ABS) {
            texte = "ABS";
        } else if(note.getStatut() == EnumStatutNote.ATT || note.getValeur() == null) {
            texte = "ATT";
        } else {
            texte = String.format(Locale.FRANCE, "%.1f / 20", note.getValeur());
        }

        return texte;
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

    /**
     * Formats a coefficient, removing the decimal part if it's a whole number.
     *
     * @param coeff the coefficient to format
     * @return the text to display
     */
    private String formatCoeff(double coeff) {
        return coeff == Math.floor(coeff) ? String.valueOf((int) coeff) : String.valueOf(coeff);
    }
}