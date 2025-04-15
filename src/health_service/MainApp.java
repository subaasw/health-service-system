package health_service;

import javax.swing.*;

import health_service.model.HealthService;
import health_service.ui.MainWindow;
import health_service.ui.components.StartupDialog;

public class MainApp {
    public static void main(String[] args) {
        try {

            StartupDialog startupDialog = new StartupDialog(null);
            startupDialog.setVisible(true);

            String healthServiceName = startupDialog.getHealthServiceName();
            if (healthServiceName == null) {
                System.exit(0);
            }

            HealthService healthService = new HealthService(healthServiceName);

            SwingUtilities.invokeLater(() -> {
                MainWindow window = new MainWindow(healthService);
                window.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

}