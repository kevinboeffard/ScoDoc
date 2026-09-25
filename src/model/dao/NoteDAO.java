package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.EnumStatutNote;
import model.entity.Etudiant;
import model.entity.Evaluation;
import model.entity.Note;
/**
 * DAO implementation for the Note entity.
 * Handles all database operations for grades.
 *
 * @author Kevin Boeffard
 */
public class NoteDAO extends DAO<Note, String> {

    /**
     * Inserts the given grade into the database.
     *
     * @param element the grade to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Note element) {
        String sql;
        if(element.getValeur() != null) {
            sql = "INSERT INTO Note (etudiant, eval, valeur, statut) VALUES (?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO Note (etudiant, eval, valeur, statut) VALUES (?, ?, NULL, ?)";
        }

        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEtudiant().getNumEtudiant());
            ps.setInt(2, element.getEvaluation().getNumero());

            if(element.getValeur() != null) {
                ps.setDouble(3, element.getValeur());
                ps.setString(4, element.getStatut().name());
            } else {
                ps.setString(3, element.getStatut().name());
            }

            ret = ps.executeUpdate();
            if(ret > 0 && element.getValeur() != null) element.getEvaluation().ajouterNote(element.getEtudiant(), element.getValeur());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns all grades from the database.
     *
     * @return a list of all grades
     */
    @Override
    public List<Note> findAll() {
        String sql = "SELECT * FROM Note";
        List<Note> listNotes = new LinkedList<>();
        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Etudiant etudiant = new EtudiantDAO().findByID(rs.getString("etudiant"));
                Evaluation evaluation = new EvaluationDAO().findByID(rs.getInt("eval"));
                EnumStatutNote statut = EnumStatutNote.valueOf(rs.getString("statut"));

                Double valeur = null;
                if(rs.getObject("valeur") != null) valeur = rs.getDouble("valeur");

                Note note = new Note(etudiant, evaluation, valeur, statut);
                if(valeur != null) evaluation.ajouterNote(etudiant, valeur);
                listNotes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listNotes;
    }

    /**
     * Not implemented - notes are identified by composite key.
     */
    @Override
    public Note findByID(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByID'");
    }

    /**
     * Updates the given grade in the database.
     *
     * @param element the grade to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Note element) {
        String sql;
        if(element.getValeur() != null) {
            sql = "UPDATE Note SET valeur=?, statut=? WHERE etudiant=? AND eval=?";
        } else {
            sql = "UPDATE Note SET valeur=NULL, statut=? WHERE etudiant=? AND eval=?";
        }

        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            if(element.getValeur() != null) {
                ps.setDouble(1, element.getValeur());
                ps.setString(2, element.getStatut().name());
                ps.setString(3, element.getEtudiant().getNumEtudiant());
                ps.setInt(4, element.getEvaluation().getNumero());
            } else {
                ps.setString(1, element.getStatut().name());
                ps.setString(2, element.getEtudiant().getNumEtudiant());
                ps.setInt(3, element.getEvaluation().getNumero());
            }

            ret = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given grade from the database.
     *
     * @param element the grade to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Note element) {
        String sql = "DELETE FROM Note WHERE etudiant=? AND eval=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEtudiant().getNumEtudiant());
            ps.setInt(2, element.getEvaluation().getNumero());
            ret = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
}