package mir;

import java.util.ArrayList;
import java.util.Arrays;

public class Instruction extends Value {
    // 实际上是 Instruction
    public ArrayList<Value> values = new ArrayList<>();
    public InsType insType; // 指令类型
    public ValueType getEleType;    // for getelementptr，获取到的变量的类型

    public Instruction(ValueType type, String name) {
        super(type, name);
    }

    public Instruction(InsType Instype, String name, ValueType type, Value...values) {
        super(type, name);
        this.insType = Instype;
        this.values.addAll(Arrays.asList(values));
        for (Value value: values) {
            value.use.addUser(this);
        }
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder("  ");
        switch (insType) {
            case add:
                break;
            case sub:
                break;
            case mul:
                break;
            case sdiv:
                break;
            case srem:
                break;
            case icmp:
                break;
            case and:
                break;
            case or:
                break;
            case call:
                break;
            case alloca:
                stringBuilder.append(name).append(" = alloca ").append(type.pointTo).append('\n');
                break;
            case load:
                break;
            case store:
                stringBuilder.append("store ").append(values.get(0)).append(", ").append(values.get(1)).append('\n');
                break;
            case getelementptr:
                stringBuilder.append(name).append(" = getelementptr ").append(getEleType.pointTo).append(", ");
                for (Value value: values) {
                    stringBuilder.append(value).append(", ");
                }
                stringBuilder.append('\n');
                break;
            case phi:
                break;
            case zext:
                break;
            case trunc:
                break;
            case br:
                break;
            case ret:
                stringBuilder.append("ret ");
                if (values.isEmpty()) {
                    stringBuilder.append("void\n");
                } else {
                    stringBuilder.append(values.get(0)).append('\n');
                }
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
    }

}
