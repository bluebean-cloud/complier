package backend;

public class DynamicInt {
    private int value;

    public DynamicInt(int value) {
        this.value = value;
    }

    public void add(int value) {
        this.value += value;
    }

    public int getValue() {
        return value;
    }

}
