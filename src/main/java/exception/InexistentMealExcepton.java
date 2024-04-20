package exception;

public class InexistentMealExcepton extends RuntimeException {
    public InexistentMealExcepton(String mealName) {
        super("The Meal doesn't exist: " + mealName);
    }
}
