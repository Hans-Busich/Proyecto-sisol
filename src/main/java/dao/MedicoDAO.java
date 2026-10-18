package dao;

import model.Medico;
import java.sql.*;

public class MedicoDAO {

    public int registrarMedico(Medico m) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();

        String sql = "INSERT INTO Medico(nombres, apellidos, especialidad, cmp) VALUES (?,?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, m.getNombres());
        ps.setString(2, m.getApellidos());
        ps.setString(3, m.getEspecialidad());
        ps.setString(4, m.getCmp());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            int id = rs.getInt(1);
            m.setIdMedico(id);
            return id;
        }
        return -1;
    }
}
