package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.GroupeTD;
import model.entity.Module;
import model.entity.SuitModule;

/**
 * DAO implementation for the SuitModule association entity.
 * Handles all database operations for the group-module assignments.
 * 
 * @author Kevin Boeffard
 */
public class SuitModuleDAO extends DAO<SuitModule, String> {

    /**
     * Inserts the given group-module assignment into the database.
     *
     * @param element the group-module assignment to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(SuitModule element) {
        String sql = "INSERT INTO SuitModule (groupeTD, promo, module) VALUES (?, ?, ?)";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getGroupe().getNom());
            ps.setString(2, element.getGroupe().getPromotion().getNomPromo().name());
            ps.setString(3, element.getModule().getCode());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Returns all group-module assignments from the database.
     * Associates each assignment with its corresponding group and module,
     * and adds the module to the group's module list.
     *
     * @return a list of all group-module assignments
     */
    @Override
    public List<SuitModule> findAll() {
        String sql = "SELECT * FROM SuitModule";

        List<SuitModule> listSuitModules = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                GroupeTD groupeTD = new GroupeTDDAO().findByID(rs.getString("groupeTD") + "-" + rs.getString("promo"));
                Module module = new ModuleDAO().findByID(rs.getString("module"));
                groupeTD.ajouterModule(module);
                SuitModule suitModule = new SuitModule(groupeTD, module);
                listSuitModules.add(suitModule);
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listSuitModules;
    }

    /**
     * Not implemented for this entity.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public SuitModule findByID(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByID'");
    }

    /**
     * Not implemented for this entity.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public int update(SuitModule element) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Deletes the given group-module assignment from the database.
     *
     * @param element the group-module assignment to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(SuitModule element) {
        String sql = "DELETE FROM SuitModule WHERE groupeTD=? AND promo=? AND module=?";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getGroupe().getNom());
            ps.setString(2, element.getGroupe().getPromotion().getNomPromo().name());
            ps.setString(3, element.getModule().getCode());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }
    
}