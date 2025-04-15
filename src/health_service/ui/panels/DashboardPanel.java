package health_service.ui.panels;

import java.awt.*;
import javax.swing.*;

import health_service.model.*;
import health_service.ui.components.*;

public class DashboardPanel extends BasePanel {
    private DashboardWidget clinicsWidget;
    private DashboardWidget hospitalsWidget;
    private DashboardWidget patientsWidget;
    private DashboardWidget proceduresWidget;
    private ActivityTable activityTable;
    private JPanel activityPanel;
    private JPanel emptyStatePanel;
    private JScrollPane tableScrollPane;

    public DashboardPanel(HealthService healthService) {
        super(healthService);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);

        JLabel subtitleLabel = new JLabel("Healthcare Management System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.BLACK);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(createStatsPanel(), BorderLayout.NORTH);
        contentPanel.add(createActivityPanel(), BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        clinicsWidget = new DashboardWidget("Active Clinics", "0", "🏥");
        hospitalsWidget = new DashboardWidget("Registered Hospitals", "0", "🏣");
        patientsWidget = new DashboardWidget("Total Patients", "0", "👤");
        proceduresWidget = new DashboardWidget("Total Procedures", "0", "⚕️");

        statsPanel.add(clinicsWidget);
        statsPanel.add(hospitalsWidget);
        statsPanel.add(patientsWidget);
        statsPanel.add(proceduresWidget);

        return statsPanel;
    }

    private JPanel createActivityPanel() {
        activityPanel = new JPanel(new BorderLayout(0, 10));
        activityPanel.setBackground(Color.WHITE);

        // Activity Header
        JLabel activityLabel = new JLabel("Recent Activity");
        activityLabel.setFont(new Font("Arial", Font.BOLD, 18));
        activityLabel.setForeground(Color.BLACK);
        activityPanel.add(activityLabel, BorderLayout.NORTH);

        // Activity Table
        activityTable = new ActivityTable(healthService);
        tableScrollPane = new JScrollPane(activityTable);
        tableScrollPane.setBackground(Color.WHITE);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Empty State Panel
        emptyStatePanel = createEmptyStatePanel();

        // Initially show empty state or table based on data
        updateActivityDisplay();

        return activityPanel;
    }

    private JPanel createEmptyStatePanel() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(40, 20, 40, 20)));

        // Icon
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Messages
        JLabel messageLabel = new JLabel("No Recent Activities");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.DARK_GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subMessageLabel = new JLabel("Activities will appear here as you use the system");
        subMessageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subMessageLabel.setForeground(Color.GRAY);
        subMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly
        emptyPanel.add(Box.createVerticalGlue());
        emptyPanel.add(iconLabel);
        emptyPanel.add(Box.createVerticalStrut(10));
        emptyPanel.add(messageLabel);
        emptyPanel.add(Box.createVerticalStrut(5));
        emptyPanel.add(subMessageLabel);
        emptyPanel.add(Box.createVerticalGlue());

        return emptyPanel;
    }

    private void updateActivityDisplay() {
        // Remove existing components
        activityPanel.remove(tableScrollPane);
        activityPanel.remove(emptyStatePanel);

        // Add appropriate component based on data
        if (healthService.getRecentActivities().isEmpty()) {
            activityPanel.add(emptyStatePanel, BorderLayout.CENTER);
        } else {
            activityPanel.add(tableScrollPane, BorderLayout.CENTER);
        }

        // Refresh the display
        activityPanel.revalidate();
        activityPanel.repaint();
    }

    @Override
    public void refreshData() {
        // Update widgets
        long clinicCount = healthService.getFacilities().stream()
                .filter(f -> f instanceof Clinic)
                .count();
        clinicsWidget.updateValue(String.valueOf(clinicCount));

        long hospitalCount = healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .count();
        hospitalsWidget.updateValue(String.valueOf(hospitalCount));

        int patientCount = healthService.getPatients().size();
        patientsWidget.updateValue(String.valueOf(patientCount));

        long procedureCount = healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .map(f -> (Hospital) f)
                .mapToLong(h -> h.getProcedures().size())
                .sum();
        proceduresWidget.updateValue(String.valueOf(procedureCount));

        // Update activity table and display
        activityTable.refreshData();
        updateActivityDisplay();

        // Refresh the entire panel
        revalidate();
        repaint();
    }

}