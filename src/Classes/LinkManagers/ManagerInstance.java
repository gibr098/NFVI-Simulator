package Classes.LinkManagers;

import Classes.Links.LinkInstance;

public final class ManagerInstance {
    private LinkInstance link;

    private ManagerInstance(LinkInstance x){
        link = x;
    }

    public LinkInstance getLink(){
        return link;
    }

    public static void insert(LinkInstance y){
        if(y!= null){//} && y.getCOTServer().getLinkInstance()==null && y.getContainer().InstanceNumber()==0){
            ManagerInstance k = new ManagerInstance(y);
            y.getCOTServer().insertforManagerInstance(k);
            y.getContainer().insertforManagerInstance(k);
        }
    }

    public static void remove(LinkInstance y){
        if(y!= null && y.getCOTServer().getLinkInstance().equals(y)){
            ManagerInstance k = new ManagerInstance(y);
            y.getCOTServer().removeforManagerInstance(k);
            y.getContainer().removeforManagerInstance(k);
        }
    }
}