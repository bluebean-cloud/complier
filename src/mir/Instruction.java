package mir;

import java.util.ArrayList;
import java.util.Arrays;

public class Instruction extends Value {
    // 实际上是 Instruction
    public ArrayList<Value> values = new ArrayList<>();
    public InsType insType;

    public Instruction(Type type, String name) {
        super(type, name);
    }

    public Instruction(InsType type, Value...values) {
        super(Type.INS, type.toString());
        this.insType = type;
        this.values.addAll(Arrays.asList(values));
        for (Value value: values) {
            value.use.addUser(this);
        }
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
