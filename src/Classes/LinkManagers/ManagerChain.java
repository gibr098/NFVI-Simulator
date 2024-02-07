package Classes.LinkManagers;

import Classes.Links.LinkChain;
import Classes.Links.LinkContain;

public class ManagerChain {
    private LinkChain link;

    private ManagerChain(LinkChain x){
        link = x;
    }

    public LinkChain getLink(){
        return link;
    }

    public static void insert(LinkChain y){
        if(y!=null){
            ManagerChain k = new ManagerChain(y);
            y.getService().insertforManagerChain(k);
            y.getVNF().insertforManagerChain(k);
        }
    }

    public static void remove(LinkChain y){
        try{
            if(y!=null){
                ManagerChain k = new ManagerChain(y);
                y.getService().removeforManagerChain(k);
                y.getVNF().removeforManagerChain(k);
    
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
