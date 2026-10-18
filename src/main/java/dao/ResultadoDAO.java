package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ResultadoLaboratorio;

public class ResultadoDAO {

    public int registrarResultado(int idOrden, String descripcion, String valores) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_registrar_resultado(?,?,?)}");

        cs.setInt(1, idOrden);
        cs.setString(2, descripcion);
        cs.setString(3, valores);

        cs.execute();
        ResultSet rs = cs.getResultSet();

        if (rs != null && rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    public void validarResultado(int idResultado, String validador) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_validar_resultado(?,?)}");
        cs.setInt(1, idResultado);
        cs.setString(2, validador);
        cs.execute();
        cs.close();
    }

    public ResultadoLaboratorio obtenerResultadoPorId(int idResultado) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT idResultado, idOrden, fechaResultado, descripcion, valores, validado FROM Resultadolaboratorio WHERE idResultado = ?");
        ps.setInt(1, idResultado);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            ResultadoLaboratorio r = new ResultadoLaboratorio();
            r.setIdResultado(rs.getInt("idResultado"));
            r.setIdOrden(rs.getInt("idOrden"));
            r.setFechaResultado(rs.getTimestamp("fechaResultado").toLocalDateTime());
            r.setDescripcion(rs.getString("descripcion"));
            r.setValores(rs.getString("valores"));
            r.setValidado(rs.getBoolean("validado"));
            return r;
        }
        return null;
    }

    public int registrarEntregaResultado(int idResultado, String metodoEntrega, String entregadoPor, String observaciones) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_entregar_resultado(?,?,?,?)}");
        cs.setInt(1, idResultado);
        cs.setString(2, metodoEntrega);
        cs.setString(3, entregadoPor);
        cs.setString(4, observaciones);
        cs.execute();
        ResultSet rs = cs.getResultSet();
        if (rs != null && rs.next()) return rs.getInt(1);
        return -1;
    }
    
    // MÉTODO NUEVO PARA CONSULTA MÉDICO
    public List<ResultadoLaboratorio> listarResultadosPorMedico(int idMedico) throws SQLException {
        List<ResultadoLaboratorio> lista = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_consultar_resultados_por_medico(?)}");
        cs.setInt(1, idMedico);
        
        boolean hasResults = cs.execute();
        if (hasResults) {
            ResultSet rs = cs.getResultSet();
            while (rs.next()) {
                ResultadoLaboratorio r = new ResultadoLaboratorio();
                r.setIdResultado(rs.getInt("idResultado"));
                r.setIdOrden(rs.getInt("idOrden"));
                r.setFechaResultado(rs.getTimestamp("fechaResultado").toLocalDateTime());
                r.setDescripcion(rs.getString("descripcion"));
                r.setValores(rs.getString("valores"));
                r.setValidado(rs.getBoolean("validado"));
                lista.add(r);
            }
        }
        cs.close();
        return lista;
    }
}   