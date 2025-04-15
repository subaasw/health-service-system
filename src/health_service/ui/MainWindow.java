package health_service.ui;

import java.awt.*;
import javax.swing.*;

import health_service.model.HealthService;
import health_service.ui.components.Sidebar;
import health_service.ui.panels.*;

public class MainWindow extends JFrame {
    private HealthService healthService;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Sidebar sidebar;

    public MainWindow(HealthService healthService) {
        this.healthService = healthService;
        setTitle("Health Service Management - " + healthService.getName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Create sidebar
        sidebar = new Sidebar(this);
        add(sidebar, BorderLayout.WEST);

        // Create content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Add panels
        contentPanel.add(new DashboardPanel(healthService), "DASHBOARD");
        contentPanel.add(new PatientsPanel(healthService), "PATIENTS");
        contentPanel.add(new HospitalsPanel(healthService), "HOSPITALS");
        contentPanel.add(new ClinicsPanel(healthService), "CLINICS");
        contentPanel.add(new ProceduresPanel(healthService), "PROCEDURES");
        contentPanel.add(new DataManagementPanel(healthService), "DATA_MANAGEMENT");

        add(contentPanel, BorderLayout.CENTER);
    }

    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
        if (name.equals("DASHBOARD")) {
            refreshDashboard();
        }
        refreshAllPanels();
    }

    public void refreshDashboard() {
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof DashboardPanel) {
                ((DashboardPanel) comp).refreshData();
                break;
            }
        }
    }

    public HealthService getHealthService() {
        return healthService;
    }

    public void refreshAllPanels() {
        for (Component component : contentPanel.getComponents()) {
            if (component instanceof BasePanel) {
                ((BasePanel) component).refreshData();
            }
        }
    }
}
