package Classes.LinkManagers;

import Classes.Links.LinkContain;
import Classes.Links.LinkProvide;

public class ManagerProvide {
    private LinkProvide link;

    private ManagerProvide(LinkProvide x){
        link = x;
    }

    public LinkProvide getLink(){
        return link;
    }

    public static void insert(LinkProvide y){
        if(y!=null){
            ManagerProvide k = new ManagerProvide(y);
            y.getNFVI().insertforManagerProvide(k);
            y.getService().insertforManagerProvide(k);
        }
    }

    public static void remove(LinkProvide y){
        try{
            if(y!=null){
                ManagerProvide k = new ManagerProvide(y);
                y.getNFVI().removeforManagerProvide(k);
                y.getService().removeforManagerProvide(k);
    
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
}
