package mir;

import mir.derivedValue.Function;
import mir.derivedValue.GlobalVar;
import mir.derivedValue.StringLiteral;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    public static final Manager MANAGER = new Manager();
    private Manager() {}
    public ArrayList<Function> functions = new ArrayList<>();
    public HashMap<String, GlobalVar> globalVars = new HashMap<>();
    public HashMap<String, StringLiteral> stringPool = new HashMap<>();

    public void addGlobalVar(String name, GlobalVar globalVar) {
        globalVars.put(name, globalVar);
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public String printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("declare dso_local i32 @getint()\n")
                .append("declare dso_local void @putint(i32)\n")
                .append("declare dso_local void @putch(i32)\n")
                .append("declare dso_local void @putstr(i8*)\n\n");
        for (GlobalVar globalVar: globalVars.values()) {
            stringBuilder.append(globalVar.printCodes());
        }
        stringBuilder.append('\n');
        for (Function function: functions) {
            stringBuilder.append(function.printCodes()).append('\n');
        }
        return stringBuilder.toString();
    }

}
