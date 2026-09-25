package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.Optional;
import model.dao.ModuleDAO;
import model.dao.SuitModuleDAO;
import model.entity.DirecteurEtudes;
import model.entity.EnumModule;
import model.entity.EnumPromo;
import model.entity.GroupeTD;
import model.entity.Module;
import model.entity.Promotion;
import model.entity.SuitModule;
import view.App;

/**
 * Controller for the Directeur des Études modules management view.
 * Lists every module grouped by promotion, lets the director create
 * or delete modules, and assign/remove the tutorial groups following each module.
 * @author Kevin Boeffard
 */
public class ModulesDEController {

    @FXML private VBox conteneurPromotions;
    @FXML private Label lblMessage;

    /** The connected Directeur des Études. */
    private DirecteurEtudes directeur;

    /** Names of the promotions whose modules list is currently expanded. */
    private Set<EnumPromo> promosOuvertes;

    /** Names of the promotions whose "new module" form is currently visible. */
    private Set<EnumPromo> formulairesOuverts;

    /**
     * Initializes the view: builds the list of promotion cards.
     */
    @FXML
    public void initialize() {
        directeur = (DirecteurEtudes) App.utilisateurConnecte;
        promosOuvertes = new HashSet<>();
        formulairesOuverts = new HashSet<>();
        afficherPromotions();
    }

    /**
     * Rebuilds the list of promotion cards, restoring previously expanded sections.
     */
    private void afficherPromotions() {
        conteneurPromotions.getChildren().clear();

        for(Promotion promo : App.listPromotions) {
            conteneurPromotions.getChildren().add(creerCartePromotion(promo));
        }
    }

    /**
     * Builds the card for a given promotion: a clickable header and a hidden modules pane.
     *
     * @param promo the promotion to build the card for
     * @return the card as a VBox
     */
    private VBox creerCartePromotion(Promotion promo) {
        Label titre = new Label(promo.getNomPromo().name());
        titre.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label nb = new Label(promo.getModules().size() + " module(s) · " + promo.getGroupes().size() + " groupe(s)");
        nb.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        boolean ouvert = promosOuvertes.contains(promo.getNomPromo());

        Label fleche = new Label(ouvert ? "▴" : "▾");
        fleche.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox entete = new HBox(10, titre, espace, nb, fleche);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setStyle("-fx-cursor: hand;");

        VBox contenu = new VBox(14);
        contenu.setVisible(ouvert);
        contenu.setManaged(ouvert);

        Button btnNouveauModule = new Button("+ Nouveau module");
        btnNouveauModule.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6px 12px; -fx-background-radius: 6px; -fx-cursor: hand;");

        VBox formulaire = creerFormulaireNouveauModule(promo);
        boolean formulaireOuvert = formulairesOuverts.contains(promo.getNomPromo());
        formulaire.setVisible(formulaireOuvert);
        formulaire.setManaged(formulaireOuvert);

        btnNouveauModule.setOnAction(e -> basculerFormulaire(promo, formulaire));

        FlowPane modulesPane = new FlowPane();
        modulesPane.setHgap(16.0);
        modulesPane.setVgap(16.0);

        for(Module module : promo.getModules()) {
            modulesPane.getChildren().add(creerCarteModule(promo, module));
        }

        contenu.getChildren().addAll(btnNouveauModule, formulaire, modulesPane);

        entete.setOnMouseClicked(e -> basculerPromotion(promo, contenu, fleche));

        VBox carte = new VBox(0, entete, contenu);
        carte.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #e2e8f0; -fx-border-radius: 12px; -fx-padding: 18px;");

        return carte;
    }

    /**
     * Toggles the visibility of a promotion's content pane and remembers the new state.
     *
     * @param promo   the promotion to toggle
     * @param contenu the pane to show or hide
     * @param fleche  the arrow label to update
     */
    private void basculerPromotion(Promotion promo, VBox contenu, Label fleche) {
        boolean visible = !contenu.isVisible();

        contenu.setVisible(visible);
        contenu.setManaged(visible);
        fleche.setText(visible ? "▴" : "▾");

        if(visible) {
            promosOuvertes.add(promo.getNomPromo());
        } else {
            promosOuvertes.remove(promo.getNomPromo());
        }
    }

