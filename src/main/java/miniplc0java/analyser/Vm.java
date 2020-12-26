package miniplc0java.analyser;

import java.io.OutputStream;
import java.io.PrintStream;

enum InstructionType{
    nop,
    push,
    pop,
    popn,
    dup,
    loca,
    arga,
    globa,
    load,
    store,
    alloc,
    free,
    stackalloc,
    add,
    sub,
    mul,
    div,
    shl,
    shr,
    and,
    or,
    xor,
    not,
    cmp,
    neg,
    itof,
    ftoi,
    shrl,
    setlt,
    setgt,
    br,
    brf,
    brt,
    call,
    ret,
    callname,
    scan,
    print,
    println,
    prints,
    panic,
    breakl,
    continuel,
    putc,
    ;
}

class Instruction{
    InstructionType type;
    int opn;
    int index;
    boolean withop;
    String zj="";
    public Instruction(InstructionType type){
        this.type=type;
        this.opn=-1;
        this.withop=false;
//        this.index=Vm.getVm().topFunction().bodynum;
    }
    public Instruction(InstructionType type,int opn){
        this.type=type;
        this.opn=opn;
        this.withop=true;
//        this.index=Vm.getVm().topFunction().bodynum;
    }
    public Instruction(int index,InstructionType type){
        this.type=type;
        this.withop=true;
        this.index=index;
    }
    public Instruction(InstructionType type,String zj){
        this.type=type;
        this.withop=true;
        this.zj=zj;
    }
    public void info(){
        System.out.println(type+(withop?(zj.equals("")?""+opn:zj):"")+"");
    }
    public void out(){
        int n=8;
        String ejz="";
        {
            if(this.type==InstructionType.nop){
                ejz="0x00";
            }
            else if(this.type==InstructionType.putc){
                ejz="0x55";
            }
            else if(this.type==InstructionType.push){
                ejz="0x01";
                n=8;
            }
            else if(this.type==InstructionType.pop){
                ejz="0x02";
            }
            else if(this.type==InstructionType.popn){
                ejz="0x03";
                n=4;
            }
            else if(this.type==InstructionType.dup){
                ejz="0x04";
            }
            else if(this.type==InstructionType.loca){
                ejz="0x0a";
                n=4;
            }
            else if(this.type==InstructionType.arga){
                ejz="0x0b";
                n=4;
            }
            else if(this.type==InstructionType.globa){
                ejz="0x0c";
                n=4;
            }
            else if(this.type==InstructionType.load){
                ejz="0x13";
            }
            else if(this.type==InstructionType.store){
                ejz="0x17";
            }
            else if(this.type==InstructionType.alloc){
                ejz="0x18";
            }
            else if(this.type==InstructionType.free){
                ejz="0x19";
            }
            else if(this.type==InstructionType.stackalloc){
                ejz="0x1a";
                n=4;
            }
            else if(this.type==InstructionType.add){
                ejz="0x20";
            }
            else if(this.type==InstructionType.sub){
                ejz="0x21";
            }
            else if(this.type==InstructionType.mul){
                ejz="0x22";
            }
            else if(this.type==InstructionType.div){
                ejz="0x23";
            }
            else if(this.type==InstructionType.shl){
                ejz="0x29";
            }
            else if(this.type==InstructionType.shr){
                ejz="0x2a";
            }
            else if(this.type==InstructionType.and){
                ejz="0x2b";
            }
            else if(this.type==InstructionType.or){
                ejz="0x2c";
            }
            else if(this.type==InstructionType.xor){
                ejz="0x2d";
            }
            else if(this.type == InstructionType.not){
                ejz="0x2e";
            }
            else if(this.type==InstructionType.cmp){
                ejz="0x30";
            }
            else if(this.type==InstructionType.neg){
                ejz="0x35";
            }
            else if(this.type==InstructionType.itof){
                ejz="0x36";
            }
            else if(this.type==InstructionType.ftoi){
                ejz="0x37";
            }
            else if(this.type==InstructionType.shrl){
                ejz="0x38";
            }
            else if(this.type==InstructionType.setlt){
                ejz="0x39";
            }
            else if(this.type==InstructionType.setgt){
                ejz="0x3a";
            }
            else if(this.type==InstructionType.br){
                ejz="0x41";
                n=4;
            }
            else if(this.type==InstructionType.brf){
                ejz="0x42";
                n=4;
            }
            else if(this.type==InstructionType.brt){
                ejz="0x43";
                n=4;
            }
            else if(this.type==InstructionType.call){
                ejz="0x48";
                n=4;
            }
            else if(this.type==InstructionType.ret){
                ejz="0x49";
            }
            else if(this.type==InstructionType.callname){
                ejz="0x4a";
                n=4;
            }
            else if(this.type==InstructionType.scan){
                ejz="0x50";
            }
            else if(this.type==InstructionType.print){
                ejz="0x54";
            }
            else if(this.type==InstructionType.prints){
                ejz="0x57";
            }
            else if(this.type==InstructionType.println){
                ejz="0x58";
            }
            else if(this.type==InstructionType.panic){
                ejz="0xfe";
            }
            else if(this.type==InstructionType.breakl){
                ejz="0x41";
                n=4;
            }
            else if(this.type==InstructionType.continuel){
                ejz="0x41";
                n=4;
            }
        }
        System.out.print(ejz);
        System.out.print(" ");
        if(withop) System.out.print(Vm.ejz(n,opn));
//        if(withop) System.out.print(opn);
    }
    public void debug(){
        int n=8;
        String ejz="";
        {
            if(this.type==InstructionType.nop){
                ejz="nop";
            }
            else if(this.type==InstructionType.putc){
                ejz="print.c";
            }
            else if(this.type==InstructionType.push){
                ejz="push";
                n=8;
            }
            else if(this.type==InstructionType.pop){
                ejz="pop";
            }
            else if(this.type==InstructionType.popn){
                ejz="popn";
                n=4;
            }
            else if(this.type==InstructionType.dup){
                ejz="dup";
            }
            else if(this.type==InstructionType.loca){
                ejz="loca";
                n=4;
            }
            else if(this.type==InstructionType.arga){
                ejz="arga";
                n=4;
            }
            else if(this.type==InstructionType.globa){
                ejz="globa";
                n=4;
            }
            else if(this.type==InstructionType.load){
                ejz="load";
            }
            else if(this.type==InstructionType.store){
                ejz="store";
            }
            else if(this.type==InstructionType.alloc){
                ejz="alloc";
            }
            else if(this.type==InstructionType.free){
                ejz="free";
            }
            else if(this.type==InstructionType.stackalloc){
                ejz="stackalloc";
                n=4;
            }
            else if(this.type==InstructionType.add){
                ejz="add";
            }
            else if(this.type==InstructionType.sub){
                ejz="sub";
            }
            else if(this.type==InstructionType.mul){
                ejz="mul";
            }
            else if(this.type==InstructionType.div){
                ejz="div";
            }
            else if(this.type==InstructionType.shl){
                ejz="shl";
            }
            else if(this.type==InstructionType.shr){
                ejz="shr";
            }
            else if(this.type==InstructionType.and){
                ejz="and";
            }
            else if(this.type==InstructionType.or){
                ejz="or";
            }
            else if(this.type==InstructionType.xor){
                ejz="xor";
            }
            else if(this.type == InstructionType.not){
                ejz="not";
            }
            else if(this.type==InstructionType.cmp){
                ejz="cmp";
            }
            else if(this.type==InstructionType.neg){
                ejz="neg";
            }
            else if(this.type==InstructionType.itof){
                ejz="itof";
            }
            else if(this.type==InstructionType.ftoi){
                ejz="ftoi";
            }
            else if(this.type==InstructionType.shrl){
                ejz="shrl";
            }
            else if(this.type==InstructionType.setlt){
                ejz="setlt";
            }
            else if(this.type==InstructionType.setgt){
                ejz="setgt";
            }
            else if(this.type==InstructionType.br){
                ejz="br";
                n=4;
            }
            else if(this.type==InstructionType.brf){
                ejz="br.false";
                n=4;
            }
            else if(this.type==InstructionType.brt){
                ejz="br.true";
                n=4;
            }
            else if(this.type==InstructionType.call){
                ejz="call";
                n=4;
            }
            else if(this.type==InstructionType.ret){
                ejz="ret";
            }
            else if(this.type==InstructionType.callname){
                ejz="callname";
                n=4;
            }
            else if(this.type==InstructionType.scan){
                ejz="scan";
            }
            else if(this.type==InstructionType.print){
                ejz="print.i";
            }
            else if(this.type==InstructionType.prints){
                ejz="print.s";
            }
            else if(this.type==InstructionType.println){
                ejz="println";
            }
            else if(this.type==InstructionType.panic){
                ejz="panic";
            }
            else if(this.type==InstructionType.breakl){
                ejz="br";
                n=4;
            }
            else if(this.type==InstructionType.continuel){
                ejz="br";
                n=4;
            }
        }
        System.out.print(ejz);
        System.out.print(" ");
//        if(withop) System.out.print(Vm.ejz(n,opn));
        if(withop) System.out.print(opn);
    }
    public void eout(PrintStream output){
        int n=8;
        String ejz="";
        {
            if(this.type==InstructionType.nop){
                ejz=Vm.zejzs(1,0x00);
            }
            else if(this.type==InstructionType.putc){
                ejz=Vm.zejzs(1,0x55);
            }
            else if(this.type==InstructionType.push){
                ejz=Vm.zejzs(1,0x01);
                n=8;
            }
            else if(this.type==InstructionType.pop){
                ejz=Vm.zejzs(1,0x02);
            }
            else if(this.type==InstructionType.popn){
                ejz=Vm.zejzs(1,0x03);
                n=4;
            }
            else if(this.type==InstructionType.dup){
                ejz=Vm.zejzs(1,0x04);
            }
            else if(this.type==InstructionType.loca){
                ejz=Vm.zejzs(1,0x0a);
                n=4;
            }
            else if(this.type==InstructionType.arga){
                ejz=Vm.zejzs(1,0x0b);
                n=4;
            }
            else if(this.type==InstructionType.globa){
                ejz=Vm.zejzs(1,0x0c);
                n=4;
            }
            else if(this.type==InstructionType.load){
                ejz=Vm.zejzs(1,0x13);
            }
            else if(this.type==InstructionType.store){
                ejz=Vm.zejzs(1,0x17);
            }
            else if(this.type==InstructionType.alloc){
                ejz=Vm.zejzs(1,0x18);
            }
            else if(this.type==InstructionType.free){
                ejz=Vm.zejzs(1,0x19);
            }
            else if(this.type==InstructionType.stackalloc){
                ejz=Vm.zejzs(1,0x1a);
                n=4;
            }
            else if(this.type==InstructionType.add){
                ejz=Vm.zejzs(1,0x20);
            }
            else if(this.type==InstructionType.sub){
                ejz=Vm.zejzs(1,0x21);
            }
            else if(this.type==InstructionType.mul){
                ejz=Vm.zejzs(1,0x22);
            }
            else if(this.type==InstructionType.div){
                ejz=Vm.zejzs(1,0x23);
            }
            else if(this.type==InstructionType.shl){
                ejz=Vm.zejzs(1,0x29);
            }
            else if(this.type==InstructionType.shr){
                ejz=Vm.zejzs(1,0x2a);
            }
            else if(this.type==InstructionType.and){
                ejz=Vm.zejzs(1,0x2b);
            }
            else if(this.type==InstructionType.or){
                ejz=Vm.zejzs(1,0x2c);
            }
            else if(this.type==InstructionType.xor){
                ejz=Vm.zejzs(1,0x2d);
            }
            else if(this.type == InstructionType.not){
                ejz=Vm.zejzs(1,0x2e);
            }
            else if(this.type==InstructionType.cmp){
                ejz=Vm.zejzs(1,0x30);
            }
            else if(this.type==InstructionType.neg){
                ejz=Vm.zejzs(1,0x34);
            }
            else if(this.type==InstructionType.itof){
                ejz=Vm.zejzs(1,0x36);
            }
            else if(this.type==InstructionType.ftoi){
                ejz=Vm.zejzs(1,0x37);
            }
            else if(this.type==InstructionType.shrl){
                ejz=Vm.zejzs(1,0x38);
            }
            else if(this.type==InstructionType.setlt){
                ejz=Vm.zejzs(1,0x39);
            }
            else if(this.type==InstructionType.setgt){
                ejz=Vm.zejzs(1,0x3a);
            }
            else if(this.type==InstructionType.br){
                ejz=Vm.zejzs(1,0x41);
                n=4;
            }
            else if(this.type==InstructionType.brf){
                ejz=Vm.zejzs(1,0x42);
                n=4;
            }
            else if(this.type==InstructionType.brt){
                ejz=Vm.zejzs(1,0x43);
                n=4;
            }
            else if(this.type==InstructionType.call){
                ejz=Vm.zejzs(1,0x48);
                n=4;
            }
            else if(this.type==InstructionType.ret){
                ejz=Vm.zejzs(1,0x49);
            }
            else if(this.type==InstructionType.callname){
                ejz=Vm.zejzs(1,0x4a);
                n=4;
            }
            else if(this.type==InstructionType.scan){
                ejz=Vm.zejzs(1,0x50);
            }
            else if(this.type==InstructionType.print){
                ejz=Vm.zejzs(1,0x54);
            }
            else if(this.type==InstructionType.prints){
                ejz=Vm.zejzs(1,0x57);
            }
            else if(this.type==InstructionType.println){
                ejz=Vm.zejzs(1,0x58);
            }
            else if(this.type==InstructionType.panic){
                ejz=Vm.zejzs(1,0xfe);
            }
            else if(this.type==InstructionType.breakl){
                ejz=Vm.zejzs(1,0x41);
                n=4;
            }
            else if(this.type==InstructionType.continuel){
                ejz=Vm.zejzs(1,0x41);
                n=4;
            }
        }
        output.print(ejz);
//        output.print(" ");
        if(withop) output.print(Vm.zejzs(n,opn));
    }
}

