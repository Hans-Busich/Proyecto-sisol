package ui;
import controller.LaboratorioController;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PanelReportes extends JPanel {
    private LaboratorioController controller;
    private JButton btnGenerar;
    private JTextArea txtReporte;

    public PanelReportes(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(6,6));
        btnGenerar = new JButton("Generar Reporte Estadístico");
        txtReporte = new JTextArea();
        add(btnGenerar, BorderLayout.NORTH);
        add(new JScrollPane(txtReporte), BorderLayout.CENTER);

        btnGenerar.addActionListener(e -> generar());
    }

    private void generar() {
        try {
            Map<String,Object> rpt = (Map<String,Object>) controller.generarReporte();
            txtReporte.setText("");
            txtReporte.append("Exámenes por tipo:\n");
            List<Map<String,Object>> examenes = (List<Map<String,Object>>) rpt.get("examenes");
            for (Map<String,Object> row : examenes) {
                txtReporte.append(row.get("tipoExamen") + " -> " + row.get("cantidad") + "\n");
            }
            txtReporte.append("\nHoras promedio (orden->resultado): " + rpt.get("horas_promedio") + "\n\n");
            txtReporte.append("Top 5 exámenes:\n");
            List<Map<String,Object>> top = (List<Map<String,Object>>) rpt.get("top5");
            for (Map<String,Object> row : top) {
                txtReporte.append(row.get("tipoExamen") + " -> " + row.get("cnt") + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            txtReporte.setText("Error: " + ex.getMessage());
        }
    }
}
