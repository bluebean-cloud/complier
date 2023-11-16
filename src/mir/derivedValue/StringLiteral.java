package mir.derivedValue;

import mir.Value;
import mir.ValueType;

public class StringLiteral extends Value {

    public String content;

    public StringLiteral(String content) {
        super(new ValueType(ValueType.Type.STR), ".str");
        StringBuilder stringBuilder = new StringBuilder();
        for (Character c: content.toCharArray()) {
            if (c == '\\') {
                stringBuilder.append("\\\\");
            } else if (c == '"') {
                stringBuilder.append("\\\"");
            } else {
                stringBuilder.append(c);
            }
        }
        this.content = stringBuilder.toString();
    }

}