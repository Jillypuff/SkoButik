package gui;

import javax.swing.*;
import java.awt.*;

import db.Repository;
import gui.panels.*;

public class GUI extends JFrame{

    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel contentPanel = new JPanel();

    JButton loginButton = new JButton("Login");
    JButton inventoryButton = new JButton("Inventory");
    JButton cartButton = new JButton("Cart");
    JButton exitButton = new JButton("Exit");

    CardLayout cardLayout;

    LoginPanel loginPanel;
    InventoryPanel inventoryPanel;
    CartPanel cartPanel;

    Repository repository = new Repository();

    GUI(){
        mainPanel = new JPanel(new BorderLayout());
        buttonPanel = new JPanel();
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        loadAllPanels();
        addButtons();

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        setupMainFrame();
    }

    private void setupMainFrame() {
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void loadAllPanels() {
        loginPanel = new LoginPanel(repository);
        contentPanel.add(loginPanel, "Login");
        inventoryPanel = new InventoryPanel(repository);
        contentPanel.add(inventoryPanel, "Inventory");
        cartPanel = new CartPanel(repository);
        contentPanel.add(cartPanel, "Cart");
    }

    private void addButtons() {
        buttonPanel.add(loginButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(cartButton);
        buttonPanel.add(exitButton);
        addActionListeners();
    }

    private void addActionListeners() {
        loginButton.addActionListener(e -> cardLayout.show(contentPanel, "Login"));
        inventoryButton.addActionListener(e -> {
            inventoryPanel.currentUser = loginPanel.currentUser;
            cardLayout.show(contentPanel, "Inventory");
        });
        cartButton.addActionListener(e ->{
            cartPanel.currentUser = loginPanel.currentUser;
            cartPanel.loadUserCart();
            cardLayout.show(contentPanel, "Cart");
        });
        exitButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        new GUI();
    }
}
