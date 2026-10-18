package ui;

import controller.LaboratorioController;

import javax.swing.*;
import java.awt.*;

public class PanelRegistrarPaciente extends JPanel {

    private LaboratorioController controller;
    private JTextField txtDni, txtNombres, txtApellidos, txtFecha, txtTelefono, txtEmail;
    private JButton btnGuardar;
    private JLabel lblStatus;

    public PanelRegistrarPaciente(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        JPanel form = new JPanel(new GridLayout(7,2,6,6));

        txtDni = new JTextField();
        txtNombres = new JTextField();
        txtApellidos = new JTextField();
        txtFecha = new JTextField("YYYY-MM-DD");
        txtTelefono = new JTextField();
        txtEmail = new JTextField();

        btnGuardar = new JButton("Registrar Paciente");
        lblStatus = new JLabel(" ");

        form.add(new JLabel("DNI:")); form.add(txtDni);
        form.add(new JLabel("Nombres:")); form.add(txtNombres);
        form.add(new JLabel("Apellidos:")); form.add(txtApellidos);
        form.add(new JLabel("Fecha Nac (YYYY-MM-DD):")); form.add(txtFecha);
        form.add(new JLabel("Teléfono:")); form.add(txtTelefono);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("")); form.add(btnGuardar);

        add(form, BorderLayout.NORTH);
        add(lblStatus, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> registrarPaciente());
    }

    private void registrarPaciente() {
        try {
            int id = controller.registrarPaciente(
                    txtDni.getText().trim(),
                    txtNombres.getText().trim(),
                    txtApellidos.getText().trim(),
                    txtFecha.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtEmail.getText().trim()
            );
            if (id > 0) {
                lblStatus.setText("Paciente registrado correctamente. ID = " + id);
            } else {
                lblStatus.setText("No se pudo registrar el paciente.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