    /**
     * Toggles the visibility of the "new module" form and remembers the new state.
     *
     * @param promo      the promotion the form belongs to
     * @param formulaire the form to show or hide
     */
    private void basculerFormulaire(Promotion promo, VBox formulaire) {
        boolean visible = !formulaire.isVisible();

        formulaire.setVisible(visible);
        formulaire.setManaged(visible);

        if(visible) {
            formulairesOuverts.add(promo.getNomPromo());
        } else {
            formulairesOuverts.remove(promo.getNomPromo());
        }
    }

    /**
     * Builds the "new module" form for a given promotion: code, title, type and a create button.
     *
     * @param promo the promotion to create the module in
     * @return the form as a VBox
     */
    private VBox creerFormulaireNouveauModule(Promotion promo) {
        TextField champCode = new TextField();
        champCode.setPromptText("Code (ex : R2.08)");
        champCode.setPrefWidth(140.0);

        TextField champIntitule = new TextField();
        champIntitule.setPromptText("Intitulé");
        champIntitule.setPrefWidth(220.0);

        ComboBox<EnumModule> comboType = new ComboBox<>();
        comboType.setItems(FXCollections.observableArrayList(EnumModule.values()));
        comboType.getSelectionModel().selectFirst();
        comboType.setPrefWidth(120.0);

        Button btnCreer = new Button("Créer");
        btnCreer.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6px 14px; -fx-background-radius: 6px; -fx-cursor: hand;");

        btnCreer.setOnAction(e -> creerModule(promo, champCode.getText().trim(), champIntitule.getText().trim(), comboType.getSelectionModel().getSelectedItem()));

        HBox ligne = new HBox(8, champCode, champIntitule, comboType, btnCreer);
        ligne.setAlignment(Pos.CENTER_LEFT);

        VBox formulaire = new VBox(8, ligne);
        formulaire.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8px; -fx-padding: 12px;");

        return formulaire;
    }

    /**
     * Validates the new module fields, creates the module and persists it.
     *
     * @param promo    the promotion to create the module in
     * @param code     the module code
     * @param intitule the module title
     * @param type     the module type
     */
    private void creerModule(Promotion promo, String code, String intitule, EnumModule type) {
        if(code.isEmpty()) {
            afficherErreur("Le code du module ne peut pas être vide.");
        } else if(intitule.isEmpty()) {
            afficherErreur("L'intitulé du module ne peut pas être vide.");
        } else if(type == null) {
            afficherErreur("Sélectionnez un type de module.");
        } else {
            try {
                Module module = directeur.creerModule(code, intitule, type, promo);

                if(new ModuleDAO().create(module) > 0) {
                    App.listModules.add(module);
                    formulairesOuverts.remove(promo.getNomPromo());
                    promosOuvertes.add(promo.getNomPromo());
                    afficherPromotions();
                    afficherSucces("Module " + module.getCode() + " créé avec succès.");
                } else {
                    promo.retirerModule(module);
                    afficherErreur("Erreur lors de l'enregistrement en base.");
                }
            } catch(IllegalArgumentException e) {
                afficherErreur(e.getMessage());
            }
        }
    }

