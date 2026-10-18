package ui;

import controller.LaboratorioController;

import javax.swing.*;
import java.awt.*;

public class PanelRegistrarMedico extends JPanel {

    private LaboratorioController controller;
    private JTextField txtNombres, txtApellidos, txtEspecialidad, txtCmp;
    private JButton btnGuardar;
    private JLabel lblStatus;

    public PanelRegistrarMedico(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(6,2,6,6));
        txtNombres = new JTextField();
        txtApellidos = new JTextField();
        txtEspecialidad = new JTextField();
        txtCmp = new JTextField();
        btnGuardar = new JButton("Registrar Médico");
        lblStatus = new JLabel(" ");

        add(new JLabel("Nombres:")); add(txtNombres);
        add(new JLabel("Apellidos:")); add(txtApellidos);
        add(new JLabel("Especialidad:")); add(txtEspecialidad);
        add(new JLabel("CMP:")); add(txtCmp);
        add(new JLabel("")); add(btnGuardar);
        add(new JLabel("")); add(lblStatus);

        btnGuardar.addActionListener(e -> registrarMedico());
    }

    private void registrarMedico() {
        try {
            int id = controller.registrarMedico(
                    txtNombres.getText().trim(),
                    txtApellidos.getText().trim(),
                    txtEspecialidad.getText().trim(),
                    txtCmp.getText().trim()
            );
            if (id > 0) lblStatus.setText("Médico registrado ID = " + id);
            else lblStatus.setText("No se pudo registrar el médico.");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
