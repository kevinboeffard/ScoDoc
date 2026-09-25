package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.ACommeModule;
import model.entity.Enseignant;
import model.entity.Module;

/**
 * DAO implementation for the ACommeModule association entity.
 * Handles all database operations for the teacher-module assignments.
 * 
 * @author Kevin Boeffard
 */
public class ACommeModuleDAO extends DAO<ACommeModule, String> {

    /**
     * Inserts the given teacher-module assignment into the database.
     * Also adds the module to the teacher's module list.
     *
     * @param element the teacher-module assignment to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(ACommeModule element) {
        String sql = "INSERT INTO ACommeModule (ens, module) VALUES (?, ?)";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEnseignant().getIdent());
            ps.setString(2, element.getModule().getCode());
            ret = ps.executeUpdate();
            if(ret > 0) element.getEnseignant().ajouterModule(element.getModule());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns all teacher-module assignments from the database.
     * Associates each assignment with its corresponding teacher and module.
     *
     * @return a list of all teacher-module assignments
     */
    @Override
    public List<ACommeModule> findAll() {
        String sql = "SELECT * FROM ACommeModule";
        List<ACommeModule> listACM = new LinkedList<>();
        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Enseignant ens = new EnseignantDAO().findByID(rs.getString("ens"));
                Module mod = new ModuleDAO().findByID(rs.getString("module"));
                ACommeModule acm = new ACommeModule(ens, mod);
                ens.ajouterModule(mod);
                listACM.add(acm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listACM;
    }

    /**
     * Not implemented for this entity.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public ACommeModule findByID(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByID'");
    }

    /**
     * Not implemented for this entity.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public int update(ACommeModule element) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Deletes the given teacher-module assignment from the database.
     * Also removes the module from the teacher's module list.
     *
     * @param element the teacher-module assignment to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(ACommeModule element) {
        String sql = "DELETE FROM ACommeModule WHERE ens=? AND module=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getEnseignant().getIdent());
            ps.setString(2, element.getModule().getCode());
            ret = ps.executeUpdate();
            if(ret > 0) element.getEnseignant().retirerModule(element.getModule());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
}
