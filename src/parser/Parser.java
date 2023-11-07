package parser;

import lexer.Lexer;
import lexer.TokenType;
import parser.syntaxTreeNodes.Number;
import parser.syntaxTreeNodes.*;
import util.ErrorLog;
import util.GlobalConfigure;
import util.Judge;

public class Parser {
    public static final Parser PARSER = new Parser();
    public CompUnit root;
    private Parser() {}

    public void run() {
        root = parseCompUnit();
    }


    private CompUnit parseCompUnit() {
        CompUnit compUnit = new CompUnit();
        while (!Lexer.LEXER.isEnd()) {
            if (Lexer.LEXER.preView(2).content.equals("(")) {
                if (Lexer.LEXER.preView(1).content.equals("main")) {
                    compUnit.mainFuncDef = parseMainFuncDef();
                } else {
                    compUnit.funcDefs.add(parseFuncDef());
                }
            } else {
                    compUnit.decls.add(parseDecl());
            }
        }
        return compUnit;
    }

    private Decl parseDecl() {
        Decl decl = new Decl();
        if (Judge.isOf(Lexer.LEXER.curContent(), "const")) {
            decl.constDecl = parseConstDecl();
        } else {
            decl.varDecl = parseVarDecl();
        }
        return decl;
    }

    private ConstDecl parseConstDecl() {
        ConstDecl constDecl = new ConstDecl();
        Lexer.LEXER.next("const");
        Lexer.LEXER.next("int");
        constDecl.constDefs.add(parseConstDef());
        while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
            Lexer.LEXER.next(",");
            constDecl.constDefs.add(parseConstDef());
        }
        try {
            Lexer.LEXER.next(";");
        } catch (RuntimeException e) {
            if (GlobalConfigure.ERROR) {
                ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
            }
        }
        return constDecl;
    }

    private ConstDef parseConstDef() {
        ConstDef constDef = new ConstDef();
        constDef.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            constDef.constExp1 = parseConstExp();
            Lexer.LEXER.next("]");
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            constDef.constExp2 = parseConstExp();
            Lexer.LEXER.next("]");
        }
        Lexer.LEXER.next("=");
        constDef.constInitVal = parseConstInitVal();
        return constDef;
    }

    private ConstInitVal parseConstInitVal() {
        ConstInitVal constInitVal = new ConstInitVal();
        if (Judge.isOf(Lexer.LEXER.curContent(), "{")) {
            Lexer.LEXER.next("{");
            if (!Judge.isOf(Lexer.LEXER.curContent(), "}")) {
                constInitVal.constInitVals.add(parseConstInitVal());
            }
            while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
                Lexer.LEXER.next(",");
                constInitVal.constInitVals.add(parseConstInitVal());
            }
            Lexer.LEXER.next("}");
        } else {
            constInitVal.constExp = parseConstExp();
        }
        return constInitVal;
    }

    private VarDecl parseVarDecl() {
        VarDecl varDecl = new VarDecl();
        Lexer.LEXER.next("int");
        varDecl.varDefs.add(parseVarDef());
        while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
            Lexer.LEXER.next(",");
            varDecl.varDefs.add(parseVarDef());
        }
        try {
            Lexer.LEXER.next(";");
        } catch (RuntimeException e) {
            if (GlobalConfigure.ERROR) {
                ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
            }
        }
        return varDecl;
    }

    private VarDef parseVarDef() {
        VarDef varDef = new VarDef();
        varDef.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            varDef.constExp1 = parseConstExp();
            Lexer.LEXER.next("]");
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            varDef.constExp2 = parseConstExp();
            Lexer.LEXER.next("]");
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "=")) {
            Lexer.LEXER.next("=");
            varDef.initVal = parseInitVal();
        }
        return varDef;
    }

    private InitVal parseInitVal() {
        InitVal initVal = new InitVal();
        if (Judge.isOf(Lexer.LEXER.curContent(), "{")) {
            Lexer.LEXER.next("{");
            if (!Judge.isOf(Lexer.LEXER.curContent(), "}")) {
                initVal.initVals.add(parseInitVal());
                while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
                    Lexer.LEXER.next(",");
                    initVal.initVals.add(parseInitVal());
                }
            }
            Lexer.LEXER.next("}");
        } else {
            initVal.exp = parseExp();
        }
        return initVal;
    }

    private FuncDef parseFuncDef() {
        FuncDef funcDef = new FuncDef();
        funcDef.funcType = parseFuncType();
        funcDef.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        Lexer.LEXER.next("(");
        if (!Judge.isOf(Lexer.LEXER.curContent(), ")")) {
            funcDef.funcFParams = parseFuncFParams();
        }
        Lexer.LEXER.next(")");
        funcDef.block = parseBlock();
        return funcDef;
    }

    private MainFuncDef parseMainFuncDef() {
        MainFuncDef mainFuncDef = new MainFuncDef();
        Lexer.LEXER.next("int");
        Lexer.LEXER.next("main");
        Lexer.LEXER.next("(");
        Lexer.LEXER.next(")");
        mainFuncDef.block = parseBlock();
        return mainFuncDef;
    }

    private FuncType parseFuncType() {
        FuncType funcType = new FuncType();
        funcType.type = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        return funcType;
    }

    private FuncFParams parseFuncFParams() {
        FuncFParams funcFParams = new FuncFParams();
        funcFParams.funcFParams.add(parseFuncFParam());
        while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
            Lexer.LEXER.next(",");
            funcFParams.funcFParams.add(parseFuncFParam());
        }
        return funcFParams;
    }

    private FuncFParam parseFuncFParam() {
        FuncFParam funcFParam = new FuncFParam();
        Lexer.LEXER.next("int");
        funcFParam.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            Lexer.LEXER.next("]");
            funcFParam.deep = 1;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            funcFParam.constExp = parseConstExp();
            Lexer.LEXER.next("]");
            funcFParam.deep = 2;
        }
        return funcFParam;
    }

    private Block parseBlock() {
        Block block = new Block();
        Lexer.LEXER.next("{");
        while (!Judge.isOf(Lexer.LEXER.curContent(), "}")) {
            block.items.add(parseBlockItem());
        }
        Lexer.LEXER.next("}");
        return block;
    }

    private BlockItem parseBlockItem() {
        BlockItem blockItem = new BlockItem();
        if (Judge.isOf(Lexer.LEXER.curContent(), "int", "const")) {
            blockItem.decl = parseDecl();
            blockItem.type = SyntaxType.Decl;
        } else {
            blockItem.stmt = parseStmt();
            blockItem.type = SyntaxType.Stmt;
        }
        return blockItem;
    }

    private Stmt parseStmt() {
        Stmt stmt = new Stmt();
        if (Judge.isOf(Lexer.LEXER.curContent(), "{")) {
            stmt.type = SyntaxType.Block;
            stmt.block = parseBlock();
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "if")) {
            stmt.type = SyntaxType.If;
            Lexer.LEXER.next("if");
            Lexer.LEXER.next("(");
            stmt.cond = parseCond();
            Lexer.LEXER.next(")");
            stmt.stmt1 = parseStmt();
            if (Judge.isOf(Lexer.LEXER.curContent(), "else")) {
                Lexer.LEXER.next("else");
                stmt.stmt2 = parseStmt();
            }
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "for")) {
            stmt.type = SyntaxType.For;
            Lexer.LEXER.next("for");
            Lexer.LEXER.next("(");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ";")) {
                stmt.forStmt1 = parseForStmt();
            }
            Lexer.LEXER.next(";");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ";")) {
                stmt.cond = parseCond();
            }
            Lexer.LEXER.next(";");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ")")) {
                stmt.forStmt2 = parseForStmt();
            }
            Lexer.LEXER.next(")");
            stmt.stmt1 = parseStmt();
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "break")) {
            stmt.type = SyntaxType.Break;
            Lexer.LEXER.next("break");
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "continue")) {
            stmt.type = SyntaxType.Continue;
            Lexer.LEXER.next("continue");
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "return")) {
            stmt.type = SyntaxType.Return;
            Lexer.LEXER.next("return");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ";") && !Lexer.LEXER.isNewLine()) {
                stmt.exp = parseExp();
            }
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "printf")) {
            stmt.type = SyntaxType.Printf;
            Lexer.LEXER.next("printf");
            Lexer.LEXER.next("(");
            stmt.formatString = Lexer.LEXER.peek();
            Lexer.LEXER.next();
            while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
                Lexer.LEXER.next(",");
                stmt.exps.add(parseExp());
            }
            Lexer.LEXER.next(")");
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        }
        if (Lexer.LEXER.containGetInt()) {
            stmt.type = SyntaxType.GetInt;
            stmt.lVal = parseLVal();
            Lexer.LEXER.next("=");
            Lexer.LEXER.next("getint");
            Lexer.LEXER.next("(");
            Lexer.LEXER.next(")");
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        }
        if (Lexer.LEXER.containAssign()) {
            stmt.type = SyntaxType.Assign;
            stmt.lVal = parseLVal();
            Lexer.LEXER.next("=");
            stmt.exp = parseExp();
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        } else {
            stmt.type = SyntaxType.Empty;
            if (!Judge.isOf(Lexer.LEXER.curContent(), ";")) {
                stmt.type = SyntaxType.Exp;
                stmt.exp = parseExp();
            }
            try {
                Lexer.LEXER.next(";");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                }
            }
            return stmt;
        }
    }

    private ForStmt parseForStmt() {
        ForStmt forStmt = new ForStmt();
        forStmt.lVal = parseLVal();
        Lexer.LEXER.next("=");
        forStmt.exp = parseExp();
        return forStmt;
    }

    private Exp parseExp() {
        Exp exp = new Exp();
        exp.addExp = parseAddExp();
        return exp;
    }

    private Cond parseCond() {
        Cond cond = new Cond();
        cond.lOrExp = parseLOrExp();
        return cond;
    }

    private LVal parseLVal() {
        LVal lVal = new LVal();
        lVal.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            lVal.exp1 = parseExp();
            Lexer.LEXER.next("]");
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            lVal.exp2 = parseExp();
            Lexer.LEXER.next("]");
        }
        return lVal;
    }

    private PrimaryExp parsePrimaryExp() {
        PrimaryExp primaryExp = new PrimaryExp();
        if (Judge.isOf(Lexer.LEXER.curContent(), "(")) {
            primaryExp.type = SyntaxType.Exp;
            Lexer.LEXER.next("(");
            primaryExp.exp = parseExp();
            Lexer.LEXER.next(")");
            return primaryExp;
        }
        if (Lexer.LEXER.peek().tokenType.equals(TokenType.INTCON)) {
            primaryExp.type = SyntaxType.Number;
            primaryExp.number = parseNumber();
            return primaryExp;
        } else {
            primaryExp.type = SyntaxType.LVal;
            primaryExp.lVal = parseLVal();
            return primaryExp;
        }
    }

    private Number parseNumber() {
        Number number = new Number();
        number.number = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        return number;
    }

    private UnaryExp parseUnaryExp() {
        UnaryExp unaryExp = new UnaryExp();
        if (Judge.isOf(Lexer.LEXER.curContent(), "+", "-", "!")) {
            unaryExp.type = SyntaxType.UnaryOp;
            unaryExp.unaryOp = parseUnaryOp();
            unaryExp.unaryExp = parseUnaryExp();
            return unaryExp;
        }
        if (Lexer.LEXER.peek().tokenType.equals(TokenType.IDENFR) && Lexer.LEXER.preView(1).content.equals("(")) {
            unaryExp.type = SyntaxType.FuncCall;
            unaryExp.ident = Lexer.LEXER.peek();
            Lexer.LEXER.next();
            Lexer.LEXER.next("(");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ")", "*", "/", "%", ";")) {
                unaryExp.funcRParams = parseFuncRParams();
            }
            Lexer.LEXER.next(")");
            return unaryExp;
        } else {
            unaryExp.type = SyntaxType.PrimaryExp;
            unaryExp.primaryExp = parsePrimaryExp();
            return unaryExp;
        }
    }

    private UnaryOp parseUnaryOp() {
        UnaryOp unaryOp = new UnaryOp();
        unaryOp.op = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        return unaryOp;
    }

    private FuncRParams parseFuncRParams() {
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.exps.add(parseExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
            Lexer.LEXER.next(",");
            funcRParams.exps.add(parseExp());
        }
        return funcRParams;
    }

    private MulExp parseMulExp() {
        MulExp mulExp = new MulExp();
        mulExp.unaryExps.add(parseUnaryExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), "*", "/", "%")) {
            mulExp.ops.add(Lexer.LEXER.peek());
            Lexer.LEXER.next();
            mulExp.unaryExps.add(parseUnaryExp());
        }
        return mulExp;
    }

    private AddExp parseAddExp() {
        AddExp addExp = new AddExp();
        addExp.mulExps.add(parseMulExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), "+", "-")) {
            addExp.ops.add(Lexer.LEXER.peek());
            Lexer.LEXER.next();
            addExp.mulExps.add(parseMulExp());
        }
        return addExp;
    }

    private RelExp parseRelExp() {
        RelExp relExp = new RelExp();
        relExp.addExps.add(parseAddExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), "<", ">", "<=", ">=")) {
            relExp.ops.add(Lexer.LEXER.peek());
            Lexer.LEXER.next();
            relExp.addExps.add(parseAddExp());
        }
        return relExp;
    }

    private EqExp parseEqExp() {
        EqExp eqExp = new EqExp();
        eqExp.relExps.add(parseRelExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), "==", "!=")) {
            eqExp.ops.add(Lexer.LEXER.peek());
            Lexer.LEXER.next();
            eqExp.relExps.add(parseRelExp());
        }
        return eqExp;
    }

    private LAndExp parseLAndExp() {
        LAndExp lAndExp = new LAndExp();
        lAndExp.eqExps.add(parseEqExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), "&&")) {
            Lexer.LEXER.next("&&");
            lAndExp.eqExps.add(parseEqExp());
        }
        return lAndExp;
    }

    private LOrExp parseLOrExp() {
        LOrExp lOrExp = new LOrExp();
        lOrExp.andExps.add(parseLAndExp());
        while (Judge.isOf(Lexer.LEXER.curContent(), "||")) {
            Lexer.LEXER.next("||");
            lOrExp.andExps.add(parseLAndExp());
        }
        return lOrExp;
    }

    private ConstExp parseConstExp() {
        ConstExp constExp = new ConstExp();
        constExp.addExp = parseAddExp();
        return constExp;
    }

}
