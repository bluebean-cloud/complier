package backend;

import java.util.ArrayList;

public class LFunction {
    public ArrayList<MIPSInstruction> instructions = new ArrayList<>();
    public ArrayList<VirtualReg> virtualRegs = new ArrayList<>();
    public int spillNumber;
    public String name;

    public LFunction(String name) {
        this.name = name;
    }

    public void addVirtualReg(VirtualReg virtualReg) {
        virtualRegs.add(virtualReg);
    }

    public void reAlloc() {
        for (VirtualReg virtualReg: virtualRegs) {
            if (virtualReg.spill) {
                virtualReg.offset = spillNumber * 4;
                spillNumber++;
            }
        }
        for (MIPSInstruction instruction: instructions) {
            if (instruction.needReallocStack && instruction.symbol == 1) {
                instruction.imm += spillNumber * 4 * instruction.symbol + 4 * 19;
            } else if (instruction.needReallocStack) {
                instruction.imm -= spillNumber * 4;
            }
        }
    }


}
