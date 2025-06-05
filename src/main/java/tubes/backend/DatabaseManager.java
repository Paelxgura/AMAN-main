
package tubes.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_NAME = "aman_app.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);

            if (connection != null) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }

        } catch (SQLException e) {
            System.err.println("Koneksi ke SQLite gagal: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi SQLite: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup Statement SQLite: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup ResultSet SQLite: " + e.getMessage());
            }
        }
    }
}