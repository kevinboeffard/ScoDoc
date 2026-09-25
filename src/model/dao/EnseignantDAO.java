package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.DirecteurEtudes;
import model.entity.Enseignant;
import view.App;

/**
 * DAO implementation for the Enseignant entity.
 * Handles all database operations for teachers.
 * Manages both the Utilisateur and Enseignant tables.
 * 
 * @author Kevin Boeffard
 */
public class EnseignantDAO extends DAO<Enseignant, String> {

    /**
     * Inserts the given teacher into the database.
     * Inserts a row in both the Utilisateur and Enseignant tables.
     *
     * @param element the teacher to insert
     * @return the total number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Enseignant element) {
        String sqlUtilisateur = "INSERT INTO Utilisateur (email, motDePasse, nom, prenom) VALUES (?, ?, ?, ?)";
        String sqlEnseignant = "INSERT INTO Enseignant (identifiant, estDe, userEns) VALUES (?, ?, ?)";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement psUtilisateur = conn.prepareStatement(sqlUtilisateur);
            PreparedStatement psEnseignant = conn.prepareStatement(sqlEnseignant)
        ) {
            psUtilisateur.setString(1, element.getEmail());
            psUtilisateur.setString(2, element.getMdp());
            psUtilisateur.setString(3, element.getNom());
            psUtilisateur.setString(4, element.getPrenom());

            psEnseignant.setString(1, element.getIdent());
            psEnseignant.setBoolean(2, element instanceof DirecteurEtudes);
            psEnseignant.setString(3, element.getEmail());
            ret = psUtilisateur.executeUpdate() + psEnseignant.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Returns all teachers from the database.
     *
     * @return a list of all teachers
     */
    @Override
    public List<Enseignant> findAll() {
        String sql = "SELECT * FROM Enseignant JOIN Utilisateur ON Enseignant.userEns = Utilisateur.email";

        List<Enseignant> listEnseignants = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Enseignant enseignant;
                if(rs.getBoolean("estDe")) {
                    enseignant = new DirecteurEtudes(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("identifiant")
                    );
                } else {
                    enseignant = new Enseignant(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getString("identifiant")
                    );
                }

                listEnseignants.add(enseignant);
            }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listEnseignants;
    }

    /**
     * Searches for a teacher by their identifier in the application's teacher list.
     *
     * @param id the identifier of the teacher to search for
     * @return the teacher with the given identifier, or null if not found
     */
    @Override
    public Enseignant findByID(String id) {
        Enseignant enseignant = null;

        int i = 0;
        while(i < App.listEnseignants.size() && enseignant == null) {
            if(App.listEnseignants.get(i).getIdent().equalsIgnoreCase(id)) enseignant = App.listEnseignants.get(i);
            i++;
        }

        return enseignant;
    }

    /**
     * Updates the given teacher in the database and in the application's teacher list.
     * Updates both the Utilisateur and Enseignant tables.
     *
     * @param element the teacher to update
     * @return the total number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Enseignant element) {
        String sqlUtilisateur = "UPDATE Utilisateur SET motDePasse=?, nom=?, prenom=? WHERE email=?";
        String sqlEnseignant = "UPDATE Enseignant SET identifiant=? WHERE identifiant=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement psUtilisateur = conn.prepareStatement(sqlUtilisateur);
            PreparedStatement psEnseignant = conn.prepareStatement(sqlEnseignant)
        ) {
            psUtilisateur.setString(1, element.getMdp());
            psUtilisateur.setString(2, element.getNom());
            psUtilisateur.setString(3, element.getPrenom());
            psUtilisateur.setString(4, element.getEmail());

            psEnseignant.setString(1, element.getIdent());
            psEnseignant.setString(2, element.getIdent());

            ret = psUtilisateur.executeUpdate() + psEnseignant.executeUpdate();
            if(ret > 0) {
                int i = App.listEnseignants.indexOf(element);
                if(i != -1) App.listEnseignants.set(i, element);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given teacher from the database and from the application's teacher list.
     * Deletes from both the Enseignant and Utilisateur tables.
     *
     * @param element the teacher to delete
     * @return the total number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Enseignant element) {
        String sqlEnseignant = "DELETE FROM Enseignant WHERE identifiant=?";
        String sqlUtilisateur = "DELETE FROM Utilisateur WHERE email=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement psEnseignant = conn.prepareStatement(sqlEnseignant);
            PreparedStatement psUtilisateur = conn.prepareStatement(sqlUtilisateur)
        ) {
            psEnseignant.setString(1, element.getIdent());
            psUtilisateur.setString(1, element.getEmail());

            ret = psEnseignant.executeUpdate() + psUtilisateur.executeUpdate();
            if(ret > 0) App.listEnseignants.remove(element);
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
    
}
