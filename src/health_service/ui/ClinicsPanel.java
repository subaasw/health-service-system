package health_service.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import health_service.model.*;
import health_service.ui.components.ClinicDialog;
import health_service.ui.panels.BasePanel;

public class ClinicsPanel extends BasePanel {
    private JTable clinicsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("#0.0'%'");

    public ClinicsPanel(HealthService healthService) {
        super(healthService);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and toolbar
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main table panel
        add(createTablePanel(), BorderLayout.CENTER);

        // Statistics panel
        add(createStatisticsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Clinics Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Toolbar
        headerPanel.add(createToolbar(), BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(Color.WHITE);

        // Search field
        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Search clinics...");
        searchField.addActionListener(e -> filterClinics());

        // Buttons
        JButton addButton = createStyledButton("➕ Add");
        JButton editButton = createStyledButton("✏️ Edit");
        JButton deleteButton = createStyledButton("🗑️ Delete");

        addButton.addActionListener(e -> showAddClinicDialog());
        editButton.addActionListener(e -> showEditClinicDialog());
        deleteButton.addActionListener(e -> deleteSelectedClinic());

        toolbar.add(searchField);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        return toolbar;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        createTable();
        JScrollPane scrollPane = new JScrollPane(clinicsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Consultation Fee", "Gap Percentage", "Patient Count" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2)
                    return Double.class;
                if (columnIndex == 3)
                    return Double.class;
                if (columnIndex == 4)
                    return Integer.class;
                return String.class;
            }
        };

        clinicsTable = new JTable(tableModel);
        setupTableAppearance();
    }

    private void setupTableAppearance() {
        clinicsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clinicsTable.setRowHeight(30);
        clinicsTable.setShowGrid(true);
        clinicsTable.setGridColor(Color.LIGHT_GRAY);
        clinicsTable.getTableHeader().setReorderingAllowed(false);
        clinicsTable.getTableHeader().setBackground(Color.WHITE);
        clinicsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Cell renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value instanceof Number ? currencyFormat.format(value) : "");
            }
        };
        currencyRenderer.setHorizontalAlignment(JLabel.RIGHT);

        DefaultTableCellRenderer percentRenderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText(value instanceof Number ? percentFormat.format(value) : "");
            }
        };
        percentRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Apply renderers
        clinicsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        clinicsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Name
        clinicsTable.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer); // Fee
        clinicsTable.getColumnModel().getColumn(3).setCellRenderer(percentRenderer); // Gap
        clinicsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Patient Count

        // Set column widths
        clinicsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        clinicsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        clinicsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Fee
        clinicsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Gap
        clinicsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Patient Count
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Create stat labels with placeholder values
        JLabel totalClinicsLabel = new JLabel("Total Clinics: 0");
        JLabel avgFeeLabel = new JLabel("Average Fee: $0.00");
        JLabel avgGapLabel = new JLabel("Average Gap: 0%");

        // Style labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        totalClinicsLabel.setFont(labelFont);
        avgFeeLabel.setFont(labelFont);
        avgGapLabel.setFont(labelFont);

        statsPanel.add(totalClinicsLabel);
        statsPanel.add(avgFeeLabel);
        statsPanel.add(avgGapLabel);

        return statsPanel;
    }

    private void updateStatistics() {
        List<Clinic> clinics = getClinics();

        double avgFee = clinics.stream()
                .mapToDouble(Clinic::getFee)
                .average()
                .orElse(0.0);

        double avgGap = clinics.stream()
                .mapToDouble(Clinic::getGapPercent)
                .average()
                .orElse(0.0);

        // Update statistics labels
        JPanel statsPanel = (JPanel) getComponent(2);
        ((JLabel) statsPanel.getComponent(0)).setText("Total Clinics: " + clinics.size());
        ((JLabel) statsPanel.getComponent(1)).setText("Average Fee: " + currencyFormat.format(avgFee));
        ((JLabel) statsPanel.getComponent(2)).setText("Average Gap: " + percentFormat.format(avgGap));
    }

    private void showAddClinicDialog() {
        ClinicDialog dialog = new ClinicDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add Clinic",
                null);

        if (dialog.showDialog()) {
            Clinic newClinic = dialog.getClinic();
            if (newClinic != null) {
                healthService.addFacility(newClinic);
                refreshData();
            }
        }
    }

    private void showEditClinicDialog() {
        Clinic clinic = getSelectedClinic();
        if (clinic == null)
            return;

        ClinicDialog dialog = new ClinicDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Clinic",
                clinic);

        if (dialog.showDialog()) {
            refreshData();
        }
    }

    private void deleteSelectedClinic() {
        Clinic clinic = getSelectedClinic();
        if (clinic == null)
            return;

        // Check if clinic has patients
        int patientCount = getClinicPatientCount(clinic);
        if (patientCount > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete clinic with active patients.\nPlease transfer or discharge all patients first.",
                    "Cannot Delete",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete clinic: " + clinic.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            healthService.removeFacility(clinic);
            refreshData();
        }
    }

    private List<Clinic> getClinics() {
        return healthService.getFacilities().stream()
                .filter(f -> f instanceof Clinic)
                .map(f -> (Clinic) f)
                .toList();
    }

    private Clinic getSelectedClinic() {
        int selectedRow = clinicsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a clinic",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int clinicId = (int) tableModel.getValueAt(selectedRow, 0);
        return getClinics().stream()
                .filter(c -> c.getId() == clinicId)
                .findFirst()
                .orElse(null);
    }

    private int getClinicPatientCount(Clinic clinic) {
        return (int) healthService.getPatients().stream()
                .filter(p -> p.getCurrentFacility() != null &&
                        p.getCurrentFacility().getId() == clinic.getId())
                .count();
    }

    private void filterClinics() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);

        getClinics().stream()
                .filter(c -> c.getName().toLowerCase().contains(searchText))
                .forEach(this::addClinicToTable);

        updateStatistics();
    }

    private void addClinicToTable(Clinic clinic) {
        tableModel.addRow(new Object[] {
                clinic.getId(),
                clinic.getName(),
                clinic.getFee(),
                clinic.getGapPercent(),
                getClinicPatientCount(clinic)
        });
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);
        getClinics().forEach(this::addClinicToTable);
        updateStatistics();
    }
}