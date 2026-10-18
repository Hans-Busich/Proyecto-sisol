package model;

import java.time.LocalDateTime;

public class OrdenLaboratorio {
    // Enum para controlar el flujo de estados
    public enum EstadoOrden {
        PENDIENTE,
        EN_PROCESO,
        COMPLETADO, // Resultado validado y/o entregado
        ENTREGADO
    }

    private int idOrden;
    private LocalDateTime fechaOrden;
    private int idPaciente;
    private int idMedico;
    private String tipoExamen;
    private String observaciones;
    private EstadoOrden estado;

    public OrdenLaboratorio() {}

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public LocalDateTime getFechaOrden() { return fechaOrden; }
    public void setFechaOrden(LocalDateTime fechaOrden) { this.fechaOrden = fechaOrden; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getTipoExamen() { return tipoExamen; }
    public void setTipoExamen(String tipoExamen) { this.tipoExamen = tipoExamen; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    
    // Método auxiliar para convertir String de BD a Enum
    public void setEstadoDesdeString(String estadoStr) {
        try {
            if (estadoStr == null) {
                this.estado = EstadoOrden.PENDIENTE;
            } else {
                // Mapeo simple, ajusta según los strings exactos de tu BD
                if (estadoStr.equalsIgnoreCase("Registrada")) this.estado = EstadoOrden.PENDIENTE;
                else this.estado = EstadoOrden.valueOf(estadoStr.toUpperCase());
            }
        } catch (Exception e) {
            this.estado = EstadoOrden.PENDIENTE;
        }
    }
}