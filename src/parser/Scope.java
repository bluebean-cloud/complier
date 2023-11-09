package parser;

import parser.syntaxTreeNodes.ConstDef;
import parser.syntaxTreeNodes.VarDef;

import java.util.ArrayList;

public class Scope {

    public Scope parent;
    public ArrayList<ConstDef> constDefs = new ArrayList<>();
    public ArrayList<VarDef> varDefs = new ArrayList<>();

    public void addDef(ConstDef constDef) {
        constDefs.add(constDef);
    }

    public void addDef(VarDef varDef) {
        varDefs.add(varDef);
    }

    public VarDef findVarDef(String name) {
        Scope temScope = this;
        while (temScope != null) {
            for (VarDef varDef: temScope.varDefs) {
                if (varDef.ident.content.equals(name)) {
                    return varDef;
                }
            }
            temScope = temScope.parent;
        }
        return null;
    }

    public ConstDef findConstDef(String name) {
        Scope temScope = this;
        while (temScope != null) {
            for (ConstDef constDef: temScope.constDefs) {
                if (constDef.ident.content.equals(name)) {
                    return constDef;
                }
            }
            temScope = temScope.parent;
        }
        return null;
    }

    public Scope() {}
    public Scope(Scope scope) {
        parent = scope;
    }
}
