package miniplc0java.analyser;
class Function{
    String name;
    int fid;
    String[] zl=new String[1000];
    public Function(String name,int fid){
        this.name=name;
        this.fid=fid;
    }
}

public class FunctionList {
    public static FunctionList FL=null;
    Function[] flist=new Function[100];
    int fnum;
    public FunctionList getFunctionList(){
        if(FL==null) FL=new FunctionList();
        return FL;
    }
    private FunctionList(){
        fnum=0;
    }
}
