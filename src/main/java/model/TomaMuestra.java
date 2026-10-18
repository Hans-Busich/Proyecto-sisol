package model;

import java.time.LocalDateTime;

public class TomaMuestra {
    private int idToma;
    private int idOrden;
    private String tipoMuestra;
    private LocalDateTime fechaToma;       // fecha real cuando se realiza
    private LocalDateTime fechaProgramada; // fecha programada (agendada)
    private int idTecnico;
    private String estado; // PROGRAMADA, REALIZADA, CANCELADA, etc.

    public TomaMuestra() {}

    public int getIdToma() { return idToma; }
    public void setIdToma(int idToma) { this.idToma = idToma; }

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public String getTipoMuestra() { return tipoMuestra; }
    public void setTipoMuestra(String tipoMuestra) { this.tipoMuestra = tipoMuestra; }

    public LocalDateTime getFechaToma() { return fechaToma; }
    public void setFechaToma(LocalDateTime fechaToma) { this.fechaToma = fechaToma; }

    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public int getIdTecnico() { return idTecnico; }
    public void setIdTecnico(int idTecnico) { this.idTecnico = idTecnico; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
