package service.strategy;

import model.ResultadoLaboratorio;
import java.io.FileWriter;
import java.io.IOException;

// Estrategia Concreta: Simulación de generación de PDF
public class PdfReporteStrategy implements IReporteStrategy {
    @Override
    public void generarReporte(ResultadoLaboratorio resultado, String nombrePaciente) {
        System.out.println("--- INICIANDO ESTRATEGIA PDF ---");
        // Aquí iría la librería iText o similar. Simulamos creando un archivo de texto.
        String contenido = "CLÍNICA BIOSALUD\n" +
                           "Paciente: " + nombrePaciente + "\n" +
                           "Resultado ID: " + resultado.getIdResultado() + "\n" +
                           "Conclusiones: " + resultado.getConclusiones() + "\n" +
                           "Validado: " + (resultado.isValidado() ? "SI" : "NO");
        
        try (FileWriter writer = new FileWriter("Reporte_" + resultado.getIdResultado() + ".txt")) {
            writer.write(contenido);
            System.out.println("Archivo PDF (simulado) generado correctamente para " + nombrePaciente);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}