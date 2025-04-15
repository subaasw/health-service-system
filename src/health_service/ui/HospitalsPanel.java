package health_service.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import health_service.model.*;
import health_service.ui.panels.BasePanel;

public class HospitalsPanel extends BasePanel {
    private JTable hospitalsTable;
    private DefaultTableModel hospitalTableModel;
    private JTextField searchField;

    public HospitalsPanel(HealthService healthService) {
        super(healthService);
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Hospitals Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        headerPanel.add(createToolbar(), BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(Color.WHITE);

        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Search hospitals...");
        searchField.addActionListener(e -> filterHospitals());

        JButton addButton = createStyledButton("➕ Add Hospital");
        JButton editButton = createStyledButton("✏️ Edit");
        JButton deleteButton = createStyledButton("🗑️ Delete");

        // Add listeners
        addButton.addActionListener(e -> showAddHospitalDialog());
        editButton.addActionListener(e -> showEditHospitalDialog());
        deleteButton.addActionListener(e -> deleteSelectedHospital());

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
        JScrollPane scrollPane = new JScrollPane(hospitalsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void createTable() {
        hospitalTableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Admission Probability" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2)
                    return Double.class;
                return String.class;
            }
        };

        hospitalsTable = new JTable(hospitalTableModel);
        setupTableAppearance();
    }

    private void setupTableAppearance() {
        hospitalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hospitalsTable.setRowHeight(30);
        hospitalsTable.setShowGrid(true);
        hospitalsTable.setGridColor(Color.LIGHT_GRAY);
        hospitalsTable.getTableHeader().setReorderingAllowed(false);
        hospitalsTable.getTableHeader().setBackground(Color.WHITE);
        hospitalsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Set column renderers and widths
        for (int i = 0; i < hospitalsTable.getColumnCount(); i++) {
            hospitalsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        hospitalsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        hospitalsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        hospitalsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Probability
    }

    private void showAddHospitalDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Hospital", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JSpinner probSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Admission Probability:"), gbc);
        gbc.gridx = 1;
        dialog.add(probSpinner, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Hospital hospital = new Hospital(name, (Double) probSpinner.getValue());
                healthService.addFacility(hospital);
                refreshData();
                selectHospitalById(hospital.getId());
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please enter a name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditHospitalDialog() {
        Hospital hospital = getSelectedHospital();
        if (hospital == null) {
            showWarningMessage("Please select a hospital to edit");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Hospital", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(hospital.getName(), 20);
        JSpinner probSpinner = new JSpinner(new SpinnerNumberModel(
                hospital.getProbAdmit(), 0.0, 1.0, 0.1));

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Admission Probability:"), gbc);
        gbc.gridx = 1;
        dialog.add(probSpinner, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                hospital.setName(name);
                hospital.setProbAdmit((Double) probSpinner.getValue());
                refreshData();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please enter a name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedHospital() {
        Hospital hospital = getSelectedHospital();
        if (hospital == null) {
            showWarningMessage("Please select a hospital to delete");
            return;
        }

        if (confirmDelete(hospital)) {
            healthService.removeFacility(hospital);
            refreshData();
        }
    }

    private Hospital getSelectedHospital() {
        int selectedRow = hospitalsTable.getSelectedRow();
        if (selectedRow == -1)
            return null;

        int hospitalId = (int) hospitalTableModel.getValueAt(selectedRow, 0);
        return healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital && f.getId() == hospitalId)
                .map(f -> (Hospital) f)
                .findFirst()
                .orElse(null);
    }

    private boolean confirmDelete(Hospital hospital) {
        return JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete hospital: " + hospital.getName() + "?",
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

    private void filterHospitals() {
        String searchText = searchField.getText().toLowerCase();
        hospitalTableModel.setRowCount(0);

        healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .map(f -> (Hospital) f)
                .filter(h -> h.getName().toLowerCase().contains(searchText))
                .forEach(this::addHospitalToTable);
    }

    private void selectHospitalById(int hospitalId) {
        for (int i = 0; i < hospitalTableModel.getRowCount(); i++) {
            if ((int) hospitalTableModel.getValueAt(i, 0) == hospitalId) {
                hospitalsTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void addHospitalToTable(Hospital hospital) {
        hospitalTableModel.addRow(new Object[] {
                hospital.getId(),
                hospital.getName(),
                hospital.getProbAdmit()
        });
    }

    @Override
    public void refreshData() {
        int selectedRow = hospitalsTable.getSelectedRow();
        int selectedHospitalId = -1;
        if (selectedRow >= 0) {
            selectedHospitalId = (int) hospitalTableModel.getValueAt(selectedRow, 0);
        }

        hospitalTableModel.setRowCount(0);
        healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .map(f -> (Hospital) f)
                .forEach(this::addHospitalToTable);

        if (selectedHospitalId != -1) {
            selectHospitalById(selectedHospitalId);
        }
    }
}