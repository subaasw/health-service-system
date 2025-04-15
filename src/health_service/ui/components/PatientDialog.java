package health_service.ui.components;

import java.awt.*;
import javax.swing.*;

import health_service.model.*;

public class PatientDialog extends JDialog {
    private final Patient patient;
    private final HealthService healthService;
    private JTextField nameField;
    private JCheckBox privateCheck;
    private JComboBox<MedicalFacility> facilityCombo;
    private boolean approved = false;

    public PatientDialog(Frame owner, String title, Patient patient, HealthService healthService) {
        super(owner, title, true);
        this.patient = patient;
        this.healthService = healthService;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create components
        nameField = new JTextField(20);
        privateCheck = new JCheckBox("Private Patient");
        facilityCombo = createFacilityComboBox();

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Facility:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(facilityCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(privateCheck, gbc);

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set initial values if editing
        if (patient != null) {
            nameField.setText(patient.getName());
            privateCheck.setSelected(patient.isPrivate());
            facilityCombo.setSelectedItem(patient.getCurrentFacility());
        }

        pack();
        setLocationRelativeTo(getOwner());
    }

    private JComboBox<MedicalFacility> createFacilityComboBox() {
        JComboBox<MedicalFacility> combo = new JComboBox<>();
        combo.addItem(null); // "No Facility" option

        // Add facilities sorted by type (Hospitals first, then Clinics)
        healthService.getFacilities().stream()
                .filter(f -> f instanceof Hospital)
                .forEach(combo::addItem);

        healthService.getFacilities().stream()
                .filter(f -> f instanceof Clinic)
                .forEach(combo::addItem);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("No Facility");
                } else {
                    MedicalFacility facility = (MedicalFacility) value;
                    String type = facility instanceof Hospital ? "Hospital" : "Clinic";
                    setText(facility.getName() + " (" + type + ")");
                }
                return this;
            }
        });

        return combo;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
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

        MedicalFacility selectedFacility = (MedicalFacility) facilityCombo.getSelectedItem();

        try {
            if (patient == null) {
                // Creating new patient
                Patient newPatient = new Patient(name, privateCheck.isSelected());
                healthService.addPatient(newPatient);

                if (selectedFacility != null) {
                    // Ensure the visit is processed
                    boolean visitResult = selectedFacility.visit(newPatient);
                    if (!visitResult) {
                        // If visit wasn't successful, at least set the facility
                        newPatient.setCurrentFacility(selectedFacility);
                    }
                }
            } else {
                // Updating existing patient
                patient.setName(name);
                patient.setIsPrivate(privateCheck.isSelected());

                // Handle facility change
                if (selectedFacility != patient.getCurrentFacility()) {
                    // Clear current facility first
                    if (patient.getCurrentFacility() != null) {
                        patient.setCurrentFacility(null);
                    }

                    if (selectedFacility != null) {
                        // Process visit to new facility
                        boolean visitResult = selectedFacility.visit(patient);
                        if (!visitResult) {
                            // If visit wasn't successful, at least set the facility
                            patient.setCurrentFacility(selectedFacility);
                        }
                    }
                }
            }

            approved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving patient: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean showDialog() {
        setVisible(true);
        return approved;
    }
}