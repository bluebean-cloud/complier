package backend;

public class VirtualReg {
    private static int cnt = 0;
    public int id;
    public VirtualReg() {
        id = cnt;
        cnt++;
    }

    @Override
    public String toString() {
        return "$" + id;
    }

}