class GlobalVariable{
    int id;
    String name="";//for human to see
    boolean isconst;
    boolean string;
    String svalue;
    int ivalue;
    int[] ejz;
    public GlobalVariable(int id,String name,int value,boolean isconst){
        this.id=id;
        this.name=name;
        this.ivalue=value;
        string=false;
        this.isconst=isconst;
    }

    public GlobalVariable(int id,String svalue){
        this.id=id;
        this.svalue=svalue;
        this.name=svalue;
        string=true;
        this.isconst=true;
    }
    public void out(){
        System.out.print(Vm.ejz(1,(isconst)?1:0));
        System.out.println();
        if(!string){
            System.out.print(Vm.ejz(4,8));
            System.out.println();
            System.out.print(Vm.ejz(8,ivalue));
        }
        else{
            int l=svalue.length();
            System.out.print(Vm.ejz(4,l));
            System.out.println();
            for(int i=0;i<l;i++){
                int c=svalue.toCharArray()[i];
                System.out.print(Vm.ejz(1,c));
            }
        }
        System.out.println();
    }
    public void eout(PrintStream output){
        output.print(Vm.zejzs(1,(isconst)?1:0));
//        output.println();
        if(!string){
            output.print(Vm.zejzs(4,8));
//            output.println();
            output.print(Vm.zejzs(8,ivalue));
        }
        else{
            int l=svalue.length();
            output.print(Vm.zejzs(4,l));
//            output.println();
            for(int i=0;i<l;i++){
                int c=svalue.toCharArray()[i];
                output.print(Vm.zejzs(1,c));
            }
        }
//        output.println();
    }
}

