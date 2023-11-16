package mir.derivedValue;

import mir.Instruction;
import mir.Value;
import mir.ValueType;

import java.util.ArrayList;

public class Function extends Value {
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    public ArrayList<Value> params = new ArrayList<>();
    private ArrayList<Instruction> decls = new ArrayList<>();
    public int cnt = 0;
    public ValueType retType;

    public Function(ValueType type, String name) {
        super(type, name);
    }

    public void addBlock(BasicBlock block) {
        blocks.add(block);
    }

    public void addDecl(Instruction instruction) {
        decls.add(instruction);
    }

    public void addParam(Value param) {
        params.add(param);
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local ")
                .append(retType).append(" ").append(name).append("(");
        boolean first = true;
        for (Value param: params) {
            if (!first) {
                stringBuilder.append(", ");
            }
            first = false;
            stringBuilder.append(param);
        }
        stringBuilder.append(") {\n");
        for (Instruction decl: decls) {
            stringBuilder.append(decl.printCodes());
        }
        for (BasicBlock block: blocks) {
            stringBuilder.append('\n').append(block.printCodes());
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

}
