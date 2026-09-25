package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.Etudiant;
import model.entity.GroupeTD;
import view.App;

/**
 * DAO implementation for the Etudiant entity.
 * Handles all database operations for students.
 * Manages both the Utilisateur and Etudiant tables.
 * 
 * @author Kevin Boeffard
 */
public class EtudiantDAO extends DAO<Etudiant, String> {

    /**
     * Inserts the given student into the database.
     * Inserts a row in both the Utilisateur and Etudiant tables.
     *
     * @param etudiant the student to insert
     * @return the total number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Etudiant etudiant) {
        String sqlUtilisateur = "INSERT INTO Utilisateur (email, motDePasse, nom, prenom) VALUES (?, ?, ?, ?)";
        String sqlEtudiant = "INSERT INTO Etudiant (numero, userEtud, groupeTD, promo) VALUES (?, ?, ?, ?)";
        
        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement psUtilisateur = conn.prepareStatement(sqlUtilisateur);
            PreparedStatement psEtudiant = conn.prepareStatement(sqlEtudiant)
        ) {
            psUtilisateur.setString(1, etudiant.getEmail());
            psUtilisateur.setString(2, etudiant.getMdp());
            psUtilisateur.setString(3, etudiant.getNom());
            psUtilisateur.setString(4, etudiant.getPrenom());

            psEtudiant.setString(1, String.valueOf(etudiant.getNumEtudiant()));
            psEtudiant.setString(2, etudiant.getEmail());
            psEtudiant.setString(3, etudiant.getGroupe().getNom());
            psEtudiant.setString(4, etudiant.getGroupe().getPromotion().getNomPromo().name());
            
            ret = psUtilisateur.executeUpdate() + psEtudiant.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Returns all students from the database.
     * Associates each student with their corresponding tutorial group from the application's group list.
     *
     * @return a list of all students
     */
    @Override
    public List<Etudiant> findAll() {
        String sql = "SELECT * FROM Etudiant JOIN Utilisateur ON Etudiant.userEtud = Utilisateur.email";

        List<Etudiant> listEtudiants = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Etudiant etudiant = new Etudiant(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("motDePasse"),
                    rs.getString("numero")
                );

                listEtudiants.add(etudiant);

                for(GroupeTD groupeTD : App.listGroupesTDs) {
                    if(groupeTD.getNom().equalsIgnoreCase(rs.getString("groupeTD")) && groupeTD.getPromotion().getNomPromo().name().equalsIgnoreCase(rs.getString("promo"))) {
                        groupeTD.ajouterEtudiant(etudiant);
                        etudiant.setGroupe(groupeTD);
                    }
                }
            }
        } catch (SQLException e) {
            
            e.printStackTrace();
        }
        
        return listEtudiants;
    }

    /**
     * Searches for a student by their student number in the application's student list.
     *
     * @param id the student number to search for
     * @return the student with the given number, or null if not found
     */
    @Override
    public Etudiant findByID(String id) {
        Etudiant etudiant = null;
        int i = 0;
        while(i < App.listEtudiants.size() && etudiant == null) {
            if(App.listEtudiants.get(i).getNumEtudiant().equalsIgnoreCase(id)) etudiant = App.listEtudiants.get(i);
            i++;
        }
        return etudiant;
    }
    
    /**
     * Updates the given student in the database and in the application's student list.
     * Updates both the Utilisateur and Etudiant tables.
     *
     * @param element the student to update
     * @return the total number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Etudiant element) {
        String sqlU = "UPDATE Utilisateur SET motDePasse=?, nom=?, prenom=? WHERE email=?";
        String sqlE = "UPDATE Etudiant SET groupeTD=?, promo=? WHERE numero=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement psUtilisateur = conn.prepareStatement(sqlU);
            PreparedStatement psEtudiant = conn.prepareStatement(sqlE)
        ) {
            psUtilisateur.setString(1, element.getMdp());
            psUtilisateur.setString(2, element.getNom());
            psUtilisateur.setString(3, element.getPrenom());
            psUtilisateur.setString(4, element.getEmail());

            psEtudiant.setString(1, element.getGroupe().getNom());
            psEtudiant.setString(2, element.getGroupe().getPromotion().getNomPromo().name());
            psEtudiant.setString(3, element.getNumEtudiant());

            ret = psUtilisateur.executeUpdate() + psEtudiant.executeUpdate();
            if(ret > 0) {
                int i = App.listEtudiants.indexOf(element);
                if(i != -1) App.listEtudiants.set(i, element);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given student from the database and from the application's student list.
     * Deletes from both the Etudiant and Utilisateur tables.
     *
     * @param element the student to delete
     * @return the total number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Etudiant element) {
        String sqlE = "DELETE FROM Etudiant WHERE numero=?";
        String sqlU = "DELETE FROM Utilisateur WHERE email=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement psEtudiant = conn.prepareStatement(sqlE);
            PreparedStatement psUtilisateur = conn.prepareStatement(sqlU)
        ) {
            psEtudiant.setString(1, element.getNumEtudiant());
            psUtilisateur.setString(1, element.getEmail());
            ret = psEtudiant.executeUpdate() + psUtilisateur.executeUpdate();
            if(ret > 0) App.listEtudiants.remove(element);
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
    
}
