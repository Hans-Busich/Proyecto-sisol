package ui;
import controller.LaboratorioController;
import model.ResultadoLaboratorio;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelConsultaMedico extends JPanel {
    private LaboratorioController controller;
    private JTextField txtIdMedico;
    private JButton btnBuscar;
    private JTextArea txtResultados;
    private JLabel lblStatus;

    public PanelConsultaMedico(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(6,6));
        JPanel top = new JPanel(new FlowLayout());
        txtIdMedico = new JTextField(6);
        btnBuscar = new JButton("Consultar Resultados");
        top.add(new JLabel("ID Médico:")); top.add(txtIdMedico); top.add(btnBuscar);
        add(top, BorderLayout.NORTH);

        txtResultados = new JTextArea();
        add(new JScrollPane(txtResultados), BorderLayout.CENTER);
        lblStatus = new JLabel(" ");
        add(lblStatus, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> consultar());
    }

    private void consultar() {
        try {
            int idMed = Integer.parseInt(txtIdMedico.getText().trim());
            List<ResultadoLaboratorio> lista = (List<ResultadoLaboratorio>) controller.consultarResultadosMedico(idMed);
            txtResultados.setText("");
            for (ResultadoLaboratorio r : lista) {
                txtResultados.append("ID: " + r.getIdResultado() + " | Orden: " + r.getIdOrden() + " | Validado: " + r.isValidado() + "\n");
                txtResultados.append("Descripcion: " + r.getDescripcion() + "\n");
                txtResultados.append("Valores: " + r.getValores() + "\n\n");
            }
            lblStatus.setText("Resultados: " + lista.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
