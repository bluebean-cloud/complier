package mir.derivedValue;

import mir.Instruction;
import mir.Value;

import java.util.ArrayList;

public class BasicBlock extends Value {
    public ArrayList<Instruction> instructions = new ArrayList<>();

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public BasicBlock(Type type, String name) {
        super(type, name);
    }
}
