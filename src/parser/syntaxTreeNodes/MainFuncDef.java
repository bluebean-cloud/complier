package parser.syntaxTreeNodes;

import lexer.Token;

public class MainFuncDef implements SyntaxTreeNode {
    public Block block;
    @Override
    public String printSyntaxTree() {
        return Token.getTokenString("int") + '\n' +
                Token.getTokenString("main") + '\n' +
                Token.getTokenString("(") + '\n' +
                Token.getTokenString(")") + '\n' +
                block.printSyntaxTree() + '\n' +
                "<MainFuncDef>";
    }
}
