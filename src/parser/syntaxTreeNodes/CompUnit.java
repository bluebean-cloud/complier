package parser.syntaxTreeNodes;

import java.util.ArrayList;

public class CompUnit implements SyntaxTreeNode {
    public ArrayList<Decl> decls = new ArrayList<>();
    public ArrayList<FuncDef> funcDefs = new ArrayList<>();
    public MainFuncDef mainFuncDef;

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Decl decl : decls) {
            stringBuilder.append(decl.printSyntaxTree()).append('\n');
        }
        for (FuncDef funcDef: funcDefs) {
            stringBuilder.append(funcDef.printSyntaxTree()).append('\n');
        }
        if (mainFuncDef != null) {
            stringBuilder.append(mainFuncDef.printSyntaxTree()).append('\n');
        }
        stringBuilder.append("<CompUnit>");
        return stringBuilder.toString();
    }

}
