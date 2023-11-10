package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import parser.syntaxTreeNodes.Number;
import parser.syntaxTreeNodes.*;
import util.ErrorLog;
import util.GlobalConfigure;
import util.Judge;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    public static final Parser PARSER = new Parser();
    public CompUnit root;
    private Parser() {}

    public void run() {
        root = parseCompUnit();
    }

    boolean inLoop = false;
    boolean funcBlock = false;
    HashMap<String, FuncDef> funcDefs = new HashMap<>();
    FuncDef curFunc;
    public Scope curScope = new Scope();


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
        boolean check = checkRide(Lexer.LEXER.curContent(), Lexer.LEXER.curLine());
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
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
        }
        Lexer.LEXER.next("=");
        constDef.constInitVal = parseConstInitVal();
        if (check) {
            curScope.addDef(constDef);
        }
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
        boolean check = checkRide(Lexer.LEXER.curContent(), Lexer.LEXER.curLine());
        varDef.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            varDef.constExp1 = parseConstExp();
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            varDef.constExp2 = parseConstExp();
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "=")) {
            Lexer.LEXER.next("=");
            varDef.initVal = parseInitVal();
        }
        if (check) {
            curScope.addDef(varDef);
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
        curFunc = funcDef;
        funcDef.funcType = parseFuncType();
        boolean check = checkRide(Lexer.LEXER.curContent(), Lexer.LEXER.curLine());
        funcDef.ident = Lexer.LEXER.peek();
        curScope = new Scope(curScope);
        funcBlock = true;

        Lexer.LEXER.next();
        Lexer.LEXER.next("(");
        if (!Judge.isOf(Lexer.LEXER.curContent(), ")", "{")) {
            funcDef.funcFParams = parseFuncFParams();
        }
        try {
            Lexer.LEXER.next(")");
        } catch (RuntimeException e) {
            if (GlobalConfigure.ERROR) {
                ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "j");
            }
        }
        if (check) {
            funcDefs.put(funcDef.getFuncName(), funcDef);
        }
        funcDef.block = parseBlock(true);
        funcDef.checkReturn(Lexer.LEXER.preView(-1).line);
        curScope = curScope.parent;
        curFunc = null;
        return funcDef;
    }

    private MainFuncDef parseMainFuncDef() {
        MainFuncDef mainFuncDef = new MainFuncDef();
        Lexer.LEXER.next("int");
        checkRide("main", Lexer.LEXER.curLine());
        Lexer.LEXER.next("main");
        Lexer.LEXER.next("(");
        try {
            Lexer.LEXER.next(")");
        } catch (RuntimeException e) {
            if (GlobalConfigure.ERROR) {
                ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "j");
            }
        }
        curScope = new Scope(curScope);
        mainFuncDef.block = parseBlock(false);
        mainFuncDef.checkReturn(Lexer.LEXER.preView(-1).line);
        curScope = curScope.parent;
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
        checkRide(Lexer.LEXER.curContent(), Lexer.LEXER.curLine());
        funcFParam.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
            funcFParam.deep = 1;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            funcFParam.constExp = parseConstExp();
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
            funcFParam.deep = 2;
        }
        curScope.addDef(new VarDef(funcFParam));
        return funcFParam;
    }

    private Block parseBlock(boolean funcBlock) {
        Block block = new Block();
        if (!funcBlock) {
            curScope = new Scope(curScope);
        }
        Lexer.LEXER.next("{");
        while (!Judge.isOf(Lexer.LEXER.curContent(), "}")) {
            block.items.add(parseBlockItem());
        }
        Lexer.LEXER.next("}");
        if (!funcBlock) {
            curScope = curScope.parent;
        }
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
        int temCnt = Lexer.LEXER.cnt;
        if (Judge.isOf(Lexer.LEXER.curContent(), "{")) {
            stmt.type = SyntaxType.Block;
            stmt.block = parseBlock(false);
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "if")) {
            stmt.type = SyntaxType.If;
            Lexer.LEXER.next("if");
            Lexer.LEXER.next("(");
            stmt.cond = parseCond();
            try {
                Lexer.LEXER.next(")");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "j");
                }
            }

            curScope = new Scope(curScope);
            stmt.stmt1 = parseStmt();
            curScope = curScope.parent;

            if (Judge.isOf(Lexer.LEXER.curContent(), "else")) {
                Lexer.LEXER.next("else");
                curScope = new Scope(curScope);
                stmt.stmt2 = parseStmt();
                curScope = curScope.parent;
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
            boolean tem = inLoop;
            inLoop = true;
            curScope = new Scope(curScope);
            stmt.stmt1 = parseStmt();
            curScope = curScope.parent;
            inLoop = tem;
            return stmt;
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "break")) {
            stmt.type = SyntaxType.Break;
            if (GlobalConfigure.ERROR && !inLoop) {
                ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.curLine(), "m");
            }
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
            if (GlobalConfigure.ERROR && !inLoop) {
                ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.curLine(), "m");
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
        if (Judge.isOf(Lexer.LEXER.curContent(), "return")) {
            stmt.type = SyntaxType.Return;
            Lexer.LEXER.next("return");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ";") && !Lexer.LEXER.isNewLine()) {
                stmt.exp = parseExp();
                checkReturn(stmt, Lexer.LEXER.curLine());
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
            int line = Lexer.LEXER.curLine();
            Lexer.LEXER.next("printf");
            Lexer.LEXER.next("(");
            stmt.formatString = Lexer.LEXER.peek();
            Lexer.LEXER.next();
            int cnt = 0;
            while (Judge.isOf(Lexer.LEXER.curContent(), ",")) {
                Lexer.LEXER.next(",");
                stmt.exps.add(parseExp());
                cnt++;
            }
            if (cnt != stmt.formatString.getFormatSpecifierNum()) {
                ErrorLog.ERROR_LOGS.addErrorLog(line, "l");
            }
            try {
                Lexer.LEXER.next(")");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "j");
                }
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
        if (Lexer.LEXER.containGetInt()) {
            stmt.type = SyntaxType.GetInt;
            String name = Lexer.LEXER.curContent();
            int line = Lexer.LEXER.curLine();
            stmt.lVal = parseLVal();
            try {
                Lexer.LEXER.next("=");
            } catch (RuntimeException e) {
                Lexer.LEXER.cnt = temCnt;
                stmt.type = SyntaxType.Exp;
                stmt.lVal = null;
                stmt.exp = parseExp();
                try {
                    Lexer.LEXER.next(";");
                } catch (RuntimeException e1) {
                    if (GlobalConfigure.ERROR) {
                        ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                    }
                }
                return stmt;
            }
            checkIsConst(name, line);
            Lexer.LEXER.next("getint");
            Lexer.LEXER.next("(");
            try {
                Lexer.LEXER.next(")");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "j");
                }
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
        if (Lexer.LEXER.containAssign()) {
            stmt.type = SyntaxType.Assign;
            String name = Lexer.LEXER.curContent();
            int line = Lexer.LEXER.curLine();
            stmt.lVal = parseLVal();
            try {
                Lexer.LEXER.next("=");
            } catch (RuntimeException e) {
                Lexer.LEXER.cnt = temCnt;
                stmt.type = SyntaxType.Exp;
                stmt.lVal = null;
                stmt.exp = parseExp();
                try {
                    Lexer.LEXER.next(";");
                } catch (RuntimeException e1) {
                    if (GlobalConfigure.ERROR) {
                        ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "i");
                    }
                }
                return stmt;
            }
            checkIsConst(name, line);
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
        checkIsConst(Lexer.LEXER.curContent(), Lexer.LEXER.curLine());
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
        checkUndefined(Lexer.LEXER.peek(), Lexer.LEXER.curLine());
        lVal.ident = Lexer.LEXER.peek();
        Lexer.LEXER.next();
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            lVal.exp1 = parseExp();
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
        }
        if (Judge.isOf(Lexer.LEXER.curContent(), "[")) {
            Lexer.LEXER.next("[");
            lVal.exp2 = parseExp();
            try {
                Lexer.LEXER.next("]");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "k");
                }
            }
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

            // 检查是否未定义
            checkUndefined(Lexer.LEXER.peek(), Lexer.LEXER.curLine());
            unaryExp.ident = Lexer.LEXER.peek();
            Lexer.LEXER.next();
            Lexer.LEXER.next("(");
            if (!Judge.isOf(Lexer.LEXER.curContent(), ")", "*", "/", "%", ";")) {
                unaryExp.funcRParams = parseFuncRParams();
            }

            // 检查实参
            checkFuncRParam(unaryExp.funcRParams, unaryExp.ident.content, unaryExp.ident.line);
            try {
                Lexer.LEXER.next(")");
            } catch (RuntimeException e) {
                if (GlobalConfigure.ERROR) {
                    ErrorLog.ERROR_LOGS.addErrorLog(Lexer.LEXER.preView(-1).line, "j");
                }
            }
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

    private boolean checkRide(String name, int line) {
        if (!GlobalConfigure.ERROR) {   // 判断重定义
            return true;
        }
        if (curScope.parent == null) {
            if (funcDefs.containsKey(name)) {
                ErrorLog.ERROR_LOGS.addErrorLog(line, "b");
                return false;
            }
        }
        for (ConstDef constDef: curScope.constDefs) {
            if (name.equals(constDef.getName())) {
                ErrorLog.ERROR_LOGS.addErrorLog(line, "b");
                return false;
            }
        }
        for (VarDef varDef: curScope.varDefs) {
            if (name.equals(varDef.getName())) {
                ErrorLog.ERROR_LOGS.addErrorLog(line, "b");
                return false;
            }
        }
        return true;
    }

    private void checkUndefined(Token token, int line) {
        String name = token.content;
        if (!token.tokenType.equals(TokenType.IDENFR)) {
            return;
        }
        if (!GlobalConfigure.ERROR) {
            return;
        }
        Scope temScope = curScope;
        while (temScope != null) {
            for (ConstDef constDef: temScope.constDefs) {
                if (name.equals(constDef.getName())) {
                    return;
                }
            }
            for (VarDef varDef: temScope.varDefs) {
                if (name.equals(varDef.getName())) {
                    return;
                }
            }
            temScope = temScope.parent;
        }
        if (funcDefs.containsKey(name)) {
            return;
        }
        ErrorLog.ERROR_LOGS.addErrorLog(line, "c");
    }

    private void checkReturn(Stmt stmt, int line) {
        if (!GlobalConfigure.ERROR) {
            return;
        }
        if (stmt.exp != null) { // return 有返回值
            if (curFunc == null || curFunc.getFuncType().equals("int")) {
                return;
            }
            // 无返回值的函数返回了值
            ErrorLog.ERROR_LOGS.addErrorLog(line, "f");
        }
    }

    private void checkIsConst(String name, int line) {
        if (!GlobalConfigure.ERROR) {
            return;
        }
        Scope temScope = curScope;
        while (temScope != null) {
            for (ConstDef constDef: temScope.constDefs) {
                if (name.equals(constDef.getName())) {
                    ErrorLog.ERROR_LOGS.addErrorLog(line, "h");
                    return;
                }
            }
            for (VarDef varDef: temScope.varDefs) {
                if (name.equals(varDef.getName())) {
                    return;
                }
            }
            temScope = temScope.parent;
        }
    }

    public FuncDef findFuncDef(String name) {
        if (funcDefs.containsKey(name)) {
            return funcDefs.get(name);
        }
        return null;
    }

    private void checkFuncRParam(FuncRParams funcRParams, String funcName, int line) {
        if (!GlobalConfigure.ERROR) {
            return;
        }
        FuncDef funcDef = findFuncDef(funcName);
        if (funcDef == null) {  // 未定义，之前已经处理过了
            return;
        }
        if (funcRParams == null && funcDef.funcFParams == null) {
            return;     // 直接调用无形参函数
        }
        if (funcRParams == null || funcDef.funcFParams == null) {
            ErrorLog.ERROR_LOGS.addErrorLog(line, "d");
            return;     // 给无形参的函数传参 或 有形参不传参数
        }
        ArrayList<Exp> exps = funcRParams.exps;
        ArrayList<FuncFParam> funcFParams = funcDef.funcFParams.funcFParams;
        if (exps.size() != funcFParams.size()) {
            ErrorLog.ERROR_LOGS.addErrorLog(line, "d");
            return;     // 实参和形参数量不一致
        }
        for (int i = 0; i < exps.size(); i++) {
            UnaryExp unaryExp = exps.get(i).getFirstUnaryExp();
            int type = -1;   // 0: number, 1: lVal, 2: funcCall
            LVal lVal = null;
            int deep = funcFParams.get(i).deep;
            // 得到第一个 UnaryExp 的值
            while (unaryExp != null &&
                    (unaryExp.type.equals(SyntaxType.PrimaryExp) || unaryExp.type.equals(SyntaxType.UnaryOp))) {
                if (unaryExp.type.equals(SyntaxType.PrimaryExp)) {
                    switch (unaryExp.primaryExp.type) {
                        case Exp:
                            unaryExp = unaryExp.primaryExp.exp.getFirstUnaryExp();
                            break;
                        case LVal:
                            type = 1;
                            lVal = unaryExp.primaryExp.lVal;
                            unaryExp = null;
                            break;
                        case Number:
                            unaryExp = null;
                            type = 0;
                            break;
                    }
                } else {
                    unaryExp = unaryExp.unaryExp;
                }
            }
            if (unaryExp != null) {
                FuncDef temFuncDef = findFuncDef(unaryExp.ident.content);   // 查找函数
                if (temFuncDef == null) {   // 未定义已经处理过了
                    return;
                }
                if (deep > 0 || temFuncDef.funcType.type.content.equals("void")) {
                    ErrorLog.ERROR_LOGS.addErrorLog(line, "e");
                    return; // 函数只能返回 int 变量。若形参不为 int 或函数返回 void 则发生错误
                }
                return;
            }
            if (type == 0) {
                if (deep != 0) {
                    ErrorLog.ERROR_LOGS.addErrorLog(line, "e");
                    return;     // 传入的为数字，但形参不为 int
                }
            } else {
                VarDef varDef = curScope.findVarDef(lVal.ident.content);
                ConstDef constDef = curScope.findConstDef(lVal.ident.content);
                if (varDef != null) {   // deep 指示了应当传入什么类型的变量
                    deep -= varDef.constExp1 != null ? 1 : 0;
                    deep -= varDef.constExp2 != null ? 1 : 0;
                    deep += lVal.exp1 != null ? 1 : 0;
                    deep += lVal.exp2 != null ? 1 : 0;
                } else {
                    deep -= constDef.constExp1 != null ? 1 : 0;
                    deep -= constDef.constExp2 != null ? 1 : 0;
                    deep += lVal.exp1 != null ? 1 : 0;
                    deep += lVal.exp2 != null ? 1 : 0;
                }
                if (deep != 0) {
                    ErrorLog.ERROR_LOGS.addErrorLog(line, "e");
                    return;
                }
            }
        }
    }

}
