package health_service.ui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import health_service.ui.MainWindow;

public class Sidebar extends JPanel {
    private MainWindow mainWindow;
    private static final int WIDTH = 200;
    private JButton selectedButton;

    public Sidebar(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        addNavigationButton("Dashboard", "DASHBOARD", "🏠");
        addNavigationButton("Patients", "PATIENTS", "👥");
        addNavigationButton("Hospitals", "HOSPITALS", "🏥");
        addNavigationButton("Clinics", "CLINICS", "🏣");
        addNavigationButton("Procedures", "PROCEDURES", "⚕️");
        addNavigationButton("Data Management", "DATA_MANAGEMENT", "💾");

        add(Box.createVerticalGlue());
    }

    private void addNavigationButton(String text, String panelName, String icon) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        button.add(iconLabel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        button.setForeground(Color.DARK_GRAY);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(WIDTH, 45));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(new Color(245, 245, 245));
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(Color.WHITE);
                }
            }
        });

        button.addActionListener(e -> {
            mainWindow.showPanel(panelName);
            updateSelectedButton(button);
        });

        if (panelName.equals("DASHBOARD")) {
            updateSelectedButton(button);
        }

        add(button);
        add(Box.createVerticalStrut(1));
    }

    private void updateSelectedButton(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.WHITE);
            selectedButton.setForeground(Color.DARK_GRAY);
        }

        selectedButton = button;
        selectedButton.setBackground(new Color(51, 122, 183));
        selectedButton.setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(
                0, 0, Color.WHITE,
                0, h, new Color(250, 250, 250));

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}