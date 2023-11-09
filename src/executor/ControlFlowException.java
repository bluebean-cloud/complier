package executor;

public class ControlFlowException extends RuntimeException {
    public enum Type {
        CONTINUE, BREAK
    }

    public Type type;

    public ControlFlowException(Type type) {
        this.type = type;
    }

}
