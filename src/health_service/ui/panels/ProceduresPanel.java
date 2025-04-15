package health_service.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import health_service.model.*;
import health_service.ui.components.ProcedureDialog;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class ProceduresPanel extends BasePanel {
    private JTable proceduresTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    public ProceduresPanel(HealthService healthService) {
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
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Procedures Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(titleLabel, BorderLayout.WEST);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setBackground(Color.WHITE);

        // Search field
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search procedures...");
        searchField.addActionListener(e -> filterProcedures());

        // Buttons
        JButton addButton = createStyledButton("➕ Add");
        JButton editButton = createStyledButton("✏️ Edit");
        JButton deleteButton = createStyledButton("🗑️ Delete");

        addButton.addActionListener(e -> showAddProcedureDialog());
        editButton.addActionListener(e -> showEditProcedureDialog());
        deleteButton.addActionListener(e -> deleteSelectedProcedure());

        toolbar.add(searchField);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        header.add(toolbar, BorderLayout.EAST);
        return header;
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
        JScrollPane scrollPane = new JScrollPane(proceduresTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new String[] { "ID", "Hospital", "Name", "Description", "Elective", "Cost" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 4 -> Boolean.class;
                    case 5 -> Double.class;
                    default -> String.class;
                };
            }
        };

        proceduresTable = new JTable(tableModel);
        setupTableAppearance();
    }

    private void setupTableAppearance() {
        proceduresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proceduresTable.setRowHeight(30);
        proceduresTable.setShowGrid(true);
        proceduresTable.setGridColor(Color.LIGHT_GRAY);
        proceduresTable.getTableHeader().setReorderingAllowed(false);
        proceduresTable.getTableHeader().setBackground(Color.WHITE);
        proceduresTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

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
        for (int i = 0; i < proceduresTable.getColumnCount(); i++) {
            if (i == 5) { // Cost column
                proceduresTable.getColumnModel().getColumn(i).setCellRenderer(currencyRenderer);
            } else {
                proceduresTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Set column widths
        proceduresTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        proceduresTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Hospital
        proceduresTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        proceduresTable.getColumnModel().getColumn(3).setPreferredWidth(250); // Description
        proceduresTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Elective
        proceduresTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Cost
    }

    private void showAddProcedureDialog() {
        List<Hospital> hospitals = getAvailableHospitals();
        if (hospitals.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hospitals available. Please add a hospital first.",
                    "No Hospitals",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProcedureDialog dialog = new ProcedureDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add Procedure",
                hospitals,
                null);

        if (dialog.showDialog()) {
            refreshData();
        }
    }

    private void showEditProcedureDialog() {
        int selectedRow = proceduresTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a procedure to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int procedureId = (int) tableModel.getValueAt(selectedRow, 0);
        Hospital hospital = findHospitalByProcedureId(procedureId);
        if (hospital == null)
            return;

        Procedure procedure = findProcedureById(hospital, procedureId);
        if (procedure == null)
            return;

        ProcedureDialog dialog = new ProcedureDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Procedure",
                getAvailableHospitals(),
                procedure);

        if (dialog.showDialog()) {
            refreshData();
        }
    }

    private void deleteSelectedProcedure() {
        int selectedRow = proceduresTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a procedure to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int procedureId = (int) tableModel.getValueAt(selectedRow, 0);
        Hospital hospital = findHospitalByProcedureId(procedureId);
        if (hospital == null)
            return;

        Procedure procedure = findProcedureById(hospital, procedureId);
        if (procedure == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete procedure: " + procedure.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            hospital.getProcedures().remove(procedure);
            refreshData();
        }
    }

    private List<Hospital> getAvailableHospitals() {
        return healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .map(f -> (Hospital) f)
                .toList();
    }

    private Hospital findHospitalByProcedureId(int procedureId) {
        return healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .map(f -> (Hospital) f)
                .filter(h -> h.getProcedures().stream()
                        .anyMatch(p -> p.getId() == procedureId))
                .findFirst()
                .orElse(null);
    }

    private Procedure findProcedureById(Hospital hospital, int procedureId) {
        return hospital.getProcedures().stream()
                .filter(p -> p.getId() == procedureId)
                .findFirst()
                .orElse(null);
    }

    private void filterProcedures() {
        String searchText = searchField.getText().toLowerCase();
        refreshData(); // Reset table

        if (!searchText.isEmpty()) {
            for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
                String name = (String) tableModel.getValueAt(i, 2);
                String desc = (String) tableModel.getValueAt(i, 3);
                if (!name.toLowerCase().contains(searchText) &&
                        !desc.toLowerCase().contains(searchText)) {
                    tableModel.removeRow(i);
                }
            }
        }
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);

        for (Hospital hospital : getAvailableHospitals()) {
            for (Procedure procedure : hospital.getProcedures()) {
                tableModel.addRow(new Object[] {
                        procedure.getId(),
                        hospital.getName(),
                        procedure.getName(),
                        procedure.getDescription(),
                        procedure.isElective(),
                        procedure.getCost()
                });
            }
        }
    }
}
