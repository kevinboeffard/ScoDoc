package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.EnumPromo;
import model.entity.Promotion;
import view.App;

/**
 * DAO implementation for the Promotion entity.
 * Handles all database operations for promotions.
 * 
 * @author Kevin Boeffard
 */
public class PromotionDAO extends DAO<Promotion, String>{

    /**
     * Inserts the given promotion into the database.
     *
     * @param element the promotion to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Promotion element) {
        String sql = "INSERT INTO Promotion (code) VALUES (?)";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getNomPromo().name());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Returns all promotions from the database.
     *
     * @return a list of all promotions
     */
    @Override
    public List<Promotion> findAll() {
        String sql = "SELECT * FROM Promotion";

        List<Promotion> listPromotions = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {

            while(rs.next()) {
                listPromotions.add(new Promotion(EnumPromo.valueOf(rs.getString("code"))));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listPromotions;
    }

    /**
     * Searches for a promotion by its code in the application's promotion list.
     *
     * @param id the code of the promotion to search for
     * @return the promotion with the given code, or null if not found
     */
    @Override
    public Promotion findByID(String id) {
        Promotion promotion = null;
        int i = 0;
        while(i < App.listPromotions.size() && promotion == null) {
            if(App.listPromotions.get(i).getNomPromo().name().equalsIgnoreCase(id)) {
                promotion = App.listPromotions.get(i);
            }
            i++;
        }
        return promotion;
    }

    /**
     * Updates the given promotion in the database and in the application's promotion list.
     *
     * @param element the promotion to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Promotion element) {
        String sql = "UPDATE Promotion SET code = ? WHERE code = ?";
        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getNomPromo().name());
            ps.setString(2, element.getNomPromo().name());
            ret = ps.executeUpdate();
            if(ret > 0) {
                int i = App.listPromotions.indexOf(element);
                if(i != -1) App.listPromotions.set(i, element);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Deletes the given promotion from the database and from the application's promotion list.
     *
     * @param element the promotion to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Promotion element) {
        String sql = "DELETE FROM Promotion WHERE code = ?";
        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getNomPromo().name());
            ret = ps.executeUpdate();
            if(ret > 0) App.listPromotions.remove(element);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }
    
}
