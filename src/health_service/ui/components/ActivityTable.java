package health_service.ui.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import health_service.model.HealthService;

public class ActivityTable extends JTable {
    private DefaultTableModel model;
    private HealthService healthService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ActivityTable(HealthService healthService) {
        this.healthService = healthService;

        // Create table model
        model = new DefaultTableModel(
                new String[] { "Date", "Type", "Description" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setModel(model);
        setupTableAppearance();
    }

    private void setupTableAppearance() {
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setGridColor(Color.LIGHT_GRAY);
        setRowHeight(30);
        setFont(new Font("Arial", Font.PLAIN, 14));

        // Setup header
        getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        getTableHeader().setBackground(Color.WHITE);
        getTableHeader().setForeground(Color.BLACK);

        // Setup selection
        setSelectionBackground(new Color(51, 122, 183, 50));
        setSelectionForeground(Color.BLACK);

        // Set column widths
        getColumnModel().getColumn(0).setPreferredWidth(150); // Date
        getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        getColumnModel().getColumn(2).setPreferredWidth(300); // Description
    }

    public void refreshData() {
        model.setRowCount(0);

        // Get recent activities and add to table
        for (String[] activity : healthService.getRecentActivities()) {
            model.addRow(new Object[] {
                    LocalDateTime.parse(activity[0]).format(DATE_FORMATTER),
                    activity[1],
                    activity[2]
            });
        }
    }
}