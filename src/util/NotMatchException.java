package util;

public class NotMatchException extends Exception {
    public NotMatchException() {}

    public String type;

    public NotMatchException(String message) {
        super(message);
        type = message;
    }




}
