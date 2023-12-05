package backend;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class RegAlloca {
    private RegAlloca() {}
    public static final RegAlloca REG_ALLOCA = new RegAlloca();

    public ArrayList<VirtualReg> virtualRegs = new ArrayList<>();
    public PriorityQueue<VirtualReg> active;

    private final ArrayList<PhysicalReg> freePhysicalReg = new ArrayList<>();
    private final ArrayList<PhysicalReg> busyPhysicalReg = new ArrayList<>();

    public void addVirtualReg(VirtualReg virtualReg) {
        virtualRegs.add(virtualReg);
    }


    public void run() {
        int cnt = 0;
        for (MIPSInstruction instruction: Translator.TRANSLATOR.instructions) {
            instruction.updateLiveInterVal(cnt);
            cnt++;
        }
        regInit();
        active = new PriorityQueue<>((o1, o2) -> o2.lifeEnd - o1.lifeEnd); // 栈顶为 lifeEnd 最大的虚拟寄存器
        int R = 16; // 可用的物理寄存器数量
        for (VirtualReg curVirtualReg: virtualRegs) {
            // 尝试对当前虚拟寄存器进行分配
            int curPoint = curVirtualReg.lifeBeign;
            for (VirtualReg virtualReg: active) {
                if (virtualReg.lifeEnd < curPoint) {
                    busyPhysicalReg.remove(virtualReg.physicalReg);
                    freePhysicalReg.add(virtualReg.physicalReg);
                }
            }
            active.removeIf(virtualReg -> virtualReg.lifeEnd < curPoint);

            if (active.size() == R) {
                VirtualReg virtualReg = active.poll();
                virtualReg.spill = true;
                curVirtualReg.physicalReg = virtualReg.physicalReg;
                virtualReg.physicalReg = null;
            } else {
                curVirtualReg.physicalReg = freePhysicalReg.get(0);
                freePhysicalReg.remove(0);
                busyPhysicalReg.add(curVirtualReg.physicalReg);
            }
            active.add(curVirtualReg);

        }
//        ArrayList<MIPSInstruction> instructions = Translator.TRANSLATOR.instructions;
//        for (int i = 0; i < instructions.size(); i++) {
//            MIPSInstruction instruction = Translator.TRANSLATOR.instructions.get(i);
//            ArrayList<VirtualReg> needSave = new ArrayList<>();
//            if (instruction.type.equals(MIPSInstruction.Type.JAL)) {
//                LFunction function = Translator.TRANSLATOR.findLFunction(instruction.label);
//                boolean saveA = false;
//                for (VirtualReg virtualReg: virtualRegs) {
//                    if (virtualReg.lifeBeign < i && virtualReg.lifeEnd > i) {
//                        if (virtualReg.spill) {
//                            saveA = true;
//                        } else {
//                            needSave.add(virtualReg);
//                        }
//                    }
//                }
//                i--;
//                if (saveA) {
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.A0, PhysicalReg.SP, 0));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.A1, PhysicalReg.SP, 0));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.A2, PhysicalReg.SP, 0));
//                    i++;
//                    function.spillNumber += 3;
//                }
//                for (VirtualReg virtualReg: needSave) {
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//                    i ++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.SW, virtualReg.physicalReg, PhysicalReg.SP, 0));
//                    i += 2;
//                    function.spillNumber++;
//                }
//                i++;
//
//                for (int j = needSave.size() - 1; j >= 0; j--) {
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.LW, virtualRegs.get(j).physicalReg, PhysicalReg.SP, 0));
//                    i ++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//                    i += 2;
//                }
//                if (saveA) {
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.A2, PhysicalReg.SP, 0));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.A1, PhysicalReg.SP, 0));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.A0, PhysicalReg.SP, 0));
//                    i++;
//                    instructions.add(i, new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//                    i++;
//                }
//            }
//        }

    }

    private void regInit() {
        freePhysicalReg.clear();
        busyPhysicalReg.clear();
        freePhysicalReg.add(PhysicalReg.T1);
        freePhysicalReg.add(PhysicalReg.T2);
        freePhysicalReg.add(PhysicalReg.T3);
        freePhysicalReg.add(PhysicalReg.T4);
        freePhysicalReg.add(PhysicalReg.T5);
        freePhysicalReg.add(PhysicalReg.T6);
        freePhysicalReg.add(PhysicalReg.T7);
        freePhysicalReg.add(PhysicalReg.T8);
        freePhysicalReg.add(PhysicalReg.T9);
        freePhysicalReg.add(PhysicalReg.S1);
        freePhysicalReg.add(PhysicalReg.S2);
        freePhysicalReg.add(PhysicalReg.S3);
        freePhysicalReg.add(PhysicalReg.S4);
        freePhysicalReg.add(PhysicalReg.S5);
        freePhysicalReg.add(PhysicalReg.S6);
        freePhysicalReg.add(PhysicalReg.S7);
    }


}
