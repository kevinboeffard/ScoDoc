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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.entity.Etudiant;
import model.entity.GroupeTD;
import model.entity.Promotion;
import view.App;

/**
 * Controller for the teacher students directory view.
 * Displays every student grouped by promotion and group, with a search bar.
 * @author Kevin Boeffard
 */
public class ElevesEnseignantController {

    @FXML private TextField champRecherche;
    @FXML private HBox conteneurStats;
    @FXML private VBox conteneurListe;

    /**
     * Initializes the view: builds the stats, the grouped view, and the search listener.
     */
    @FXML
    public void initialize() {
        afficherStatistiques();
        afficherGroupes();

        champRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            if(nouveau.trim().isEmpty()) {
                afficherGroupes();
            } else {
                afficherRecherche(nouveau.trim());
            }
        });
    }

    /**
     * Builds and displays the stats cards (total students + one per promotion).
     */
    private void afficherStatistiques() {
        conteneurStats.getChildren().clear();
        conteneurStats.getChildren().add(creerCarteStat(String.valueOf(App.listEtudiants.size()), "TOTAL ÉLÈVES"));

        for(Promotion promo : App.listPromotions) {
            conteneurStats.getChildren().add(creerCarteStat(String.valueOf(compterEtudiantsPromotion(promo)), promo.getNomPromo().name()));
        }
    }

    /**
     * Returns the number of students belonging to the groups of the given promotion.
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
     * Rebuilds the grouped view: one card per promotion, each containing its groups.
     */
    private void afficherGroupes() {
        conteneurListe.getChildren().clear();

        for(Promotion promo : App.listPromotions) {
            conteneurListe.getChildren().add(creerCartePromotion(promo));
        }
    }

    /**
     * Builds the card for a given promotion: header and a list of group cards.
     *
     * @param promo the promotion to build the card for
     * @return the card as a VBox
     */
    private VBox creerCartePromotion(Promotion promo) {
        Label titre = new Label(promo.getNomPromo().name());
        titre.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label nb = new Label(compterEtudiantsPromotion(promo) + " élèves · " + promo.getGroupes().size() + " groupes");
        nb.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox entete = new HBox(10, titre, espace, nb);
        entete.setAlignment(Pos.CENTER_LEFT);

        VBox groupes = new VBox(10);
        for(GroupeTD groupe : promo.getGroupes()) {
            groupes.getChildren().add(creerCarteGroupe(groupe));
        }

        VBox carte = new VBox(14, entete, groupes);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 18px;");

        return carte;
    }

    /**
     * Builds the card for a given group: a clickable header and a hidden students table.
     *
     * @param groupe the group to build the card for
     * @return the card as a VBox
     */
    private VBox creerCarteGroupe(GroupeTD groupe) {
        Label nom = new Label("Groupe " + groupe.getNom());
        nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label nb = new Label(groupe.getEtudiants().size() + " élèves");
        nb.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        Label fleche = new Label("▾");
        fleche.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox entete = new HBox(10, nom, espace, nb, fleche);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8px; -fx-padding: 10px 14px; -fx-cursor: hand;");

        TableView<LigneEleve> table = creerTableEleves(groupe.getEtudiants());
        table.setVisible(false);
        table.setManaged(false);

        entete.setOnMouseClicked(e -> basculerVisibilite(table, fleche));

        return new VBox(8, entete, table);
    }

    /**
     * Toggles the visibility of a students table and updates the arrow icon accordingly.
     *
     * @param table  the table to show or hide
     * @param fleche the arrow label to update
     */
    private void basculerVisibilite(TableView<LigneEleve> table, Label fleche) {
        boolean visible = !table.isVisible();

        table.setVisible(visible);
        table.setManaged(visible);
        fleche.setText(visible ? "▴" : "▾");
    }

    /**
     * Filters every student by the given text and displays the result as a single table.
     *
     * @param texte the search text
     */
    private void afficherRecherche(String texte) {
        conteneurListe.getChildren().clear();

        List<Etudiant> resultats = new ArrayList<>();
        String recherche = texte.toLowerCase(Locale.FRENCH);

        for(Etudiant etudiant : App.listEtudiants) {
            if(correspond(etudiant, recherche)) resultats.add(etudiant);
        }

        Label titre = new Label(resultats.size() + " résultat(s) pour \"" + texte + "\"");
        titre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TableView<LigneEleve> table = creerTableEleves(resultats);

        VBox carte = new VBox(10, titre, table);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 18px;");

        conteneurListe.getChildren().add(carte);
    }

    /**
     * Checks whether a student matches the given lowercase search text.
     *
     * @param etudiant  the student to check
     * @param recherche the lowercase search text
     * @return true if the student's name, first name or student number contains the text
     */
    private boolean correspond(Etudiant etudiant, String recherche) {
        return etudiant.getNom().toLowerCase(Locale.FRENCH).contains(recherche)
            || etudiant.getPrenom().toLowerCase(Locale.FRENCH).contains(recherche)
            || etudiant.getNumEtudiant().toLowerCase(Locale.FRENCH).contains(recherche);
    }

    /**
     * Builds a students table for the given list of students.
     *
     * @param etudiants the students to display
     * @return the table as a TableView
     */
    private TableView<LigneEleve> creerTableEleves(List<Etudiant> etudiants) {
        TableView<LigneEleve> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LigneEleve, String> colNum = new TableColumn<>("N° étudiant");
        TableColumn<LigneEleve, String> colNom = new TableColumn<>("Nom");
        TableColumn<LigneEleve, String> colPrenom = new TableColumn<>("Prénom");
        TableColumn<LigneEleve, String> colGroupe = new TableColumn<>("Groupe");
        TableColumn<LigneEleve, String> colMoyenne = new TableColumn<>("Moyenne générale");

        colNum.setCellValueFactory(new PropertyValueFactory<>("numEtudiant"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colGroupe.setCellValueFactory(new PropertyValueFactory<>("groupe"));
        colMoyenne.setCellValueFactory(new PropertyValueFactory<>("moyenne"));
        colMoyenne.setCellFactory(creerCellFactoryBadgeMoyenne());

        table.getColumns().addAll(colNum, colNom, colPrenom, colGroupe, colMoyenne);

        List<LigneEleve> lignes = new ArrayList<>();
        for(Etudiant etudiant : etudiants) {
            lignes.add(creerLigne(etudiant));
        }

        table.setItems(FXCollections.observableArrayList(lignes));
        table.setFixedCellSize(40);
        table.setPrefHeight(table.getFixedCellSize() * (lignes.size() + 1) + 2);
        return table;
    }

    /**
     * Builds a table row for the given student.
     *
     * @param etudiant the student to build the row for
     * @return the row as a LigneEleve
     */
    private LigneEleve creerLigne(Etudiant etudiant) {
        String groupe = "--";

        if(etudiant.getGroupe() != null) {
            groupe = etudiant.getGroupe().getPromotion().getNomPromo().name() + " - " + etudiant.getGroupe().getNom();
        }

        double moyenne = etudiant.calculerMoyenneGenerale();
        String texteMoyenne = moyenne == 0 ? "--" : formatDecimal(moyenne) + " / 20";

        return new LigneEleve(etudiant.getNumEtudiant(), etudiant.getNom(), etudiant.getPrenom(), groupe, texteMoyenne);
    }

    /**
     * Builds the cell factory used to display the average as a colored badge.
     * @return the cell factory for the "Moyenne générale" column
     */
    private Callback<TableColumn<LigneEleve, String>, TableCell<LigneEleve, String>> creerCellFactoryBadgeMoyenne() {
        return col -> new TableCell<LigneEleve, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(creerBadgeMoyenne(item));
                    setText(null);
                }
            }
        };
    }

    /**
     * Builds a colored badge for an average value.
     *
     * @param texte the text to display in the badge
     * @return the badge as a Label
     */
    private Label creerBadgeMoyenne(String texte) {
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
            label.setStyle(label.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;");
        }

        return label;
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