package health_service.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import health_service.model.*;
import health_service.ui.components.PatientDialog;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

public class PatientsPanel extends BasePanel {
    private JTable patientsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public PatientsPanel(HealthService healthService) {
        super(healthService);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Table Panel
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Patients Management");
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
        searchField.putClientProperty("JTextField.placeholderText", "Search patients...");
        searchField.addActionListener(e -> filterPatients());

        // Buttons
        JButton addButton = createStyledButton("➕ Add Patient");
        JButton editButton = createStyledButton("✏️ Edit");
        JButton deleteButton = createStyledButton("🗑️ Delete");

        // Add listeners
        addButton.addActionListener(e -> showAddPatientDialog());
        editButton.addActionListener(e -> showEditPatientDialog());
        deleteButton.addActionListener(e -> deleteSelectedPatient());

        // Add components
        toolbar.add(searchField);
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
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Private Patient", "Current Facility", "Balance" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2)
                    return Boolean.class;
                if (columnIndex == 4)
                    return Double.class;
                return String.class;
            }
        };

        patientsTable = new JTable(tableModel);
        setupTableAppearance();
    }

    private void setupTableAppearance() {
        patientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsTable.setRowHeight(30);
        patientsTable.setShowGrid(true);
        patientsTable.setGridColor(Color.LIGHT_GRAY);
        patientsTable.getTableHeader().setReorderingAllowed(false);
        patientsTable.getTableHeader().setBackground(Color.WHITE);
        patientsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

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

        // Apply renderers
        for (int i = 0; i < patientsTable.getColumnCount(); i++) {
            if (i == 4) { // Balance column
                patientsTable.getColumnModel().getColumn(i).setCellRenderer(currencyRenderer);
            } else {
                patientsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Set column widths
        patientsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        patientsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        patientsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Private
        patientsTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Facility
        patientsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Balance
    }

    private void showAddPatientDialog() {
        PatientDialog dialog = new PatientDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add Patient",
                null,
                healthService);

        if (dialog.showDialog()) {
            refreshData();

        }
    }

    private void showEditPatientDialog() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Please select a patient to edit");
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        Patient patient = findPatientById(patientId);

        if (patient != null) {
            PatientDialog dialog = new PatientDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Edit Patient",
                    patient,
                    healthService);

            if (dialog.showDialog()) {
                refreshData();
            }
        }
    }

    private void deleteSelectedPatient() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Please select a patient to delete");
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        Patient patient = findPatientById(patientId);

        if (patient != null && confirmDelete(patient)) {
            healthService.removePatient(patient);
            refreshData();
        }
    }

    private Patient findPatientById(int id) {
        return healthService.getPatients().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private boolean confirmDelete(Patient patient) {
        return JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete patient: " + patient.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    private void filterPatients() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);

        healthService.getPatients().stream()
                .filter(patient -> patient.getName().toLowerCase().contains(searchText))
                .forEach(this::addPatientToTable);
    }

    private void addPatientToTable(Patient patient) {
        tableModel.addRow(new Object[] {
                patient.getId(),
                patient.getName(),
                patient.isPrivate(),
                patient.getCurrentFacility() != null ? patient.getCurrentFacility().getName() : "None",
                patient.getBalance()
        });
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);
        healthService.getPatients().forEach(this::addPatientToTable);
    }
}