package gui.panels;

import db.Repository;
import tablesAndViews.Inventory;
import tablesAndViews.Users;

import javax.swing.*;
import java.awt.*;

import java.util.List;

public class CartPanel extends JPanel {

    JScrollPane scrollPane;
    JPanel activeOrdersPanel;
    JPanel completedOrdersPanel;

    JButton emptyCartButton;
    JButton confirmOrderButton;

    public Users currentUser;

    Repository repository;

    public CartPanel(Repository repository) {
        this.repository = repository;

        activeOrdersPanel = new JPanel();
        activeOrdersPanel.setLayout(new BoxLayout(activeOrdersPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(activeOrdersPanel);

        completedOrdersPanel = new JPanel();
        emptyCartButton = new JButton("Empty Cart");
        confirmOrderButton = new JButton("Confirm Order");
        confirmOrderButton.addActionListener(e -> {
            boolean success = repository.finishOrder(currentUser.ID());
            if (success){
                displayMessage("Order finished!");
                loadUserCart();
            }
        });
        emptyCartButton.addActionListener(e -> displayMessage("Not yet implemented :("));
        completedOrdersPanel.add(emptyCartButton);
        completedOrdersPanel.add(confirmOrderButton);

        add(scrollPane, BorderLayout.CENTER);
        add(completedOrdersPanel, BorderLayout.SOUTH);
    }

    public void loadUserCart() {
        if (currentUser == null) {
            confirmOrderButton.setEnabled(false);
            emptyCartButton.setEnabled(false);
            return;
        }
        confirmOrderButton.setEnabled(true);
        emptyCartButton.setEnabled(true);
        List<Inventory> currentCart = repository.getActiveOrder(currentUser.ID());
        if (currentCart.isEmpty()) {
            confirmOrderButton.setEnabled(false);
            emptyCartButton.setEnabled(false);
        }
        activeOrdersPanel.removeAll();
        for (Inventory product : currentCart) {
            createRow(product);
        }
        revalidate();
        repaint();
    }

    private void createRow(Inventory product){
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextField text = new JTextField(product.toString(), 40);
        text.setEditable(false);
        row.add(text);
        activeOrdersPanel.add(row);
    }

    private void displayMessage(String message){
        JOptionPane.showMessageDialog(null, message);
    }



}