    /**
     * Builds the card for a given module: header, assigned groups as chips, delete button and a form to add a group.
     *
     * @param promo  the promotion the module belongs to
     * @param module the module to build the card for
     * @return the card as a VBox
     */
    private VBox creerCarteModule(Promotion promo, Module module) {
        Label code = new Label(module.getCode());
        code.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

        Label intitule = new Label(module.getIntitule());
        intitule.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox titreGauche = new HBox(8, code, intitule);
        titreGauche.setAlignment(Pos.CENTER_LEFT);

        Region espaceTitre = new Region();
        HBox.setHgrow(espaceTitre, Priority.ALWAYS);

        Label supprimer = new Label("Supprimer");
        supprimer.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-background-color: #fee2e2; -fx-padding: 4px 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
        supprimer.setOnMouseClicked(e -> confirmerSuppressionModule(promo, module));

        HBox titre = new HBox(8, titreGauche, espaceTitre, supprimer);
        titre.setAlignment(Pos.CENTER_LEFT);

        Label labelGroupes = new Label("Groupes assignés");
        labelGroupes.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");

        FlowPane chips = new FlowPane();
        chips.setHgap(8.0);
        chips.setVgap(8.0);

        List<GroupeTD> assignes = groupesAssignes(promo, module);

        if(assignes.isEmpty()) {
            Label vide = new Label("Aucun groupe assigné");
            vide.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
            chips.getChildren().add(vide);
        } else {
            for(GroupeTD groupe : assignes) {
                chips.getChildren().add(creerChipGroupe(groupe, module));
            }
        }

        HBox ajout = creerLigneAjout(promo, module);

        VBox carte = new VBox(10, titre, labelGroupes, chips, ajout);
        carte.setPrefWidth(360.0);
        carte.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10px; -fx-padding: 14px;");

        return carte;
    }

    /**
     * Asks for confirmation before deleting a module, blocking deletion if groups are still assigned.
     *
     * @param promo  the promotion the module belongs to
     * @param module the module to delete
     */
    private void confirmerSuppressionModule(Promotion promo, Module module) {
        if(!groupesAssignes(promo, module).isEmpty()) {
            afficherErreur("Retirez d'abord tous les groupes assignés avant de supprimer ce module.");
        } else if(!module.getListeEval().isEmpty()) {
            afficherErreur("Ce module a des évaluations associées, il ne peut pas être supprimé.");
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmer la suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Supprimer définitivement le module " + module.getCode() + " — " + module.getIntitule() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();

            if(resultat.isPresent() && resultat.get() == ButtonType.OK) {
                supprimerModule(promo, module);
            }
        }
    }

    /**
     * Deletes a module from the database, removes it from the promotion, and refreshes the view.
     *
     * @param promo  the promotion the module belongs to
     * @param module the module to delete
     */
    private void supprimerModule(Promotion promo, Module module) {
        if(new ModuleDAO().delete(module) > 0) {
            promo.retirerModule(module);
            promosOuvertes.add(promo.getNomPromo());
            afficherPromotions();
            afficherSucces("Module " + module.getCode() + " supprimé avec succès.");
        } else {
            afficherErreur("Erreur lors de la suppression en base.");
        }
    }

    /**
     * Returns the groups of the given promotion currently following the given module.
     *
     * @param promo  the promotion to search groups in
     * @param module the module to check
     * @return the list of assigned groups
     */
    private List<GroupeTD> groupesAssignes(Promotion promo, Module module) {
        List<GroupeTD> liste = new ArrayList<>();

        for(GroupeTD groupe : promo.getGroupes()) {
            if(groupe.getModulesSuivis().contains(module)) liste.add(groupe);
        }

        return liste;
    }

    /**
     * Returns the groups of the given promotion not currently following the given module.
     *
     * @param promo  the promotion to search groups in
     * @param module the module to check
     * @return the list of available groups
     */
    private List<GroupeTD> groupesDisponibles(Promotion promo, Module module) {
        List<GroupeTD> liste = new ArrayList<>();

        for(GroupeTD groupe : promo.getGroupes()) {
            if(!groupe.getModulesSuivis().contains(module)) liste.add(groupe);
        }

        return liste;
    }

    /**
     * Builds a chip representing an assigned group, with a button to remove it.
     *
     * @param groupe the assigned group
     * @param module the module the group is assigned to
     * @return the chip as an HBox
     */
    private HBox creerChipGroupe(GroupeTD groupe, Module module) {
        Label nom = new Label("Groupe " + groupe.getNom());
        nom.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e293b;");

        Label retirer = new Label("×");
        retirer.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #dc2626; -fx-cursor: hand; -fx-padding: 0 0 0 6px;");
        retirer.setOnMouseClicked(e -> retirerGroupe(groupe, module));

        HBox chip = new HBox(0, nom, retirer);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #3b82f6; -fx-background-radius: 20px; -fx-padding: 4px 10px;");

        return chip;
    }

