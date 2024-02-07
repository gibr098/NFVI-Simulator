package Classes.LinkManagers;
import Classes.*;
import Classes.Links.*;
import Classes.LinkManagers.*;

public class ManagerOwn {
    private LinkOwn link;

    private ManagerOwn(LinkOwn x){
        link = x;
    }
    
    public LinkOwn getLink(){
        return link;
    }

    public static void insert(LinkOwn y){
        if(y!=null && y.getDataCenter().OwnNumber()==0 && y.getNFVIPoP().OwnNumber()==0){
            ManagerOwn k = new ManagerOwn(y);
            y.getDataCenter().insertforManagerOwn(k);
            y.getNFVIPoP().insertforManagerOwn(k);
        }
    }

    public static void remove(LinkOwn y){
        try{
            if(y!=null && y.getDataCenter().getLinkOwn().equals(y)){
                ManagerOwn k = new ManagerOwn(y);
                y.getDataCenter().removeforManagerOwn(k);
                y.getNFVIPoP().removeforManagerOwn(k);
    
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
