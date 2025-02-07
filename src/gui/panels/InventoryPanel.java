package gui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import db.Repository;
import tablesAndViews.*;

public class InventoryPanel extends JPanel {

    JScrollPane scrollPane;
    JPanel contentPanel;

    Repository repository;
    public Users currentUser;


    public InventoryPanel(Repository repository) {
        this.repository = repository;

        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        loadInventory();
    }

    private void loadInventory() {
        contentPanel.removeAll();
        List<Inventory> inventory = repository.getInventory();
        for (Inventory product : inventory) {
            createRow(product);
        }
        revalidate();
        repaint();
    }

    public void createRow(Inventory product){
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextField text = new JTextField(product.toString(), 40);
        text.setEditable(false);
        row.add(text);
        row.add(createPurchaseButton(product.ID()));
        contentPanel.add(row);
    }

    public JButton createPurchaseButton(int productID){
        JButton button = new JButton("Purchase");
        button.addActionListener(e ->{
            if (currentUser == null) {
                displayMessage("Login first to purchase shoes.");
                return;
            }

            String purchaseStatus = repository.purchaseProductByID(currentUser.ID(), productID);
            switch (purchaseStatus) {
                case "Success" -> displayMessage("Product added to cart.");
                case "Out of stock" -> {
                    displayMessage("Out of stock.");
                    loadInventory();
                }
                case null, default -> displayMessage("Unexpected error.");
            }
        });
        return button;
    }

    private void displayMessage(String message){
        JOptionPane.showMessageDialog(null, message);
    }
}
