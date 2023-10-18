package frontend.parser;

import Util.Assert;
import Util.ErrorLog;
import Util.Judge;
import Util.NotMatchException;
import frontend.lexer.Lexer;

public class Parser {

    public static Parser PARSER = new Parser();
    private Parser() {}

    public GrammarNode root;

    public void run() throws NotMatchException {
        root = parseCompUnit();
    }


    private GrammarNode parseCompUnit() throws NotMatchException {
        GrammarNode node = new GrammarNode("CompUnit");
        while (!Lexer.LEXER.isEnd()) {
            if (Lexer.LEXER.preView(2).value.equals("(")) {
                node.addChild(parseFuncDef());
            } else {
                node.addChild(parseDecl());
            }
        }
        return node;
    }

    private GrammarNode parseBType() throws NotMatchException {
        Assert.isOf(Lexer.LEXER.peek().value, Lexer.LEXER.peek().lineNumber, "int");
        GrammarNode node = new GrammarNode("BType");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parseIdent() throws NotMatchException {
        Assert.isOf(Lexer.LEXER.peek().type, "IDENFR");
        GrammarNode node = new GrammarNode("Ident");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parseSym(String symbol) throws NotMatchException {
        Assert.isOf(Lexer.LEXER.peek().value, Lexer.LEXER.peek().lineNumber, symbol);
        GrammarNode node = new GrammarNode("");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode dealErr(String symbol) {
        GrammarNode node = new GrammarNode("");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parseDecl() throws NotMatchException {
        GrammarNode node = new GrammarNode("Decl");
        if (Judge.isOf(Lexer.LEXER.peek().value, "const")) {
            node.addChild(parseConstDecl());
            return node;
        }
        node.addChild(parseVarDecl());
        return node;
    }

    private GrammarNode parseConstDecl() throws NotMatchException {
        GrammarNode node = new GrammarNode("ConstDecl");
        node.addChild(parseSym("const"));
        node.addChild(parseBType());
        node.addChild(parseConstDef());
        while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
            node.addChild(parseSym(","));
            node.addChild(parseConstDef());
        }
        try {
            node.addChild(parseSym(";"));
        } catch (NotMatchException e) {
            ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
            node.addChild(dealErr(";"));
        }
        return node;
    }

    private GrammarNode parseConstDef() throws NotMatchException {
        GrammarNode node = new GrammarNode("ConstDef");
        node.addChild(parseIdent());
        while (Judge.isOf(Lexer.LEXER.peek().value, "[")) {
            node.addChild(parseSym("["));
            node.addChild(parseConstExp());
            try {
                node.addChild(parseSym("]"));
            }  catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'k'));
                node.addChild(dealErr("]"));
            }
        }
        node.addChild(parseSym("="));
        node.addChild(parseConstInitVal());
        return node;
    }

