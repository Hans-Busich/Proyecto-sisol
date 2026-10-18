package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TomaDAO {

    // SP anterior (inserción inmediata)
    public int registrarToma(int idOrden, String tipoMuestra, int idTecnico) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_registrar_toma(?,?,?)}");
        try {
            cs.setInt(1, idOrden);
            cs.setString(2, tipoMuestra);
            cs.setInt(3, idTecnico);
            cs.execute();
            ResultSet rs = cs.getResultSet();
            if (rs != null && rs.next()) return rs.getInt(1);
            return -1;
        } finally {
            if (cs != null) cs.close();
        }
    }

    // Nuevo: programar una toma (inserta con fechaProgramada)
    public int programarToma(int idOrden, String tipoMuestra, LocalDateTime fechaProgramada, int idTecnico) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_programar_toma(?,?,?,?)}");
        try {
            cs.setInt(1, idOrden);
            cs.setString(2, tipoMuestra);
            // convertir LocalDateTime a java.sql.Timestamp
            cs.setTimestamp(3, Timestamp.valueOf(fechaProgramada));
            cs.setInt(4, idTecnico);
            cs.execute();
            ResultSet rs = cs.getResultSet();
            if (rs != null && rs.next()) return rs.getInt(1);
            return -1;
        } finally {
            if (cs != null) cs.close();
        }
    }

    // Nuevo: marcar toma ya programada como realizada (actualizar fechaToma y estado)
    public int registrarTomaRealizada(int idToma, LocalDateTime fechaReal) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_registrar_toma_realizada(?,?)}");
        try {
            cs.setInt(1, idToma);
            cs.setTimestamp(2, Timestamp.valueOf(fechaReal));
            cs.execute();
            ResultSet rs = cs.getResultSet();
            if (rs != null && rs.next()) return rs.getInt(1);
            return -1;
        } finally {
            if (cs != null) cs.close();
        }
    }

    // Opcional: obtener tomas programadas por paciente/orden/fecha (para mostrar en UI)
    public ResultSet listarTomasProgramadasPorOrden(int idOrden) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM TomaMuestra WHERE idOrden = ? AND estado = 'PROGRAMADA'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idOrden);
        return ps.executeQuery(); // ten cuidado de cerrar ResultSet en quien lo consuma
    }

    // Opcional: listar tomas próximas (ej. para agenda)
    public ResultSet listarTomasProximas(Timestamp desde, Timestamp hasta) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM TomaMuestra WHERE fechaProgramada BETWEEN ? AND ? AND estado = 'PROGRAMADA' ORDER BY fechaProgramada";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setTimestamp(1, desde);
        ps.setTimestamp(2, hasta);
        return ps.executeQuery();
    }
}
