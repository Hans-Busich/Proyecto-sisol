package service;

import dao.*;
import model.*;
import service.strategy.IReporteStrategy;
import service.strategy.PdfReporteStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class LaboratorioService {

    private final PacienteDAO pacienteDAO;

    public LaboratorioService() {
        this(new PacienteDAO());
    }

    @org.springframework.beans.factory.annotation.Autowired
    public LaboratorioService(PacienteDAO pacienteDAO) {
        this.pacienteDAO = java.util.Objects.requireNonNull(pacienteDAO, "pacienteDAO");
    }
    private MedicoDAO medicoDAO = new MedicoDAO();
    private TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private OrdenDAO ordenDAO = new OrdenDAO();
    private TomaDAO tomaDAO = new TomaDAO();
    private ResultadoDAO resultadoDAO = new ResultadoDAO();
    private NotificationService notifier = new NotificationService();
    
    // Estrategia de Reporte
    private IReporteStrategy reporteStrategy = new PdfReporteStrategy();

    public void setReporteStrategy(IReporteStrategy strategy) {
        this.reporteStrategy = strategy;
    }

    // Delegados simples
    public int registrarPaciente(Paciente p) throws Exception { return pacienteDAO.registrarPaciente(p); }
    public List<Paciente> listarPacientes() { return pacienteDAO.listarPacientes(); }
    public boolean actualizarPaciente(int id, Paciente p) { return pacienteDAO.actualizarPaciente(id, p); }
    public boolean eliminarPaciente(int id) { return pacienteDAO.eliminarPaciente(id); }
    public java.util.Optional<Paciente> obtenerPacientePorId(int id) { return pacienteDAO.obtenerPacientePorId(id); }
    public int registrarMedico(Medico m) throws Exception { return medicoDAO.registrarMedico(m); }
    public int registrarTecnico(Tecnico t) throws Exception { return tecnicoDAO.registrarTecnico(t); }
    public int registrarOrden(int idPaciente, int idMedico, String tipoExamen) throws Exception { return ordenDAO.registrarOrden(idPaciente, idMedico, tipoExamen); }
    public int registrarToma(int idOrden, String tipo, int idTecnico) throws Exception { return tomaDAO.registrarToma(idOrden, tipo, idTecnico); }
    public int registrarResultado(int idOrden, String desc, String vals) throws Exception { return resultadoDAO.registrarResultado(idOrden, desc, vals); }
    public ResultadoLaboratorio obtenerResultadoPorId(int id) throws Exception { return resultadoDAO.obtenerResultadoPorId(id); }

    // --- LÓGICA DE NEGOCIO AVANZADA ---

    // RF-06: Validación con Seguridad y Observer
    public void validarResultado(int idResultado, String validador, String rolUsuario, String emailPaciente) throws Exception {
        if (!"BIOQUIMICO".equalsIgnoreCase(rolUsuario)) {
            throw new SecurityException("Acceso Denegado: Se requiere rol BIOQUIMICO.");
        }
        resultadoDAO.validarResultado(idResultado, validador);
        
        notifier.subscribe(emailPaciente);
        notifier.notifyAllClients("Resultado " + idResultado + " VALIDADO. Ya puede pasar a recogerlo.");
        
        // Generar reporte automático al validar
        generarReportePDF(idResultado);
    }

    // RF-07: Generación de PDF (Strategy)
    public void generarReportePDF(int idResultado) throws Exception {
        ResultadoLaboratorio res = resultadoDAO.obtenerResultadoPorId(idResultado);
        if (res != null) {
            reporteStrategy.generarReporte(res, "Paciente Genérico (ID Orden " + res.getIdOrden() + ")");
        } else {
            throw new Exception("Resultado no encontrado.");
        }
    }

    // RF-03 y RF-04: Tomas
    public int programarToma(int idOrden, String tipo, LocalDateTime fecha, int idTecnico) throws Exception {
        if (fecha.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Fecha debe ser futura.");
        return tomaDAO.programarToma(idOrden, tipo, fecha, idTecnico);
    }

    public int registrarTomaRealizada(int idToma, LocalDateTime fechaReal) throws Exception {
        return tomaDAO.registrarTomaRealizada(idToma, fechaReal);
    }

    public int entregarResultado(int id, String metodo, String por, String obs, String email) throws Exception {
        return resultadoDAO.registrarEntregaResultado(id, metodo, por, obs);
    }

    public List<ResultadoLaboratorio> consultarResultadosPorMedico(int idMedico) throws Exception {
        return resultadoDAO.listarResultadosPorMedico(idMedico);
    }

    public Map<String, Object> generarReporteEstadisticas() throws Exception {
        return ordenDAO.obtenerEstadisticas();
    }
}
