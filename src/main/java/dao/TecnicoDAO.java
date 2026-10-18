package dao;

import model.Tecnico;
import java.sql.*;

public class TecnicoDAO {

    public int registrarTecnico(Tecnico t) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();

        String sql = "INSERT INTO Tecnico(nombres, apellidos, telefono) VALUES (?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, t.getNombres());
        ps.setString(2, t.getApellidos());
        ps.setString(3, t.getTelefono());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            int id = rs.getInt(1);
            t.setIdTecnico(id);
            return id;
        }
        return -1;
    }
}
