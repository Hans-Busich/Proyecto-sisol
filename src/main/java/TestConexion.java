import dao.DBConnection;

public class TestConexion {
    public static void main(String[] args) throws Exception {
        try (java.sql.Connection connection = DBConnection.getInstance().getConnection()) {
            if (!connection.isValid(5)) {
                throw new java.sql.SQLException("La conexión no es válida.");
            }
            System.out.println("Conexión exitosa");
            java.sql.DatabaseMetaData metadata = connection.getMetaData();
            System.out.println("MySQL: " + metadata.getDatabaseProductVersion());
            System.out.println("Driver: " + metadata.getDriverVersion());
        } finally {
            // La prueba termina aqui: cerrar tambien el hilo auxiliar del driver.
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        }
    }
}
