package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import javax.imageio.plugins.tiff.ExifInteroperabilityTagSet;
import java.awt.image.TileObserver;
import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;
    Symbol NS=Symbol.getSymbol();
    static Element[] nel=new Element[1000];
    static int nelptr=-1;
    String returnv=null;
    boolean afunc=false;
    boolean funcre=false;

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        return instructions;
    }

    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            System.exit(-1);
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    /**
     * 添加一个符号
     *
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(String name, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.put(name, new SymbolEntry(isConstant, isInitialized, getNextVariableOffset()));
        }
    }

    /**
     * 设置符号为已赋值
     *
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }


    public static void print(){
        System.out.println("Get you!");
    }

    private void analyseProgram() throws CompileError {
        // 示例函数，示例如何调用子程序
        NS.UpLayer();
        TokenType tt=peek().getTokenType();
        while (tt==TokenType.Fn || tt==TokenType.Let || tt==TokenType.Const){
            if(tt==TokenType.Fn){
                nelptr++;
                nel[nelptr]=new Element();
                nel[nelptr].isfunctionname=true;
                nel[nelptr].isconst=false;
                analyseFunction();
            }
            else{
                analyseStmt();
            }
            tt=peek().getTokenType();
        }
        expect(TokenType.EOF);
        if(!NS.checkMain()) throw new Error();
        NS.DownLayer();
    }

    private void analyseFunction() throws CompileError {
        // 示例函数，示例如何调用子程序
//        NS.UpLayer();
        funcre=false;
        afunc=true;
        expect(TokenType.Fn);
        var tk=expect(TokenType.Ident);
        nel[nelptr].elename=tk.getValueString();
        if(!NS.addElement(nel[nelptr])) throw new Error();

        expect(TokenType.LParen);
        TokenType tt=peek().getTokenType();
        if(tt==TokenType.Const || tt==TokenType.Ident) analyseFunctionParamList();
        expect(TokenType.RParen);
        expect(TokenType.Arrow);

//        System.out.println("1");

        analyseType();
        nelptr--;
        NS.UpLayer();
        afunc=false;
        analyseBlockStmt();

        if(!funcre){
            if(!NS.checkReturn(tk.getValueString())) throw new Error();
        }
        NS.DownLayer();
        System.out.println(tk.getValueString()+","+NS.NowPtr);
    }

    private void analyseFunctionParamList() throws CompileError {
        // 示例函数，示例如何调用子程序
        analyseFunctionParam();
        TokenType tt=peek().getTokenType();
        while (tt==TokenType.Comma){
            expect(TokenType.Comma);
            analyseFunctionParam();
            tt=peek().getTokenType();
        }
    }

    private void analyseFunctionParam() throws CompileError {
        // 示例函数，示例如何调用子程序
        nelptr++;
        nel[nelptr]=new Element();
        nel[nelptr].isconst=false;
        TokenType tt=peek().getTokenType();
        if(tt==TokenType.Const){
            nel[nelptr].isconst=true;
            expect(TokenType.Const);
        }
        var cp=expect(TokenType.Ident);
        nel[nelptr].isfunctionname=false;
        nel[nelptr].elename=cp.getValueString();
        expect(TokenType.Colon);
        System.out.println(cp.getValueString()+","+NS.NowPtr);
        analyseType();
        if(!NS.addElement(nel[nelptr])) throw new Error();
    }

    private void analyseType() throws CompileError {
        // 示例函数，示例如何调用子程序
        var tk=expect(TokenType.Ident);
        if(tk.getValueString().equals("int") || tk.getValueString().equals("void")) nel[nelptr].type=tk.getValueString();
        else throw new Error();
        if(afunc) returnv=tk.getValueString();
    }

    private void analyseBlockStmt() throws CompileError {
        // 示例函数，示例如何调用子程序
        expect(TokenType.LBrace);
        TokenType tt=peek().getTokenType();
        while (tt==TokenType.Let || tt==TokenType.Const || tt==TokenType.If || tt==TokenType.While || tt==TokenType.Break || tt==TokenType.Continue || tt==TokenType.Return || tt==TokenType.LBrace || tt==TokenType.Semicolon || tt==TokenType.Minus || tt==TokenType.LParen || tt==TokenType.Ident || tt==TokenType.Uint || tt==TokenType.StringL){
//            System.out.println("1");
            analyseStmt();
            tt=peek().getTokenType();
        }
        expect(TokenType.RBrace);
    }

    private void analyseStmt() throws CompileError {
//        System.out.println("1");
        // 示例函数，示例如何调用子程序
        TokenType tt=peek().getTokenType();
        /*两个声明型*/
        if(tt==TokenType.Let){
//            System.out.println("in");
            nelptr++;
            nel[nelptr]=new Element();
            nel[nelptr].isfunctionname=false;
            expect(TokenType.Let);
            var tk=expect(TokenType.Ident);
            nel[nelptr].elename=tk.getValueString();
            nel[nelptr].isconst=false;
            expect(TokenType.Colon);
            analyseType();
            tt=peek().getTokenType();
            if(tt==TokenType.Equal){
                expect(TokenType.Equal);
                analyseExpression();
            }
            expect(TokenType.Semicolon);
            System.out.println(tk.getValueString()+","+NS.NowPtr);
            if(!NS.addElement(nel[nelptr])) throw new Error();
            nelptr--;
        }
        else if(tt==TokenType.Const){
            nelptr++;
            nel[nelptr]=new Element();
            nel[nelptr].isfunctionname=false;
            expect(TokenType.Const);
            var tk=expect(TokenType.Ident);
            nel[nelptr].elename=tk.getValueString();
            nel[nelptr].isconst=true;
            expect(TokenType.Colon);
            analyseType();
            expect(TokenType.Equal);
            analyseExpression();
            expect(TokenType.Semicolon);
            System.out.println(tk.getValueString()+","+NS.NowPtr);
            if(!NS.addElement(nel[nelptr])) throw new Error();
            nelptr--;
        }
        /*两个声明型*/
        else if(tt==TokenType.If){
            NS.UpLayer();
            expect(TokenType.If);
            if(peek().getTokenType()==TokenType.LParen) expect(TokenType.LParen);
//            nextIf(TokenType.LParen);
            analyseExpression();
//            nextIf(TokenType.RParen);
            if(peek().getTokenType()==TokenType.RParen) expect(TokenType.RParen);
            analyseBlockStmt();
            tt=peek().getTokenType();
            while(tt==TokenType.Else){
                expect(TokenType.Else);
                if(nextIf(TokenType.If)!=null){
                    analyseExpression();
                    analyseBlockStmt();
                }
                else {
                    analyseBlockStmt();
                    break;
                }
                tt=peek().getTokenType();
            }
            System.out.println("If Block"+","+NS.NowPtr);
            NS.DownLayer();
        }
        else if(tt==TokenType.While){
            NS.UpLayer();
            expect(TokenType.While);
            analyseExpression();
            analyseBlockStmt();
            System.out.println("While Block"+","+NS.NowPtr);
            NS.DownLayer();
        }
        else if(tt==TokenType.Break){
            expect(TokenType.Break);
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Continue){
            expect(TokenType.Continue);
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Return){
            expect(TokenType.Return);
            tt=peek().getTokenType();
            if(tt==TokenType.Semicolon){
                if(!returnv.equals("void")){
                    System.out.println("No return!");
                    throw new Error();
                }
            }
            else {
                if(returnv.equals("void")){
                    System.out.println("Unexpected return!");
                    throw new Error();
                }
                analyseExpression();
            }
            expect(TokenType.Semicolon);
            funcre=true;
        }
        else if(tt==TokenType.LBrace){
            NS.UpLayer();
            analyseBlockStmt();
            System.out.println("Code Block"+","+NS.NowPtr);
            NS.DownLayer();
        }
        else if(tt==TokenType.Semicolon){
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Minus){
            analyseExpression();
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Ident){
            analyseExpression();
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.LParen){
            analyseExpression();
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Uint){
            analyseExpression();
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.StringL){
            analyseExpression();
            expect(TokenType.Semicolon);
        }
    }

    private void oldanalyseExpression() throws CompileError {
        // 示例函数，示例如何调用子程序
//        analyseNoreExpression();
//        analyseTransitionalExpression();
        Token tk=peek();
        TokenType tt=tk.getTokenType();
        String name=tk.getValueString();
        if(tt==TokenType.Minus){
            expect(TokenType.Minus);
            analyseExpression();
        }
        else if(tt==TokenType.LParen){
            expect(TokenType.LParen);
            analyseExpression();
            expect(TokenType.LParen);
        }
        else if(tt==TokenType.Ident){
            Token ele=next();
            TokenType tn=peek().getTokenType();
            if(tn==TokenType.LParen){
                if(!NS.callElement(ele.getValueString())) throw new Error();
                expect(TokenType.LParen);
                tn=peek().getTokenType();
                if(tn==TokenType.Minus || tn==TokenType.LParen || tn==TokenType.Ident || tn==TokenType.Uint || tn==TokenType.StringL) {
                    analyseCallParamList();
                }
                expect(TokenType.RParen);
            }
            else {
                if(!NS.referElement(ele.getValueString())) throw new Error();
            }
        }
        else analyseExpression1();
    }

    private void analyseExpression() throws CompileError{
        analyseExpression1();
        if(nextIf(TokenType.Equal)!=null){
            analyseExpression();
        }
    }

    private void analyseExpression1() throws CompileError{
        analyseExpression2();
        if(nextIf(TokenType.Gt)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Lt)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Ge)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Le)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Eq)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Neq)!=null){
            analyseExpression();
        }
    }

    private void analyseExpression2() throws CompileError{
        analyseExpression3();
        if(nextIf(TokenType.Plus)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Minus)!=null){
            analyseExpression();
        }
    }

    private void analyseExpression3() throws CompileError{
        analyseExpression4();
        if(nextIf(TokenType.Mult)!=null){
            analyseExpression();
        }
        else if(nextIf(TokenType.Div)!=null){
            analyseExpression();
        }
    }

    private void analyseExpression4() throws CompileError{
        if(nextIf(TokenType.Minus)!=null){
            analyseExpression();
        }
        else analyseExpression5();
    }

    private void analyseExpression5() throws CompileError{
        if(peek().getTokenType()==TokenType.Ident){
            Token tk=next();
            if(peek().getTokenType()==TokenType.LParen){
                if(!NS.callElement(tk.getValueString())) throw new Error();
                expect(TokenType.LParen);
                TokenType tn=peek().getTokenType();
                if(tn==TokenType.Minus || tn==TokenType.LParen || tn==TokenType.Ident || tn==TokenType.Uint || tn==TokenType.StringL) {
                    analyseCallParamList();
                }
//                System.out.println("fi!");
                expect(TokenType.RParen);
            }
            else{
                if(!NS.referElement(tk.getValueString())) throw new Error();
            }
        }
        else analyseExpression6();
    }

    private void analyseExpression6() throws CompileError{
        if(nextIf(TokenType.LParen)!=null){
            analyseExpression();
            expect(TokenType.RParen);
        }
        else analyseExpression7();
    }

    private void analyseExpression7() throws CompileError{
        Token tk=next();
//        System.out.println(tk.getTokenType());
        if(tk.getTokenType()==TokenType.Ident){
            if(!NS.referElement(tk.getValueString())) throw new Error();
        }
        else if(tk.getTokenType()==TokenType.Uint){

        }
        else if(tk.getTokenType()==TokenType.StringL){

        }
        else throw new Error();
    }

    private void analyseNoreExpression() throws CompileError {
        // 示例函数，示例如何调用子程序
        TokenType tt=peek().getTokenType();
        if(tt==TokenType.Minus){
            expect(TokenType.Minus);
            analyseExpression();
        }
        else if(tt==TokenType.LParen){
            expect(TokenType.LParen);
            analyseExpression();
            expect(TokenType.RParen);
        }
        else if(tt==TokenType.Uint){
            var tk=expect(TokenType.Uint);
        }
        else if(tt==TokenType.StringL){
            var tk=expect(TokenType.StringL);
            System.out.println(tk.getValueString()+","+NS.NowPtr);
        }
        else if(tt==TokenType.Ident){
            Token ident=next();
            if(nextIf(TokenType.Equal)!=null){
                if(!NS.referElement(ident.getValueString())) throw new Error();
                if(!NS.changeElement(ident.getValueString())) throw new Error();
                analyseExpression();
            }
            else if(nextIf(TokenType.LParen)!=null){
                if(!NS.callElement(ident.getValueString())) throw new Error();
                tt=peek().getTokenType();
                if(tt==TokenType.Minus || tt==TokenType.LParen || tt==TokenType.Ident || tt==TokenType.Uint || tt==TokenType.StringL) {
                    analyseCallParamList();
                }
                expect(TokenType.RParen);
            }
            else if(!NS.referElement(ident.getValueString())) throw new Error();;
            System.out.println(ident.getValueString()+","+NS.NowPtr);
        }
        else throw new AnalyzeError(ErrorCode.ExpectedToken,next().getStartPos());
    }

    private void analyseCallParamList() throws CompileError {
        // 示例函数，示例如何调用子程序
        analyseExpression();
        TokenType tt=peek().getTokenType();
        while (tt==TokenType.Comma){
            expect(TokenType.Comma);
            analyseExpression();
            tt=peek().getTokenType();
        }
    }

    private void analyseTransitionalExpression() throws CompileError {
        // 示例函数，示例如何调用子程序
        TokenType tt=peek().getTokenType();
        if(tt==TokenType.Plus || tt==TokenType.Minus || tt==TokenType.Mult || tt==TokenType.Div || tt==TokenType.Eq || tt==TokenType.Neq || tt==TokenType.Lt || tt==TokenType.Gt || tt==TokenType.Le || tt==TokenType.Ge){
            Token op=next();
            analyseExpression();
        }
        else if(tt==TokenType.As){
            expect(TokenType.As);
            analyseType();
        }
    }







