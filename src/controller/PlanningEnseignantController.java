package controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.entity.Enseignant;
import model.entity.Module;
import model.entity.Seance;
import view.App;

/**
 * Controller for the teacher schedule view.
 * Displays the sessions of a selected day across every module taught by the teacher,
 * a daily summary and a clickable month calendar.
 * @author Kevin Boeffard
 */
public class PlanningEnseignantController {

    @FXML private Label lblDate;
    @FXML private VBox conteneurSeances;
    @FXML private Label lblNbSeances;
    @FXML private Label lblVolumeHoraire;
    @FXML private Label lblDebutCours;
    @FXML private Label lblFinCours;
    @FXML private GridPane calendrier;
    @FXML private Label lblMoisAnnee;

    /** The connected teacher. */
    private Enseignant enseignant;

    /** The currently displayed day. */
    private LocalDate dateAffichee;

    /** The month currently displayed in the calendar. */
    private YearMonth moisCalendrier;

    /**
     * Initializes the view on the current day.
     */
    @FXML
    public void initialize() {
        enseignant = (Enseignant) App.utilisateurConnecte;
        dateAffichee = LocalDate.now();
        moisCalendrier = YearMonth.from(dateAffichee);
        rafraichir();
    }

    /**
     * Moves to the previous day.
     */
    @FXML
    public void handlePrecedent() {
        dateAffichee = dateAffichee.minusDays(1);
        rafraichir();
    }

    /**
     * Moves to the next day.
     */
    @FXML
    public void handleSuivant() {
        dateAffichee = dateAffichee.plusDays(1);
        rafraichir();
    }

    /**
     * Goes back to the current day.
     */
    @FXML
    public void handleAujourdhui() {
        dateAffichee = LocalDate.now();
        rafraichir();
    }

    /**
     * Refreshes the whole view for the currently displayed day.
     */
    private void rafraichir() {
        List<Seance> seances = recupererSeancesJour(dateAffichee);

        afficherDate();
        afficherTimeline(seances);
        afficherResume(seances);
        moisCalendrier = YearMonth.from(dateAffichee);
        construireCalendrier();
    }

    /**
     * Updates the date label, showing "Aujourd'hui" if the displayed day is today.
     */
    private void afficherDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        String texte = dateAffichee.format(formatter);
        texte = texte.substring(0, 1).toUpperCase(Locale.FRENCH) + texte.substring(1);

        if(dateAffichee.equals(LocalDate.now())) {
            texte += "  •  Aujourd'hui";
        }

