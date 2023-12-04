package backend;

public class VirtualReg {
    private static int cnt = 0;
    public int id;

    public boolean spill = false;

    public VirtualReg() {
        id = cnt;
        cnt++;
        RegAlloca.REG_ALLOCA.addVirtualReg(this);
    }

    @Override
    public String toString() {
        return "$" + id;
    }

}
