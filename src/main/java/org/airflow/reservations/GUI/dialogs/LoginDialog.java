package org.airflow.reservations.GUI.dialogs;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * LoginDialog class provides a dialog for user login.
 * It includes fields for email and password, a login button,
 * and a link to the sign-up dialog.
 */
public class LoginDialog extends JDialog {
    // Panels
    private JPanel mainPane;

    // Labels
    private JLabel welcomeLabel;
    private JLabel signup;

    // Input fields
    private JTextField emailField;
    private JPasswordField passwordField;

    // Buttons
    private JButton loginButton;


    /**
     * Constructor for the LoginDialog class.
     * Initializes the dialog with components for user login.
     *
     * @param parent the parent JFrame for this dialog
     */
    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);


        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // mainPane principal con padding
        mainPane = new JPanel();
        mainPane.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setBackground(Color.WHITE);

        welcomeLabel = new JLabel("Welcome back");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPane.add(welcomeLabel);

        // Username
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        mainPane.add(emailField);
        mainPane.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        mainPane.add(passwordField);
        mainPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        loginButton = new JButton("Log in");
        loginButton.setBackground(new Color(0, 122, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPane.add(loginButton);

        mainPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sign up link
        signup = new JLabel("Don't have an account? Sign up");
        signup.setForeground(new Color(100, 100, 255));
        signup.setAlignmentX(Component.CENTER_ALIGNMENT);
        signup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signup.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterDialog(parent).setVisible(true);
                JOptionPane.showMessageDialog(null, "Redirecting to sign up...");
            }
        });
        mainPane.add(signup);

        add(mainPane, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    /**
     * Main method for testing the LoginDialog.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> new LoginDialog(new JFrame()).setVisible(true));
    }

    /**
     * Gets the main panel of the dialog.
     * @return The main panel.
     */
    public JPanel getMainPane() {
        return mainPane;
    }

    /**
     * Sets the main panel of the dialog.
     * @param mainPane The main panel to set.
     */
    public void setMainPane(JPanel mainPane) {
        this.mainPane = mainPane;
    }

    /**
     * Gets the welcome label.
     * @return The welcome label.
     */
    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }

    /**
     * Sets the welcome label.
     * @param welcomeLabel The welcome label to set.
     */
    public void setWelcomeLabel(JLabel welcomeLabel) {
        this.welcomeLabel = welcomeLabel;
    }

    /**
     * Gets the signup link label.
     * @return The signup link label.
     */
    public JLabel getSignup() {
        return signup;
    }

    /**
     * Sets the signup link label.
     * @param signup The signup link label to set.
     */
    public void setSignup(JLabel signup) {
        this.signup = signup;
    }

    /**
     * Gets the email text field.
     * @return The email text field.
     */
    public JTextField getEmailField() {
        return emailField;
    }

    /**
     * Sets the email text field.
     * @param emailField The email text field to set.
     */
    public void setEmailField(JTextField emailField) {
        this.emailField = emailField;
    }

    /**
     * Gets the password field.
     * @return The password field.
     */
    public JPasswordField getPasswordField() {
        return passwordField;
    }

    /**
     * Sets the password field.
     * @param passwordField The password field to set.
     */
    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }

    /**
     * Gets the login button.
     * @return The login button.
     */
    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * Sets the login button.
     * @param loginButton The login button to set.
     */
    public void setLoginButton(JButton loginButton) {
        this.loginButton = loginButton;
    }
}
