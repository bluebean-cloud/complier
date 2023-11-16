package mir;

public class Value {
    public ValueType type;
    public Use use = new Use(); // 谁使用了我

    public String name;

    public Integer constValue;
    public boolean isConst = false;

    public Value(ValueType type, String name) {
        this.type = type;
        this.name = name;
    }

    public Value(int value, boolean isConst) {
        this.constValue = value;
        this.isConst = isConst;
        this.name = String.valueOf(value);
        this.type = ValueType.I32;   // 数值常量
    }

    @Override
    public String toString() {
        return type + " " + name;
    }

    public String singleName() {
        if (isConst) {
            return type + " " + name;
        } else {
            return name;
        }
    }

}
