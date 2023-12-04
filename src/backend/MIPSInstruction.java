package backend;

public class MIPSInstruction {
    public Type type;
    private String label;
    public VirtualReg virtualReg1;
    public VirtualReg virtualReg2;
    public VirtualReg virtualReg3;
    public PhysicalReg physicalReg1;
    public PhysicalReg physicalReg2;
    public PhysicalReg physicalReg3;
    public Integer imm;
    public DynamicInt dynamicInt;
    boolean isAlloc;
    boolean needReallocStack;

    public MIPSInstruction(Type type, String label) {
        this.label = label;
        isAlloc = true;
    }

    public MIPSInstruction(Type type, VirtualReg virtualReg1, PhysicalReg physicalReg2, Integer imm) {
        this.type = type;
        this.virtualReg1 = virtualReg1;
        this.physicalReg2 = physicalReg2;
        this.imm = imm;
        isAlloc = false;
    }

    public MIPSInstruction(Type type, VirtualReg virtualReg1, PhysicalReg physicalReg2, Integer imm, boolean needReallocStack) {
        this.type = type;
        this.virtualReg1 = virtualReg1;
        this.physicalReg2 = physicalReg2;
        this.imm = imm;
        isAlloc = false;
        this.needReallocStack = needReallocStack;
    }

    public MIPSInstruction(Type type, PhysicalReg physicalReg1, VirtualReg virtualReg2, Integer imm) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        this.virtualReg2 = virtualReg2;
        this.imm = imm;
        isAlloc = false;
    }
    public MIPSInstruction(Type type, VirtualReg virtualReg1, Integer imm) {
        this.type = type;
        this.virtualReg1 = virtualReg1;
        this.imm = imm;
        isAlloc = false;
    }

    public MIPSInstruction(Type type, VirtualReg virtualReg1, VirtualReg virtualReg2, Integer imm) {
        this.type = type;
        this.virtualReg1 = virtualReg1;
        this.virtualReg2 = virtualReg2;
        this.imm = imm;
        isAlloc = false;
    }

    public MIPSInstruction(Type type, VirtualReg virtualReg1, VirtualReg virtualReg2, VirtualReg virtualReg3) {
        this.type = type;
        this.virtualReg1 = virtualReg1;
        this.virtualReg2 = virtualReg2;
        this.virtualReg3 = virtualReg3;
        isAlloc = false;
    }

    public MIPSInstruction(Type type, PhysicalReg physicalReg1) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        isAlloc = true;
    }

    public MIPSInstruction(Type type, VirtualReg virtualReg1, String label) {
        this.type = type;
        this.label = label;
        this.virtualReg1 = virtualReg1;
    }

    public MIPSInstruction(Type type, PhysicalReg physicalReg1, PhysicalReg physicalReg2, Integer imm) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        this.physicalReg2 = physicalReg2;
        this.imm = imm;
        isAlloc = true;
    }

    public MIPSInstruction(Type type, PhysicalReg physicalReg1, PhysicalReg physicalReg2, Integer imm, boolean needReallocStack) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        this.physicalReg2 = physicalReg2;
        this.imm = imm;
        this.needReallocStack = needReallocStack;
        isAlloc = true;
    }

    public MIPSInstruction(Type type, PhysicalReg physicalReg1, PhysicalReg physicalReg2, PhysicalReg physicalReg3) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        this.physicalReg2 = physicalReg2;
        this.physicalReg3 = physicalReg3;
        isAlloc = true;
    }

    public MIPSInstruction(Type type, PhysicalReg physicalReg1, Integer imm) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        this.imm = imm;
        isAlloc = true;
    }

    public void updateLiveInterVal(int i) {
        if (virtualReg1 != null) {
            if (virtualReg1.lifeBeign == 0) {
                virtualReg1.lifeBeign = i;
            }
            virtualReg1.lifeEnd = i;
        }
        if (virtualReg2 != null) {
            if (virtualReg2.lifeBeign == 0) {
                virtualReg2.lifeBeign = i;
            }
            virtualReg2.lifeEnd = i;
        }
        if (virtualReg3 != null) {
            if (virtualReg3.lifeBeign == 0) {
                virtualReg3.lifeBeign = i;
            }
            virtualReg3.lifeEnd = i;
        }
    }

    public boolean isAlloc() {
        return isAlloc;
    }

    public MIPSInstruction(Type type) {
        this.type = type;
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (type) {
            case ADDU:
                break;
            case ADDUI:
                break;
            case SUBU:
                break;
            case MUL:
                break;
            case DIV:
                break;
            case REM:
                break;
            case LI:
                break;
            case NOP:
                break;
            case J:
                break;
            case JAL:
                break;
            case JR:
                break;
            case BNEZ:
                break;
            case SLT:
                break;
            case SLE:
                break;
            case SGT:
                break;
            case SGE:
                break;
            case SEQ:
                break;
            case SNE:
                break;
            case LW:
                break;
            case SW:
                break;
            case LA:
                break;
            case LABEL:
                break;
        }
        return stringBuilder.toString();
    }


    public enum Type {
        ADDU,
        ADDUI,
        SUBU,
        MUL,    // *
        DIV,    // /
        REM,    // %
        LI,
        NOP,

        J,      // 无跳转跳转
        JAL,    // 函数调用
        JR,     // 函数返回
        BNEZ,   // 条件语句，若不为零（真）则跳转

        SLT,    // <
        SLTI,   // < 的立即数版本。只有 slt 需要对是否是立即数进行特殊判断
        SLE,    // <=
        SGT,    // >
        SGE,    // >=
        SEQ,    // ==
        SNE,    // !=

        LW,
        SW,
        LA,
        LABEL,
        PSEUDO, // 伪指令
    }
}
