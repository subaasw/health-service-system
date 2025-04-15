package health_service.ui.components;

import javax.swing.*;
import java.awt.*;
import health_service.model.Clinic;

public class ClinicDialog extends JDialog {
    private boolean approved = false;
    private final Clinic clinic;
    private JTextField nameField;
    private JSpinner feeSpinner;
    private JSpinner gapSpinner;

    public ClinicDialog(Frame owner, String title, Clinic clinic) {
        super(owner, title, true);
        this.clinic = clinic;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize components
        nameField = new JTextField(20);
        feeSpinner = new JSpinner(new SpinnerNumberModel(50.0, 0.0, 1000.0, 5.0));
        gapSpinner = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 100.0, 1.0));

        // Set values if editing
        if (clinic != null) {
            nameField.setText(clinic.getName());
            feeSpinner.setValue(clinic.getFee());
            gapSpinner.setValue(clinic.getGapPercent());
        }

        // Layout components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Consultation Fee:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(feeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Gap Percentage:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(gapSpinner, gbc);

        // Buttons
        JPanel buttonPanel = createButtonPanel();

        // Assembly
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

        Double fee = (Double) feeSpinner.getValue();
        Double gapPercent = (Double) gapSpinner.getValue();

        try {
            if (clinic != null) {
                // Update existing clinic
                clinic.setName(name);
                clinic.setFee(fee);
                clinic.setGapPercent(gapPercent);
            }
            approved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid values: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public Clinic getClinic() {
        if (!approved)
            return null;
        if (clinic != null)
            return clinic;
        return new Clinic(
                nameField.getText().trim(),
                (Double) feeSpinner.getValue(),
                (Double) gapSpinner.getValue());
    }

    public boolean showDialog() {
        setVisible(true);
        return approved;
    }
}
