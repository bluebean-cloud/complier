package pCode;

import java.util.ArrayList;
import java.util.HashMap;

public class Actuator {
    private Actuator(){}
    public static final Actuator ACTUATOR = new Actuator();

    HashMap<String, Integer> labelToAddr = new HashMap<>();
    ArrayList<Instr> instrs;

    public void run(ArrayList<Instr> instrs) {
        this.instrs = instrs;

    }

}
