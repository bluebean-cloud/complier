package mir;

import java.util.ArrayList;
import java.util.Arrays;

public class Instruction extends Value {
    // 实际上是 Instruction
    public ArrayList<Value> values = new ArrayList<>();
    public InsType insType; // 指令类型
    public ValueType getEleType;    // for getelementptr，获取到的变量的类型
    public String funcName;
    public String cmpType;

    public void addValue(Value value) {
        values.add(value);
    }

    public Instruction(ValueType type, String name) {
        super(type, name);
    }

    public Instruction(InsType Instype, String name, ValueType type, Value...values) {
        super(type, name);
        this.insType = Instype;
        this.values.addAll(Arrays.asList(values));
        for (Value value: values) {
            if (value.use != null) {
                value.use.addUser(this);
            }
        }
    }

    public boolean isValuesConst() {
        for (Value value: values) {
            if (!value.isConst) {
                return false;
            }
        }
        return true;
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder("  ");
        boolean first = true;
        switch (insType) {
            case add:
                stringBuilder.append(name).append(" = add i32 ").append(values.get(0).singleName()).append(", ").append(values.get(1).singleName()).append('\n');
                break;
            case sub:
                stringBuilder.append(name).append(" = sub i32 ").append(values.get(0).singleName()).append(", ").append(values.get(1).singleName()).append('\n');
                break;
            case mul:
                stringBuilder.append(name).append(" = mul i32 ").append(values.get(0).singleName()).append(", ").append(values.get(1).singleName()).append('\n');
                break;
            case sdiv:
                stringBuilder.append(name).append(" = sdiv i32 ").append(values.get(0).singleName()).append(", ").append(values.get(1).singleName()).append('\n');
                break;
            case srem:
                stringBuilder.append(name).append(" = srem i32 ").append(values.get(0).singleName()).append(", ").append(values.get(1).singleName()).append('\n');
                break;
            case icmp:
                stringBuilder.append(name).append(" = icmp ").append(cmpType).append(" i32 ")
                        .append(values.get(0).singleName()).append(", ").append(values.get(1).singleName()).append('\n');
                break;
            case and:
                break;
            case or:
                break;
            case call:
                if (!name.isEmpty()) {
                    stringBuilder.append(name).append(" = ");
                }
                stringBuilder.append("call ").append(type).append(" ").append(funcName).append("(");
                for (Value value: values) {
                    if (!first) {
                        stringBuilder.append(", ");
                    }
                    first = false;
                    stringBuilder.append(value);
                }
                stringBuilder.append(")\n");
                break;
            case alloca:
                stringBuilder.append(name).append(" = alloca ").append(type.pointTo).append('\n');
                break;
            case load:
                stringBuilder.append(name).append(" = load ").append(type).append(", ").append(values.get(0)).append('\n');
                break;
            case store:
                stringBuilder.append("store ").append(values.get(0)).append(", ").append(values.get(1)).append('\n');
                break;
            case getelementptr:
                stringBuilder.append(name).append(" = getelementptr ").append(getEleType.pointTo).append(", ");
                for (Value value: values) {
                    if (!first) {
                        stringBuilder.append(", ");
                    }
                    first = false;
                    stringBuilder.append(value);
                }
                stringBuilder.append('\n');
                break;
            case phi:
                break;
            case zext:
                stringBuilder.append(name).append(" = zext ").append(values.get(0)).append(" to ").append(type).append('\n');
                break;
            case trunc:
                break;
            case br:
                if (values.size() == 1) {
                    stringBuilder.append("br label ").append(values.get(0).singleName()).append('\n');
                } else {
                    stringBuilder.append("br ").append(values.get(0))
                            .append(", label ").append(values.get(1).singleName())
                            .append(", label ").append(values.get(2).singleName()).append('\n');
                }
                break;
            case ret:
                stringBuilder.append("ret ");
                if (values.isEmpty()) {
                    stringBuilder.append("void\n");
                } else {
                    stringBuilder.append(values.get(0)).append('\n');
                }
                break;
            case label:
                stringBuilder.append(name.substring(1)).append(":\n");
                break;
        }
        return stringBuilder.toString();
    }

    public enum InsType {
        add,    // <result> = add <ty> <op1>, <op2>
        sub,    // <result> = sub <ty> <op1>, <op2>
        mul,    // <result> = mul <ty> <op1>, <op2>
        sdiv,   // <result> = sdiv <ty> <op1>, <op2>
        srem,   // <result> = srem <ty> <op1>, <op2>
        icmp,   // <result> = icmp <cond> <ty> <op1>, <op2>
        and,    // <result> = and <ty> <op1>, <op2>
        or,     // <result> = or <ty> <op1>, <op2>
        call,   // <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
        alloca, // <result> = alloca <type>
        load,   // <result> = load <ty>, <ty>* <pointer>
        store,  // store <ty> <value>, <ty>* <pointer>
        getelementptr,  // <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
                        // <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
        phi,    // <result> = phi [fast-math-flags] <ty> [ <val0>, <label0>], ...
        zext,   // <result> = zext <ty> <value> to <ty2>
        trunc,  // <result> = trunc <ty> <value> to <ty2>
        br,     // br i1 <cond>, label <iftrue>, label <iffalse>
                // br label <dest>
        ret,    // ret <type> <value> ,ret void
        label,
    }

}
