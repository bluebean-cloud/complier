package frontend.parser;

import Util.Assert;
import Util.Judge;
import frontend.lexer.Token;

import java.util.ArrayList;

public class GrammarNode {
    public String grammarType;
    public ArrayList<Token> tokens = new ArrayList<>();
    public ArrayList<GrammarNode> childs = new ArrayList<>();
    public int cnt = 0;
    public GrammarNode getChild() {
        if (cnt < childs.size()) {
            return childs.get(cnt);
        }
        return null;
    }

    public String getTokenValue() {
        assert tokens.isEmpty() || childs.isEmpty();
        if (tokens.isEmpty()) {
            return childs.get(cnt).getFirstTokenValue();
        }
        return tokens.get(0).value;
    }

    public void nextChild() {
        cnt++;
    }

    public boolean hasNextChild() {
        return cnt + 1 < childs.size();
    }

    public GrammarNode(String type) {
        grammarType = type;
    }

    public void addChild(GrammarNode node) {
        childs.add(node);
    }

    public Type type = null;

    public void setType(Type type) {
        this.type = type;
    }



    public String getFirstTokenValue() {
        if (tokens.isEmpty()) {
            return childs.get(0).getFirstTokenValue();
        }
        return tokens.get(0).value;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Token token: tokens) {
            str.append(token.toString()).append('\n');
        }
        boolean flag = true;
        for (GrammarNode node: childs) {
            str.append(node.toString());
            if (flag && Judge.isOf(grammarType, "AddExp", "MulExp", "LOrExp", "LAndExp", "EqExp", "RelExp")) {
               str.append("<").append(grammarType).append(">").append("\n");
            }
            flag = false;
        }

        if (!grammarType.isEmpty() && !Judge.isOf(grammarType,
                "BlockItem", "Decl", "BType", "Ident", "FormatString", "IntConst", "AddExp", "MulExp", "LOrExp", "LAndExp", "EqExp", "RelExp"))
            str.append("<").append(grammarType).append(">").append('\n');

        return str.toString();
    }
}
