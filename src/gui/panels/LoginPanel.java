package gui.panels;

import db.Repository;
import tablesAndViews.Users;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    JLabel usernameLabel = new JLabel("Username");
    JTextField username;
    JLabel passwordLabel = new JLabel("Password");
    JTextField password;
    JButton login;
    JButton logout;
    JLabel status;

    public Users currentUser = null;

    Repository repository;

    public LoginPanel(Repository repository) {
        this.repository = repository;
        notLoggedIn();
    }

    private void notLoggedIn() {
        clearAll();
        setLayout(new GridLayout(3, 2));

        username = new JTextField();
        password = new JPasswordField();
        login = new JButton("Login");
        status = new JLabel();

        add(usernameLabel);
        add(username);
        add(passwordLabel);
        add(password);
        add(login);
        add(status);

        login.addActionListener(e -> attemptLogin());

        repaint();
        revalidate();
    }

    private void loggedIn(){
        clearAll();
        status.setText("Welcome " + currentUser.username());
        logout = new JButton("Logout");

        add(status);
        add(logout);
        logout.addActionListener(e -> {
            notLoggedIn();
            currentUser = null;
        });

        repaint();
        revalidate();
    }

    private void clearAll() {
        removeAll();
    }

    private void attemptLogin() {
        String username = this.username.getText();
        String password = this.password.getText();
        if (username.isEmpty() || password.isEmpty()) {
            displayMessage("Must fill in username or password.");
            return;
        }
        String loginSuccess = repository.attemptLogin(username, password);
        switch (loginSuccess) {
            case "Login success" -> {
                currentUser = null;
                currentUser = repository.getUsers(username);
                if (currentUser == null) {
                    displayMessage("Failed to retrieve user.");
                    return;
                }
                loggedIn();
            }
            case "User not found" -> displayMessage("User not found.");
            case "Wrong password" -> displayMessage("Wrong password.");
            case null, default -> displayMessage("Something went wrong!");
        }
    }

    private void displayMessage(String message){
        JOptionPane.showMessageDialog(null, message);
    }
}
