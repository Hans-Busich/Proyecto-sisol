package ui;

import controller.LaboratorioController;
import javax.swing.*;
import java.awt.*;

public class PanelRegistrarResultado extends JPanel {

    private LaboratorioController controller;
    
    // Componentes gráficos
    private JTextField txtIdOrden;
    private JTextField txtDescripcion;
    private JTextField txtValores;
    
    // Campos para validación
    private JTextField txtIdResultado;
    private JTextField txtValidador;
    private JTextField txtEmailPaciente;
    
    // Botones
    private JButton btnGuardar;
    private JButton btnValidar;
    private JLabel lblStatus;

    public PanelRegistrarResultado(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        // Layout: 9 filas, 2 columnas, espaciado de 6px
        setLayout(new GridLayout(9, 2, 6, 6));

        txtIdOrden = new JTextField();
        txtDescripcion = new JTextField();
        txtValores = new JTextField();
        
        // Inicialización de campos de validación
        txtEmailPaciente = new JTextField();
        txtIdResultado = new JTextField();
        txtValidador = new JTextField();

        btnGuardar = new JButton("Registrar Resultado");
        btnValidar = new JButton("Validar y Notificar");
        lblStatus = new JLabel(" ");

        // --- Sección Registro ---
        add(new JLabel("ID Orden:"));
        add(txtIdOrden);
        
        add(new JLabel("Descripción:"));
        add(txtDescripcion);
        
        add(new JLabel("Valores (texto/JSON):"));
        add(txtValores);
        
        add(new JLabel("Email Paciente (notificación):"));
        add(txtEmailPaciente);
        
        add(new JLabel("")); 
        add(btnGuardar);

        // --- Sección Validación ---
        add(new JLabel("ID Resultado (para validar):"));
        add(txtIdResultado);
        
        add(new JLabel("Nombre Validador (Bioquímico):"));
        add(txtValidador);
        
        add(new JLabel("")); 
        add(btnValidar);
        
        add(new JLabel("Estado:"));
        add(lblStatus);

        // Listeners
        btnGuardar.addActionListener(e -> registrarResultado());
        btnValidar.addActionListener(e -> validarResultado());
    }

    private void registrarResultado() {
        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            String desc = txtDescripcion.getText().trim();
            String vals = txtValores.getText().trim();

            int idResultado = controller.registrarResultado(idOrden, desc, vals);

            if (idResultado > 0) {
                // Auto-rellenar el campo de ID para facilitar la validación inmediata
                txtIdResultado.setText(String.valueOf(idResultado));
                lblStatus.setText("Resultado registrado con éxito. ID = " + idResultado);
                lblStatus.setForeground(new Color(0, 100, 0)); // Color verde oscuro
            } else {
                lblStatus.setText("Error: No se pudo registrar el resultado.");
                lblStatus.setForeground(Color.RED);
            }
        } catch (NumberFormatException nfe) {
            lblStatus.setText("Error: El ID de Orden debe ser un número.");
            lblStatus.setForeground(Color.RED);
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
        }
    }

    private void validarResultado() {
        try {
            int idRes = Integer.parseInt(txtIdResultado.getText().trim());
            String validador = txtValidador.getText().trim();
            String email = txtEmailPaciente.getText().trim();

            // Rol simulado para probar seguridad
            String rolSimulado = "BIOQUIMICO"; 

            controller.validarResultado(idRes, validador, rolSimulado, email);

            lblStatus.setText("Validado y PDF generado.");
            lblStatus.setForeground(new Color(0, 100, 0));
        } catch (SecurityException se) {
            lblStatus.setText("Acceso Denegado.");
            JOptionPane.showMessageDialog(this, se.getMessage(), "Error Seguridad", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}