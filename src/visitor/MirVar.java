package visitor;

import mir.Value;
import mir.derivedValue.GlobalVar;

import java.util.ArrayList;

public class MirVar {
    public enum Type {
        GLOBAL_VAL, LOCAL_VAL, GLOBAL_ARRAY, LOCAL_ARRAY, CONST_VAR, CONST_ARRAY,
        LOCAL_POINTER,      // 函数传指针，int[] 或 int[][exp]
    }

    public MirVar(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public MirVar(Type type, int value) {   // 匿名常量
        this.type = type;
        this.integerLiteral = value;
    }

    public MirVar() {}

    public boolean useZeroInit = false;
    public Type type;
    public ArrayList<MirVar> compoundLiteral;  // 若为常量数组，则存于此处
    public Integer integerLiteral;  // 若为常量，则存储常量值

    public Integer size1;   // 第一维维度
    public Integer size2;   // 第二维维度

    // 若是 GLOBAL_VAL 则保存 GlobalVar
    public GlobalVar globalVar;
    public String name;     // 变量名字
    public Value addr;     // 内存地址
    public Value funcFParam;

    // 保存虚拟寄存器列表，都有哪些虚拟寄存器持有过值
    public ArrayList<Value> values = new ArrayList<>();
    public Value constValue;

    public void addValue(Value value) {
        values.add(value);
    }

    public Value getLastValue() {
        return values.get(values.size() - 1);
    }

    public MirVar getVar(int x) {
        return compoundLiteral.get(x);
    }

    public MirVar getVar(int x, int y) {
        return getVar(x).getVar(y);
    }

    private String getDataType() {
        StringBuilder stringBuilder = new StringBuilder();
        if (compoundLiteral == null) {
            stringBuilder.append("i32");
        } else {
            stringBuilder.append("[").append(compoundLiteral.size())
                    .append(" x ")
                    .append(compoundLiteral.get(0).getDataType())
                    .append("]");
        }
        return stringBuilder.toString();
    }

    public String printDefinition() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getDataType()).append(" ");
        if (integerLiteral == null) {  // 为数组类型
            if (useZeroInit) {
                stringBuilder.append("zeroinitializer");
                return stringBuilder.toString();
            }
            stringBuilder.append("[");
            stringBuilder.append(compoundLiteral.get(0).printDefinition());
            for (int i = 1; i < compoundLiteral.size(); i++) {
                stringBuilder.append(", ").append(compoundLiteral.get(i).printDefinition());
            }
            stringBuilder.append("]");
        } else {
            stringBuilder.append(integerLiteral);
        }
        return stringBuilder.toString();
    }

}
