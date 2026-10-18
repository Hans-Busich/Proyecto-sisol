package ui;

import controller.LaboratorioController;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PanelProgramarToma extends JPanel {

    private LaboratorioController controller;
    private JTextField txtIdOrden, txtTipoMuestra, txtIdTecnico, txtFechaHora;
    private JButton btnProgramar;
    private JLabel lblStatus;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PanelProgramarToma(LaboratorioController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(6,2,6,6));
        txtIdOrden = new JTextField();
        txtTipoMuestra = new JTextField();
        txtIdTecnico = new JTextField();
        txtFechaHora = new JTextField("YYYY-MM-DD HH:MM");
        btnProgramar = new JButton("Programar Toma");
        lblStatus = new JLabel(" ");

        add(new JLabel("ID Orden:")); add(txtIdOrden);
        add(new JLabel("Tipo Muestra:")); add(txtTipoMuestra);
        add(new JLabel("ID Técnico:")); add(txtIdTecnico);
        add(new JLabel("Fecha y Hora (YYYY-MM-DD HH:MM):")); add(txtFechaHora);
        add(new JLabel("")); add(btnProgramar);
        add(new JLabel("")); add(lblStatus);

        btnProgramar.addActionListener(e -> programarToma());
    }

    private void programarToma() {
        try {
            int idOrden = Integer.parseInt(txtIdOrden.getText().trim());
            String tipo = txtTipoMuestra.getText().trim();
            int idTecnico = Integer.parseInt(txtIdTecnico.getText().trim());
            LocalDateTime fecha = LocalDateTime.parse(txtFechaHora.getText().trim(), formatter);

            int idToma = controller.programarToma(idOrden, tipo, fecha, idTecnico);
            if (idToma > 0) lblStatus.setText("Toma programada ID = " + idToma + " para " + fecha.format(formatter));
            else lblStatus.setText("Error al programar la toma.");
        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
