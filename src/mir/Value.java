package mir;

public class Value {
    Type type;
    public Use use; // 谁使用了我

    public String name;

    public Integer constValue;
    public boolean isConst = false;

    public Value(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Value(int value, boolean isConst) {
        this.constValue = value;
        this.isConst = isConst;
        this.type = Type.I32;   // 数值常量
    }

    public enum Type {
        I1("i1"), I8("i8"), I32("i32"), STR(".str"), POINTER("pointer"), VOID("void"), INS("ins");

        final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


}
