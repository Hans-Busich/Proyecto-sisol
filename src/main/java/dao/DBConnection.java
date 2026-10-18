package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private static final String URL = environmentOrDefault("SISOL_DB_URL",
            "jdbc:mysql://localhost:3306/biosalud?sslMode=REQUIRED&serverTimezone=UTC");
    private static final String USER = environmentOrDefault("SISOL_DB_USER", "root");
    private static final String PASS = environmentOrDefault("SISOL_DB_PASSWORD", "1234");

    private static String environmentOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null ? defaultValue : value;
    }

   private DBConnection() throws SQLException {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASS);
    } catch (ClassNotFoundException ex) {
        throw new SQLException("No se encontró el driver de MySQL", ex);
    }
}


    public static synchronized DBConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

    public static javax.sql.DataSource createDataSource() {
        return new org.springframework.jdbc.datasource.DriverManagerDataSource(URL, USER, PASS);
    }
}