class LocalVariable{
    int off;
    int layer;
    String name="";
    int ivalue;
//    public LocalVariable(int layer,int off,String name){
//        this.layer=layer;
//        this.name=name;
//        this.off=off;
//    }
    public LocalVariable(int off,String name){
        this.name=name;
        this.off=off;
    }
}

class Function{
    int id;
    String name;
    String type;
    int bodynum;
    int retnum;
    int paranum;
    int localnum;
    Instruction[] ilist=new Instruction[1000];
    LocalVariable[] paramlist=new LocalVariable[100];
    LocalVariable[] lvlist=new LocalVariable[100];
    public Function(int id,String name,String type){


        this.id=id;
        this.name=name;
        this.type=type;
        if(type.equals("void")){
            this.retnum=0;
        }
        else if(type.equals("int")){
            this.retnum=1;
        }
        this.retnum=0;
        this.paranum=0;
        this.bodynum=0;
        localnum=0;
//        System.out.println(name+" "+this.retnum);
    }
    public void addInstruction(Instruction i){
        this.ilist[this.bodynum]=i;
        this.bodynum++;
    }
    public void addParam(LocalVariable l){
//        System.out.println(l.off+","+l.name);

//        l.off+=this.retnum;
        paramlist[paranum++]=l;
    }
    public void imint(){
//        System.out.println("ha?");
        this.retnum=1;
        for(int i=0;i<paranum;i++) paramlist[i].off++;
    }
    public void addLocalVariable(LocalVariable l){
        lvlist[localnum++]=l;
    }
    public int getParamId(String name){
        for(int i=0;i<paranum;i++){
            if(paramlist[i].name.equals(name)) return paramlist[i].off;
        }
        return -1;
    }