        lblDate.setText(texte);
    }

    /**
     * Returns every session of every module taught by the connected teacher for the given day, sorted by start time.
     *
     * @param date the day to retrieve the sessions for
     * @return the sorted list of sessions
     */
    private List<Seance> recupererSeancesJour(LocalDate date) {
        List<Seance> seances = new ArrayList<>();

        for(Module module : enseignant.getModules()) {
            for(Seance seance : module.getSeances()) {
                if(convertirDate(seance.getDate()).equals(date)) {
                    seances.add(seance);
                }
            }
        }

        seances.sort(Comparator.comparing(Seance::getHeureDeb));

        return seances;
    }

    /**
     * Converts a java.util.Date to a LocalDate using the system time zone.
     *
     * @param date the date to convert
     * @return the corresponding LocalDate
     */
    private LocalDate convertirDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Rebuilds the timeline with the given sessions.
     *
     * @param seances the sessions to display
     */
    private void afficherTimeline(List<Seance> seances) {
        conteneurSeances.getChildren().clear();

        if(seances.isEmpty()) {
            conteneurSeances.getChildren().add(creerMessageVide());
        } else {
            for(Seance seance : seances) {
                conteneurSeances.getChildren().add(creerCarteSeance(seance));
            }
        }
    }

    /**
     * Builds the placeholder label shown when there is no session that day.
     * @return the placeholder label
     */
    private Label creerMessageVide() {
        Label label = new Label("Aucun cours ce jour-là.");
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20px;");

        return label;
    }

    /**
     * Builds the card representing one session.
     *
     * @param seance the session to display
     * @return the card as a VBox
     */
    private VBox creerCarteSeance(Seance seance) {
        Label horaire = new Label(formatHeure(seance.getHeureDeb()) + " - " + formatHeure(seance.getHeureFin()));
        horaire.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label code = new Label(seance.getModule().getCode());
        code.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        HBox ligneHaut = new HBox(8, horaire, code);
        ligneHaut.setAlignment(Pos.CENTER_LEFT);

        Label intitule = new Label(seance.getModule().getIntitule());
        intitule.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label details = new Label("Groupe " + seance.getGroupe().getNom() + "  ·  " + seance.getGroupe().getPromotion().getNomPromo().name());
        details.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        VBox carte = new VBox(8, ligneHaut, intitule, details);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-width: 1 1 1 4px; -fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 #3b82f6; -fx-padding: 14px 18px;");

        return carte;
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
     * Computes and displays the daily summary (sessions count, total volume, start/end times).
     *
     * @param seances the sessions of the displayed day
     */
    private void afficherResume(List<Seance> seances) {
        long volumeMinutes = 0;
        LocalTime debut = null;
        LocalTime fin = null;

        for(Seance seance : seances) {
            volumeMinutes += Duration.between(seance.getHeureDeb(), seance.getHeureFin()).toMinutes();

            if(debut == null || seance.getHeureDeb().isBefore(debut)) debut = seance.getHeureDeb();
            if(fin == null || seance.getHeureFin().isAfter(fin)) fin = seance.getHeureFin();
        }

        lblNbSeances.setText(String.valueOf(seances.size()));
        lblVolumeHoraire.setText(formatDuree(volumeMinutes));
        lblDebutCours.setText(debut == null ? "--" : formatHeure(debut));
        lblFinCours.setText(fin == null ? "--" : formatHeure(fin));
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
     * Rebuilds the month calendar for the month currently displayed.
     */
    private void construireCalendrier() {
        calendrier.getChildren().clear();
        calendrier.getColumnConstraints().clear();

        for(int i = 0; i < 7; i++) {
            ColumnConstraints colonne = new ColumnConstraints();
            colonne.setPercentWidth(100.0 / 7);
            colonne.setHalignment(HPos.CENTER);
            calendrier.getColumnConstraints().add(colonne);
        }

        String[] joursSemaine = { "L", "M", "M", "J", "V", "S", "D" };
        for(int i = 0; i < 7; i++) {
            Label entete = new Label(joursSemaine[i]);
            entete.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
            calendrier.add(entete, i, 0);
        }

        LocalDate premierJourMois = moisCalendrier.atDay(1);
        int decalage = premierJourMois.getDayOfWeek().getValue() - 1;
        int nbJours = moisCalendrier.lengthOfMonth();

        String texteMois = premierJourMois.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
        lblMoisAnnee.setText(texteMois.substring(0, 1).toUpperCase(Locale.FRENCH) + texteMois.substring(1));

        int ligne = 1;
        int colonne = decalage;

        for(int jour = 1; jour <= nbJours; jour++) {
            LocalDate date = moisCalendrier.atDay(jour);
            calendrier.add(creerCelluleJour(date), colonne, ligne);

            colonne++;
            if(colonne == 7) {
                colonne = 0;
                ligne++;
            }
        }
    }

    /**
     * Builds a clickable cell for the given day of the calendar.
     *
     * @param date the day this cell represents
     * @return the cell as a Label
     */
    private Label creerCelluleJour(LocalDate date) {
        Label cellule = new Label(String.valueOf(date.getDayOfMonth()));
        cellule.setPrefSize(30.0, 30.0);
        cellule.setAlignment(Pos.CENTER);
        cellule.setStyle(styleCelluleJour(date));
        cellule.setOnMouseClicked(e -> selectionnerJour(date));

        return cellule;
    }

    /**
     * Returns the style of a calendar cell depending on whether it's selected, today, or a normal day.
     *
     * @param date the day to style
     * @return the inline CSS style of the cell
     */
    private String styleCelluleJour(LocalDate date) {
        String style = "-fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 50%;";

        if(date.equals(dateAffichee)) {
            style += "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;";
        } else if(date.equals(LocalDate.now())) {
            style += "-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold;";
        } else {
            style += "-fx-text-fill: #1e293b;";
        }

        return style;
    }

    /**
     * Selects the given day and refreshes the view.
     * @param date the day to select
     */
    private void selectionnerJour(LocalDate date) {
        dateAffichee = date;
        rafraichir();
    }

    /**
     * Moves the calendar to the previous month, without changing the selected day.
     */
    @FXML
    public void handleMoisPrecedent() {
        moisCalendrier = moisCalendrier.minusMonths(1);
        construireCalendrier();
    }

    /**
     * Moves the calendar to the next month, without changing the selected day.
     */
    @FXML
    public void handleMoisSuivant() {
        moisCalendrier = moisCalendrier.plusMonths(1);
        construireCalendrier();
    }
}