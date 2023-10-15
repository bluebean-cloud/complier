package frontend.visitor;

import frontend.parser.GrammarNode;
import frontend.parser.Parser;

public class Visitor {
    SymTable curTable = SymTable.SYMTABLE;

    private void visitCompUnit() {
        for (GrammarNode node: Parser.PARSER.root.childs) {
            switch (node.comment) {
                case "FUNC" -> {

                }
                case "MAIN_FUNC" -> {

                }
                default -> {

                }
            }
        }
    }

    private void visitFunc() {

    }


}
