package service.strategy;
import model.ResultadoLaboratorio;

public interface IReporteStrategy {
    void generarReporte(ResultadoLaboratorio resultado, String nombrePaciente);
}