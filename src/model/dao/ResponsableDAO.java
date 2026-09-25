package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.Responsable;
import model.entity.Enseignant;
import model.entity.Module;

/**
 * DAO implementation for the Responsable association entity.
 * Handles all database operations for the teacher-module responsibility assignments.
 * 
 * @author Kevin Boeffard
 */
public class ResponsableDAO extends DAO<Responsable, String> {

    /**
     * Inserts the given teacher-module responsibility assignment into the database.
     *
     * @param element the responsibility assignment to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Responsable element) {
        String sql = "INSERT INTO Responsable (ens, module) VALUES (?, ?)";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEnseignant().getIdent());
            ps.setString(2, element.getModule().getCode());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Returns all responsibility assignments from the database.
     * Associates each assignment with its corresponding teacher and module,
     * and adds the teacher as responsible for the module.
     *
     * @return a list of all responsibility assignments
     */
    @Override
    public List<Responsable> findAll() {
        String sql = "SELECT * FROM Responsable";

        List<Responsable> listResponsables = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Enseignant enseignant = new EnseignantDAO().findByID(rs.getString("ens"));
                Module module = new ModuleDAO().findByID(rs.getString("module"));
                Responsable responsable = new Responsable(enseignant, module);

                listResponsables.add(responsable);
                module.ajouterResponsable(enseignant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listResponsables;
    }

    /**
     * Not implemented for this entity.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public Responsable findByID(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByID'");
    }

    /**
     * Not implemented for this entity.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public int update(Responsable element) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Deletes the given responsibility assignment from the database.
     * Also removes the teacher from the module's responsible list.
     *
     * @param element the responsibility assignment to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Responsable element) {
        String sql = "DELETE FROM Responsable WHERE ens=? AND module=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEnseignant().getIdent());
            ps.setString(2, element.getModule().getCode());
            ret = ps.executeUpdate();
            
            element.getModule().retirerResponsable(element.getEnseignant());
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
    
}
