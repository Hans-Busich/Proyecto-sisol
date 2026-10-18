package ui;

import controller.LaboratorioController;

import javax.swing.*;
import java.awt.*;

public class PanelRegistrarOrden extends JPanel {

    private LaboratorioController controller;
    private JTextField txtIdPaciente, txtIdMedico, txtTipoExamen;
    private JButton btnGuardar;
    private JLabel lblStatus;

    public PanelRegistrarOrden(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(5,2,6,6));
        txtIdPaciente = new JTextField();
        txtIdMedico = new JTextField();
        txtTipoExamen = new JTextField();
        btnGuardar = new JButton("Registrar Orden");
        lblStatus = new JLabel(" ");

        add(new JLabel("ID Paciente:")); add(txtIdPaciente);
        add(new JLabel("ID Médico:")); add(txtIdMedico);
        add(new JLabel("Tipo de Examen:")); add(txtTipoExamen);
        add(new JLabel("")); add(btnGuardar);
        add(new JLabel("")); add(lblStatus);

        btnGuardar.addActionListener(e -> registrarOrden());
    }

    private void registrarOrden() {
        try {
            int idOrden = controller.registrarOrden(
                    Integer.parseInt(txtIdPaciente.getText().trim()),
                    Integer.parseInt(txtIdMedico.getText().trim()),
                    txtTipoExamen.getText().trim()
            );
            if (idOrden > 0) lblStatus.setText("Orden registrada ID = " + idOrden);
            else lblStatus.setText("No se pudo registrar la orden.");
        } catch (NumberFormatException nfe) {
            lblStatus.setText("IDs deben ser números válidos.");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
