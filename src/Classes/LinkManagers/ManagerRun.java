package Classes.LinkManagers;

import Classes.Links.LinkContain;
import Classes.Links.LinkRun;

public class ManagerRun {
    private LinkRun link;

    private ManagerRun(LinkRun x){
        link = x;
    }

    public LinkRun getLink(){
        return link;
    }
    
    public static void insert(LinkRun y){
        if(y!=null){
            ManagerRun k = new ManagerRun(y);
            y.getContainer().insertforManagerRun(k);
            y.getVNF().insertforManagerRun(k);
        }
    }

    public static void remove(LinkRun y){
        try{
            if(y!=null){
                ManagerRun k = new ManagerRun(y);
                y.getContainer().removeforManagerRun(k);
                y.getVNF().removeforManagerRun(k);
    
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
