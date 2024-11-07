package Classes.LinkManagers;

import Classes.*;
import Classes.Links.*;
import Classes.LinkManagers.*;

public class ManagerContain {
    private LinkContain link;

    private ManagerContain(LinkContain x){
        link = x;
    }

    public LinkContain getLink(){
        return link;
    }

    public static void insert(LinkContain y){
        if(y!=null){// && y.getDataCenter().ServerContainNumber()==0 && y.getCOTServer().ContainNumber() == 0){
            ManagerContain k = new ManagerContain(y);
            y.getDataCenter().insertforManagerContain(k);
            y.getCOTServer().insertforManagerContain(k);
        }
    }

    public static void remove(LinkContain y){
        try{
            if(y!=null && y.getDataCenter().getLinkContain().contains(y)){
                ManagerContain k = new ManagerContain(y);
                y.getDataCenter().removeforManagerContain(k);
                y.getCOTServer().removeforManagerContain(k);
    
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}