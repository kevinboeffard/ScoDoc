package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract generic DAO providing a base for database access operations.
 * Handles the database connection and defines the standard CRUD operations.
 *
 * @param <T> the type of the entity managed by this DAO
 * @param <K> the type of the primary key of the entity
 * 
 * @author Kevin Boeffard
 */
public abstract class DAO<T, K> {

    /** Fully qualified class name of the JDBC driver. */
    private static String driverClassName = "com.mysql.cj.jdbc.Driver";

    /** JDBC connection URL of the database. */
    private static String url = "jdbc:mysql://localhost:3306/bd_scodoc";

    /** Username used to connect to the database. */
    private static String username = "scodoc";

    /** Password used to connect to the database. */
    private static String password = "scodoc";

    /**
     * Creates and returns a connection to the database.
     *
     * @throws SQLException if a database access error occurs.
     * @throws IllegalArgumentException if the JDBC driver class is not found.
     *
     * @return a connection to the database
     */
    protected Connection getConnection() throws SQLException {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erreur de connection avec la base de donnée");
        }

        return DriverManager.getConnection(url, username, password);

    }

    /**
     * Inserts the given entity into the database.
     *
     * @param element the entity to insert
     * @return the number of rows affected
     */
    public abstract int create(T element);

    /**
     * Returns all entities of type T from the database.
     *
     * @return a list of all entities
     */
    public abstract List<T> findAll();

    /**
     * Returns the entity with the given primary key from the database.
     *
     * @param id the primary key of the entity to retrieve
     * @return the entity with the given primary key, or null if not found
     */
    public abstract T findByID(K id);

    /**
     * Updates the given entity in the database.
     *
     * @param element the entity to update
     * @return the number of rows affected
     */
    public abstract int update(T element);

    /**
     * Deletes the given entity from the database.
     *
     * @param element the entity to delete
     * @return the number of rows affected
     */
    public abstract int delete(T element);


}