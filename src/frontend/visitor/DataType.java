package frontend.visitor;

import frontend.parser.Type;

import java.util.ArrayList;

public class DataType {
    public Type type;
    public int capacity = 0;    // 数组容量
    public DataType content;   // 数组内容
    public DataType(Type type) {
        this.type = type;
    }

    ArrayList<Integer> values = new ArrayList<>();  // 最顶层的DataType持有值

    public void padding() {
        if (type.equals(Type.INT)) {
            return;
        }
        int t;
        if (content.type.equals(Type.INT)) {
            t = capacity;
        } else {
            t = capacity * content.capacity;
        }
        while (values.size() < t) {
            values.add(0);
        }
    }

    @Override
    public String toString() {
        if (type.equals(Type.ARRAY)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(content.toString());
            if (capacity == 0) {
                stringBuilder.append("[]");
            } else {
                stringBuilder.append(String.format("[%d]", capacity));
            }
            return stringBuilder.toString();
        } else {
            return type.toString().toLowerCase();
        }
    }
}
