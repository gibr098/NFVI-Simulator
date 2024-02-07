package Classes.LinkManagers;

import Classes.Links.LinkCompose;

public class ManagerCompose {
    private LinkCompose link;

    private ManagerCompose(LinkCompose x){
        link = x;
    }

    public LinkCompose getLink(){
        return link;
    }

    public static void insert(LinkCompose y){
        if(y!= null){
            ManagerCompose k = new ManagerCompose(y);
            y.getNFVI().insertforManagerCompose(k);
            y.getNFVIPoP().insertforManagerCompose(k);
        }
    }

    public static void remove(LinkCompose y){
        if(y!= null){
            ManagerCompose k = new ManagerCompose(y);
            y.getNFVI().removeforManagerCompose(k);
            y.getNFVIPoP().removeforManagerCompose(k);
        }
    }
}
