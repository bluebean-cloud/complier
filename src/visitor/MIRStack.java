package visitor;

import java.util.HashMap;

public class MIRStack {
    public MIRStack parent;

    public HashMap<String, MirVar> vars = new HashMap<>();

    public void addVar(String name, MirVar var) {
        vars.put(name, var);
    }

    public boolean contains(String name) {
        return vars.containsKey(name);
    }

    public MirVar get(String name) {
        return vars.get(name);
    }


}
