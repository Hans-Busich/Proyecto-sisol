package model;

import java.time.LocalDateTime;

public class ResultadoLaboratorio {
    private int idResultado;
    private int idOrden;
    private LocalDateTime fechaResultado;
    private String descripcion;
    private String valores;
    private String conclusiones;
    private boolean validado;

    public ResultadoLaboratorio() {}

    public int getIdResultado() { return idResultado; }
    public void setIdResultado(int idResultado) { this.idResultado = idResultado; }

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public LocalDateTime getFechaResultado() { return fechaResultado; }
    public void setFechaResultado(LocalDateTime fechaResultado) { this.fechaResultado = fechaResultado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getValores() { return valores; }
    public void setValores(String valores) { this.valores = valores; }

    public String getConclusiones() { return conclusiones; }
    public void setConclusiones(String conclusiones) { this.conclusiones = conclusiones; }

    public boolean isValidado() { return validado; }
    public void setValidado(boolean validado) { this.validado = validado; }
}
