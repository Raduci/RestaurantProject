package exception;

import model.User;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(User user) {
        super("The user already exists." + String.valueOf(user));
    }
}