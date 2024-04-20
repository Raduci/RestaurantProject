package exception;

public class TooManyMealsException extends RuntimeException{
    public TooManyMealsException(){
        super("The maximum number of meals has been exceeded.");
    }
}
