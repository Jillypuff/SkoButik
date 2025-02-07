package tablesAndViews;

public record Inventory(int ID, String name, int size, String color, int price) {

    @Override
    public String toString() {
        return name + ", size: " + size + ", " + color + ", priced: " + price;
    }
}
