package health_service.ui.panels;

import java.awt.*;
import javax.swing.*;

import health_service.utils.Colors;

public class FacilitiesPanel extends JPanel {
    private JTable facilityTable;

    public FacilitiesPanel() {
        setBackground(Colors.BACKGROUND);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table model
        String[] columns = { "ID", "Name", "Type", "Capacity", "Status" };
        Object[][] data = {};
        facilityTable = new JTable(data, columns);
        facilityTable.setBackground(Colors.SURFACE);
        facilityTable.setForeground(Colors.TEXT);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(facilityTable);
        scrollPane.setBackground(Colors.SURFACE);
        add(scrollPane, BorderLayout.CENTER);

        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Colors.BACKGROUND);

        JButton addButton = new JButton("Add Facility");
        JButton editButton = new JButton("Edit Facility");
        JButton deleteButton = new JButton("Delete Facility");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.NORTH);
    }
}
