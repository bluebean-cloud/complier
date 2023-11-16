package mir;

public class ValueType {
    Type type;
    public ValueType pointTo;
    public ValueType elementType;
    public int size;

    public static final ValueType I32 = new ValueType(Type.I32);
    public static final ValueType I1 = new ValueType(Type.I1);
    public static final ValueType I8 = new ValueType(Type.I8);
    public static final ValueType VOID = new ValueType(Type.VOID);
    public static final ValueType INS = new ValueType(Type.INS);
    public static final ValueType BLOCK = new ValueType(Type.BLOCK);
    public static final ValueType FUNCTION = new ValueType(Type.FUNCTION);

    public boolean isVoid() {
        return type.equals(Type.VOID);
    }

    public boolean isI32() {
        return type.equals(Type.I32);
    }

    public ValueType getPointType() {
        return new ValueType(Type.POINTER, this);
    }


    public ValueType(Type type) {
        this.type = type;
    }

    public ValueType(Type type, ValueType pointTo) {
        this.type = type;
        this.pointTo = pointTo;
    }

    public ValueType(Type type, ValueType elementType, int size) {
        this.type = type;
        this.elementType = elementType;
        this.size = size;
    }

    @Override
    public String toString() {
        if (type.equals(Type.POINTER)) {
            return pointTo + "*";
        } else if (type.equals(Type.ARRAY)) {
            return "[" + size + " x " + elementType + "]";
        } else {
            return type.toString();
        }
    }

    public enum Type {
        I1("i1"), I8("i8"), I32("i32"), STR(".str"), POINTER("pointer"), VOID("void"),
        INS("ins"), BLOCK("block"), FUNCTION("function"), ARRAY("array");

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