    public void out(){
        System.out.print(Vm.ejz(4,id));
        System.out.println();
        System.out.print(Vm.ejz(4,retnum));
        System.out.println();
        System.out.print(Vm.ejz(4,paranum));
        System.out.println();
        System.out.print(Vm.ejz(4,localnum));
        System.out.println();
        System.out.print(Vm.ejz(4,bodynum));
        System.out.println();
        for(int i=0;i<bodynum;i++){
            System.out.print(i+":");
//            ilist[i].out();
            ilist[i].debug();
            System.out.println();
        }
        System.out.println();
    }
    public void eout(PrintStream output){
        output.print(Vm.zejzs(4,id));
//        output.println();
        output.print(Vm.zejzs(4,retnum));
//        output.println();
        output.print(Vm.zejzs(4,paranum));
//        output.println();
        output.print(Vm.zejzs(4,localnum));
//        output.println();
        output.print(Vm.zejzs(4,bodynum));
//        output.println();
        for(int i=0;i<bodynum;i++){
            ilist[i].eout(output);
//            output.println();
        }
//        output.println();
    }
}

public class Vm {
    public static Vm v=null;
    Instruction[] vis=new Instruction[1000];
    Function[] fl=new Function[100];
    GlobalVariable[] gv=new GlobalVariable[100];
    int fln;
    int gvn;
    int num;                                                                                                                                                                                                       ;
    public static Vm getVm(){
        if(v==null) v=new Vm();
        return v;
    }
    public static String ejz(int n,int value){
        int i=0;
        n=n*2;
        char[] is=new char[n];
        String s="0x";
        if(value<0){
            int[] ejzs=sze(n*4,-value);

            for(int j=0;j<n*4;j++){
                ejzs[j]=ejzs[j]==1?0:1;
            }
            ejzs=ejy(n*4,ejzs);
            for(int j=0;j<n;j++){
                s+=ezsl(ejzs,j*4,j*4+3);
            }
            return s;
        }
        for(i=n-1;i>=0;i--){
            if(value%16<10) is[i]=(char) ('0'+value%16);
            else is[i]=(char) ('a'+value%16-10);
            value/=16;
        }
        s=s+String.valueOf(is);
        return s;
    }

