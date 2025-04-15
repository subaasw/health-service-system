package health_service.ui.components;

import javax.swing.*;

import health_service.model.*;

import java.awt.*;
import java.util.List;

public class ProcedureDialog extends JDialog {
    private boolean approved = false;
    private final List<Hospital> hospitals;
    private final Procedure existingProcedure;

    private JComboBox<Hospital> hospitalCombo;
    private JTextField nameField;
    private JTextArea descArea;
    private JCheckBox electiveCheck;
    private JSpinner costSpinner;

    public ProcedureDialog(Frame owner, String title, List<Hospital> hospitals, Procedure existingProcedure) {
        super(owner, title, true);
        this.hospitals = hospitals;
        this.existingProcedure = existingProcedure;

        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 450);
        setLocationRelativeTo(getOwner());

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add form components
        addFormComponents(contentPanel);

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Add panels to dialog
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set initial values if editing
        if (existingProcedure != null) {
            populateExistingData();
        }
    }

    private void addFormComponents(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Hospital Selection
        hospitalCombo = new JComboBox<>(hospitals.toArray(new Hospital[0]));
        hospitalCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Hospital hospital = (Hospital) value;
                    setText(hospital.getName());
                }
                return this;
            }
        });

        // Other form fields
        nameField = new JTextField(20);
        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);

        electiveCheck = new JCheckBox("Elective Procedure");
        costSpinner = new JSpinner(new SpinnerNumberModel(100.0, 0.0, 10000.0, 10.0));

        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Hospital:"), gbc);
        gbc.gridx = 1;
        panel.add(hospitalCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        panel.add(costSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(electiveCheck, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void populateExistingData() {
        // Find and select the hospital that contains this procedure
        for (Hospital hospital : hospitals) {
            if (hospital.getProcedures().contains(existingProcedure)) {
                hospitalCombo.setSelectedItem(hospital);
                hospitalCombo.setEnabled(false); // Disable changing hospital when editing
                break;
            }
        }

        nameField.setText(existingProcedure.getName());
        descArea.setText(existingProcedure.getDescription());
        electiveCheck.setSelected(existingProcedure.isElective());
        costSpinner.setValue(existingProcedure.getCost());
    }

    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a name",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hospital selectedHospital = (Hospital) hospitalCombo.getSelectedItem();
        if (selectedHospital == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a hospital",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (existingProcedure == null) {
                // Create new procedure
                Procedure newProcedure = new Procedure(
                        name,
                        descArea.getText().trim(),
                        electiveCheck.isSelected(),
                        (Double) costSpinner.getValue());
                selectedHospital.addProcedure(newProcedure);
            } else {
                // Update existing procedure
                existingProcedure.setName(name);
                existingProcedure.setDescription(descArea.getText().trim());
                existingProcedure.setElective(electiveCheck.isSelected());
                existingProcedure.setCost((Double) costSpinner.getValue());
            }

            approved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving procedure: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean showDialog() {
        setVisible(true);
        return approved;
    }
}