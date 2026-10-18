package ui;

import controller.LaboratorioController;

import javax.swing.*;
import java.awt.*;

public class PanelRegistrarTecnico extends JPanel {

    private LaboratorioController controller;
    private JTextField txtNombres, txtApellidos, txtTelefono;
    private JButton btnGuardar;
    private JLabel lblStatus;

    public PanelRegistrarTecnico(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(5,2,6,6));
        txtNombres = new JTextField();
        txtApellidos = new JTextField();
        txtTelefono = new JTextField();
        btnGuardar = new JButton("Registrar Técnico");
        lblStatus = new JLabel(" ");

        add(new JLabel("Nombres:")); add(txtNombres);
        add(new JLabel("Apellidos:")); add(txtApellidos);
        add(new JLabel("Teléfono:")); add(txtTelefono);
        add(new JLabel("")); add(btnGuardar);
        add(new JLabel("")); add(lblStatus);

        btnGuardar.addActionListener(e -> registrarTecnico());
    }

    private void registrarTecnico() {
        try {
            int id = controller.registrarTecnico(
                    txtNombres.getText().trim(),
                    txtApellidos.getText().trim(),
                    txtTelefono.getText().trim()
            );
            if (id > 0) lblStatus.setText("Técnico registrado ID = " + id);
            else lblStatus.setText("No se pudo registrar el técnico.");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