    /**
     * Builds the row letting the director add a non-assigned group to the module.
     *
     * @param promo  the promotion the module belongs to
     * @param module the module to add a group to
     * @return the row as an HBox
     */
    private HBox creerLigneAjout(Promotion promo, Module module) {
        List<GroupeTD> disponibles = groupesDisponibles(promo, module);

        if(disponibles.isEmpty()) {
            Label complet = new Label("Tous les groupes sont assignés");
            complet.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

            return new HBox(complet);
        }

        ComboBox<GroupeTD> combo = new ComboBox<>();
        combo.setCellFactory(col -> creerCelluleGroupe());
        combo.setButtonCell(creerCelluleGroupe());
        combo.setItems(FXCollections.observableArrayList(disponibles));
        combo.getSelectionModel().selectFirst();
        combo.setPrefWidth(180.0);

        Button ajouter = new Button("+ Ajouter");
        ajouter.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6px 12px; -fx-background-radius: 6px; -fx-cursor: hand;");
        ajouter.setOnAction(e -> {
            GroupeTD groupe = combo.getSelectionModel().getSelectedItem();
            if(groupe != null) ajouterGroupe(groupe, module);
        });

        HBox ligne = new HBox(8, combo, ajouter);
        ligne.setAlignment(Pos.CENTER_LEFT);

        return ligne;
    }

    /**
     * Builds a list cell displaying a group's name.
     * @return the list cell
     */
    private ListCell<GroupeTD> creerCelluleGroupe() {
        return new ListCell<GroupeTD>() {
            @Override
            protected void updateItem(GroupeTD groupe, boolean empty) {
                super.updateItem(groupe, empty);
                setText(empty || groupe == null ? null : "Groupe " + groupe.getNom());
            }
        };
    }

    /**
     * Assigns a group to a module, persists it, and refreshes the view.
     *
     * @param groupe the group to assign
     * @param module the module to assign the group to
     */
    private void ajouterGroupe(GroupeTD groupe, Module module) {
        SuitModule suitModule = new SuitModule(groupe, module);

        if(new SuitModuleDAO().create(suitModule) > 0) {
            groupe.ajouterModule(module);
            App.listSuitModules.add(suitModule);
            promosOuvertes.add(module.getPromo().getNomPromo());
            afficherPromotions();
            afficherSucces("Groupe " + groupe.getNom() + " assigné à " + module.getCode() + ".");
        } else {
            afficherErreur("Erreur lors de l'enregistrement en base.");
        }
    }

    /**
     * Removes the assignment of a group to a module, persists it, and refreshes the view.
     *
     * @param groupe the group to unassign
     * @param module the module to unassign the group from
     */
    private void retirerGroupe(GroupeTD groupe, Module module) {
        SuitModule suitModule = new SuitModule(groupe, module);

        if(new SuitModuleDAO().delete(suitModule) > 0) {
            groupe.retirerModule(module);
            retirerDeLaListe(groupe, module);
            promosOuvertes.add(module.getPromo().getNomPromo());
            afficherPromotions();
            afficherSucces("Groupe " + groupe.getNom() + " retiré de " + module.getCode() + ".");
        } else {
            afficherErreur("Erreur lors de la suppression en base.");
        }
    }

    /**
     * Removes the matching SuitModule entry from the application's in-memory list.
     *
     * @param groupe the group of the assignment to remove
     * @param module the module of the assignment to remove
     */
    private void retirerDeLaListe(GroupeTD groupe, Module module) {
        SuitModule trouve = null;

        for(SuitModule suitModule : App.listSuitModules) {
            if(suitModule.getGroupe() == groupe && suitModule.getModule() == module) trouve = suitModule;
        }

        if(trouve != null) App.listSuitModules.remove(trouve);
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
}