package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class Block implements SyntaxTreeNode{
    public ArrayList<BlockItem> items = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Token.getTokenString("{")).append('\n');
        for (BlockItem item: items) {
            stringBuilder.append(item.printSyntaxTree()).append('\n');
        }
        stringBuilder.append(Token.getTokenString("}")).append('\n')
                .append("<Block>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        Block block = new Block();
        items.forEach(item -> block.items.add((BlockItem) item.clone()));
        return block;
    }
}
