package controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.dao.PresenceDAO;
import model.entity.EnumPresence;
import model.entity.Etudiant;
import model.entity.Module;
import model.entity.Presence;
import model.entity.Seance;

/**
 * Controller for the attendance sheet view.
 * Lets the teacher mark every student of the session's group as present,
 * absent or late, and persist the result.
 * @author Kevin Boeffard
 */
public class AppelEnseignantController {

    @FXML private Label lblTitre;
    @FXML private Label lblSousTitre;
    @FXML private Label lblPresents;
    @FXML private Label lblAbsents;
    @FXML private Label lblRetards;
    @FXML private Label lblTotal;
    @FXML private Button btnFiltreTous;
    @FXML private Button btnFiltreAbsents;
    @FXML private Button btnFiltreRetards;
    @FXML private VBox conteneurEtudiants;
    @FXML private Label lblMessage;

    /** Fixed order of statuses, matching the 3 toggle buttons of each row. */
    private static final EnumPresence[] ORDRE_STATUTS = { EnumPresence.PRESENT, EnumPresence.ABSENT, EnumPresence.RETARD };

    /** The session being marked. */
    private Seance seance;

    /** Current status selection, indexed by student. */
    private Map<Etudiant, EnumPresence> statutsActuels;

    /** The 3 toggle buttons of each row, indexed by student. */
    private Map<Etudiant, Button[]> boutonsParEtudiant;

    /** The row of each student, used to apply the active filter. */
    private Map<Etudiant, HBox> lignesParEtudiant;

    /** Students who already had an attendance record when the view was opened. */
    private Set<Etudiant> etudiantsAvecPresence;

    /** Currently active filter, or null for "Tous". */
    private EnumPresence filtreActif;

    /**
     * Initializes the view with the session selected on the dashboard.
     */
    @FXML
    public void initialize() {
        seance = NavigationContext.getSeanceSelectionnee();
        statutsActuels = new LinkedHashMap<>();
        boutonsParEtudiant = new HashMap<>();
        lignesParEtudiant = new LinkedHashMap<>();
        filtreActif = null;

        if(seance == null) {
            lblTitre.setText("Aucune séance sélectionnée");
            lblSousTitre.setText("Retournez à la liste et choisissez une séance.");
        } else {
            etudiantsAvecPresence = new HashSet<>(seance.getAppel().keySet());

            afficherEntete();
            afficherEtudiants();
            recalculerStats();
        }
    }

    /**
     * Fills the title and subtitle with the session's module, group, date and schedule.
     */
    private void afficherEntete() {
        Module module = seance.getModule();
        lblTitre.setText(module.getCode() + " — " + module.getIntitule());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        LocalDate date = seance.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String texteDate = date.format(formatter);
        texteDate = texteDate.substring(0, 1).toUpperCase(Locale.FRENCH) + texteDate.substring(1);

        String horaire = formatHeure(seance.getHeureDeb()) + " - " + formatHeure(seance.getHeureFin());

        lblSousTitre.setText("Groupe " + seance.getGroupe().getNom() + " · " + texteDate + " · " + horaire);
    }

    /**
     * Builds the students list for the session's group, sorted by name.
     */
    private void afficherEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>(seance.getGroupe().getEtudiants());
        etudiants.sort(Comparator.comparing(Etudiant::getNom).thenComparing(Etudiant::getPrenom));

