package controller;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.entity.EnumPresence;
import model.entity.Etudiant;
import model.entity.Presence;
import model.entity.Seance;
import view.App;

/**
 * Controller for the student attendance view.
 * Displays global statistics and a filterable timeline of absences and delays.
 * @author Kevin Boeffard
 */
public class AbsencesEtudiantController {

    @FXML private Label lblAbsences;
    @FXML private Label lblRetards;
    @FXML private Label lblHeuresManquees;
    @FXML private Label lblAssiduite;
    @FXML private VBox conteneurAbsences;
    @FXML private Button btnTout;
    @FXML private Button btnAbsencesFiltre;
    @FXML private Button btnRetardsFiltre;

    /** The connected student. */
    private Etudiant etudiant;

    /** Every absence/delay row of the connected student, sorted from most recent to oldest. */
    private List<LigneAbsence> toutesLesLignes;

    /**
     * Initializes the view: loads the global stats and the full timeline.
     */
    @FXML
    public void initialize() {
        etudiant = (Etudiant) App.utilisateurConnecte;
        toutesLesLignes = construireLignes();

        afficherStatistiques();
        afficherTimeline(toutesLesLignes);
    }

    /**
     * Shows every absence and delay.
     */
    @FXML
    public void handleFiltreTout() {
        afficherTimeline(toutesLesLignes);
        setFiltreActif(btnTout);
    }

    /**
     * Shows only the absences.
     */
    @FXML
    public void handleFiltreAbsences() {
        afficherTimeline(filtrerParStatut(EnumPresence.ABSENT));
        setFiltreActif(btnAbsencesFiltre);
    }

    /**
     * Shows only the delays.
     */
    @FXML
    public void handleFiltreRetards() {
        afficherTimeline(filtrerParStatut(EnumPresence.RETARD));
        setFiltreActif(btnRetardsFiltre);
    }

    /**
     * Builds the list of absence/delay rows of the connected student, sorted from most recent to oldest.
     * @return the sorted list of rows
     */
    private List<LigneAbsence> construireLignes() {
        List<LigneAbsence> lignes = new ArrayList<>();

        for(Presence presence : App.listPresences) {
            if(presence.getEtudiant().equals(etudiant) && presence.getStatut() != EnumPresence.PRESENT) {
                Seance seance = presence.getSeance();
                lignes.add(new LigneAbsence(
                    seance.getDate(),
                    seance.getModule().getCode(),
                    seance.getModule().getIntitule(),
                    seance.getHeureDeb(),
                    seance.getHeureFin(),
                    presence.getStatut(),
                    determinerSemestre(seance.getModule().getCode())
                ));
            }
        }

        lignes.sort(Comparator.comparing(LigneAbsence::getDate).reversed());

        return lignes;
    }

    /**
     * Deduces the semester from the second character of the module code.
     *
     * @param codeModule the module code (e.g. "R2.02")
     * @return the semester number, or 0 if it cannot be deduced
     */
    private int determinerSemestre(String codeModule) {
        int semestre = 0;

        if(codeModule != null && codeModule.length() >= 2 && Character.isDigit(codeModule.charAt(1))) {
            semestre = Character.getNumericValue(codeModule.charAt(1));
        }

        return semestre;
    }

    /**
     * Filters the rows by attendance status.
     *
     * @param statut the status to filter on
     * @return the filtered list of rows
     */
    private List<LigneAbsence> filtrerParStatut(EnumPresence statut) {
        List<LigneAbsence> lignes = new ArrayList<>();

        for(LigneAbsence ligne : toutesLesLignes) {
            if(ligne.getStatut() == statut) lignes.add(ligne);
        }

        return lignes;
    }

    /**
     * Computes and displays the global statistics (absences, delays, missed hours, attendance rate).
     */
    private void afficherStatistiques() {
        int absences = 0;
        int retards = 0;
        long minutesManquees = 0;
        int totalSeances = 0;

        for(Presence presence : App.listPresences) {
            if(presence.getEtudiant().equals(etudiant)) {
                totalSeances++;

                if(presence.getStatut() == EnumPresence.ABSENT) {
                    absences++;
                    Seance seance = presence.getSeance();
                    minutesManquees += Duration.between(seance.getHeureDeb(), seance.getHeureFin()).toMinutes();
                } else if(presence.getStatut() == EnumPresence.RETARD) {
                    retards++;
                }
            }
        }

        int assiduite = totalSeances != 0 ? (int) ((totalSeances - absences) * 100.0 / totalSeances) : 100;

        lblAbsences.setText(String.valueOf(absences));
        lblRetards.setText(String.valueOf(retards));
        lblHeuresManquees.setText(formatDuree(minutesManquees));
        lblAssiduite.setText(assiduite + "%");
    }

    /**
     * Formats a duration in minutes as "XhYY" (or "Xh" if there are no remaining minutes).
     *
     * @param minutes the total duration in minutes
     * @return the formatted duration
     */
    private String formatDuree(long minutes) {
        long heures = minutes / 60;
        long reste = minutes % 60;

        return reste == 0 ? heures + "h" : heures + "h" + reste;
    }

