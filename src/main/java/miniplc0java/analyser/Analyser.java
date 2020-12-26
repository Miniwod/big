package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
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
    boolean assign=false;
    Token now;
    Stack sc=Stack.getStack();
    public Vm vm=Vm.getVm();
    boolean global;
    boolean func;
    boolean falsetojump;
    boolean puts;
    String putst;
    Instruction[] exprlist=new Instruction[1000];
    int eln=-1;
    public Function fstart=new Function(vm.fln,"_start","void");


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
//            throw new ExpectedTokenError(tt, token);
        }
        throw new ExpectedTokenError(tt, token);
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

    private void retop(Function f){
        f.addInstruction(new Instruction(InstructionType.globa,1));
        f.addInstruction(new Instruction(InstructionType.store));
        f.addInstruction(new Instruction(InstructionType.globa,2));
        f.addInstruction(new Instruction(InstructionType.store));
        f.addInstruction(new Instruction(InstructionType.globa,2));
        f.addInstruction(new Instruction(InstructionType.load));
        f.addInstruction(new Instruction(InstructionType.globa,1));
        f.addInstruction(new Instruction(InstructionType.load));
    }

    public static void print(){
        System.out.println("Get you!");
    }

    private void analyseProgram() throws CompileError {
//        vm.addFunction(fstart);
        vm.fln++;
        vm.addGlobalVariable(new GlobalVariable(vm.fln,"_start"));
//        vm.addGlobalVariable(new GlobalVariable(vm.fln,"bhba",-5,false));
//        vm.addGlobalVariable(new GlobalVariable(vm.fln,"bhbb",0,false));
        // 示例函数，示例如何调用子程序
        NS.UpLayer();
        TokenType tt=peek().getTokenType();
        while (tt==TokenType.Fn || tt==TokenType.Let || tt==TokenType.Const){
            if(tt==TokenType.Fn){
                func=true;
                nelptr++;
                nel[nelptr]=new Element();
                nel[nelptr].isfunctionname=true;
                nel[nelptr].isconst=false;
                analyseFunction();
                func=false;
            }
            else{
                global=true;
                analyseStmt();
                global=false;
            }
            tt=peek().getTokenType();
        }
        expect(TokenType.EOF);
        if(!NS.checkMain()) throw new Error();
        NS.DownLayer();

        NS.bls();

        if(vm.getFcType("main").equals("int")){
//            fstart.addInstruction(new Instruction(InstructionType.stackalloc,1));
        }
        int mainid=vm.getFunctionId("main");
        fstart.addInstruction(new Instruction(InstructionType.call,mainid));
        fstart.addInstruction(new Instruction(InstructionType.ret));
        vm.fl[0]=fstart;
        vm.out();
    }

    private void analyseFunction() throws CompileError {
        // 示例函数，示例如何调用子程序
//        NS.UpLayer();
        funcre=false;
        afunc=true;
        expect(TokenType.Fn);
        var tk=expect(TokenType.Ident);
        nel[nelptr].elename=tk.getValueString();
        vm.addGlobalVariable(new GlobalVariable(vm.gvn,tk.getValueString()));

//        System.out.println(tk.getValueString());
        vm.addFunction(new Function(vm.getGVId(tk.getValueString()),tk.getValueString(),""));


        expect(TokenType.LParen);
        TokenType tt=peek().getTokenType();
        NS.UpLayer();
        if(tt==TokenType.Const || tt==TokenType.Ident) analyseFunctionParamList();
        expect(TokenType.RParen);
        expect(TokenType.Arrow);


        analyseType();
        if(vm.topFunction().type.equals("int")) vm.topFunction().imint();

        if(!NS.addFunction(nel[nelptr])) throw new Error();
        nelptr--;
        afunc=false;
        analyseBlockStmt();
        NS.DownLayer();

        if(!funcre){
            if(!NS.checkReturn(tk.getValueString())) throw new Error();
        }
//        System.out.println(tk.getValueString()+","+NS.NowPtr);
        vm.topFunction().addInstruction(new Instruction(InstructionType.ret));
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

//        System.out.println(vm.topFunction().paranum);
        vm.topFunction().addParam(new LocalVariable(vm.topFunction().paranum,cp.getValueString()));

        nel[nelptr].isfunctionname=false;
        nel[nelptr].elename=cp.getValueString();
        expect(TokenType.Colon);
//        System.out.println(cp.getValueString()+","+NS.NowPtr);
        analyseType();
        if(!NS.addElement(nel[nelptr])) throw new Error();
        nelptr--;
//        vm.topFunction().paranum++;
    }

    private void analyseType() throws CompileError {
        // 示例函数，示例如何调用子程序
        var tk=expect(TokenType.Ident);
        if(tk.getValueString().equals("int") || tk.getValueString().equals("void")){
            nel[nelptr].type=tk.getValueString();
            if(afunc){
                vm.topFunction().type=tk.getValueString();
                if(tk.getValueString().equals("int")){
                    vm.topFunction().retnum=1;
                }
                else vm.topFunction().retnum=0;
            }
        }
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
        // 示例函数，示例如何调用子程序
        TokenType tt=peek().getTokenType();
        /*两个声明型*/
        if(tt==TokenType.Let){
            int id=0;
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

            int tmpans;

            if(global){
                vm.addGlobalVariable(new GlobalVariable(vm.gvn,tk.getValueString(),0,false));
                fstart.addInstruction(new Instruction(InstructionType.globa,vm.getGVId(tk.getValueString())));
            }
            else {
                Function f=vm.topFunction();
                id=f.localnum;
                f.addLocalVariable(new LocalVariable(id,tk.getValueString()));
                f.addInstruction(new Instruction(InstructionType.loca,id));
            }

            if(tt==TokenType.Equal){
//                assign=true;
                expect(TokenType.Equal);
                analyseExpression();
                //Push Result In Function

                tmpans=sc.pop().value;
//                assign=false;
            }
            else{
                tmpans=0;
                if(global) fstart.addInstruction(new Instruction(InstructionType.push,0));
                else vm.topFunction().addInstruction(new Instruction(InstructionType.push,0));
            }

            if(global){
                fstart.addInstruction(new Instruction(InstructionType.store));
            }
            else {
                vm.topFunction().addInstruction(new Instruction(InstructionType.store));
            }

            expect(TokenType.Semicolon);
//            System.out.println(tk.getValueString()+","+NS.NowPtr);
            if(!NS.addElement(id,nel[nelptr])) throw new Error();
            NS.changeIdent(tk.getValueString(),tmpans);
            nelptr--;
        }
        else if(tt==TokenType.Const){
            int id=0;
            nelptr++;
            nel[nelptr]=new Element();
            nel[nelptr].isfunctionname=false;
            expect(TokenType.Const);
            var tk=expect(TokenType.Ident);
            nel[nelptr].elename=tk.getValueString();
            nel[nelptr].isconst=true;

            if(global){
                vm.addGlobalVariable(new GlobalVariable(vm.gvn,tk.getValueString(),0,true));
                fstart.addInstruction(new Instruction(InstructionType.globa,vm.getGVId(tk.getValueString())));
            }
            else {
                Function f=vm.topFunction();
                id=f.localnum;
                f.addLocalVariable(new LocalVariable(id,tk.getValueString()));
                f.addInstruction(new Instruction(InstructionType.loca,id));
            }

            expect(TokenType.Colon);
            analyseType();
            expect(TokenType.Equal);
//            assign=true;
            analyseExpression();
//            assign=false;

            if(global){
                fstart.addInstruction(new Instruction(InstructionType.store));
            }
            else {
                vm.topFunction().addInstruction(new Instruction(InstructionType.store));
            }

            int tmpans=sc.pop().value;
            expect(TokenType.Semicolon);
//            System.out.println(tk.getValueString()+","+NS.NowPtr);
            if(!NS.addElement(id,nel[nelptr])) throw new Error();
            NS.changeIdent(tk.getValueString(),tmpans);
            nelptr--;
        }
        /*两个声明型 END*/

        else if(tt==TokenType.If){
            NS.UpLayer();
            expect(TokenType.If);
            int startindex=0;
            int elseindex=0;
            int endindex=0;
            if(peek().getTokenType()==TokenType.LParen) expect(TokenType.LParen);

            analyseExpression();
            startindex=vm.topFunction().bodynum;
            vm.topFunction().addInstruction(new Instruction(falsetojump?InstructionType.brf:InstructionType.brt));


            if(peek().getTokenType()==TokenType.RParen) expect(TokenType.RParen);
            analyseBlockStmt();
            tt=peek().getTokenType();
            if(tt==TokenType.Else){
                elseindex=vm.topFunction().bodynum;
                expect(TokenType.Else);
//                tt=peek().getTokenType();
//                if(tt==TokenType.If){
//                    analyseStmt();
//                }
//                else {
//                    analyseBlockStmt();
//                }
                analyseStmt();
                vm.topFunction().ilist[startindex].withop=true;
                vm.topFunction().ilist[startindex].opn=elseindex-startindex-1;
            }
            else {
                endindex=vm.topFunction().bodynum;
                vm.topFunction().ilist[startindex].withop=true;
                vm.topFunction().ilist[startindex].opn=endindex-startindex-1;
            }
//            System.out.println("If Block"+","+NS.NowPtr);
            NS.DownLayer();
        }
        else if(tt==TokenType.While){
            NS.UpLayer();
            expect(TokenType.While);

            int startindex=vm.topFunction().bodynum;
            analyseExpression();
            int originindex=vm.topFunction().bodynum;
            vm.topFunction().addInstruction(new Instruction(falsetojump?InstructionType.brf:InstructionType.brt));
            analyseBlockStmt();
            int endindex=vm.topFunction().bodynum;
            for(int i=startindex;i<endindex;i++){
                if(vm.topFunction().ilist[i].type==InstructionType.breakl && !vm.topFunction().ilist[i].withop){
                    vm.topFunction().ilist[i].withop=true;
                    vm.topFunction().ilist[i].opn=endindex-vm.topFunction().ilist[i].index; //In fact,it is endindex+1-(index+1)
                }
                if(vm.topFunction().ilist[i].type==InstructionType.continuel && !vm.topFunction().ilist[i].withop){
                    vm.topFunction().ilist[i].withop=true;
                    vm.topFunction().ilist[i].opn=startindex-vm.topFunction().ilist[i].index-1;
                }
            }
            vm.topFunction().ilist[originindex].withop=true;
            vm.topFunction().ilist[originindex].opn=endindex-originindex; //In fact,it is endindex+1-(originindex+1)
            vm.topFunction().addInstruction(new Instruction(InstructionType.br,startindex-endindex-1));

//            System.out.println("While Block"+","+NS.NowPtr);
            NS.DownLayer();
        }
        else if(tt==TokenType.Break){
            expect(TokenType.Break);
            vm.topFunction().addInstruction(new Instruction(vm.topFunction().bodynum,InstructionType.breakl));
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Continue){
            expect(TokenType.Continue);
            vm.topFunction().addInstruction(new Instruction(vm.topFunction().bodynum,InstructionType.continuel));
            expect(TokenType.Semicolon);
        }
        else if(tt==TokenType.Return){
            expect(TokenType.Return);
            tt=peek().getTokenType();
            if(tt==TokenType.Semicolon){
                if(!returnv.equals("void")){
//                    System.out.println("No return!");
                    throw new Error();
                }
            }
            else {
                if(returnv.equals("void")){
//                    System.out.println("Unexpected return!");
                    throw new Error();
                }
                vm.topFunction().addInstruction(new Instruction(InstructionType.arga,0));
                analyseExpression();
                vm.topFunction().addInstruction(new Instruction(InstructionType.store));
            }
            expect(TokenType.Semicolon);
            funcre=true;
            vm.topFunction().addInstruction(new Instruction(InstructionType.ret));
        }
        else if(tt==TokenType.LBrace){
            NS.UpLayer();
            analyseBlockStmt();
//            System.out.println("Code Block"+","+NS.NowPtr);
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

    private void analyseExpression() throws CompileError{
        analyseExpression1();
        while (nextIf(TokenType.Equal)!=null){
            if(!NS.changeElement(now.getValueString())) throw new Error();
            analyseExpression1();

            if(global){
                fstart.addInstruction(new Instruction(InstructionType.store));
            }
            else{
                vm.topFunction().addInstruction(new Instruction(InstructionType.store));
            }

//            System.out.println(vm.topFunction().name);
            String a=sc.pop().type;
            String b=sc.pop().type;
//            System.out.println(a+" "+b);
            if(!a.equals(b)) throw new Error();
            sc.push("void");
        }
    }

    private void analyseExpression1() throws CompileError{
        analyseExpression2();
        TokenType tk=peek().getTokenType();
        while(tk==TokenType.Gt || tk==TokenType.Lt || tk==TokenType.Ge || tk==TokenType.Le || tk==TokenType.Eq || tk==TokenType.Neq){
            if(nextIf(TokenType.Gt)!=null){
                analyseExpression2();

//                retop(vm.topFunction());
                vm.topFunction().addInstruction(new Instruction(InstructionType.cmp));
                vm.topFunction().addInstruction(new Instruction(InstructionType.setgt));
                falsetojump=true;


                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Lt)!=null){
                analyseExpression2();

//                retop(vm.topFunction());
                vm.topFunction().addInstruction(new Instruction(InstructionType.cmp));
                vm.topFunction().addInstruction(new Instruction(InstructionType.setlt));
                falsetojump=true;

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Ge)!=null){
                analyseExpression2();

//                retop(vm.topFunction());
                vm.topFunction().addInstruction(new Instruction(InstructionType.cmp));
                vm.topFunction().addInstruction(new Instruction(InstructionType.setgt));
                falsetojump=false;

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Le)!=null){
                analyseExpression2();

//                retop(vm.topFunction());
                vm.topFunction().addInstruction(new Instruction(InstructionType.cmp));
                vm.topFunction().addInstruction(new Instruction(InstructionType.setlt));
                falsetojump=false;

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Eq)!=null){
                analyseExpression2();

//                retop(vm.topFunction());
                vm.topFunction().addInstruction(new Instruction(InstructionType.cmp));
                falsetojump=false;

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Neq)!=null){
                analyseExpression2();

//                retop(vm.topFunction());
                vm.topFunction().addInstruction(new Instruction(InstructionType.cmp));
                falsetojump=true;

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            tk=peek().getTokenType();
        }
    }

    private void analyseExpression2() throws CompileError{
        analyseExpression3();
        TokenType tk=peek().getTokenType();
        while(tk==TokenType.Plus || tk==TokenType.Minus){
            if(nextIf(TokenType.Plus)!=null){
                analyseExpression3();

                if(global){
//                    retop(fstart);
                    fstart.addInstruction(new Instruction(InstructionType.add));
                }
                else{
//                    retop(vm.topFunction());
                    vm.topFunction().addInstruction(new Instruction(InstructionType.add));
                }

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Minus)!=null){
                analyseExpression3();

                if(global){
//                    retop(fstart);
                    fstart.addInstruction(new Instruction(InstructionType.sub));
                }
                else{
//                    retop(vm.topFunction());
                    vm.topFunction().addInstruction(new Instruction(InstructionType.sub));
                }

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            tk=peek().getTokenType();
        }
    }

    private void analyseExpression3() throws CompileError{
        analyseExpression4();
        TokenType tk=peek().getTokenType();
        while(tk==TokenType.Mult || tk==TokenType.Div){
            if(nextIf(TokenType.Mult)!=null){
                analyseExpression4();

                if(global){
//                    retop(fstart);
                    fstart.addInstruction(new Instruction(InstructionType.mul));
                }
                else {
//                    retop(vm.topFunction());
                    vm.topFunction().addInstruction(new Instruction(InstructionType.mul));
                }

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            else if(nextIf(TokenType.Div)!=null){
                analyseExpression4();

                if(global){
//                    retop(fstart);
                    fstart.addInstruction(new Instruction(InstructionType.div));
                }
                else {
//                    retop(vm.topFunction());
                    vm.topFunction().addInstruction(new Instruction(InstructionType.div));
                }

                if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
                sc.push("int");
            }
            tk=peek().getTokenType();
        }
    }

    private void analyseExpression4() throws CompileError{
        if(nextIf(TokenType.Minus)!=null){
            sc.push("int");
            analyseExpression4();
            if(global){
                fstart.addInstruction(new Instruction(InstructionType.neg));
            }
            else {
                vm.topFunction().addInstruction(new Instruction(InstructionType.neg));
            }
            if(!sc.pop().type.equals(sc.pop().type)) throw new Error();
            sc.push("int");
        }
        else analyseExpression5();
    }

    private void analyseExpression5() throws CompileError{
        if(peek().getTokenType()==TokenType.Ident){
            Token tk=next();
            if(peek().getTokenType()==TokenType.LParen){
                expect(TokenType.LParen);
                if(tk.getValueString().equals("putstr")){
//                    expect(TokenType.LParen);
                    puts=true;
                    analyseCallParamList();
                    vm.topFunction().addInstruction(new Instruction(InstructionType.prints,vm.getGVId(putst)));
//                    expect(TokenType.RParen);
                    puts=false;
                }
                else if(tk.getValueString().equals("putint")){
                    analyseCallParamList();
                    vm.topFunction().addInstruction(new Instruction(InstructionType.print));
                }
                else if(tk.getValueString().equals("putln")){
                    vm.topFunction().addInstruction(new Instruction(InstructionType.println));
                }
                else if(tk.getValueString().equals("getint")){
                    sc.push("int");
                    vm.topFunction().addInstruction(new Instruction(InstructionType.scan));
                }
                else if(tk.getValueString().equals("putchar")){
//                    System.out.println("ha");
                    analyseCallParamList();
                    vm.topFunction().addInstruction(new Instruction(InstructionType.putc));
//                    System.out.println("ha");
                }
                else {
                    if(!NS.callElement(tk.getValueString())) throw new Error();
                    sc.push(NS.getElementType(tk.getValueString()));

                    if(vm.getFcType(tk.getValueString()).equals("int")){
                        vm.topFunction().addInstruction(new Instruction(InstructionType.stackalloc,1));
                    }

//                    expect(TokenType.LParen);
                    TokenType tn=peek().getTokenType();
                    if(tn==TokenType.Minus || tn==TokenType.LParen || tn==TokenType.Ident || tn==TokenType.Uint || tn==TokenType.StringL) {
                        analyseCallParamList();
                    }

                    vm.topFunction().addInstruction(new Instruction(InstructionType.call,vm.getFunctionId(tk.getValueString())));

                }
                expect(TokenType.RParen);
            }
            else{
                falsetojump=true;
                if(!NS.referElement(tk.getValueString())) throw new Error();
//                System.out.println(NS.getElementType(tk.getValueString())+" "+tk.getValueString());
                sc.push(NS.getElementType(tk.getValueString()));

                if(global){
                    fstart.addInstruction(new Instruction(InstructionType.globa,vm.getGVId(tk.getValueString())));
                    if(peek().getTokenType()!=TokenType.Equal) fstart.addInstruction(new Instruction(InstructionType.load));
                }
                else {
                    int id;
                    boolean globals=NS.checkGlobal(tk.getValueString());
                    if(globals){
                        id=vm.getGVId(tk.getValueString());
                        vm.topFunction().addInstruction(new Instruction(InstructionType.globa,id));
                        if(peek().getTokenType()!=TokenType.Equal) vm.topFunction().addInstruction(new Instruction(InstructionType.load));
                    }
                    else{
                        id=NS.getlvId(tk.getValueString());
                        if(id==-1){
                            id=vm.topFunction().getParamId(tk.getValueString());
                            vm.topFunction().addInstruction(new Instruction(InstructionType.arga,id));
                        }
                        else {
                            vm.topFunction().addInstruction(new Instruction(InstructionType.loca,id));
                        }
//                        System.out.println(assign);
                        if(peek().getTokenType()!=TokenType.Equal) vm.topFunction().addInstruction(new Instruction(InstructionType.load));
                    }
                }

                now=tk;
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
        if(tk.getTokenType()==TokenType.Ident){
            if(!NS.referElement(tk.getValueString())) throw new Error();
            sc.push(NS.getElementType(tk.getValueString()));

            if(global){
                fstart.addInstruction(new Instruction(InstructionType.globa,vm.getGVId(tk.getValueString())));
                if(peek().getTokenType()!=TokenType.Equal) fstart.addInstruction(new Instruction(InstructionType.load));
            }
            else {
                int id;
                boolean globals=NS.checkGlobal(tk.getValueString());
                if(globals){
                    id=vm.getGVId(tk.getValueString());
                    vm.topFunction().addInstruction(new Instruction(InstructionType.globa,id));
                    if(peek().getTokenType()!=TokenType.Equal) vm.topFunction().addInstruction(new Instruction(InstructionType.load));
                }
                else{
                    id=NS.getlvId(tk.getValueString());
                    if(id==-1){
                        id=vm.topFunction().getParamId(tk.getValueString());
                        vm.topFunction().addInstruction(new Instruction(InstructionType.arga,id));
                    }
                    else {
                        vm.topFunction().addInstruction(new Instruction(InstructionType.loca,id));
                    }
//                    System.out.println(assign);
                    if(peek().getTokenType()!=TokenType.Equal) vm.topFunction().addInstruction(new Instruction(InstructionType.load));
                }
            }

            falsetojump=true;
            now=tk;
        }
        else if(tk.getTokenType()==TokenType.Uint){

            int a=Integer.parseInt(tk.getValueString());

            falsetojump=a!=0;

            sc.push("int");
            if(global){
                fstart.addInstruction(new Instruction(InstructionType.push,a));
            }
            else{
                vm.topFunction().addInstruction(new Instruction(InstructionType.push,a));
            }
        }
        else if(tk.getTokenType()==TokenType.StringL){
            NS.addStringL(tk.getValueString());

            vm.addGlobalVariable(new GlobalVariable(vm.gvn,tk.getValueString()));
//            vm.topFunction().addInstruction(new Instruction(InstructionType.globa,vm.getGVId(tk.getValueString())));
            if(puts) putst=tk.getValueString();
        }
        else throw new Error();
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


}
