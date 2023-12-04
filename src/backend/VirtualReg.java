package backend;

public class VirtualReg {
    private static int cnt = 0;
    public int id;
    public PhysicalReg physicalReg = null;

    public boolean spill = false;
    public int lifeBeign = -1;
    public int lifeEnd;

    public int offset;

    public VirtualReg() {
        id = cnt;
        cnt++;
        RegAlloca.REG_ALLOCA.addVirtualReg(this);
    }

    public String printCode() {
        if (spill) {
            return null;
        } else {
            return physicalReg.name.toString();
        }
    }


    @Override
    public String toString() {
        return "$" + id;
    }

}
