package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

import model.entity.GroupeTD;
import model.entity.Module;
import model.entity.Seance;
import view.App;

/**
 * DAO implementation for the Seance entity.
 * Handles all database operations for sessions.
 *
 * @author Kevin Boeffard
 */
public class SeanceDAO extends DAO<Seance, Integer> {

    /**
     * Inserts the given session into the database.
     * Retrieves and sets the generated key after insertion.
     *
     * @param element the session to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Seance element) {
        String sql = "INSERT INTO Seance (module, groupeTD, promo, dateSeance, horaireDebut, horaireFin, dateSaisie) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, element.getModule().getCode());
            ps.setString(2, element.getGroupe().getNom());
            ps.setString(3, element.getGroupe().getPromotion().getNomPromo().name());
            ps.setDate(4, new Date(element.getDate().getTime()));
            ps.setTime(5, Time.valueOf(element.getHeureDeb()));
            ps.setTime(6, Time.valueOf(element.getHeureFin()));
            ps.setDate(7, new Date(System.currentTimeMillis()));

            ret = ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) element.setNumero(keys.getInt(1));
            }

            if(ret > 0) element.getModule().ajouterSeance(element);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns all sessions from the database.
     *
     * @return a list of all sessions
     */
    @Override
    public List<Seance> findAll() {
        String sql = "SELECT * FROM Seance";
        List<Seance> listSeances = new LinkedList<>();
        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Module module = new ModuleDAO().findByID(rs.getString("module"));
                GroupeTD groupe = new GroupeTDDAO().findByID(rs.getString("groupeTD") + "-" + rs.getString("promo"));

                Seance seance = new Seance(
                    rs.getDate("dateSeance"),
                    rs.getTime("horaireDebut").toLocalTime(),
                    rs.getTime("horaireFin").toLocalTime(),
                    module,
                    groupe
                );

                seance.setNumero(rs.getInt("numero"));
                module.ajouterSeance(seance);
                listSeances.add(seance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listSeances;
    }

    /**
     * Searches for a session by its number in the application's session list.
     *
     * @param id the number of the session to search for
     * @return the session with the given number, or null if not found
     */
    @Override
    public Seance findByID(Integer id) {
        Seance seance = null;
        int i = 0;
        while(i < App.listSeances.size() && seance == null) {
            if(App.listSeances.get(i).getNumero() == id) seance = App.listSeances.get(i);
            i++;
        }
        return seance;
    }

    /**
     * Updates the given session in the database and in the application's session list.
     *
     * @param element the session to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Seance element) {
        String sql = "UPDATE Seance SET module=?, groupeTD=?, promo=?, dateSeance=?, horaireDebut=?, horaireFin=? WHERE numero=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, element.getModule().getCode());
            ps.setString(2, element.getGroupe().getNom());
            ps.setString(3, element.getGroupe().getPromotion().getNomPromo().name());
            ps.setDate(4, new Date(element.getDate().getTime()));
            ps.setTime(5, Time.valueOf(element.getHeureDeb()));
            ps.setTime(6, Time.valueOf(element.getHeureFin()));
            ps.setInt(7, element.getNumero());

            ret = ps.executeUpdate();
            if(ret > 0) {
                int i = App.listSeances.indexOf(element);
                if(i != -1) App.listSeances.set(i, element);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given session from the database and from the application's session list.
     *
     * @param element the session to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Seance element) {
        String sql = "DELETE FROM Seance WHERE numero=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, element.getNumero());
            ret = ps.executeUpdate();
            if(ret > 0) App.listSeances.remove(element);
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
}