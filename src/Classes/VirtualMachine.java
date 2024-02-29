package Classes;

import Classes.Links.*;
import Classes.LinkManagers.*;

import java.util.HashSet;
import java.util.Set;



public class VirtualMachine {
    private final String name;


    //VM -> CONTAINER
    private HashSet<LinkInstance> linkset;

    //VM -> COTS
    private LinkVM link;
    

    public VirtualMachine(String name){
        this.name = name;

        linkset = new HashSet<LinkInstance>();
    }

    public String getName(){
        return name;
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



    //COTS -> CONTAINER
    public Set<LinkInstance> getLinkInstance(){
        return (HashSet<LinkInstance>)linkset.clone();
    }

    public void insertLinkInstance(LinkInstance t){
        if(t!=null && t.getCOTServer()==this){
            ManagerInstance.insert(t);
        }
    }
    public void removeLinkInstance(LinkInstance t){
        if(t!=null && t.getCOTServer()==this){
            ManagerInstance.remove(t);
        }
    }

    public void insertforManagerInstance(ManagerInstance a){
        if (a != null) linkset.add(a.getLink());
    }

    public void removeforManagerInstance(ManagerInstance a){
        if (a != null) linkset = null;
    }

    //COTS -> DATA CENTER
    public LinkContain getLinkContain() throws Exception{
        if (link == null) throw new Exception("Minimal cardinality violated: Server must be contained in a Data Center");
        else return link;
    }

    public void insertLinkContain(LinkContain t){
        if(t!=null && t.getCOTServer() == this){
            ManagerContain.insert(t);
        }
    }

    public void removeLinkContain(LinkContain t){
        if(t!=null && t.getCOTServer() == this){
            ManagerContain.remove(t);
        }
    }

    public void insertforManagerContain(ManagerContain a){
        if (a != null) link = a.getLink();
    }

    public void removeforManagerContain(ManagerContain a){
        if (a != null) link = null;
    }

    public String getTotalInfo(){
        String info="";
        info+= this.name+" is contained in "+link.getDataCenter().getName()+" and has instantiatied "+ this.getContainerNumber()+" containers: ";
        for (LinkInstance l : linkset) {
            info+=l.getContainer().getName()+" ";
        }
        for (LinkInstance l : linkset) {
            info+="\n"+l.getContainer().getTotalInfo();
        }


        return info;
    }

    public String getTotalResourcesInfo(){
        String info = this.name+" (";
        info+="ram: "+this.ram+"GB";
        info+=", cpu: "+this.cpu+" cores";
        info+=", storage: "+this.storage+"GB";
        info+=", network: "+this.network;
        info+=", cpu capacity: "+this.cpu_capacity;
        info+=")";

        return info;
    }
}
