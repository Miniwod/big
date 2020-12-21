package miniplc0java.analyser;
class StackEle{
    String name;
    String type;
    int value;
    String svalue;
    boolean iscall=false;
    public void info(){
        System.out.println("Type:"+type+",Name:"+name+",Value:"+(type.equals("stringl")?svalue:value));
    }
    public StackEle(String name,String type,int value){
        this.name=name;
        this.type=type;
        this.value=value;
    }
    public StackEle(String name,String type){
        this.name=name;
        this.type=type;
        this.value=-1;
        if(type.equals("call")) this.iscall=true;
    }
    public StackEle(String type,int value){
        this.name="Uint";
        this.type=type;
        this.value=value;
    }
    public StackEle(int a,String type,String value){
        this.name="StringL";
        this.type=type;
        this.svalue=value;
    }
    public StackEle(String type){
        this.type=type;
    }
}

public class Stack {
    public static Stack Sc=null;
    StackEle[] Sl=new StackEle[1005];
    StackEle[] tmp=new StackEle[1005];
    int Ptr;
    int BasePtr;
    int tmpPtr;
    public static Stack getStack(){
        if(Sc==null) Sc=new Stack();
        return Sc;
    }
    private Stack(){
        Ptr=-1;
        BasePtr=0;
        tmpPtr=-1;
    }
    public boolean push(StackEle se){
        if(Ptr>=1000) return false;
        Sl[++Ptr]=se;
        if(se.type.equals("uint")) tmp[++tmpPtr]=new StackEle("uint",se.value);
        else tmp[++tmpPtr]=se;
        return true;
    }

    public boolean push(String type){
        return this.push(new StackEle(type));
    }

    public StackEle pop(){
        return Sl[Ptr--];
    }

    public void bl(){
        for(int i=0;i<=Ptr;i++){
            System.out.println(Sl[i].type);
        }
    }
}