    private GrammarNode parseConstInitVal() throws NotMatchException {
        GrammarNode node = new GrammarNode("ConstInitVal");
        if (Judge.isOf(Lexer.LEXER.peek().value, "{")) {
            node.addChild(parseSym("{"));
            if (!Judge.isOf(Lexer.LEXER.peek().value, "}")) {
                node.addChild(parseConstInitVal());
                while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
                    node.addChild(parseSym(","));
                    node.addChild(parseConstInitVal());
                }
            }
            node.addChild(parseSym("}"));
            return node;
        }
        node.addChild(parseConstExp());
        return node;
    }

    private GrammarNode parseVarDecl() throws NotMatchException {
        GrammarNode node = new GrammarNode("VarDecl");
        node.addChild(parseBType());
        node.addChild(parseVarDef());
        while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
            node.addChild(parseSym(","));
            node.addChild(parseVarDef());
        }try {
            node.addChild(parseSym(";"));
        } catch (NotMatchException e) {
            ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
            node.addChild(dealErr(";"));
        }
        return node;
    }
    private GrammarNode parseVarDef() throws NotMatchException {
        GrammarNode node = new GrammarNode("VarDef");
        node.addChild(parseIdent());
        while (Judge.isOf(Lexer.LEXER.peek().value, "[")) {
            node.addChild(parseSym("["));
            node.addChild(parseConstExp());
            try {
                node.addChild(parseSym("]"));
            }  catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'k'));
                node.addChild(dealErr("]"));
            }
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "=")) {
            node.addChild(parseSym("="));
            node.addChild(parseInitVal());
        }
        return node;
    }

    private GrammarNode parseInitVal() throws NotMatchException {
        GrammarNode node = new GrammarNode("InitVal");
        if (Judge.isOf(Lexer.LEXER.peek().value, "{")) {
            node.addChild(parseSym("{"));
            if (!Judge.isOf(Lexer.LEXER.peek().value, "}")) {
                node.addChild(parseInitVal());
                while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
                    node.addChild(parseSym(","));
                    node.addChild(parseInitVal());
                }
            }
            node.addChild(parseSym("}"));
            return node;
        }
        node.addChild(parseExp());
        return node;
    }

    private GrammarNode parseFuncDef() throws NotMatchException {
        GrammarNode node = new GrammarNode("FuncDef");
        node.setType(Type.FUNC_DEF);
        if (Lexer.LEXER.preView(1).value.equals("main")) {
            node.grammarType = "MainFuncDef";
            node.addChild(parseSym("int"));
            node.addChild(parseSym("main"));
            node.addChild(parseSym("("));
            try {
                node.addChild(parseSym(")"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
                node.addChild(dealErr(")"));
            }
            node.addChild(parseBlock());
            node.setType(Type.MAIN_FUNC_DEF);
            return node;
        }
        node.addChild(parseFuncType());
        node.addChild(parseIdent());
        node.addChild(parseSym("("));
        if (!Judge.isOf(Lexer.LEXER.peek().value, ")", "{")) {
            node.addChild(parseFuncFParams());
        }
        try {
            node.addChild(parseSym(")"));
        } catch (NotMatchException e) {
            ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
            node.addChild(dealErr(")"));
        }
        node.addChild(parseBlock());
        return node;
    }

    private GrammarNode parseFuncType() throws NotMatchException {
        Assert.isOf(Lexer.LEXER.peek().value, "int", "void");
        GrammarNode node = new GrammarNode("FuncType");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parseFuncFParams() throws NotMatchException {
        GrammarNode node = new GrammarNode("FuncFParams");
        node.addChild(parseFuncFParam());
        while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
            node.addChild(parseSym(","));
            node.addChild(parseFuncFParam());
        }
        return node;
    }

    private GrammarNode parseFuncFParam() throws NotMatchException {
        GrammarNode node = new GrammarNode("FuncFParam");
        node.addChild(parseBType());
        node.addChild(parseIdent());
        if (Lexer.LEXER.peek().value.equals("[")) {
            node.addChild(parseSym("["));
            try {
                node.addChild(parseSym("]"));
            }  catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'k'));
                node.addChild(dealErr("]"));
            }
            while (Lexer.LEXER.peek().value.equals("[")) {
                node.addChild(parseSym("["));
                node.addChild(parseConstExp());
                try {
                    node.addChild(parseSym("]"));
                }  catch (NotMatchException e) {
                    ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'k'));
                    node.addChild(dealErr("]"));
                }
            }
        }
        return node;
    }

    private GrammarNode parseFuncRParams() throws NotMatchException {
        GrammarNode node = new GrammarNode("FuncRParams");
        node.addChild(parseExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
            node.addChild(parseSym(","));
            node.addChild(parseExp());
        }
        return node;
    }

    private GrammarNode parseBlock() throws NotMatchException {
        GrammarNode node = new GrammarNode("Block");
        node.addChild(parseSym("{"));
        while (!Judge.isOf(Lexer.LEXER.peek().value, "}")) {
            node.addChild(parseBlockItem());
        }
        node.addChild(parseSym("}"));
        return node;
    }

    private GrammarNode parseBlockItem() throws NotMatchException {
        GrammarNode node = new GrammarNode("BlockItem");
        if (Judge.isOf(Lexer.LEXER.peek().value, "int", "const")) {
            node.addChild(parseDecl());
            node.setType(Type.DECL);
            return node;
        }
        node.addChild(parseStmt());
        node.setType(Type.STMT);
        return node;
    }

    private GrammarNode parseStmt() throws NotMatchException {
        GrammarNode node = new GrammarNode("Stmt");
        if (Judge.isOf(Lexer.LEXER.peek().value, "{")) {
            node.setType(Type.BLOCK_STMT);
            node.addChild(parseBlock());
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "if")) {
            node.setType(Type.IF_STMT);
            node.addChild(parseSym("if"));
            node.addChild(parseSym("("));
            node.addChild(parseCond());
            try {
                node.addChild(parseSym(")"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
                node.addChild(dealErr(")"));
            }
            node.addChild(parseStmt());
            if (Judge.isOf(Lexer.LEXER.peek().value, "else")) {
                node.addChild(parseSym("else"));
                node.addChild(parseStmt());
            }
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "for")) {
            node.setType(Type.FOR_STMT);
            node.addChild(parseSym("for"));
            node.addChild(parseSym("("));
            if (!Judge.isOf(Lexer.LEXER.peek().value, ";") && Lexer.LEXER.peek().value.charAt(0) != ')')
                node.addChild(parseForStmt());
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            if (!Judge.isOf(Lexer.LEXER.peek().value, ";") && Lexer.LEXER.peek().value.charAt(0) != ')')
                node.addChild(parseCond());
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            if (!Judge.isOf(Lexer.LEXER.peek().value, ")"))
                node.addChild(parseForStmt());
            try {
                node.addChild(parseSym(")"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
                node.addChild(dealErr(")"));
            }
            node.addChild(parseStmt());
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "while")) {
            node.setType(Type.WHILE_STMT);
            node.addChild(parseSym("while"));
            node.addChild(parseSym("("));
            node.addChild(parseCond());
            node.addChild(parseSym(")"));
            node.addChild(parseStmt());
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "break")) {
            node.setType(Type.BREAK_STMT);
            node.addChild(parseSym("break"));
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "continue")) {
            node.setType(Type.CONTINUE_STMT);
            node.addChild(parseSym("continue"));
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "return")) {
            node.setType(Type.RETURN_STMT);
            node.addChild(parseSym("return"));
            if (!Judge.isOf(Lexer.LEXER.peek().value, ";") && !Lexer.LEXER.isNewLine())
                node.addChild(parseExp());
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().value, "printf")) {
            node.setType(Type.PRINTF_STMT);
            node.addChild(parseSym("printf"));
            node.addChild(parseSym("("));
            node.addChild(parseFormatString());
            while (Judge.isOf(Lexer.LEXER.peek().value, ",")) {
                node.addChild(parseSym(","));
                node.addChild(parseExp());
            }
            try {
                node.addChild(parseSym(")"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
                node.addChild(dealErr(")"));
            }
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            return node;
        }
        if (Lexer.LEXER.containGetInt()) {
            node.setType(Type.GETINT_STMT);
            node.addChild(parseLVal());
            node.addChild(parseSym("="));
            node.addChild(parseSym("getint"));
            node.addChild(parseSym("("));
            try {
                node.addChild(parseSym(")"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
                node.addChild(dealErr(")"));
            }
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            return node;
        }
        if (Lexer.LEXER.containAssign()) {
            node.setType(Type.ASSIGN_STMT);
            node.addChild(parseLVal());
            node.addChild(parseSym("="));
            node.addChild(parseExp());
            try {
                node.addChild(parseSym(";"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
                node.addChild(dealErr(";"));
            }
            return node;
        }
        node.setType(Type.EMPTY_STMT);
        if (!Judge.isOf(Lexer.LEXER.peek().value, ";")) {
            node.setType(Type.EXP_STMT);
            node.addChild(parseExp());
        }
        try {
            node.addChild(parseSym(";"));
        } catch (NotMatchException e) {
            ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'i'));
            node.addChild(dealErr(";"));
        }
        return node;
    }

    private GrammarNode parseFormatString() throws NotMatchException {
        Assert.isOf(Lexer.LEXER.peek().type, "STRCON");
        GrammarNode node = new GrammarNode("FormatString");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parseForStmt() throws NotMatchException {
        GrammarNode node = new GrammarNode("ForStmt");
        node.addChild(parseLVal());
        node.addChild(parseSym("="));
        node.addChild(parseExp());
        return node;
    }

    private GrammarNode parseCond() throws NotMatchException {
        GrammarNode node = new GrammarNode("Cond");
        node.addChild(parseLOrExp());
        return node;
    }

    private GrammarNode parseLOrExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("LOrExp");
        node.addChild(parseLAndExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, "||")) {
            node.addChild(parseSym("||"));
            node.addChild(parseLOrExp());
        }
        return node;
    }

    private GrammarNode parseLAndExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("LAndExp");
        node.addChild(parseEqExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, "&&")) {
            node.addChild(parseSym("&&"));
            node.addChild(parseLAndExp());
        }
        return node;
    }

    private GrammarNode parseEqExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("EqExp");
        node.addChild(parseRelExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, "==", "!=")) {
            node.addChild(parseSym(Lexer.LEXER.peek().value));
            node.addChild(parseEqExp());
        }
        return node;
    }

    private GrammarNode parseRelExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("RelExp");
        node.addChild(parseAddExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, "<", ">", "<=", ">=")) {
            node.addChild(parseSym(Lexer.LEXER.peek().value));
            node.addChild(parseRelExp());
        }
        return node;
    }

    private GrammarNode parseConstExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("ConstExp");
        node.addChild(parseAddExp());
        return node;
    }

    private GrammarNode parseAddExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("AddExp");
        node.addChild(parseMulExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, "+", "-")) {
            node.addChild(parseSym(Lexer.LEXER.peek().value));
            node.addChild(parseAddExp());
        }
        return node;
    }

    private GrammarNode parseMulExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("MulExp");
        node.addChild(parseUnaryExp());
        while (Judge.isOf(Lexer.LEXER.peek().value, "*", "/", "%")) {
            node.addChild(parseSym(Lexer.LEXER.peek().value));
            node.addChild(parseMulExp());
        }
        return node;
    }

    private GrammarNode parseUnaryExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("UnaryExp");
        if (Judge.isOf(Lexer.LEXER.peek().value, "+", "-", "!")) {
            node.addChild(parseUnaryOp(Lexer.LEXER.peek().value));
            node.addChild(parseUnaryExp());
            return node;
        }
        if (Lexer.LEXER.preView(0).type.equals("IDENFR") && Lexer.LEXER.preView(1).value.equals("(")) {
            node.addChild(parseIdent());
            node.addChild(parseSym("("));
            if (!Lexer.LEXER.peek().value.equals(")") && !Judge.isOf(Lexer.LEXER.peek().value, "+", "-", "*", "/", "%", ";")) {
                node.addChild(parseFuncRParams());
            }
            try {
                node.addChild(parseSym(")"));
            } catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'j'));
                node.addChild(dealErr(")"));
            }
            return node;
        }
        node.addChild(parsePrimaryExp());
        return node;
    }

    private GrammarNode parseUnaryOp(String op) throws NotMatchException {
        Assert.isOf(op, "+", "-", "!");
        GrammarNode node = new GrammarNode("UnaryOp");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parsePrimaryExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("PrimaryExp");
        if (Judge.isOf(Lexer.LEXER.peek().value, "(")) {
            node.addChild(parseSym("("));
            node.addChild(parseExp());
            node.addChild(parseSym(")"));
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().type, "IDENFR")) {
            node.addChild(parseLVal());
            return node;
        }
        if (Judge.isOf(Lexer.LEXER.peek().type, "INTCON")) {
            node.addChild(parseNumber());
            return node;
        }
        throw new NotMatchException("in line " + Lexer.LEXER.peek().lineNumber + ": " + Lexer.LEXER.peek().value);
    }

    private GrammarNode parseLVal() throws NotMatchException {
        GrammarNode node = new GrammarNode("LVal");
        node.addChild(parseIdent());
        while (Judge.isOf(Lexer.LEXER.peek().value, "[")) {
            node.addChild(parseSym("["));
            node.addChild(parseExp());
            try {
                node.addChild(parseSym("]"));
            }  catch (NotMatchException e) {
                ErrorLog.ERRORLIST.add(new ErrorLog(Lexer.LEXER.preView(-1).lineNumber, 'k'));
                node.addChild(dealErr("]"));
            }
        }
        return node;
    }

    private GrammarNode parseNumber() throws NotMatchException {
        GrammarNode node = new GrammarNode("Number");
        node.addChild(parseIntConst());
        return node;
    }

    private GrammarNode parseIntConst() throws NotMatchException {
        Assert.isOf(Lexer.LEXER.peek().type, "INTCON");
        GrammarNode node = new GrammarNode("IntConst");
        node.tokens.add(Lexer.LEXER.peek());
        Lexer.LEXER.nextToken();
        return node;
    }

    private GrammarNode parseExp() throws NotMatchException {
        GrammarNode node = new GrammarNode("Exp");
        node.addChild(parseAddExp());
        return node;
    }

}
