package ui;

import controller.LaboratorioController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PanelRegistrarToma extends JPanel {

    private LaboratorioController controller;
    
    // Componentes para Toma Inmediata
    private JTextField txtIdOrden, txtTipoMuestra, txtIdTecnico;
    private JButton btnRegistrarInmediata;

    // Componentes para Toma Programada
    private JTextField txtIdToma, txtFechaHoraReal;
    private JButton btnMarcarRealizada;
    
    private JLabel lblStatus;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PanelRegistrarToma(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        // 1. Layout principal: 2 filas (una para cada sección), 1 columna
        setLayout(new GridLayout(2, 1, 20, 20)); // 20px de espacio vertical entre paneles
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margen exterior

        // --- SECCIÓN 1: TOMA INMEDIATA ---
        JPanel pnlInmediata = new JPanel(new BorderLayout(10, 10));
        // Borde con título para agrupar visualmente
        pnlInmediata.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registrar Nueva Toma (Inmediata)", TitledBorder.LEFT, TitledBorder.TOP));
        
        // Formulario interno
        JPanel formInmediata = new JPanel(new GridLayout(3, 2, 10, 15)); // 3 filas, 2 col, espacios
        txtIdOrden = new JTextField();
        txtTipoMuestra = new JTextField();
        txtIdTecnico = new JTextField();
        
        formInmediata.add(new JLabel("ID Orden:"));
        formInmediata.add(txtIdOrden);
        formInmediata.add(new JLabel("Tipo Muestra:"));
        formInmediata.add(txtTipoMuestra);
        formInmediata.add(new JLabel("ID Técnico:"));
        formInmediata.add(txtIdTecnico);

        // Panel para el botón (para que no se estire)
        JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrarInmediata = new JButton("Registrar Toma");
        btnPanel1.add(btnRegistrarInmediata);

        pnlInmediata.add(formInmediata, BorderLayout.CENTER);
        pnlInmediata.add(btnPanel1, BorderLayout.SOUTH);


        // --- SECCIÓN 2: TOMA PROGRAMADA ---
        JPanel pnlProgramada = new JPanel(new BorderLayout(10, 10));
        pnlProgramada.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Actualizar Toma Programada", TitledBorder.LEFT, TitledBorder.TOP));

        // Formulario interno
        JPanel formProgramada = new JPanel(new GridLayout(2, 2, 10, 15));
        txtIdToma = new JTextField();
        txtFechaHoraReal = new JTextField("YYYY-MM-DD HH:MM"); // Placeholder sugerido
        
        formProgramada.add(new JLabel("ID Toma (Programada):"));
        formProgramada.add(txtIdToma);
        formProgramada.add(new JLabel("Fecha Real (YYYY-MM-DD HH:MM):"));
        formProgramada.add(txtFechaHoraReal);

        // Panel para botón y status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnMarcarRealizada = new JButton("Marcar como REALIZADA");
        lblStatus = new JLabel("Listo.");
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setForeground(Color.BLUE);

        btnPanel2.add(btnMarcarRealizada);
        bottomPanel.add(btnPanel2, BorderLayout.NORTH);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);

        pnlProgramada.add(formProgramada, BorderLayout.CENTER);
        pnlProgramada.add(bottomPanel, BorderLayout.SOUTH);

        // --- AGREGAR PANELES PRINCIPALES ---
        add(pnlInmediata);
        add(pnlProgramada);

        // --- ACTION LISTENERS ---
        btnRegistrarInmediata.addActionListener(e -> registrarInmediata());
        btnMarcarRealizada.addActionListener(e -> marcarRealizada());
    }

    private void registrarInmediata() {
        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            String tipo = txtTipoMuestra.getText().trim();
            int idTecnico = Integer.parseInt(txtIdTecnico.getText().trim());
            
            int idToma = controller.registrarToma(idOrden, tipo, idTecnico);
            
            if (idToma > 0) {
                lblStatus.setText("Exito: Toma registrada con ID " + idToma);
                lblStatus.setForeground(new Color(0, 100, 0)); // Verde oscuro
                limpiarInmediata();
            } else {
                lblStatus.setText("Error: No se pudo registrar.");
                lblStatus.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("Error: Verifique que los ID sean números.");
            lblStatus.setForeground(Color.RED);
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
        }
    }

    private void marcarRealizada() {
        try {
            int idToma = Integer.parseInt(txtIdToma.getText().trim());
            // Nota: Es mejor usar un JSpinner o JCalendar, pero validamos el texto por ahora
            String fechaTexto = txtFechaHoraReal.getText().trim();
            if(fechaTexto.equals("YYYY-MM-DD HH:MM") || fechaTexto.isEmpty()) {
                // Si no escribieron nada, usamos la hora actual automáticamente
                fechaTexto = LocalDateTime.now().format(formatter);
            }
            
            LocalDateTime fechaReal = LocalDateTime.parse(fechaTexto, formatter);
            int res = controller.registrarTomaRealizada(idToma, fechaReal);
            
            if (res > 0) {
                lblStatus.setText("Toma " + res + " marcada como REALIZADA.");
                lblStatus.setForeground(new Color(0, 100, 0));
                limpiarProgramada();
            } else {
                lblStatus.setText("Error al actualizar toma.");
                lblStatus.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error (Formato fecha: YYYY-MM-DD HH:MM): " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
        }
    }
    
    private void limpiarInmediata() {
        txtIdOrden.setText("");
        txtTipoMuestra.setText("");
        txtIdTecnico.setText("");
    }
    
    private void limpiarProgramada() {
        txtIdToma.setText("");
        txtFechaHoraReal.setText(LocalDateTime.now().format(formatter));
    }
}   