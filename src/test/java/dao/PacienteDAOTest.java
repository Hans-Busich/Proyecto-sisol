package dao;

import model.Paciente;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PacienteDAOTest {
    @Test
    void eliminaSoloElPacienteIndicado() {
        Fixture fixture = new Fixture(false, false, false);
        assertTrue(fixture.dao().eliminarPaciente(42));
        assertEquals(42, fixture.parameters.get(1));
        assertEquals(42, fixture.parameters.get(2));
        assertEquals(42, fixture.parameters.get(3));
        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    @Test
    void noEliminaPacienteConRegistrosAsociados() {
        Fixture fixture = new Fixture(false, true, false);
        fixture.updatedRows = 0;
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> fixture.dao().eliminarPaciente(42));
    }

    @Test
    void eliminarPacienteAusenteDevuelveFalse() {
        Fixture fixture = new Fixture(false, false, false);
        fixture.updatedRows = 0;
        assertFalse(fixture.dao().eliminarPaciente(42));
    }
    @Test
    void actualizaSoloElIdIndicadoPorLaRuta() {
        Fixture fixture = new Fixture(false, true, false);
        Paciente paciente = paciente();
        paciente.setIdPaciente(999);
        assertTrue(fixture.dao().actualizarPaciente(42, paciente));
        assertEquals(42, fixture.parameters.get(7));
        assertEquals("12345678", fixture.parameters.get(1));
        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    @Test
    void actualizarPacienteInexistenteNoAnunciaExito() {
        Fixture fixture = new Fixture(false, false, false);
        fixture.updatedRows = 0;
        assertFalse(fixture.dao().actualizarPaciente(42, paciente()));
    }

    @Test
    void guardarSinCambiosEsValidoSiElPacienteExiste() {
        Fixture fixture = new Fixture(false, true, false);
        fixture.updatedRows = 0;
        assertTrue(fixture.dao().actualizarPaciente(42, paciente()));
    }
    @Test
    void leeIdDespuesDeConteoYCierraRecursos() {
        Fixture fixture = new Fixture(true, true, false);
        Paciente paciente = paciente();
        assertEquals(42, fixture.dao().registrarPaciente(paciente));
        assertEquals(42, paciente.getIdPaciente());
        assertEquals("12345678", fixture.parameters.get(1));
        assertEquals(Date.valueOf(paciente.getFechaNacimiento()), fixture.parameters.get(4));
        assertEquals("ana@example.test", fixture.parameters.get(6));
        fixture.assertClosed();
    }

    @Test
    void ausenciaDeIdEsErrorYCierraRecursos() {
        Fixture fixture = new Fixture(false, false, false);
        Paciente paciente = paciente();
        assertThrows(DataAccessException.class, () -> fixture.dao().registrarPaciente(paciente));
        assertEquals(0, paciente.getIdPaciente());
        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    @Test
    void errorSqlTambienLiberaConexion() {
        Fixture fixture = new Fixture(false, true, true);
        assertThrows(DataAccessException.class, () -> fixture.dao().registrarPaciente(paciente()));
        assertTrue(fixture.statementClosed);
        assertTrue(fixture.connectionClosed);
    }

    @Test
    void consultaMapeaFechaNulaYUsaParametro() {
        Fixture fixture = new Fixture(false, true, false);
        Paciente paciente = fixture.dao().obtenerPacientePorId(42).orElseThrow();
        assertEquals(42, paciente.getIdPaciente());
        assertEquals("Ana", paciente.getNombres());
        assertNull(paciente.getFechaNacimiento());
        assertEquals(42, fixture.parameters.get(1));
        fixture.assertClosed();
    }

    @Test
    void consultaSinFilasDevuelveOptionalVacio() {
        Fixture fixture = new Fixture(false, false, false);
        assertTrue(fixture.dao().obtenerPacientePorId(42).isEmpty());
        fixture.assertClosed();
    }

    private static Paciente paciente() {
        return new Paciente("12345678", "Ana", "Perez", LocalDate.of(1995, 1, 2),
                "999999999", "ana@example.test");
    }

    private static class Fixture {
        final boolean leadingCount, hasRow, fail;
        final Map<Object, Object> parameters = new HashMap<>();
        boolean advanced, rowRead, resultClosed, statementClosed, connectionClosed;
        int updatedRows = 1;
        Fixture(boolean leadingCount, boolean hasRow, boolean fail) {
            this.leadingCount = leadingCount;
            this.hasRow = hasRow;
            this.fail = fail;
        }
        PacienteDAO dao() {
            ResultSet result = proxy(ResultSet.class, (method, args) -> switch (method) {
                case "next" -> { boolean next = hasRow && !rowRead; rowRead = true; yield next; }
                case "getInt" -> 42;
                case "getString" -> "nombres".equals(args[0]) ? "Ana" : "dato";
                case "getDate" -> null;
                case "wasNull" -> false;
                case "close" -> { resultClosed = true; yield null; }
                default -> null;
            });
            CallableStatement statement = proxy(CallableStatement.class, (method, args) -> {
                if (method.startsWith("set") && args != null && args.length == 2) {
                    parameters.put(args[0], args[1]);
                    return null;
                }
                return switch (method) {
                    case "execute" -> {
                        if (fail) throw new SQLException("Fallo de prueba", "45000");
                        yield !leadingCount && hasRow;
                    }
                    case "executeQuery", "getResultSet" -> result;
                    case "executeUpdate" -> updatedRows;
                    case "getUpdateCount" -> leadingCount && !advanced ? 1 : -1;
                    case "getMoreResults" -> { advanced = true; yield hasRow && !rowRead; }
                    case "close" -> { statementClosed = true; yield null; }
                    default -> null;
                };
            });
            Connection connection = proxy(Connection.class, (method, args) -> switch (method) {
                case "prepareCall", "prepareStatement" -> statement;
                case "close" -> { connectionClosed = true; yield null; }
                default -> null;
            });
            DataSource dataSource = proxy(DataSource.class,
                    (method, args) -> "getConnection".equals(method) ? connection : null);
            return new PacienteDAO(new JdbcTemplate(dataSource));
        }
        void assertClosed() {
            assertTrue(resultClosed);
            assertTrue(statementClosed);
            assertTrue(connectionClosed);
        }
    }

    @FunctionalInterface
    interface Call { Object invoke(String method, Object[] args) throws Throwable; }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, Call call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (self, method, args) -> {
                    if (method.getName().equals("equals")) return self == args[0];
                    if (method.getName().equals("hashCode")) return System.identityHashCode(self);
                    if (method.getName().equals("toString")) return type.getSimpleName() + "Test";
                    Object value = call.invoke(method.getName(), args);
                    if (value == null && method.getReturnType() == boolean.class) return false;
                    if (value == null && method.getReturnType() == int.class) return 0;
                    return value;
                });
    }
}
