package Classes.LinkManagers;

import Classes.Links.LinkVM;

public final class ManagerVM {
    private LinkVM link;

    private ManagerVM(LinkVM x){
        link = x;
    }

    public LinkVM getLink(){
        return link;
    }

    public static void insert(LinkVM y){
        if(y!= null){//} && y.getCOTServer().getLinkInstance()==null && y.getContainer().InstanceNumber()==0){
            ManagerVM k = new ManagerVM(y);
            y.getVirtualMachine().insertforManagerVM(k);
            y.getCOTServer().insertforManagerVM(k);
        }
    }

    public static void remove(LinkVM y){
        if(y!= null && y.getVirtualMachine().getLinkInstance().equals(y)){
            ManagerVM k = new ManagerVM(y);
            y.getVirtualMachine().removeforManagerVM(k);
            y.getCOTServer().removeforManagerVM(k);
        }
    }
}