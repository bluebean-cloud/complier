package executor;

public class ReturnException extends RuntimeException {
    public int value;

    public ReturnException(int value) {
        this.value = value;
    }

}