//    private void analyseMain() throws CompileError {
//        // 主过程 -> 常量声明 变量声明 语句序列
//        analyseConstantDeclaration();
//        analyseVariableDeclaration();
//        analyseStatementSequence();
//        //throw new Error("Not implemented");
//    }
//
//    private void analyseConstantDeclaration() throws CompileError {
//        // 示例函数，示例如何解析常量声明
//        // 常量声明 -> 常量声明语句*
//
//        // 如果下一个 token 是 const 就继续
//        while (nextIf(TokenType.Const) != null) {
//            // 常量声明语句 -> 'const' 变量名 '=' 常表达式 ';'
//
//            // 变量名
//            var nameToken = expect(TokenType.Ident);
//            // 加入符号表
//            String name = (String) nameToken.getValue();
//            addSymbol(name, true, true, nameToken.getStartPos());
//
//            // 等于号
//            expect(TokenType.Equal);
//
//            // 常表达式
//            var value = analyseConstantExpression();
//            // 分号
//            expect(TokenType.Semicolon);
//
//            // 这里把常量值直接放进栈里，位置和符号表记录的一样。
//            // 更高级的程序还可以把常量的值记录下来，遇到相应的变量直接替换成这个常数值，
//            // 我们这里就先不这么干了。
//            instructions.add(new Instruction(Operation.LIT, value));
//        }
//    }
//
//    private void analyseVariableDeclaration() throws CompileError {
//        // 变量声明 -> 变量声明语句*
//
//        // 如果下一个 token 是 var 就继续
//        while (nextIf(TokenType.Var) != null) {
//            // 变量声明语句 -> 'var' 变量名 ('=' 表达式)? ';'
//
//            // 变量名
//            var nameToken=expect(TokenType.Ident);
//            // 变量初始化了吗
//            //int it;
//            boolean initialized = false;
//            if(nextIf(TokenType.Equal)!=null){
//                analyseExpression();
//                initialized=true;
//            }
//
//            // 下个 token 是等于号吗？如果是的话分析初始化
//
//            // 分析初始化的表达式
//
//            // 分号
//            expect(TokenType.Semicolon);
//
//            // 加入符号表，请填写名字和当前位置（报错用）
//            String name = /* 名字 */(String) nameToken.getValue();
//            addSymbol(name, initialized, false, /* 当前位置 */ nameToken.getStartPos());
//
//            // 如果没有初始化的话在栈里推入一个初始值
//            if (!initialized) {
//                instructions.add(new Instruction(Operation.LIT, 0));
//            }
//        }
//    }
//
//    private void analyseStatementSequence() throws CompileError {
//        // 语句序列 -> 语句*
//        // 语句 -> 赋值语句 | 输出语句 | 空语句
//
//        while (true) {
//            // 如果下一个 token 是……
//            var peeked = peek();
//            if (peeked.getTokenType() == TokenType.Ident) {
//                // 调用相应的分析函数
//                analyseAssignmentStatement();
//                // 如果遇到其他非终结符的 FIRST 集呢？
//            }else if(peeked.getTokenType()==TokenType.Print){
//                analyseOutputStatement();
//            }
//            else {
//                // 都不是，摸了
//                break;
//            }
//        }
//        //throw new Error("Not implemented");
//    }
//
//    private int analyseConstantExpression() throws CompileError {
//        // 常表达式 -> 符号? 无符号整数
//        boolean negative = false;
//        if (nextIf(TokenType.Plus) != null) {
//            negative = false;
//        } else if (nextIf(TokenType.Minus) != null) {
//            negative = true;
//        }
//
//        var token = expect(TokenType.Uint);
//        int value = Integer.parseInt((String) token.getValue());
//        if (negative) {
//            value = -value;
//        }
//
//        return value;
//    }
//
//    private void analyseExpression() throws CompileError {
//        // 表达式 -> 项 (加法运算符 项)*
//        // 项
//        analyseItem();
//
//        while (true) {
//            // 预读可能是运算符的 token
//            var op = peek();
//            if (op.getTokenType() != TokenType.Plus && op.getTokenType() != TokenType.Minus) {
//                break;
//            }
//
//            // 运算符
//            next();
//
//            // 项
//            analyseItem();
//
//            // 生成代码
//            if (op.getTokenType() == TokenType.Plus) {
//                instructions.add(new Instruction(Operation.ADD));
//            } else if (op.getTokenType() == TokenType.Minus) {
//                instructions.add(new Instruction(Operation.SUB));
//            }
//        }
//    }
//
//    private void analyseAssignmentStatement() throws CompileError {
//        // 赋值语句 -> 标识符 '=' 表达式 ';'
//
//        // 分析这个语句
//
//        // 标识符是什么？
//        var nameToken=expect(TokenType.Ident);
//        String name = (String) nameToken.getValue();
//        var symbol = symbolTable.get(name);
//        if (symbol == null) {
//            // 没有这个标识符
//            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
//        } else if (symbol.isConstant) {
//            // 标识符是常量
//            throw new AnalyzeError(ErrorCode.AssignToConstant, /* 当前位置 */ nameToken.getStartPos());
//        }
//        // 设置符号已初始化
//        initializeSymbol(name, nameToken.getStartPos());
//        expect(TokenType.Equal);
//        analyseExpression();
//        expect(TokenType.Semicolon);
//        // 把结果保存
//        var offset = getOffset(name, nameToken.getStartPos());
//        instructions.add(new Instruction(Operation.STO, offset));
//    }
//
//    private void analyseOutputStatement() throws CompileError {
//        // 输出语句 -> 'print' '(' 表达式 ')' ';'
//        expect(TokenType.Print);
////        next();
//        expect(TokenType.LParen);
////        next();
//
//        analyseExpression();
//
//        expect(TokenType.RParen);
////        next();
//        expect(TokenType.Semicolon);
////        next();
//
//        instructions.add(new Instruction(Operation.WRT));
//    }
//
//    private void analyseItem() throws CompileError {
//        // 项 -> 因子 (乘法运算符 因子)*
//
//        // 因子
//        analyseFactor();
//
//        while (true) {
//            // 预读可能是运算符的 token
//            Token op = peek();
//
//            // 运算符
//            if(op.getTokenType()!=TokenType.Mult && op.getTokenType()!=TokenType.Div) break;
//            next();
//            // 因子
//            analyseFactor();
//            // 生成代码
//            if (op.getTokenType() == TokenType.Mult) {
//                instructions.add(new Instruction(Operation.MUL));
//            } else if (op.getTokenType() == TokenType.Div) {
//                instructions.add(new Instruction(Operation.DIV));
//            }
//        }
//    }
//
//    private void analyseFactor() throws CompileError {
//        // 因子 -> 符号? (标识符 | 无符号整数 | '(' 表达式 ')')
//
//        boolean negate;
//        if (nextIf(TokenType.Minus) != null) {
//            negate = true;
//            // 计算结果需要被 0 减
//            instructions.add(new Instruction(Operation.LIT, 0));
//        } else {
//            nextIf(TokenType.Plus);
//            negate = false;
//        }
//
//        if (check(TokenType.Ident)) {
//            // 是标识符
//
//            // 加载标识符的值
//            var nameToken=expect(TokenType.Ident);
//            String name = /* 快填 */ (String) nameToken.getValue();
//            var symbol = symbolTable.get(name);
//            if (symbol == null) {
//                // 没有这个标识符
//                throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
//            } else if (!symbol.isInitialized) {
//                // 标识符没初始化
//                throw new AnalyzeError(ErrorCode.NotInitialized, /* 当前位置 */ nameToken.getStartPos());
//            }
//            var offset = getOffset(name, nameToken.getStartPos());
//            instructions.add(new Instruction(Operation.LOD, offset));
//        } else if (check(TokenType.Uint)) {
//            // 是整数
//            // 加载整数值
//            int value = 0;
//            value=analyseConstantExpression();
//            instructions.add(new Instruction(Operation.LIT, value));
//        } else if (check(TokenType.LParen)) {
//            // 是表达式
//            // 调用相应的处理函数
//            next();
//            analyseExpression();
//            expect(TokenType.RParen);
//        } else {
//            // 都不是，摸了
//            throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
//        }
//
//        if (negate) {
//            instructions.add(new Instruction(Operation.SUB));
//        }
////        throw new Error("Not implemented");
//    }
}