    /**
     * Rebuilds the timeline with the given rows, grouped by semester.
     *
     * @param lignes the rows to display
     */
    private void afficherTimeline(List<LigneAbsence> lignes) {
        conteneurAbsences.getChildren().clear();

        int semestreActuel = -1;

        for(LigneAbsence ligne : lignes) {
            if(ligne.getSemestre() != semestreActuel) {
                semestreActuel = ligne.getSemestre();
                conteneurAbsences.getChildren().add(creerEnteteSemestre(semestreActuel));
            }

            conteneurAbsences.getChildren().add(creerCarteAbsence(ligne));
        }

        if(lignes.isEmpty()) {
            conteneurAbsences.getChildren().add(creerMessageVide());
        }
    }

    /**
     * Builds the header label for a semester group.
     *
     * @param semestre the semester number
     * @return the header label
     */
    private Label creerEnteteSemestre(int semestre) {
        Label label = new Label("Semestre " + semestre);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-padding: 10px 0 0 4px;");

        return label;
    }

    /**
     * Builds the placeholder label shown when there is nothing to display.
     * @return the placeholder label
     */
    private Label creerMessageVide() {
        Label label = new Label("Aucun événement à afficher.");
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20px;");

        return label;
    }

    /**
     * Builds the card representing one absence or delay.
     *
     * @param ligne the row to display
     * @return the card as an HBox
     */
    private HBox creerCarteAbsence(LigneAbsence ligne) {
        boolean estAbsence = ligne.getStatut() == EnumPresence.ABSENT;
        String couleur = estAbsence ? "#dc2626" : "#ca8a04";

        VBox date = creerDate(ligne.getDate(), couleur);
        VBox infos = creerInfos(ligne);
        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);
        Label badge = creerBadgeStatut(ligne.getStatut());

        HBox carte = new HBox(16, date, infos, espace, badge);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-width: 1 1 1 4px; -fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + couleur + "; -fx-padding: 14px 18px;");

        return carte;
    }

    /**
     * Builds the date block (day number + month abbreviation) of a card.
     *
     * @param date    the date to display
     * @param couleur the color of the day number
     * @return the date block as a VBox
     */
    private VBox creerDate(Date date, String couleur) {
        SimpleDateFormat formatJour = new SimpleDateFormat("dd", Locale.FRENCH);
        SimpleDateFormat formatMois = new SimpleDateFormat("MMM", Locale.FRENCH);

        Label lblJour = new Label(formatJour.format(date));
        lblJour.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + couleur + ";");

        String mois = formatMois.format(date);
        Label lblMois = new Label(mois.substring(0, Math.min(3, mois.length())).toUpperCase(Locale.FRENCH));
        lblMois.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

        VBox conteneur = new VBox(0, lblJour, lblMois);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setMinWidth(45.0);

        return conteneur;
    }

    /**
     * Builds the central info block (module + schedule) of a card.
     *
     * @param ligne the row to display
     * @return the info block as a VBox
     */
    private VBox creerInfos(LigneAbsence ligne) {
        Label code = new Label(ligne.getModuleCode());
        code.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2px 8px; -fx-background-radius: 6px;");

        Label intitule = new Label(ligne.getModuleIntitule());
        intitule.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox ligneTitre = new HBox(8, code, intitule);
        ligneTitre.setAlignment(Pos.CENTER_LEFT);

        Label horaire = new Label(formatHoraire(ligne));
        horaire.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        return new VBox(4, ligneTitre, horaire);
    }

    /**
     * Formats the schedule line of a card ("10h00 - 12h00 · Durée : 2h").
     *
     * @param ligne the row to format
     * @return the formatted schedule text
     */
    private String formatHoraire(LigneAbsence ligne) {
        long minutes = Duration.between(ligne.getHeureDeb(), ligne.getHeureFin()).toMinutes();

        return formatHeure(ligne.getHeureDeb()) + " - " + formatHeure(ligne.getHeureFin()) + " · Durée : " + formatDuree(minutes);
    }

    /**
     * Formats a time as "HHhMM".
     *
     * @param heure the time to format
     * @return the formatted time
     */
    private String formatHeure(LocalTime heure) {
        return String.format("%02dh%02d", heure.getHour(), heure.getMinute());
    }

    /**
     * Builds the status badge (Absence/Retard) of a card.
     *
     * @param statut the attendance status to display
     * @return the badge as a Label
     */
    private Label creerBadgeStatut(EnumPresence statut) {
        Label badge = new Label(statut == EnumPresence.ABSENT ? "Absence" : "Retard");
        String couleur = statut == EnumPresence.ABSENT
            ? "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;"
            : "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04;";

        badge.setStyle("-fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: bold;" + couleur);

        return badge;
    }

    /**
     * Highlights the active filter button and resets the others.
     * @param actif the filter button to mark as active
     */
    private void setFiltreActif(Button actif) {
        Button[] boutons = { btnTout, btnAbsencesFiltre, btnRetardsFiltre };

        for(Button bouton : boutons) {
            boolean estActif = (bouton == actif);
            String fond = estActif ? "#3b82f6" : "white";
            String texte = estActif ? "white" : "#64748b";
            String bordure = estActif ? "" : "-fx-border-color: #e2e8f0; -fx-border-radius: 8px;";

            bouton.setStyle("-fx-background-color: " + fond + "; -fx-text-fill: " + texte + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 16px; -fx-background-radius: 8px; -fx-cursor: hand;" + bordure);
        }
    }
}