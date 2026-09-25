package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import model.entity.Evaluation;
import model.entity.Module;
import view.App;

/**
 * DAO implementation for the Evaluation entity.
 * Handles all database operations for evaluations.
 *
 * @author Kevin Boeffard
 */
public class EvaluationDAO extends DAO<Evaluation, Integer> {

    /**
     * Inserts the given evaluation into the database.
     * Retrieves and sets the generated key after insertion.
     *
     * @param element the evaluation to insert
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int create(Evaluation element) {
        String sql = "INSERT INTO Evaluation (coeff, duree, module) VALUES (?, ?, ?)";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setDouble(1, element.getCoeff());
            if(element.getDuree() != null) ps.setTime(2, Time.valueOf(element.getDuree()));
            else ps.setNull(2, java.sql.Types.TIME);
            ps.setString(3, element.getModule().getCode());

            ret = ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(keys.next()) element.setNumero(keys.getInt(1));
            }

            if(ret > 0) element.getModule().ajouterEvaluation(element);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns all evaluations from the database.
     *
     * @return a list of all evaluations
     */
    @Override
    public List<Evaluation> findAll() {
        String sql = "SELECT * FROM Evaluation";
        List<Evaluation> listEvaluations = new LinkedList<>();
        try (
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while(rs.next()) {
                Module module = new ModuleDAO().findByID(rs.getString("module"));

                LocalTime duree = null;
                if(rs.getTime("duree") != null) duree = rs.getTime("duree").toLocalTime();

                Evaluation evaluation = new Evaluation(
                    String.valueOf(rs.getInt("numero")),
                    rs.getDouble("coeff"),
                    module,
                    duree
                );

                evaluation.setNumero(rs.getInt("numero"));
                module.ajouterEvaluation(evaluation);
                listEvaluations.add(evaluation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listEvaluations;
    }

    /**
     * Searches for an evaluation by its number in the application's evaluation list.
     *
     * @param id the number of the evaluation to search for
     * @return the evaluation with the given number, or null if not found
     */
    @Override
    public Evaluation findByID(Integer id) {
        Evaluation evaluation = null;
        int i = 0;
        while(i < App.listEvaluations.size() && evaluation == null) {
            if(App.listEvaluations.get(i).getNumero() == id) evaluation = App.listEvaluations.get(i);
            i++;
        }
        return evaluation;
    }

    /**
     * Updates the given evaluation in the database and in the application's evaluation list.
     *
     * @param element the evaluation to update
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int update(Evaluation element) {
        String sql = "UPDATE Evaluation SET coeff=?, duree=?, module=? WHERE numero=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setDouble(1, element.getCoeff());
            if(element.getDuree() != null) ps.setTime(2, Time.valueOf(element.getDuree()));
            else ps.setNull(2, java.sql.Types.TIME);
            ps.setString(3, element.getModule().getCode());
            ps.setInt(4, element.getNumero());

            ret = ps.executeUpdate();
            if(ret > 0) {
                int i = App.listEvaluations.indexOf(element);
                if(i != -1) App.listEvaluations.set(i, element);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }

    /**
     * Deletes the given evaluation from the database and from the application's evaluation list.
     *
     * @param element the evaluation to delete
     * @return the number of rows affected, or -1 if an error occurred
     */
    @Override
    public int delete(Evaluation element) {
        String sql = "DELETE FROM Evaluation WHERE numero=?";
        int ret = -1;
        try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, element.getNumero());
            ret = ps.executeUpdate();
            if(ret > 0) App.listEvaluations.remove(element);
        } catch (SQLException e) { e.printStackTrace(); }
        return ret;
    }
}