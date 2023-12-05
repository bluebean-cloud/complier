package backend;

public class MIPSInstruction {
    public Type type;
    public String label;
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
    public LFunction lFunction;

    public MIPSInstruction(Type type, String label) {
        this.type = type;
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
    public int symbol = 1;

    public MIPSInstruction(Type type, PhysicalReg physicalReg1, PhysicalReg physicalReg2, Integer imm, boolean needReallocStack, int symbol) {
        this.type = type;
        this.physicalReg1 = physicalReg1;
        this.physicalReg2 = physicalReg2;
        this.imm = imm;
        this.needReallocStack = needReallocStack;
        isAlloc = true;
        this.symbol = symbol;
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

    public MIPSInstruction(Type type, VirtualReg virtualReg1, VirtualReg virtualReg2, PhysicalReg physicalReg3) {
        this.type = type;
        this.virtualReg1 = virtualReg1;
        this.virtualReg2 = virtualReg2;
        this.physicalReg3 = physicalReg3;
        isAlloc = false;
    }

    public MIPSInstruction(Type type) {
        this.type = type;
    }

    public void updateLiveInterVal(int i) {
        if (virtualReg1 != null) {
            if (virtualReg1.lifeBeign == -1) {
                virtualReg1.lifeBeign = i;
            }
            virtualReg1.lifeEnd = i;
        }
        if (virtualReg2 != null) {
            if (virtualReg2.lifeBeign == -1) {
                virtualReg2.lifeBeign = i;
            }
            virtualReg2.lifeEnd = i;
        }
        if (virtualReg3 != null) {
            if (virtualReg3.lifeBeign == -1) {
                virtualReg3.lifeBeign = i;
            }
            virtualReg3.lifeEnd = i;
        }
    }

    public boolean isAlloc() {
        return isAlloc;
    }

    private String printAddU() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("addu ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ').append(virtualReg3.physicalReg);
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printAddUI() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2 != null) {
            if (virtualReg2.spill) {
                stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
                virtualReg2.physicalReg = PhysicalReg.A0;
            }
        }
        stringBuilder.append("addiu ");
        if (virtualReg1 != null) {
            if (virtualReg1.spill) {
                virtualReg1.physicalReg = PhysicalReg.A1;
            }
            stringBuilder.append(virtualReg1.physicalReg).append(' ');
        } else {
            stringBuilder.append(physicalReg1).append(' ');
        }
        if (virtualReg2 != null) {
            stringBuilder.append(virtualReg2.physicalReg).append(' ');
        } else {
            stringBuilder.append(physicalReg2).append(' ');
        }
        stringBuilder.append(imm);
        if (virtualReg1 != null && virtualReg1.spill) {
            stringBuilder.append("\nsw $a1 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSubU() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("subu ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printMul() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("mul ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printDiv() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("div ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printRem() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("rem ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSLT() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("slt ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(physicalReg3);
        }

        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSLTI() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("slti ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ').append(imm);
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSLE() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("sle ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSGT() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("sgt ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSGE() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("sge ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSEQ() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("seq ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSNE() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg2.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg3 != null && virtualReg3.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg3.physicalReg = PhysicalReg.A1;
        }
        if (virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A2;
        }
        stringBuilder.append("sne ").append(virtualReg1.physicalReg).append(' ').append(virtualReg2.physicalReg).append(' ');
        if (virtualReg3 != null) {
            stringBuilder.append(virtualReg3.physicalReg);
        } else {
            stringBuilder.append(imm);
        }
        if (virtualReg1.spill) {
            stringBuilder.append("\nsw $a2 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printLi() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg1 != null && virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A0;
        }
        stringBuilder.append("li ");
        if (virtualReg1 != null) {
            stringBuilder.append(virtualReg1.physicalReg).append(' ');
        } else {
            stringBuilder.append(physicalReg1).append(' ');
        }
        stringBuilder.append(imm);
        return stringBuilder.toString();
    }

    private String printBNEZ() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg1.spill) {
            stringBuilder.append("lw $a0 ").append(virtualReg1.offset).append("($sp)\n");
            virtualReg1.physicalReg = PhysicalReg.A0;
        }
        stringBuilder.append("bnez ").append(virtualReg1.physicalReg).append(' ').append(label);
        return stringBuilder.toString();
    }

    private String printLW() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg1 != null && virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg2 != null && virtualReg2.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A1;
        }
        stringBuilder.append("lw ");
        if (virtualReg1 != null) {
            stringBuilder.append(virtualReg1.physicalReg).append(' ');
        } else {
            stringBuilder.append(physicalReg1).append(' ');
        }
        if (imm != null) {
            stringBuilder.append(imm);
        }
        if (virtualReg2 != null) {
            stringBuilder.append('(').append(virtualReg2.physicalReg).append(')');
        } else if (physicalReg2 != null) {
            stringBuilder.append('(').append(physicalReg2).append(')');
        } else {
            stringBuilder.append(label);
        }
        if (virtualReg1 != null && virtualReg1.spill) {
            stringBuilder.append("\nsw $a0 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printSW() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg1 != null && virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg2 != null && virtualReg2.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A1;
        }
        stringBuilder.append("sw ");
        if (virtualReg1 != null) {
            stringBuilder.append(virtualReg1.physicalReg).append(' ');
        } else {
            stringBuilder.append(physicalReg1).append(' ');
        }
        if (imm != null) {
            stringBuilder.append(imm);
        }
        if (virtualReg2 != null) {
            stringBuilder.append('(').append(virtualReg2.physicalReg).append(')');
        } else if (physicalReg2 != null) {
            stringBuilder.append('(').append(physicalReg2).append(')');
        } else {
            stringBuilder.append(label);
        }
        if (virtualReg1 != null && virtualReg1.spill) {
            stringBuilder.append("\nsw $a0 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    private String printLA() {
        StringBuilder stringBuilder = new StringBuilder();
        if (virtualReg1 != null && virtualReg1.spill) {
            virtualReg1.physicalReg = PhysicalReg.A0;
        }
        if (virtualReg2 != null && virtualReg2.spill) {
            stringBuilder.append("lw $a1 ").append(virtualReg2.offset).append("($sp)\n");
            virtualReg2.physicalReg = PhysicalReg.A1;
        }
        stringBuilder.append("la ");
        if (virtualReg1 != null) {
            stringBuilder.append(virtualReg1.physicalReg).append(' ');
        } else {
            stringBuilder.append(physicalReg1).append(' ');
        }
        if (imm != null) {
            stringBuilder.append(imm);
        }
        if (virtualReg2 != null) {
            stringBuilder.append('(').append(virtualReg2.physicalReg).append(')');
        } else if (physicalReg2 != null) {
            stringBuilder.append('(').append(physicalReg2).append(')');
        } else {
            stringBuilder.append(label);
        }
        if (virtualReg1 != null && virtualReg1.spill) {
            stringBuilder.append("\nsw $a0 ").append(virtualReg1.offset).append("($sp)");
        }
        return stringBuilder.toString();
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (type) {
            case ADDU:
                return printAddU();
            case ADDUI:
                return printAddUI();
            case SUBU:
                return printSubU();
            case MUL:
                return printMul();
            case DIV:
                return printDiv();
            case REM:
                return printRem();
            case LI:
                return printLi();
            case NOP:
                return "nop";
            case J:
                return "j " + label;
            case JAL:
                return "jal " + label;
            case JR:
                return "jr " + physicalReg1;
            case BNEZ:
                return printBNEZ();
            case SLT:
                return printSLT();
            case SLTI:
                return printSLTI();
            case SLE:
                return printSLE();
            case SGT:
                return printSGT();
            case SGE:
                return printSGE();
            case SEQ:
                return printSEQ();
            case SNE:
                return printSNE();
            case LW:
                return printLW();
            case SW:
                return printSW();
            case LA:
                return printLA();
            case LABEL:
                return label + ':';
            case SYSCALL:
                return "syscall";
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
        SYSCALL,
    }
}
