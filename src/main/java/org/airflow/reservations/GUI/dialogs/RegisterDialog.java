package org.airflow.reservations.GUI.dialogs;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * RegisterDialog class provides a dialog for user registration.
 * It includes fields for first name, last name, email, and password,
 * along with a register button and a link to the login dialog.
 */
public class RegisterDialog extends JDialog {
    // Components of the RegisterDialog
    // Panels
    private final JPanel mainPane;

    // Labels
    private final JLabel createAccountLabel;
    private final JLabel loginLabel;

    // Input fields
    private final JTextField firstNameTextField;
    private final JTextField lastNameTextField;
    private final JTextField emailTextField;
    private final JPasswordField passwordField;

    // Register button
    private final JButton registerButton;

    /**
     * Constructor for the RegisterDialog class.
     * Initializes the dialog with components for user registration.
     *
     * @param parent the parent JFrame for this dialog
     */
    public RegisterDialog(JFrame parent) {
        super(parent, "Register", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // mainPane principal con padding
        mainPane = new JPanel();
        mainPane.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setBackground(Color.WHITE);

        createAccountLabel = new JLabel("Create your Account");
        createAccountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        createAccountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPane.add(createAccountLabel);

        // First Name
        firstNameTextField = new JTextField();
        firstNameTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        firstNameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        firstNameTextField.setBorder(BorderFactory.createTitledBorder("First Name"));
        mainPane.add(firstNameTextField);

        mainPane.add(Box.createRigidArea(new Dimension(0, 10)));

        // Last Name
        lastNameTextField = new JTextField();
        lastNameTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        lastNameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        lastNameTextField.setBorder(BorderFactory.createTitledBorder("Last Name"));
        mainPane.add(lastNameTextField);

        mainPane.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email
        emailTextField = new JTextField();
        emailTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailTextField.setBorder(BorderFactory.createTitledBorder("Email"));
        mainPane.add(emailTextField);

        mainPane.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        mainPane.add(passwordField);

        mainPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register Button
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 122, 255));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPane.add(registerButton);

        mainPane.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Label
        loginLabel = new JLabel("Already have an account? Log in");
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loginLabel.setForeground(new Color(0, 122, 255));
        loginLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose(); // Close the register dialog
                new LoginDialog(parent).setVisible(true); // Open the login dialog
            }
        });
        mainPane.add(loginLabel);

        add(BorderLayout.CENTER, mainPane);
        getRootPane().setBackground(new Color(245, 245, 245));

        // Center the dialog on the screen
        setLocationRelativeTo(parent);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        SwingUtilities.invokeLater((() -> new RegisterDialog(new JFrame()).setVisible(true)));
    }

    //Getters and Setters

    public JPanel getMainPane() {
        return mainPane;
    }

    public JLabel getCreateAccountLabel() {
        return createAccountLabel;
    }

    public JLabel getLoginLabel() {
        return loginLabel;
    }

    public JTextField getFirstNameTextField() {
        return firstNameTextField;
    }

    public JTextField getLastNameTextField() {
        return lastNameTextField;
    }

    public JTextField getEmailTextField() {
        return emailTextField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }
}
