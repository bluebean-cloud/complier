package backend;

import java.util.ArrayList;

public class RegAlloca {
    private RegAlloca() {}
    public static final RegAlloca REG_ALLOCA = new RegAlloca();

    public ArrayList<VirtualReg> virtualRegs = new ArrayList<>();


    public void addVirtualReg(VirtualReg virtualReg) {
        virtualRegs.add(virtualReg);
    }

    public enum RealReg {

    }

}
