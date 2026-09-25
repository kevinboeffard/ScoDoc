package controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.entity.Enseignant;
import model.entity.Module;
import model.entity.Seance;
import view.App;

/**
 * Controller for the teacher attendance dashboard.
 * Lists every session of the teacher's modules, grouped by date,
 * with an indicator of whether the attendance sheet has been filled.
 * @author Kevin Boeffard
 */
public class AbsencesEnseignantController {

    @FXML private VBox conteneurSeances;

    /** The connected teacher. */
    private Enseignant enseignant;

    /**
     * Initializes the view: builds the grouped list of sessions.
     */
    @FXML
    public void initialize() {
        enseignant = (Enseignant) App.utilisateurConnecte;
        afficherSeances();
    }

    /**
     * Rebuilds the grouped list of sessions (Aujourd'hui / À venir / Séances passées).
     */
    private void afficherSeances() {
        conteneurSeances.getChildren().clear();

        List<Seance> aujourdhuiListe = new ArrayList<>();
        List<Seance> avenirListe = new ArrayList<>();
        List<Seance> passeesListe = new ArrayList<>();
        LocalDate aujourdhui = LocalDate.now();

        for(Seance seance : recupererSeances()) {
            LocalDate date = convertirDate(seance.getDate());

            if(date.equals(aujourdhui)) aujourdhuiListe.add(seance);
            else if(date.isAfter(aujourdhui)) avenirListe.add(seance);
            else passeesListe.add(seance);
        }

        ajouterGroupe("Aujourd'hui", aujourdhuiListe, true);
        ajouterGroupe("À venir", avenirListe, true);
        ajouterGroupe("Séances passées", passeesListe, false);

        if(conteneurSeances.getChildren().isEmpty()) {
            Label vide = new Label("Aucune séance trouvée pour vos modules.");
            vide.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20px;");
            conteneurSeances.getChildren().add(vide);
        }
    }

    /**
     * Returns every session of every module taught by the connected teacher.
     * @return the list of sessions
     */
    private List<Seance> recupererSeances() {
        List<Seance> seances = new ArrayList<>();

        for(Module module : enseignant.getModules()) {
            seances.addAll(module.getSeances());
        }

        return seances;
    }

    /**
     * Adds a labeled group of session cards to the view, sorted by date and time.
     *
     * @param titre    the group title
     * @param seances  the sessions to display
     * @param croissant true to sort chronologically, false for most recent first
     */
    private void ajouterGroupe(String titre, List<Seance> seances, boolean croissant) {
        if(!seances.isEmpty()) {
            Comparator<Seance> comparateur = Comparator
                .comparing((Seance s) -> convertirDate(s.getDate()))
                .thenComparing(Seance::getHeureDeb);

            if(!croissant) comparateur = comparateur.reversed();

            seances.sort(comparateur);

            Label entete = new Label(titre);
            entete.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #64748b; -fx-padding: 10px 0 0 4px;");
            conteneurSeances.getChildren().add(entete);

            for(Seance seance : seances) {
                conteneurSeances.getChildren().add(creerCarteSeance(seance));
            }
        }
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
     * Builds the card representing one session, clickable to open its attendance sheet.
     *
     * @param seance the session to display
     * @return the card as an HBox
     */
    private HBox creerCarteSeance(Seance seance) {
        boolean appelFait = appelComplet(seance);

        VBox dateBox = creerDate(seance);

        Label code = new Label(seance.getModule().getCode());
        code.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 2px 8px; -fx-background-radius: 6px;");

        Label intitule = new Label(seance.getModule().getIntitule());
        intitule.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox ligneTitre = new HBox(8, code, intitule);
        ligneTitre.setAlignment(Pos.CENTER_LEFT);

        Label details = new Label("Groupe " + seance.getGroupe().getNom() + "  ·  " + formatHeure(seance.getHeureDeb()) + " - " + formatHeure(seance.getHeureFin()));
        details.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        VBox infos = new VBox(4, ligneTitre, details);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox carte = new HBox(16, dateBox, infos, espace, creerBadgeAppel(appelFait));
        carte.setAlignment(Pos.CENTER_LEFT);

        String couleurBordure = appelFait ? "#16a34a" : "#f59e0b";
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-width: 1 1 1 4px; -fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + couleurBordure + "; -fx-padding: 14px 18px; -fx-cursor: hand;");

        carte.setOnMouseClicked(e -> ouvrirAppel(seance));

        return carte;
    }

    /**
     * Builds the date block (day number + month abbreviation) of a card.
     *
     * @param seance the session to display the date of
     * @return the date block as a VBox
     */
    private VBox creerDate(Seance seance) {
        SimpleDateFormat formatJour = new SimpleDateFormat("dd", Locale.FRENCH);
        SimpleDateFormat formatMois = new SimpleDateFormat("MMM", Locale.FRENCH);

        Label lblJour = new Label(formatJour.format(seance.getDate()));
        lblJour.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        String mois = formatMois.format(seance.getDate());
        Label lblMois = new Label(mois.substring(0, Math.min(3, mois.length())).toUpperCase(Locale.FRENCH));
        lblMois.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

        VBox conteneur = new VBox(0, lblJour, lblMois);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setMinWidth(45.0);

        return conteneur;
    }

    /**
     * Builds the "Appel fait" / "Appel à faire" badge.
     *
     * @param appelFait whether the attendance sheet is complete
     * @return the badge as a Label
     */
    private Label creerBadgeAppel(boolean appelFait) {
        Label badge = new Label(appelFait ? "Appel fait" : "Appel à faire");
        String couleur = appelFait
            ? "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;"
            : "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04;";

        badge.setStyle("-fx-padding: 4px 12px; -fx-background-radius: 20px; -fx-font-size: 12px; -fx-font-weight: bold;" + couleur);

        return badge;
    }

    /**
     * Checks whether every student of the session's group has an attendance record.
     *
     * @param seance the session to check
     * @return true if the attendance sheet is complete
     */
    private boolean appelComplet(Seance seance) {
        int nbEtudiants = seance.getGroupe().getEtudiants().size();
        return nbEtudiants > 0 && seance.getAppel().size() == nbEtudiants;
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
     * Saves the selected session and opens the attendance sheet.
     * @param seance the session to mark
     */
    private void ouvrirAppel(Seance seance) {
        NavigationContext.setSeanceSelectionnee(seance);
        NavigationContext.getMainController().chargerVue("appel_enseignant.fxml");
    }
}