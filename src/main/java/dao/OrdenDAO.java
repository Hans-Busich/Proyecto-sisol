package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdenDAO {

    public int registrarOrden(int idPaciente, int idMedico, String tipoExamen) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_registrar_orden(?,?,?)}");

        cs.setInt(1, idPaciente);
        cs.setInt(2, idMedico);
        cs.setString(3, tipoExamen);

        cs.execute();

        ResultSet rs = cs.getResultSet();
        if (rs != null && rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }
    
    // MÉTODO NUEVO PARA REPORTES
    public Map<String, Object> obtenerEstadisticas() throws SQLException {
        Map<String, Object> reporte = new HashMap<>();
        Connection conn = DBConnection.getInstance().getConnection();
        CallableStatement cs = conn.prepareCall("{CALL sp_reporte_estadisticas()}");
        
        boolean hasResults = cs.execute();
        
        // 1. Exámenes por tipo
        if (hasResults) {
            ResultSet rs1 = cs.getResultSet();
            List<Map<String, Object>> listaExamenes = new ArrayList<>();
            while (rs1.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("tipoExamen", rs1.getString("tipoExamen"));
                fila.put("cantidad", rs1.getInt("cantidad"));
                listaExamenes.add(fila);
            }
            reporte.put("examenes", listaExamenes);
        }
        
        // 2. Promedio Horas
        if (cs.getMoreResults()) {
            ResultSet rs2 = cs.getResultSet();
            if (rs2.next()) {
                reporte.put("horas_promedio", rs2.getDouble("horas_promedio"));
            } else {
                reporte.put("horas_promedio", 0.0);
            }
        }
        
        // 3. Top 5
        if (cs.getMoreResults()) {
            ResultSet rs3 = cs.getResultSet();
            List<Map<String, Object>> listaTop = new ArrayList<>();
            while (rs3.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("tipoExamen", rs3.getString("tipoExamen"));
                fila.put("cnt", rs3.getInt("cnt"));
                listaTop.add(fila);
            }
            reporte.put("top5", listaTop);
        }
        
        cs.close();
        return reporte;
    }
}