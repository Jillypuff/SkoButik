package db;

import tablesAndViews.Inventory;
import tablesAndViews.Users;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Repository {

    private final String dbUsername;
    private final String dbPassword;
    private final String dbConnectionString;

    public Repository() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/util/Settings.properties"));
        } catch (IOException e) {
            errorHandler("Cannot find properties file");
        }
        dbUsername = prop.getProperty("name");
        dbPassword = prop.getProperty("password");
        dbConnectionString = prop.getProperty("connectionString");
    }

    private void errorHandler(int errorCode) {
        System.err.println(errorCode);
    }

    private void errorHandler(String message) {
        System.err.println(message);
    }

    // Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword)

    public String attemptLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword);
        CallableStatement callableStatement = connection.prepareCall
                ("CALL AttemptLogin(?, ?, ?);")) {
            callableStatement.setString(1, username);
            callableStatement.setString(2, password);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.executeQuery();

            return callableStatement.getString(3);
        } catch (SQLException e) {
            errorHandler(e.getErrorCode());
        }
        return null;
    }

    public List<Inventory> getInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM inventory\n" +
                "INNER JOIN shoe ON inventory.ID = shoe.ID\n" +
                "WHERE shoe.stock > 0;");) {
            while (resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int size = resultSet.getInt("Size");
                String color = resultSet.getString("Color");
                int price = resultSet.getInt("Price");
                inventoryList.add(new Inventory(ID, name, size, color, price));
            }
        } catch (SQLException e){
            errorHandler("Failed to get inventory");
        }
        return inventoryList;
    }

    public Users getUsers(String username){
        try (Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword);
        PreparedStatement preparedstatement = connection.prepareStatement("SELECT * FROM Users WHERE username = ?")) {
            preparedstatement.setString(1, username);
            ResultSet resultSet = preparedstatement.executeQuery();

            resultSet.next();
            int ID = resultSet.getInt("ID");
            String firstName = resultSet.getString("firstName");
            String surname = resultSet.getString("surname");
            String place = resultSet.getString("place");
            String passwords = resultSet.getString("passwords");
            return new Users(ID, firstName, surname, place, username, passwords);
        } catch (SQLException e){
            errorHandler("Failed to get users");
        }
        return null;
    }

    public String purchaseProductByID(int usersID, int productID) {
        try (Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword);
             CallableStatement callableStatement = connection.prepareCall
                     ("CALL AddToCart(?, ?, ?);")) {
            callableStatement.setInt(1, usersID);
            callableStatement.setInt(2, productID);
            callableStatement.registerOutParameter(3, Types.CHAR);
            callableStatement.executeQuery();

            return callableStatement.getString(3);
        } catch (SQLException e){
            errorHandler("Failed to check product stock");
        }
        return null;
    }

    public List<Inventory> getActiveOrder(int usersID) {
        List<Inventory> activeOrderList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT inventory.ID, inventory.Name, inventory.Size, inventory.Color, inventory.Price FROM inventory\n" +
                     "INNER JOIN content ON content.shoeID = inventory.ID\n" +
                     "INNER JOIN orders ON orders.ID = content.ordersID\n" +
                     "INNER JOIN users ON users.ID = orders.usersID\n" +
                     "WHERE users.ID = " + usersID + " AND orders.status = 'Active';")){

            while (resultSet.next()) {
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int size = resultSet.getInt("Size");
                String color = resultSet.getString("Color");
                int price = resultSet.getInt("Price");
                activeOrderList.add(new Inventory(ID, name, size, color, price));
            }

        } catch (SQLException e){
            errorHandler("Failed to get inventory");
        }

        return activeOrderList;
    }

    public boolean finishOrder(int usersID){
        try (Connection connection = DriverManager.getConnection(dbConnectionString, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement
                     ("UPDATE orders SET status = 'Payed' WHERE usersID = " + usersID + " AND status = 'Active'")){
             preparedStatement.executeUpdate();
             return true;
        } catch (SQLException e){
                 errorHandler("Failed to finish order");
        }
        return false;
    }
}
