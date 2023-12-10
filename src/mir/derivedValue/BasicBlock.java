package mir.derivedValue;

import mir.Instruction;
import mir.Value;
import mir.ValueType;

import java.util.ArrayList;

public class BasicBlock extends Value {
    public ArrayList<Instruction> instructions = new ArrayList<>();

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public BasicBlock(ValueType type, String name) {
        super(type, name);
    }

    public int renameRegs(String funcName, int cnt) {
        for (Instruction instruction: instructions) {
            if (!instruction.name.isEmpty()) {
                instruction.name += "_" + funcName + "_" + cnt;
                cnt++;
            }
        }
        return cnt;
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!name.isEmpty()) {
            stringBuilder.append(name).append(":\n");
        }
        for (Instruction instruction: instructions) {
            stringBuilder.append(instruction.printCodes());
        }
        return stringBuilder.toString();
    }

}
