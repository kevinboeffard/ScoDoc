package controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.entity.Enseignant;
import model.entity.Etudiant;
import model.entity.GroupeTD;
import model.entity.Module;
import model.entity.Seance;
import view.App;

/**
 * Controller for the teacher dashboard view.
 * Displays global statistics and a card per module taught.
 * @author Kevin Boeffard
 */
public class DashboardEnseignantController {

    @FXML private Label lblBienvenue;
    @FXML private Label lblNbModules;
    @FXML private Label lblNbEtudiants;
    @FXML private Label lblNbEvaluations;
    @FXML private Label lblNbSeancesJour;
    @FXML private VBox conteneurModules;

    /** The connected teacher. */
    private Enseignant enseignant;

    /**
     * Initializes the view: loads the global stats and the module cards.
     */
    @FXML
    public void initialize() {
        enseignant = (Enseignant) App.utilisateurConnecte;

        lblBienvenue.setText("Bonjour " + enseignant.getPrenom() + " " + enseignant.getNom());

        afficherStatistiques();
        afficherModules();
    }

    /**
     * Computes and displays the global statistics (modules, students, evaluations, sessions today).
     */
    private void afficherStatistiques() {
        List<Module> modules = enseignant.getModules();
        int nbEvaluations = 0;
        int nbSeancesJour = 0;

        for(Module module : modules) {
            nbEvaluations += module.getListeEval().size();
            nbSeancesJour += compterSeancesAujourdhui(module);
        }

        lblNbModules.setText(String.valueOf(modules.size()));
        lblNbEtudiants.setText(String.valueOf(compterEtudiants(modules)));
        lblNbEvaluations.setText(String.valueOf(nbEvaluations));
        lblNbSeancesJour.setText(String.valueOf(nbSeancesJour));
    }

    /**
     * Builds and adds one card per module taught by the connected teacher.
     */
    private void afficherModules() {
        for(Module module : enseignant.getModules()) {
            conteneurModules.getChildren().add(creerCarteModule(module));
        }
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
     * Returns the number of distinct students following at least one of the given modules.
     *
     * @param modules the modules to check
     * @return the number of distinct students
     */
    private int compterEtudiants(List<Module> modules) {
        Set<Etudiant> etudiants = new HashSet<>();

        for(GroupeTD groupe : App.listGroupesTDs) {
            for(Module module : groupe.getModulesSuivis()) {
                if(moduleDansListe(module, modules)) {
                    etudiants.addAll(groupe.getEtudiants());
                }
            }
        }

        return etudiants.size();
    }

    /**
     * Returns the number of distinct students following the given module.
     *
     * @param module the module to check
     * @return the number of distinct students
     */
    private int compterEtudiantsModule(Module module) {
        Set<Etudiant> etudiants = new HashSet<>();

        for(GroupeTD groupe : App.listGroupesTDs) {
            if(moduleDansListe(module, groupe.getModulesSuivis())) {
                etudiants.addAll(groupe.getEtudiants());
            }
        }

        return etudiants.size();
    }

    /**
     * Returns the number of sessions of the given module that take place today.
     *
     * @param module the module to check
     * @return the number of sessions today
     */
    private int compterSeancesAujourdhui(Module module) {
        int compteur = 0;
        LocalDate aujourdhui = LocalDate.now();

        for(Seance seance : module.getSeances()) {
            LocalDate date = seance.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if(date.equals(aujourdhui)) compteur++;
        }

        return compteur;
    }

    /**
     * Builds the card for a given module: header and mini-stats.
     *
     * @param module the module to build the card for
     * @return the card as a VBox
     */
    private VBox creerCarteModule(Module module) {
        VBox carte = new VBox(12);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 18px;");
        carte.getChildren().add(creerEnteteModule(module));
        carte.getChildren().add(creerStatsModule(module));

        return carte;
    }

    /**
     * Builds the header row of a module card: code and title.
     *
     * @param module the module to display
     * @return the header as an HBox
     */
    private HBox creerEnteteModule(Module module) {
        Label code = new Label(module.getCode());
        code.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label intitule = new Label(module.getIntitule());
        intitule.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox entete = new HBox(10, code, intitule);
        entete.setAlignment(Pos.CENTER_LEFT);

        return entete;
    }

    /**
     * Builds the mini-stats row of a module card (average, evaluations, sessions, students).
     *
     * @param module the module to display
     * @return the mini-stats row as an HBox
     */
    private HBox creerStatsModule(Module module) {
        double moyenne = module.calculerMoyenne();
        String texteMoyenne = moyenne == 0 ? "--" : formatDecimal(moyenne) + " / 20";

        return new HBox(10,
            creerMiniCarte(texteMoyenne, "MOYENNE PROMO"),
            creerMiniCarte(String.valueOf(module.getListeEval().size()), "ÉVALUATIONS"),
            creerMiniCarte(String.valueOf(module.getSeances().size()), "SÉANCES"),
            creerMiniCarte(String.valueOf(compterEtudiantsModule(module)), "ÉTUDIANTS")
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
     * Formats a double with one decimal, French locale (comma separator).
     *
     * @param valeur the value to format
     * @return the formatted value
     */
    private String formatDecimal(double valeur) {
        return String.format(Locale.FRANCE, "%.1f", valeur);
    }
}