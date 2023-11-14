package mir.derivedValue;

import mir.Value;

import java.util.ArrayList;

public class Function extends Value {
    public ArrayList<BasicBlock> blocks = new ArrayList<>();


    public Function(Type type, String name) {
        super(type, name);
    }
}
