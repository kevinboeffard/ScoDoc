package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import model.entity.GroupeTD;
import model.entity.Promotion;
import view.App;

/**
 * DAO implementation for the GroupeTD entity.
 * Handles all database operations for tutorial groups.
 * 
 * @author Kevin Boeffard
 */
public class GroupeTDDAO extends DAO<GroupeTD, String>{

    /**
     * Inserts the given tutorial group into the database.
     *
     * @param element the tutorial group to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(GroupeTD element) {
        String sql = "INSERT INTO GroupeTD (sigle, promo) VALUES (?, ?)";

        int ret = -1;

        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getNom());
            ps.setString(2, element.getPromotion().getNomPromo().name());

            ret = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Returns all tutorial groups from the database.
     * Associates each group with its corresponding promotion from the application's promotion list.
     *
     * @return a list of all tutorial groups
     */
    @Override
    public List<GroupeTD> findAll() {
        String sql = "SELECT * FROM GroupeTD";

        List<GroupeTD> listGroupeTDs = new LinkedList<>();

        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Promotion promotion = new PromotionDAO().findByID(rs.getString("promo"));

                GroupeTD groupeTD = new GroupeTD(rs.getString("sigle") , promotion);
                promotion.ajouterGroupe(groupeTD);

                listGroupeTDs.add(groupeTD);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listGroupeTDs;
    }

    /**
     * Searches for a tutorial group by its composite key in the application's group list.
     * The id format is "sigle-promo" (e.g. "A-BUT1").
     *
     * @param id the composite key of the tutorial group to search for
     * @return the tutorial group with the given key, or null if not found
     */
    @Override
    public GroupeTD findByID(String id) {
        // id = "A-BUT1" par exemple
        GroupeTD groupeTD = null;
        int i = 0;
        while(i < App.listGroupesTDs.size() && groupeTD == null) {
            GroupeTD g = App.listGroupesTDs.get(i);
            String key = g.getNom() + "-" + g.getPromotion().getNomPromo().name();
            if(key.equalsIgnoreCase(id)) groupeTD = g;
            i++;
        }
        return groupeTD;
    }

    /**
     * Updates the given tutorial group in the database and in the application's group list.
     *
     * @param element the tutorial group to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(GroupeTD element) {
        String sql = "UPDATE GroupeTD SET sigle = ?, promo = ? WHERE sigle = ? AND promo = ?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getNom());
            ps.setString(2, element.getPromotion().getNomPromo().name());
            ps.setString(3, element.getNom());
            ps.setString(4, element.getPromotion().getNomPromo().name());
            ret = ps.executeUpdate();
            if(ret > 0) {
                int i = App.listGroupesTDs.indexOf(element);
                if(i != -1) App.listGroupesTDs.set(i, element);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given tutorial group from the database and from the application's group list.
     *
     * @param element the tutorial group to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(GroupeTD element) {
        String sql = "DELETE FROM GroupeTD WHERE sigle = ? AND promo = ?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getNom());
            ps.setString(2, element.getPromotion().getNomPromo().name());
            ret = ps.executeUpdate();
            if(ret > 0) App.listGroupesTDs.remove(element);
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    
    
}
