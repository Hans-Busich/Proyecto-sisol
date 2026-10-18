package controller;

import model.*;
import service.LaboratorioService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class LaboratorioController {

    private final LaboratorioService service;

    public LaboratorioController() {
        this(new LaboratorioService());
    }

    public LaboratorioController(LaboratorioService service) {
        this.service = java.util.Objects.requireNonNull(service, "service");
    }

    public int registrarPaciente(String dni, String nombres, String apellidos, String fechaNacimiento, String telefono, String email) throws Exception {
        Paciente p = new Paciente(dni, nombres, apellidos, java.time.LocalDate.parse(fechaNacimiento), telefono, email);
        return service.registrarPaciente(p);
    }

    public int registrarMedico(String nombres, String apellidos, String especialidad, String cmp) throws Exception {
        Medico m = new Medico(nombres, apellidos, especialidad, cmp);
        return service.registrarMedico(m);
    }

    public int registrarTecnico(String nombres, String apellidos, String telefono) throws Exception {
        // Corrección constructor
        Tecnico t = new Tecnico();
        t.setNombres(nombres);
        t.setApellidos(apellidos);
        t.setTelefono(telefono);
        t.setEspecialidad("General");
        return service.registrarTecnico(t);
    }

    public int registrarOrden(int idPaciente, int idMedico, String tipoExamen) throws Exception {
        return service.registrarOrden(idPaciente, idMedico, tipoExamen);
    }

    public int registrarToma(int idOrden, String tipoMuestra, int idTecnico) throws Exception {
        return service.registrarToma(idOrden, tipoMuestra, idTecnico);
    }
    
    public int programarToma(int idOrden, String tipo, LocalDateTime fecha, int idTecnico) throws Exception {
        return service.programarToma(idOrden, tipo, fecha, idTecnico);
    }
    
    public int registrarTomaRealizada(int idToma, LocalDateTime fechaReal) throws Exception {
        return service.registrarTomaRealizada(idToma, fechaReal);
    }

    public int registrarResultado(int idOrden, String descripcion, String valores) throws Exception {
        return service.registrarResultado(idOrden, descripcion, valores);
    }

    public void validarResultado(int idResultado, String validador, String rolUsuario, String emailPaciente) throws Exception {
        service.validarResultado(idResultado, validador, rolUsuario, emailPaciente);
    }

    public ResultadoLaboratorio obtenerResultado(int idResultado) throws Exception {
        return service.obtenerResultadoPorId(idResultado);
    }

    public int entregarResultado(int idResultado, String metodo, String entregadoPor, String observaciones, String emailPaciente) throws Exception {
        return service.entregarResultado(idResultado, metodo, entregadoPor, observaciones, emailPaciente);
    }
    
    public void generarPDFResultado(int idResultado) throws Exception {
        service.generarReportePDF(idResultado);
    }

    public List<ResultadoLaboratorio> consultarResultadosMedico(int idMedico) throws Exception {
        return service.consultarResultadosPorMedico(idMedico);
    }

    public Map<String,Object> generarReporte() throws Exception {
        return service.generarReporteEstadisticas();
    }
}