        if(etudiants.isEmpty()) {
            Label vide = new Label("Aucun élève dans ce groupe.");
            vide.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20px;");
            conteneurEtudiants.getChildren().add(vide);
        } else {
            for(Etudiant etudiant : etudiants) {
                conteneurEtudiants.getChildren().add(creerLigneEtudiant(etudiant));
            }
        }
    }

    /**
     * Builds the row for a given student: name and 3 status toggle buttons.
     *
     * @param etudiant the student to build the row for
     * @return the row as an HBox
     */
    private HBox creerLigneEtudiant(Etudiant etudiant) {
        EnumPresence statutInitial = seance.getAppel().getOrDefault(etudiant, EnumPresence.PRESENT);
        statutsActuels.put(etudiant, statutInitial);

        Label nom = new Label(etudiant.getNumEtudiant() + " — " + etudiant.getNom() + " " + etudiant.getPrenom());
        nom.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");
        nom.setPrefWidth(300.0);

        Button btnPresent = creerBoutonStatut("Présent", EnumPresence.PRESENT, etudiant);
        Button btnAbsent = creerBoutonStatut("Absent", EnumPresence.ABSENT, etudiant);
        Button btnRetard = creerBoutonStatut("En retard", EnumPresence.RETARD, etudiant);

        boutonsParEtudiant.put(etudiant, new Button[] { btnPresent, btnAbsent, btnRetard });

        HBox boutons = new HBox(8, btnPresent, btnAbsent, btnRetard);

        HBox ligne = new HBox(16, nom, boutons);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px; -fx-padding: 10px 14px;");

        lignesParEtudiant.put(etudiant, ligne);
        restyleBoutons(etudiant);

        return ligne;
    }

    /**
     * Builds a single status toggle button for a student.
     *
     * @param texte    the button label
     * @param statut   the status this button represents
     * @param etudiant the student this button belongs to
     * @return the configured button
     */
    private Button creerBoutonStatut(String texte, EnumPresence statut, Etudiant etudiant) {
        Button bouton = new Button(texte);
        bouton.setOnAction(e -> selectionnerStatut(etudiant, statut));

        return bouton;
    }

    /**
     * Updates the status of a student and refreshes the styles, stats and filter.
     *
     * @param etudiant the student to update
     * @param statut   the new status
     */
    private void selectionnerStatut(Etudiant etudiant, EnumPresence statut) {
        statutsActuels.put(etudiant, statut);
        restyleBoutons(etudiant);
        recalculerStats();
        appliquerFiltre();
    }

    /**
     * Restyles the 3 toggle buttons of a student to highlight the currently selected status.
     * @param etudiant the student whose buttons to restyle
     */
    private void restyleBoutons(Etudiant etudiant) {
        Button[] boutons = boutonsParEtudiant.get(etudiant);
        EnumPresence actuel = statutsActuels.get(etudiant);

        for(int i = 0; i < boutons.length; i++) {
            boutons[i].setStyle(styleBouton(ORDRE_STATUTS[i], ORDRE_STATUTS[i] == actuel));
        }
    }

    /**
     * Returns the inline style of a status toggle button.
     *
     * @param statut the status this button represents
     * @param actif  whether this button is the currently selected one
     * @return the inline CSS style
     */
    private String styleBouton(EnumPresence statut, boolean actif) {
        String base = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;";
        String couleur;

        if(!actif) {
            couleur = "-fx-background-color: white; -fx-text-fill: #94a3b8; -fx-border-color: #e2e8f0; -fx-border-radius: 6px;";
        } else if(statut == EnumPresence.PRESENT) {
            couleur = "-fx-background-color: #dcfce7; -fx-text-fill: #16a34a;";
        } else if(statut == EnumPresence.ABSENT) {
            couleur = "-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;";
        } else {
            couleur = "-fx-background-color: #fef9c3; -fx-text-fill: #ca8a04;";
        }

        return base + couleur;
    }

    /**
     * Recomputes and displays the attendance stats (présents/absents/retards/total).
     */
    private void recalculerStats() {
        int presents = 0;
        int absents = 0;
        int retards = 0;

        for(EnumPresence statut : statutsActuels.values()) {
            if(statut == EnumPresence.PRESENT) presents++;
            else if(statut == EnumPresence.ABSENT) absents++;
            else retards++;
        }

        lblPresents.setText(String.valueOf(presents));
        lblAbsents.setText(String.valueOf(absents));
        lblRetards.setText(String.valueOf(retards));
        lblTotal.setText(String.valueOf(statutsActuels.size()));
    }

    /**
     * Shows every student, regardless of their status.
     */
    @FXML
    public void handleFiltreTous() {
        filtreActif = null;
        appliquerFiltre();
        setFiltreActif(btnFiltreTous);
    }

    /**
     * Shows only the students currently marked as absent.
     */
    @FXML
    public void handleFiltreAbsents() {
        filtreActif = EnumPresence.ABSENT;
        appliquerFiltre();
        setFiltreActif(btnFiltreAbsents);
    }

    /**
     * Shows only the students currently marked as late.
     */
    @FXML
    public void handleFiltreRetards() {
        filtreActif = EnumPresence.RETARD;
        appliquerFiltre();
        setFiltreActif(btnFiltreRetards);
    }

    /**
     * Shows or hides each student row depending on the active filter.
     */
    private void appliquerFiltre() {
        for(Etudiant etudiant : lignesParEtudiant.keySet()) {
            boolean visible = filtreActif == null || statutsActuels.get(etudiant) == filtreActif;
            HBox ligne = lignesParEtudiant.get(etudiant);

            ligne.setVisible(visible);
            ligne.setManaged(visible);
        }
    }

    /**
     * Highlights the active filter button and resets the others.
     * @param actif the filter button to mark as active
     */
    private void setFiltreActif(Button actif) {
        Button[] boutons = { btnFiltreTous, btnFiltreAbsents, btnFiltreRetards };

        for(Button bouton : boutons) {
            boolean estActif = (bouton == actif);
            String fond = estActif ? "#3b82f6" : "white";
            String texte = estActif ? "white" : "#64748b";
            String bordure = estActif ? "" : "-fx-border-color: #e2e8f0; -fx-border-radius: 8px;";

            bouton.setStyle("-fx-background-color: " + fond + "; -fx-text-fill: " + texte + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 16px; -fx-background-radius: 8px; -fx-cursor: hand;" + bordure);
        }
    }

    /**
     * Marks every student as present.
     */
    @FXML
    public void handleTousPresents() {
        for(Etudiant etudiant : statutsActuels.keySet()) {
            statutsActuels.put(etudiant, EnumPresence.PRESENT);
            restyleBoutons(etudiant);
        }

        recalculerStats();
        appliquerFiltre();
    }

    /**
     * Returns to the absences dashboard.
     */
    @FXML
    public void handleRetour() {
        NavigationContext.getMainController().chargerVue("absences_enseignant.fxml");
    }

    /**
     * Saves the attendance status of every student of the session.
     */
    @FXML
    public void handleValider() {
        int erreurs = 0;

        for(Etudiant etudiant : statutsActuels.keySet()) {
            EnumPresence statut = statutsActuels.get(etudiant);
            Presence presence = new Presence(etudiant, seance, statut);

            int resultat;
            if(etudiantsAvecPresence.contains(etudiant)) {
                resultat = new PresenceDAO().update(presence);
            } else {
                resultat = new PresenceDAO().create(presence);
            }

            if(resultat > 0) {
                seance.ajouterPresence(etudiant, statut);
                etudiantsAvecPresence.add(etudiant);
            } else {
                erreurs++;
            }
        }

        if(erreurs > 0) {
            afficherErreur(erreurs + " erreur(s) lors de l'enregistrement.");
        } else {
            afficherSucces("Feuille d'appel enregistrée avec succès.");
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

    /**
     * Formats a time as "HHhMM".
     *
     * @param heure the time to format
     * @return the formatted time
     */
    private String formatHeure(LocalTime heure) {
        return String.format("%02dh%02d", heure.getHour(), heure.getMinute());
    }
}