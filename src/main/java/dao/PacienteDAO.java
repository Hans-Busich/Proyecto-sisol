package dao;

import model.Paciente;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PacienteDAO {
    private static final String COLUMNAS = "idPaciente, dni, nombres, apellidos, fechaNacimiento, telefono, email";
    private final JdbcTemplate jdbcTemplate;

    // Swing conserva su constructor, pero obtiene conexiones independientes.
    public PacienteDAO() {
        this(new JdbcTemplate(DBConnection.createDataSource()));
    }

    @Autowired
    public PacienteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate");
    }

    public int registrarPaciente(Paciente p) {
        Objects.requireNonNull(p, "paciente");
        Objects.requireNonNull(p.getFechaNacimiento(), "fechaNacimiento");
        Integer id = jdbcTemplate.execute("{CALL sp_registrar_paciente(?,?,?,?,?,?)}",
                (CallableStatementCallback<Integer>) cs -> {
            cs.setString(1, p.getDni());
            cs.setString(2, p.getNombres());
            cs.setString(3, p.getApellidos());
            cs.setDate(4, Date.valueOf(p.getFechaNacimiento()));
            cs.setString(5, p.getTelefono());
            cs.setString(6, p.getEmail());

            boolean resultado = cs.execute();
            // Saltar conteos de actualizacion anteriores al SELECT del ID.
            while (true) {
                if (resultado) {
                    try (ResultSet rs = cs.getResultSet()) {
                        if (rs != null && rs.next()) {
                            int generatedId = rs.getInt(1);
                            if (!rs.wasNull() && generatedId > 0) return generatedId;
                        }
                    }
                } else if (cs.getUpdateCount() == -1) {
                    break;
                }
                resultado = cs.getMoreResults();
            }
            throw new SQLException("El procedimiento no devolvió un ID de paciente válido.");
        });
        if (id == null) throw new IllegalStateException("No se recibió el ID del paciente.");
        p.setIdPaciente(id);
        return id;
    }

    public List<Paciente> listarPacientes() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Paciente ORDER BY idPaciente",
                (rs, rowNum) -> mapearPaciente(rs));
    }

    public boolean actualizarPaciente(int idPaciente, Paciente p) {
        if (idPaciente <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        Objects.requireNonNull(p, "paciente");
        Objects.requireNonNull(p.getFechaNacimiento(), "fechaNacimiento");
        int filas = jdbcTemplate.update(
                "UPDATE Paciente SET dni = ?, nombres = ?, apellidos = ?, fechaNacimiento = ?, telefono = ?, email = ? WHERE idPaciente = ?",
                p.getDni(), p.getNombres(), p.getApellidos(), Date.valueOf(p.getFechaNacimiento()),
                p.getTelefono(), p.getEmail(), idPaciente);
        // Algunos drivers devuelven cero cuando se guardan los mismos valores.
        return filas > 0 || obtenerPacientePorId(idPaciente).isPresent();
    }

    public Optional<Paciente> obtenerPacientePorId(int idPaciente) {
        if (idPaciente <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        List<Paciente> pacientes = jdbcTemplate.query(
                "SELECT " + COLUMNAS + " FROM Paciente WHERE idPaciente = ?",
                (rs, rowNum) -> mapearPaciente(rs), idPaciente);
        return pacientes.stream().findFirst();
    }

    public boolean eliminarPaciente(int idPaciente) {
        if (idPaciente <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        int filas = jdbcTemplate.update(
                "DELETE FROM Paciente WHERE idPaciente = ? "
                + "AND NOT EXISTS (SELECT 1 FROM OrdenLaboratorio WHERE idPaciente = ?) "
                + "AND NOT EXISTS (SELECT 1 FROM Factura WHERE idPaciente = ?)",
                idPaciente, idPaciente, idPaciente);
        if (filas == 0 && obtenerPacientePorId(idPaciente).isPresent()) {
            throw new org.springframework.dao.DataIntegrityViolationException("Paciente con registros asociados.");
        }
        return filas > 0;
    }

    private static Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setIdPaciente(rs.getInt("idPaciente"));
        p.setDni(rs.getString("dni"));
        p.setNombres(rs.getString("nombres"));
        p.setApellidos(rs.getString("apellidos"));
        Date fecha = rs.getDate("fechaNacimiento");
        p.setFechaNacimiento(fecha == null ? null : fecha.toLocalDate());
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        return p;
    }

}
