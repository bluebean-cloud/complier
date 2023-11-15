package mir.derivedValue;

import mir.Value;
import mir.ValueType;

public class FunctionParam extends Value {

    public FunctionParam(ValueType type, String name) {
        super(type, name);
    }

    public String printCodes() {
        return type + " " + name;
    }

}
