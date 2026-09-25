package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.entity.EnumPresence;
import model.entity.Etudiant;
import model.entity.Module;
import model.entity.Note;
import model.entity.Presence;
import model.entity.Seance;
import view.App;

public class DashboardEtudiantController {

    @FXML private Label avatarLabel;
    @FXML private Label nomEtudiant;
    @FXML private Label promoEtudiant;
    @FXML private Label numeroEtudiant;
    @FXML private Label moyenneLabel;
    @FXML private Label nbModulesLabel;
    @FXML private Label assiduitéLabel;
    @FXML private Label nbAbsences;
    @FXML private Label nbRetards;
    @FXML private Label nbModulesTexte;
    @FXML private TableView<LigneNote> tableNotes;
    @FXML private TableColumn<LigneNote, String> colModule;
    @FXML private TableColumn<LigneNote, String> colNote;
    @FXML private TableColumn<LigneNote, Integer> colCoeff;
    @FXML private GridPane gridModules;

    /**
     * Shows the new-user form, resetting its fields to default values.
     */
    @FXML
    public void initialize() {
        Etudiant etudiant = (Etudiant) App.utilisateurConnecte;

        // Infos étudiant
        avatarLabel.setText(
            String.valueOf(etudiant.getPrenom().charAt(0)) +
            String.valueOf(etudiant.getNom().charAt(0))
        );
        nomEtudiant.setText(etudiant.getPrenom() + " " + etudiant.getNom());
        promoEtudiant.setText(etudiant.getGroupe().getPromotion().getNomPromo().name() + " - Informatique");
        numeroEtudiant.setText("N° " + etudiant.getNumEtudiant());

        // Moyenne
        double moyenne = etudiant.calculerMoyenneGenerale();
        moyenneLabel.setText(String.format("%.1f", moyenne));

        // Nb modules
        int nbModules = etudiant.getGroupe().getModulesSuivis().size();
        nbModulesLabel.setText(String.valueOf(nbModules));
        nbModulesTexte.setText(nbModules + " modules - " + etudiant.getGroupe().getPromotion().getNomPromo().name());

        // Absences et retards
        int absences = 0;
        int retards = 0;
        for(Presence p : App.listPresences) {
            if(p.getEtudiant().equals(etudiant)) {
                if(p.getStatut() == EnumPresence.ABSENT) absences++;
                if(p.getStatut() == EnumPresence.RETARD) retards++;
            }
        }
        nbAbsences.setText(String.valueOf(absences));
        nbRetards.setText(String.valueOf(retards));

        // Assiduité
        int totalSeances = 0;
        for(Seance s : App.listSeances) {
            if(s.getGroupe().equals(etudiant.getGroupe())) totalSeances++;
        }
        int assiduite = totalSeances > 0 ? (int)((totalSeances - absences) * 100.0 / totalSeances) : 100;
        assiduitéLabel.setText(assiduite + "%");

        // Notes dans la TableView
        colModule.setCellValueFactory(new PropertyValueFactory<>("module"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colCoeff.setCellValueFactory(new PropertyValueFactory<>("coeff"));
        colNote.setCellFactory(col -> new TableCell<LigneNote, String>() {
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if(empty || item == null) {
                setGraphic(null);
            } else {
                Label label = new Label(item);
                label.setStyle("-fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: bold;");

                // Couleur selon la note
                try {
                    double val = Double.parseDouble(item.split(" ")[0].replace(",", "."));
                    if(val >= 14) {
                        label.setStyle(label.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;");
                    } else if(val >= 10) {
                        label.setStyle(label.getStyle() + "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04;");
                    } else {
                        label.setStyle(label.getStyle() + "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;");
                    }
                } catch(NumberFormatException e) {
                    label.setStyle(label.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;");
                }

                setGraphic(label);
                setText(null);
            }
        }
    });

        for(Note note : App.listNotes) {
            if(note.getEtudiant().equals(etudiant)) {
                String valeur = note.getValeur() != null
                    ? String.format("%.1f / 20", note.getValeur())
                    : note.getStatut().name();
                tableNotes.getItems().add(new LigneNote(
                    note.getEvaluation().getModule().getCode(),
                    valeur,
                    (int) note.getEvaluation().getCoeff()
                ));
                tableNotes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            }
        }

        // Modules dans le GridPane
        int col = 0;
        int row = 0;
        for(Module m : etudiant.getGroupe().getModulesSuivis()) {
            HBox chip = new HBox();
            chip.setSpacing(8);
            chip.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 6px; -fx-padding: 6px 10px;");
            Label code = new Label(m.getCode());
            code.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px;");
            Label nom = new Label(m.getIntitule());
            nom.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px;");
            chip.getChildren().addAll(code, nom);
            gridModules.add(chip, col, row);
            col++;
            if(col == 3) { col = 0; row++; }
        }
    }


    public static class LigneNote {
        private String module;
        private String note;
        private int coeff;

        public LigneNote(String module, String note, int coeff) {
            this.module = module;
            this.note = note;
            this.coeff = coeff;
        }

        public String getModule() { return this.module; }
        public String getNote() { return this.note; }
        public int getCoeff() { return this.coeff; }
    }
}