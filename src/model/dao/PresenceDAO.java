package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.Etudiant;
import model.entity.EnumPresence;
import model.entity.Presence;
import model.entity.Seance;

/**
 * DAO implementation for the Presence entity.
 * Handles all database operations for attendance records.
 *
 * @author Kevin Boeffard
 */
public class PresenceDAO extends DAO<Presence, String> {

    /**
     * Inserts the given attendance record into the database.
     *
     * @param element the attendance record to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Presence element) {
        String sql = "INSERT INTO Presence (etudiant, seance, statut) VALUES (?, ?, ?)";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEtudiant().getNumEtudiant());
            ps.setInt(2, element.getSeance().getNumero());
            ps.setString(3, element.getStatut().name());

            ret = ps.executeUpdate();
            if(ret > 0) element.getSeance().ajouterPresence(element.getEtudiant(), element.getStatut());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns all attendance records from the database.
     *
     * @return a list of all attendance records
     */
    @Override
    public List<Presence> findAll() {
        String sql = "SELECT * FROM Presence";
        List<Presence> listPresences = new LinkedList<>();
        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Etudiant etudiant = new EtudiantDAO().findByID(rs.getString("etudiant"));
                Seance seance = new SeanceDAO().findByID(rs.getInt("seance"));
                EnumPresence statut = EnumPresence.valueOf(rs.getString("statut"));

                Presence presence = new Presence(etudiant, seance, statut);
                seance.ajouterPresence(etudiant, statut);
                listPresences.add(presence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listPresences;
    }

    /**
     * Not implemented - presence records are identified by composite key.
     */
    @Override
    public Presence findByID(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByID'");
    }

    /**
     * Updates the attendance status of the given record in the database.
     *
     * @param element the attendance record to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Presence element) {
        String sql = "UPDATE Presence SET statut=? WHERE etudiant=? AND seance=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getStatut().name());
            ps.setString(2, element.getEtudiant().getNumEtudiant());
            ps.setInt(3, element.getSeance().getNumero());
            ret = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given attendance record from the database.
     *
     * @param element the attendance record to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Presence element) {
        String sql = "DELETE FROM Presence WHERE etudiant=? AND seance=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEtudiant().getNumEtudiant());
            ps.setInt(2, element.getSeance().getNumero());
            ret = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
}