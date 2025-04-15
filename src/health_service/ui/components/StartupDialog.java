package health_service.ui.components;

import javax.swing.*;
import java.awt.*;

public class StartupDialog extends JDialog {
    private String healthServiceName = null;
    private JTextField nameField;

    public StartupDialog(Frame owner) {
        super(owner, "Welcome to Health Service Management", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Welcome to Health Service Management");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subLabel = new JLabel("Please enter your health service name to begin");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subLabel.setForeground(Color.GRAY);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.putClientProperty("JTextField.placeholderText", "Enter health service name");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 20, 5);
        mainPanel.add(subLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(nameField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton startButton = new JButton("Start");
        JButton exitButton = new JButton("Exit");

        startButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setPreferredSize(new Dimension(100, 30));

        startButton.addActionListener(e -> onStart());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private void onStart() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a health service name",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        healthServiceName = name;
        dispose();
    }

    public String getHealthServiceName() {
        return healthServiceName;
    }
}
