package frontend.parser;

import Util.Judge;
import frontend.lexer.Token;

import java.util.ArrayList;

public class GrammarNode {
    public String grammarType;
    public ArrayList<Token> tokens = new ArrayList<>();
    public ArrayList<GrammarNode> childs = new ArrayList<>();

    public GrammarNode(String type) {
        grammarType = type;
    }

    public void addChild(GrammarNode node) {
        childs.add(node);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Token token: tokens) {
            str.append(token.toString()).append('\n');
        }
        for (GrammarNode node: childs) {
            str.append(node.toString());
        }
        if (!grammarType.isEmpty() && !Judge.isOf(grammarType, "BlockItem", "Decl", "BType", "Ident", "FormatString"))
            str.append("<").append(grammarType).append(">").append('\n');
        return str.toString();
    }
}
