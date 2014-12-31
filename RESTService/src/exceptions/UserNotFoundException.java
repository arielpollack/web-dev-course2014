package exceptions;

/**
 * Created by arielpollack on 12/30/14.
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("User not found");
    }
}
