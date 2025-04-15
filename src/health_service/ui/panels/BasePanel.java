package health_service.ui.panels;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

import health_service.model.HealthService;
import health_service.ui.RefreshablePanel;
import health_service.utils.Colors;

public abstract class BasePanel extends JPanel implements RefreshablePanel {
    protected final HealthService healthService;

    public BasePanel(HealthService healthService) {
        this.healthService = healthService;
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    protected JLabel createTitleLabel(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Colors.TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return titleLabel;
    }

    protected JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Colors.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new LineBorder(Colors.PRIMARY_DARK, 1));
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Colors.PRIMARY_DARK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Colors.PRIMARY);
            }
        });

        return button;
    }

    protected JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        return panel;
    }

    public abstract void refreshData();
}