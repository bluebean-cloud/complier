package mir.derivedValue;

import mir.Value;
import mir.ValueType;
import visitor.MirVar;

public class GlobalVar extends Value {
    public MirVar mirVar;  // 与符号表中的全局变量关联

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(" = dso_local global ")
                .append(mirVar.printDefinition());
        if (mirVar.type.equals(MirVar.Type.GLOBAL_VAL)) {
            stringBuilder.append(", align 4\n");
        } else {
            stringBuilder.append(", align 16\n");
        }
        return stringBuilder.toString();
    }

    public GlobalVar(ValueType type, String name) {
        super(type, name);
    }
}
