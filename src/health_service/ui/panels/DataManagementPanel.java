package health_service.ui.panels;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import health_service.model.*;
import health_service.ui.MainWindow;

public class DataManagementPanel extends BasePanel {
    private JTextArea dataInfoArea; // Added to show data info
    private JLabel statusLabel;

    public DataManagementPanel(HealthService healthService) {
        super(healthService);
        initComponents();
        updateDataInfo(); // Show initial data
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Data Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton exportButton = createStyledButton("Export Data");
        JButton importButton = createStyledButton("Import Data");

        exportButton.addActionListener(e -> exportData());
        importButton.addActionListener(e -> importData());

        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);

        // Top Panel combining title and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Data Info Area
        dataInfoArea = new JTextArea();
        dataInfoArea.setEditable(false);
        dataInfoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        dataInfoArea.setBackground(new Color(245, 245, 245));
        dataInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(dataInfoArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Assembly
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Health Service Data (*.hsd)", "hsd"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()))) {
                HealthServiceData data = (HealthServiceData) ois.readObject();

                healthService.setName(data.getServiceName());
                healthService.clearAll();

                for (MedicalFacility facility : data.getFacilities()) {
                    healthService.addFacility(facility);
                }

                for (Patient patient : data.getPatients()) {
                    healthService.addPatient(patient);
                }

                statusLabel.setText("✅ Data imported successfully from: " + fileChooser.getSelectedFile().getName());
                statusLabel.setForeground(new Color(0, 150, 0));

                // Update window title
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof MainWindow) {
                    MainWindow mainWindow = (MainWindow) window;
                    mainWindow.setTitle("Health Service Management - " + healthService.getName());
                    mainWindow.refreshAllPanels(); // Refresh all panels
                }

                updateDataInfo();
            } catch (Exception ex) {
                statusLabel.setText("❌ Error importing data: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
            }
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Health Service Data (*.hsd)", "hsd"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".hsd")) {
                file = new File(file.getPath() + ".hsd");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                HealthServiceData data = new HealthServiceData(
                        healthService.getName(),
                        healthService.getPatients(),
                        healthService.getFacilities());
                oos.writeObject(data);
                statusLabel.setText("✅ Data exported successfully to: " + file.getName());
                statusLabel.setForeground(new Color(0, 150, 0));

                // Refresh all panels after export
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof MainWindow) {
                    ((MainWindow) window).refreshAllPanels();
                }

                updateDataInfo();
            } catch (Exception ex) {
                statusLabel.setText("❌ Error exporting data: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
            }
        }
    }

    private void updateDataInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Current System Data:\n");
        info.append("===================\n\n");
        info.append("Health Service: ").append(healthService.getName()).append("\n\n");

        info.append("Patients (").append(healthService.getPatients().size()).append("):\n");
        for (Patient patient : healthService.getPatients()) {
            info.append("- ").append(patient.getName())
                    .append(patient.isPrivate() ? " (Private)" : " (Public)")
                    .append("\n");
        }
        info.append("\n");

        info.append("Facilities (").append(healthService.getFacilities().size()).append("):\n");
        for (MedicalFacility facility : healthService.getFacilities()) {
            if (facility instanceof Hospital) {
                Hospital hospital = (Hospital) facility;
                info.append("- [Hospital] ").append(hospital.getName())
                        .append(" (").append(hospital.getProcedures().size()).append(" procedures)\n");
            } else if (facility instanceof Clinic) {
                Clinic clinic = (Clinic) facility;
                info.append("- [Clinic] ").append(clinic.getName())
                        .append(" (Fee: $").append(String.format("%.2f", clinic.getFee())).append(")\n");
            }
        }

        dataInfoArea.setText(info.toString());
        dataInfoArea.setCaretPosition(0); // Scroll to top
    }

    @Override
    public void refreshData() {
        updateDataInfo();
    }
}