    public static int[] ejy(int n,int[] ejzs){
        boolean one=true;
        int i;
        if(ejzs[n-1]==0){
            ejzs[n-1]=1;
            return ejzs;
        }
        for(i=n-1;i>=0;i--){
            if(ejzs[i]==0){
                ejzs[i]=1;
                break;
            }
            ejzs[i]=0;
        }
        return ejzs;
    }

    public static char ezsl(int[] ejz,int l,int r){
        int ans=0;
        for(int i=l;i<=r;i++){
            ans+=pow(2,r-i)*ejz[i];
        }
        return (ans>=10)?(char) ('a'+ans-10):(char) ('0'+ans);
    }

    public static int[] sze(int n,int value){
        int[] ejzs=new int[n];
        for(int i=n-1;i>=0;i--){
            ejzs[i]=value%2;
            value/=2;
        }
        return ejzs;
    }

    public static int ezs(int n,int ejzs[]){
        int i,ans=0;
        for(i=0;i<n;i++){
            ans+=pow(2,n-i-1)*ejzs[i];
        }
        return ans;
    }

    public static int pow(int i,int p){
        int ans=1;
        for(int j=0;j<p;j++) ans*=i;
        return ans;
    }

    private Vm(){
        fln=0;
        gvn=0;
        num=0;
    }

