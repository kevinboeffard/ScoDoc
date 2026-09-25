package view;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.dao.ACommeModuleDAO;
import model.dao.EnseignantDAO;
import model.dao.EtudiantDAO;
import model.dao.EvaluationDAO;
import model.dao.GroupeTDDAO;
import model.dao.ModuleDAO;
import model.dao.NoteDAO;
import model.dao.PresenceDAO;
import model.dao.PromotionDAO;
import model.dao.ResponsableDAO;
import model.dao.SeanceDAO;
import model.dao.SuitModuleDAO;
import model.entity.ACommeModule;
import model.entity.Enseignant;
import model.entity.Etudiant;
import model.entity.Evaluation;
import model.entity.GroupeTD;
import model.entity.Promotion;
import model.entity.Responsable;
import model.entity.Seance;
import model.entity.SuitModule;
import model.entity.Utilisateur;
import model.entity.Module;
import model.entity.Note;
import model.entity.Presence;

/**
 * Entry point of the ScoDoc JavaFX application.
 * Loads every entity from the database into static lists shared across
 * the application, then displays the login page.
 * @author Kevin Boeffard
 */
public class App extends Application {

    /** The currently connected user, or null if no one is connected. */
    public static Utilisateur utilisateurConnecte;

    /** Every promotion loaded from the database. */
    public static List<Promotion> listPromotions;

    /** Every tutorial group loaded from the database. */
    public static List<GroupeTD> listGroupesTDs;

    /** Every student loaded from the database. */
    public static List<Etudiant> listEtudiants;

    /** Every teacher loaded from the database. */
    public static List<Enseignant> listEnseignants;

    /** Every module loaded from the database. */
    public static List<Module> listModules;

    /** Every ACommeModule association loaded from the database. */
    public static List<ACommeModule> listACM;

    /** Every group-module assignment loaded from the database. */
    public static List<SuitModule> listSuitModules;

    /** Every module-teacher responsibility loaded from the database. */
    public static List<Responsable> listResponsables;

    /** Every session loaded from the database. */
    public static List<Seance> listSeances;

    /** Every attendance record loaded from the database. */
    public static List<Presence> listPresences;

    /** Every evaluation loaded from the database. */
    public static List<Evaluation> listEvaluations;

    /** Every grade loaded from the database. */
    public static List<Note> listNotes;

    /**
     * Loads every entity from the database into the application's static lists,
     * then loads and shows the login page.
     *
     * @param stage the primary stage of the application
     * @throws Exception if the login page FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {

        // - Chargement des données de la BDD

        listPromotions = new PromotionDAO().findAll();
        listGroupesTDs = new GroupeTDDAO().findAll();
        listEtudiants = new EtudiantDAO().findAll();
        listEnseignants = new EnseignantDAO().findAll();
        listModules = new ModuleDAO().findAll();
        listACM = new ACommeModuleDAO().findAll();
        listSuitModules = new SuitModuleDAO().findAll();
        listResponsables = new ResponsableDAO().findAll();
        listSeances = new SeanceDAO().findAll();
        listEvaluations = new EvaluationDAO().findAll();
        listNotes = new NoteDAO().findAll();
        listPresences = new PresenceDAO().findAll();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/login_page.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("ScoDoc");
        stage.show();
        
    }

    /**
     * Launches the JavaFX application.
     * @param args command-line arguments, unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}