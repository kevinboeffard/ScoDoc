package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.EnumModule;
import model.entity.Module;
import model.entity.Promotion;
import view.App;

/**
 * DAO implementation for the Module entity.
 * Handles all database operations for modules.
 * 
 * @author Kevin Boeffard
 */
public class ModuleDAO extends DAO<Module, String> {

    /**
     * Inserts the given module into the database.
     *
     * @param element the module to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Module element) {
        String sql = "INSERT INTO Module (code, intitule, typeModule, promo) VALUES (?, ?, ?, ?)";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getCode());
            ps.setString(2, element.getIntitule());
            ps.setString(3, element.getType().name());
            ps.setString(4, element.getPromo().getNomPromo().name());

            ret = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns all modules from the database.
     * Associates each module with its corresponding promotion from the application's promotion list,
     * and adds the module to that promotion's module list.
     *
     * @return a list of all modules
     */
    @Override
    public List<Module> findAll() {
        String sql = "SELECT * FROM Module";

        List<Module> listModules = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            
            while(rs.next()) {
                Promotion promotion = new PromotionDAO().findByID(rs.getString("promo"));
                Module module = new Module(
                    rs.getString("code"),
                    rs.getString("intitule"),
                    EnumModule.valueOf(rs.getString("typeModule")),
                    promotion
                );

                promotion.ajouterModule(module);
                listModules.add(module);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listModules;
        
    }

    /**
     * Searches for a module by its code in the application's module list.
     *
     * @param id the code of the module to search for
     * @return the module with the given code, or null if not found
     */
    @Override
    public Module findByID(String id) {
        Module module = null;
        int i = 0;
        while(i < App.listModules.size() && module == null) {
            if(App.listModules.get(i).getCode().equalsIgnoreCase(id)) {
                module = App.listModules.get(i);
            }
            i++;
        }
        return module;
    }

    /**
     * Updates the given module in the database and in the application's module list.
     *
     * @param element the module to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Module element) {
        String sql = "UPDATE Module SET intitule=?, typeModule=?, promo=? WHERE code=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getIntitule());
            ps.setString(2, element.getType().name());
            ps.setString(3, element.getPromo().getNomPromo().name());
            ps.setString(4, element.getCode());

            ret = ps.executeUpdate();
            if(ret > 0) {
                int i = App.listModules.indexOf(element);
                if(i != -1) App.listModules.set(i, element);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given module from the database and from the application's module list.
     *
     * @param element the module to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Module element) {
        String sql = "DELETE FROM Module WHERE code=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getCode());
            ret = ps.executeUpdate();
            if(ret > 0) App.listModules.remove(element);
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
}