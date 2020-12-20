package miniplc0java.analyser;

class Element{
    String elename="";
    String type="";
    boolean isconst;
    boolean isfunctionname;
    boolean isheader;
    boolean isavailable;
    int headernum;
    int value;
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

public class Symbol {
    public static String[] StandardFunction={"getint","getdouble","getchar","putint","putdouble","putchar","putstr","putln"};
    public static Symbol NSymbol=null;
    public Element[][] Symbols=new Element[20][100];
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
        NowPtr--;
    }
    public boolean addElement(Element ta){
        int i;
        if(ta.type.equals("void")){
            System.out.println("Void Variable!");
            return false;
        }
        for(i=1;i<=Symbols[NowPtr][0].headernum;i++){
            if(Symbols[NowPtr][i].elename.equals(ta.elename)){
                System.out.println("重复声明!");
                return false;
            }
        }
        Symbols[NowPtr][0].headernum++;
        Symbols[NowPtr][Symbols[NowPtr][0].headernum]=ta;
        return true;
    }

    public boolean referElement(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && !Symbols[i][j].isfunctionname) return true;
            }
        }
        System.out.println("未定义变量!");
        return false;
    }

    public boolean changeElement(String eleName){
        int i,j;
        for(i=NowPtr;i>=0;i--){
            for(j=1;j<=Symbols[i][0].headernum;j++){
                if(Symbols[i][j].elename.equals(eleName) && Symbols[i][j].isconst){
                    System.out.println("Change Const!");
                    return false;
                }
            }
        }
        return true;
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
        System.out.println("未定义函数!");
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
        System.out.println("No Main Function!");
        return false;
    }
}
