package ui;

import javax.swing.*;
import java.awt.*;
import controller.LaboratorioController;

public class MainFrame extends JFrame {

    private LaboratorioController controller;

    public MainFrame() {
        controller = new LaboratorioController();
        initUI();
    }

    private void initUI() {
        setTitle("BioSalud - Sistema de Laboratorio");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Pacientes", new PanelRegistrarPaciente(controller));
        tabs.addTab("Médicos", new PanelRegistrarMedico(controller));
        tabs.addTab("Técnicos", new PanelRegistrarTecnico(controller));
        tabs.addTab("Órdenes", new PanelRegistrarOrden(controller));
        tabs.addTab("Tomas", new PanelRegistrarToma(controller));
        tabs.addTab("Resultados", new PanelRegistrarResultado(controller));
        tabs.addTab("Entregas", new PanelEntregarResultado(controller));
        tabs.addTab("Consulta Médico", new PanelConsultaMedico(controller));
        tabs.addTab("Reportes", new PanelReportes(controller));
        tabs.addTab("Programar Toma", new PanelProgramarToma(controller));



        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Opcional: establecer Look & Feel del sistema
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}