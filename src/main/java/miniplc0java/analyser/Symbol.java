package miniplc0java.analyser;

import java.awt.*;

class Element{
    String elename="";
    String type="";
    boolean isconst;
    boolean isfunctionname;
    boolean isheader;
    boolean isavailable;
    int headernum;
    int value;
    int id;
    public Element(){};
    public Element(String elename,String type,boolean isconst,boolean isfunctionname){
        this.elename=elename;
        this.type=type;
        this.isconst=isconst;
        this.isfunctionname=isfunctionname;
        this.isheader=false;
        this.headernum=-1;
        this.isavailable=false;
    }
    public Element(boolean isheader){
        this.isheader=true;
        this.headernum=0;
        this.isavailable=true;
    }
}

class StringL{
    int id;
    String svalue;
    int[] ivalue;
    public StringL(int id,String s){
        this.id=id;
        this.svalue=s;
        this.ivalue=new int[s.length()];
        for(int i=0;i<s.length();i++){
            ivalue[i]=s.toCharArray()[i];
        }
    }
}

public class Symbol {
    public static String[] StandardFunction={"getint","getdouble","getchar","putint","putdouble","putchar","putstr","putln"};
    public static Symbol NSymbol=null;
    public Element[][] Symbols=new Element[20][100];

    public StringL[] Sl=new StringL[100];
    int slnum=0;

    public int NowPtr;
    public int TempPtr;
    public static Symbol getSymbol(){
        if(NSymbol==null) NSymbol=new Symbol();
        return NSymbol;
    }
    private Symbol(){
        NowPtr=-1;
        TempPtr=-1;
    }
    public void UpLayer(){
        NowPtr++;
        Symbols[NowPtr][0]=new Element(true);
    }
    public void DownLayer(){
        Symbols[NowPtr][0].isavailable=false;
        bl(NowPtr);
        NowPtr--;
    }
    public boolean addElement(Element ta){
        ta.id=-1;
        int i;
        if(ta.type.equals("void")){
//            System.out.println("Void Variable!");
            return false;
        }
        for(i=1;i<=Symbols[NowPtr][0].headernum;i++){
            if(Symbols[NowPtr][i].elename.equals(ta.elename)){
//                System.out.println("重复声明!");
                return false;
            }
        }
        Symbols[NowPtr][0].headernum++;
        Symbols[NowPtr][Symbols[NowPtr][0].headernum]=ta;
        return true;
    }

    public boolean addElement(int id,Element ta){
        ta.id=id;
        int i;
        if(ta.type.equals("void")){
//            System.out.println("Void Variable!");
            return false;
        }
        for(i=1;i<=Symbols[NowPtr][0].headernum;i++){
            if(Symbols[NowPtr][i].elename.equals(ta.elename)){
//                System.out.println("重复声明!");
                return false;
            }
        }
        Symbols[NowPtr][0].headernum++;
        Symbols[NowPtr][Symbols[NowPtr][0].headernum]=ta;
        return true;
    }

    public boolean addFunction(Element ta){
        int i;
        TempPtr=NowPtr-1;
        for(i=1;i<=Symbols[TempPtr][0].headernum;i++){
            if(Symbols[TempPtr][i].elename.equals(ta.elename)){
//                System.out.println("重复声明!");
                return false;
            }
        }
        Symbols[TempPtr][0].headernum++;
        Symbols[TempPtr][Symbols[TempPtr][0].headernum]=ta;
        return true;
    }

    public boolean referElement(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && !Symbols[i][j].isfunctionname) return true;
            }
        }
//        System.out.println("未定义变量!");
        return false;
    }

    public boolean checkGlobal(String eleName){
        int i,j;
        for(i=NowPtr;i>=1;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && !Symbols[i][j].isfunctionname) return false;
            }
        }
        return true;
    }

    public int getlvId(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && !Symbols[i][j].isfunctionname) return Symbols[i][j].id;
            }
        }
        return -1;
    }

    public boolean changeElement(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && Symbols[i][j].isconst){
//                    System.out.println("Change Const!");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean changeIdent(String eleName,int value){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && !Symbols[i][j].isfunctionname && !Symbols[i][j].isconst){
                    Symbols[i][j].value=value;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean callElement(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && Symbols[i][j].isfunctionname) return true;
            }
        }
        for(i=0;i<8;i++){
            if(StandardFunction[i].equals(eleName)) return true;
        }
//        System.out.println("未定义函数!");
        return false;
    }

    public boolean findFunction(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && Symbols[i][j].isfunctionname) return true;
            }
        }
        for(i=0;i<8;i++){
            if(StandardFunction[i].equals(eleName)) return true;
        }
        return false;
    }

    public boolean checkMain(){
        for(int i=1;i<=Symbols[0][0].headernum;i++){
            if(Symbols[0][i].elename.equals("main") && Symbols[0][i].isfunctionname) return true;
        }
//        System.out.println("No Main Function!");
        return false;
    }

    public int getIdentValue(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && !Symbols[i][j].isfunctionname) return Symbols[i][j].value;
            }
        }
        return -1;
    }

    public boolean checkReturn(String ele){
        for(int i=1;i<=Symbols[0][0].headernum;i++){
//            System.out.println(Symbols[0][i].elename);
            if(Symbols[0][i].elename.equals(ele) && Symbols[0][i].type.equals("void")) return true;
        }
//        System.out.println("Where is my return?");
        return false;
    }

    public void bl(int layer){
        for(int i=1;i<=Symbols[layer][0].headernum;i++){
//            if(!Symbols[layer][i].isfunctionname) System.out.println(Symbols[layer][i].elename+"'s value:"+Symbols[layer][i].value);
        }
    }

    public String getElementType(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName)) return Symbols[i][j].type;
            }
        }
        if(eleName.equals("getint")) return "int";
        else if(eleName.equals("getdouble")) return "double";
        else if(eleName.equals("getchar")) return "int";
        else if(eleName.equals("putint")) return "void";
        else if(eleName.equals("putdouble")) return "void";
        else if(eleName.equals("putchar")) return "void";
        else if(eleName.equals("putstr")) return "void";
        else if(eleName.equals("putln")) return "void";
        return "unknown";
    }

    public void addStringL(String s){
        int i;
        for(i=0;i<slnum;i++){
            if(Sl[i].svalue.equals(s)) return;
        }
        Sl[slnum]=new StringL(slnum,s);
        slnum++;
    }

    public int getId(String s){
        int i;
        for(i=0;i<slnum;i++){
            if(Sl[i].svalue.equals(s)) return Sl[i].id;
        }
        return -1;
    }

    public void bls(){
        for(int i=0;i<slnum;i++){
//            System.out.println(Sl[i].id+","+Sl[i].svalue);
        }
    }
}
