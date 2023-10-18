package frontend.visitor;

import frontend.parser.Type;

public class DataType {
    public Type type;
    public int capacity = 0;    // 数组维度
    public DataType content;   // 数组内容
    public DataType(Type type) {
        this.type = type;
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