    public static String zejzs(int n,int value){
        int i=0;
        n=n*2;
        char[] is=new char[n*4];
        String s="";
        if(value<0){
            int[] ejzs=sze(n*4,-value);
            for(int j=0;j<n*4;j++) {
                ejzs[j] = ejzs[j] == 1 ? 0 : 1;
            }
            ejzs=ejy(n*4,ejzs);
            for(int j=0;j<n*4;j++){
                s+=(char) (ejzs[j]+'0');
            }
            return s;
        }
        n=n*4;
        for(i=n-1;i>=0;i--){
            is[i]=(char) (value%2+'0');
            value/=2;
        }
        s=s+String.valueOf(is);
        return s;
    }

    public void addInstruction(Instruction i){
        vis[num++]=i;
    }
    public void bl(){
        int i=0;
        for(i=0;i<num;i++) vis[i].info();
    }
    public void out(){
        System.out.print("0x73203b3e");
        System.out.println();
        System.out.print("0x00000001");
        System.out.println();
        System.out.println();
        System.out.print(ejz(4,gvn));
        System.out.println();
        System.out.println();
        for(int i=0;i<gvn;i++){
            gv[i].out();
            System.out.println();
        }
        System.out.print(ejz(4,fln));
        System.out.println();
        System.out.println();
        for(int i=0;i<fln;i++){
            fl[i].out();
        }
    }
    public void eout(PrintStream output){
        output.print(zejzs(4,0x73203b3e));
//        output.println();

        output.print(zejzs(4,0x00000001));
//        output.println();
//        output.println();

        output.print(zejzs(4,gvn));
//        output.println();
//        output.println();
        for(int i=0;i<gvn;i++){
            gv[i].eout(output);
//            output.println();
        }
        output.print(ejz(4,fln));
//        output.println();
//        output.println();
        for(int i=0;i<fln;i++){
            fl[i].eout(output);
        }
    }
    public Function topFunction(){
        if(fln>0) return fl[fln-1];
        return null;
    }
    public int getGVId(String name){
        for(int i=0;i<gvn;i++){
            if(gv[i].name.equals(name)) return gv[i].id;
        }
        return -1;
    }
    public int getFunctionId(String name){
        for(int i=1;i<fln;i++){
            if(fl[i].name.equals(name)) return i;
        }
        return -1;
    }
    public void addFunction(Function f){
        fl[fln]=f;
        fln++;
    }
    public void addGlobalVariable(GlobalVariable gvs){
        gv[gvn]=gvs;
        gvn++;
    }
    public String getFcType(String name){
        String res="";
        for(int i=1;i<fln;i++){
            if(fl[i].name.equals(name)) res=fl[i].type;
        }
        return res;
    }
}
