package health_service.ui.components;

import javax.swing.*;
import java.awt.*;

public class DashboardWidget extends JPanel {
    private JLabel valueLabel;
    private JLabel titleLabel;
    private JLabel iconLabel;

    public DashboardWidget(String title, String value, String icon) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(new Color(51, 122, 183));

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.BLACK);

        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLabel.setForeground(new Color(51, 122, 183));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void updateValue(String value) {
        valueLabel.setText(value);
    }
}