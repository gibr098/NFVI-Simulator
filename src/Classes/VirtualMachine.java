package Classes;

import Classes.Links.*;
import Classes.LinkManagers.*;

import java.util.HashSet;
import java.util.Set;


public class VirtualMachine {
    private final String name;

    private boolean runningService;
    private String type;


    //VM -> CONTAINER
    private HashSet<LinkInstance> linkset;

    //VM -> COTS
    private LinkVM link;
    

    public VirtualMachine(String name){
        this.name = name;
        this.runningService = false;

        this.type = "Medium";

        linkset = new HashSet<LinkInstance>();
    }

    public String getType(){
        return this.type;
    }
    public void setType(String size){
        this.type = size;
    }

    public String getName(){
        return name;
    }

    public boolean isRunningService(){
        return runningService;
    }

    public void setRunningServiceState(boolean state){
        runningService = state;
    }

    public int getContainerNumber(){
        return linkset.size();
    }

    public String getRunningContainers(){
        String s = "";
        for (LinkInstance l : linkset) {
            s+=l.getContainer().getName()+" ";
        }
        return s;
    }

    public boolean allContainersBusy(){
        boolean ret = true;
        for(LinkInstance li: linkset){
            ret = ret && li.getContainer().isBusy();
        }
        return ret;
    }



    //VM -> CONTAINER
    public Set<LinkInstance> getLinkInstance(){
        return (HashSet<LinkInstance>)linkset.clone();
    }

    public void insertLinkInstance(LinkInstance t){
        if(t!=null && t.getVirtualMachine()==this){
            ManagerInstance.insert(t);
        }
    }
    public void removeLinkInstance(LinkInstance t){
        if(t!=null && t.getVirtualMachine()==this){
            ManagerInstance.remove(t);
        }
    }

    public void insertforManagerInstance(ManagerInstance a){
        if (a != null) linkset.add(a.getLink());
    }

    public void removeforManagerInstance(ManagerInstance a){
        if (a != null) linkset = null;
    }

    //VM -> COTS
    public LinkVM getLinkVM() throws Exception{
        if (link == null) throw new Exception("Minimal cardinality violated: Server must be contained in a Data Center");
        else return link;
    }

    public void insertLinkVM(LinkVM t){
        if(t!=null && t.getVirtualMachine() == this){
            ManagerVM.insert(t);
        }
    }

    public void removeLinkVM(LinkVM t){
        if(t!=null && t.getVirtualMachine() == this){
            ManagerVM.remove(t);
        }
    }

    public void insertforManagerVM(ManagerVM a){
        if (a != null) link = a.getLink();
    }

    public void removeforManagerVM(ManagerVM a){
        if (a != null) link = null;
    }

    public String getTotalInfo(){
        String info="";
        info+= this.name+" is contained in "+link.getCOTServer().getName()+" and has instantiatied "+ this.getContainerNumber()+" containers: ";
        for (LinkInstance l : linkset) {
            info+=l.getContainer().getName()+" ";
        }
        for (LinkInstance l : linkset) {
            info+="\n"+l.getContainer().getTotalInfo();
        }

        return info;
    }

}
