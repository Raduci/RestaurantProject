package model;

import java.util.Objects;

public class Meal {
    private int ID = 0;
    private String name;
    private double price;
    private String quantity;

    public Meal(){}

    public Meal(int id, String name, double price, String quantity) {}

    public Meal(String name, double price, String quantity){}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return Double.compare(meal.price, price) == 0 && Objects.equals(name, meal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
