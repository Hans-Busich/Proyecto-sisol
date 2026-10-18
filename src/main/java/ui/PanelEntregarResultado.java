package ui;

import controller.LaboratorioController;
import model.ResultadoLaboratorio;
import javax.swing.*;
import java.awt.*;

public class PanelEntregarResultado extends JPanel {

    private LaboratorioController controller;
    
    private JTextField txtIdResultado;
    private JTextArea txtDetalle;
    private JTextField txtEntregadoPor;
    private JTextField txtObservaciones;
    private JComboBox<String> cmbMetodo;
    private JButton btnBuscar, btnEntregar, btnGenerarPDF;
    private JLabel lblStatus;
    
    private ResultadoLaboratorio resultadoActual;

    public PanelEntregarResultado(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Búsqueda ---
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtIdResultado = new JTextField(10);
        btnBuscar = new JButton("Buscar Resultado");
        pnlTop.add(new JLabel("ID Resultado:"));
        pnlTop.add(txtIdResultado);
        pnlTop.add(btnBuscar);
        
        add(pnlTop, BorderLayout.NORTH);

        // --- Panel Central: Detalles y Formulario ---
        JPanel pnlCenter = new JPanel(new GridLayout(2, 1));
        
        txtDetalle = new JTextArea();
        txtDetalle.setEditable(false);
        txtDetalle.setBorder(BorderFactory.createTitledBorder("Detalle del Resultado"));
        pnlCenter.add(new JScrollPane(txtDetalle));

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos de Entrega"));
        
        cmbMetodo = new JComboBox<>(new String[]{"PRESENCIAL", "CORREO", "WEB"});
        txtEntregadoPor = new JTextField();
        txtObservaciones = new JTextField();
        
        pnlForm.add(new JLabel("Método de Entrega:"));
        pnlForm.add(cmbMetodo);
        pnlForm.add(new JLabel("Entregado Por (Usuario):"));
        pnlForm.add(txtEntregadoPor);
        pnlForm.add(new JLabel("Observaciones:"));
        pnlForm.add(txtObservaciones);
        
        pnlCenter.add(pnlForm);
        add(pnlCenter, BorderLayout.CENTER);

        // --- Panel Inferior: Botones de Acción ---
        JPanel pnlBot = new JPanel(new FlowLayout());
        btnEntregar = new JButton("Confirmar Entrega");
        btnGenerarPDF = new JButton("Generar PDF");
        lblStatus = new JLabel(" ");
        
        btnEntregar.setEnabled(false); 
        btnGenerarPDF.setEnabled(false);

        pnlBot.add(btnEntregar);
        pnlBot.add(btnGenerarPDF);
        pnlBot.add(lblStatus);
        
        add(pnlBot, BorderLayout.SOUTH);

        // --- Eventos ---
        btnBuscar.addActionListener(e -> buscarResultado());
        btnEntregar.addActionListener(e -> entregarResultado());
        btnGenerarPDF.addActionListener(e -> generarPDF());
    }

    private void buscarResultado() {
        try {
            int id = Integer.parseInt(txtIdResultado.getText().trim());
            resultadoActual = controller.obtenerResultado(id);

            if (resultadoActual != null) {
                txtDetalle.setText("ID: " + resultadoActual.getIdResultado() + "\n" +
                                   "Descripción: " + resultadoActual.getDescripcion() + "\n" +
                                   "Validado: " + (resultadoActual.isValidado() ? "SÍ" : "NO"));
                
                // Solo permitimos entregar si está validado
                if (resultadoActual.isValidado()) {
                    btnEntregar.setEnabled(true);
                    btnGenerarPDF.setEnabled(true);
                    lblStatus.setText("Resultado encontrado.");
                    lblStatus.setForeground(Color.BLUE);
                } else {
                    btnEntregar.setEnabled(false);
                    btnGenerarPDF.setEnabled(false);
                    lblStatus.setText("El resultado existe pero NO está validado aún.");
                    lblStatus.setForeground(Color.RED);
                }
            } else {
                lblStatus.setText("Resultado no encontrado.");
                txtDetalle.setText("");
                btnEntregar.setEnabled(false);
            }
        } catch (NumberFormatException ex) {
            lblStatus.setText("El ID debe ser numérico.");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }

    private void entregarResultado() {
        try {
            if (resultadoActual == null) return;
            
            controller.entregarResultado(
                resultadoActual.getIdResultado(),
                (String) cmbMetodo.getSelectedItem(),
                txtEntregadoPor.getText(),
                txtObservaciones.getText(),
                "paciente@email.com" 
            );
            
            lblStatus.setText("Entrega registrada correctamente.");
            lblStatus.setForeground(new Color(0, 100, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error al entregar: " + ex.getMessage());
        }
    }

    private void generarPDF() {
        try {
            if (resultadoActual == null) return;
            
            controller.generarPDFResultado(resultadoActual.getIdResultado());
            
            lblStatus.setText("PDF generado correctamente en la carpeta del proyecto.");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error generando PDF: " + ex.getMessage());
        }
    }
}