package Classes;

import java.util.HashSet;
import java.util.Set;

import Classes.LinkManagers.*;
import Classes.Links.*;

public class Container{
    private final String name;
    private int ram;
    private int cpu;
    private int storage;
    private int network;

    private int cpu_usage;

    private boolean busy;

    private HashSet<VNF> VNFs;

    //CONTAINER -> COTS
    public LinkInstance link;
    public static final int MIN_LINK_INSTANCE = 1;
    public static final int MAX_LINK_INSTANCE = 1;

    public int InstanceNumber(){
        if(link == null){
            return 0;
        }else return 1;
    }

    //Container -> VNF
    public HashSet<LinkRun> linkset;

    public Container(String name, int ram, int cpu, int storage, int network, int cpu_usage){
        this.name = name;
        this.ram = ram;
        this.cpu = cpu;
        this.storage = storage;
        this. network = network;
        this.cpu_usage = cpu_usage;

        this.busy = false;

        linkset = new HashSet<LinkRun>();
    }

    public boolean isBusy(){
        return busy;
    }

    public void setBusyState(boolean state){
        busy = state;
    }

    public String getName(){
        return name;
    }

    public int getRam(){
        return ram;
    }

    public int getCpu(){
        return cpu;
    }

    public int getStorage(){
        return storage;
    }

    public void addStorage(int additionalStorage){
        storage+=additionalStorage;
    }

    public int getNetwork(){
        return network;
    }

    public int getVNFNumber(){
        return linkset.size();
    }

    public Set<VNF> getVNFs(){
        return (HashSet<VNF>)VNFs.clone();
    }

    //Container -> COTS
    public LinkInstance getLinkInstance() throws Exception{
        if (link == null) throw new Exception("cardinalità minima violata Container");
        else
            return link;
    }

    public void insertLinkInstance(LinkInstance t){
        if(t!=null && t.getContainer()==this){
            ManagerInstance.insert(t);
        }
    }
    public void removeLinkInstance(LinkInstance t){
        if(t!=null && t.getContainer()==this){
            ManagerInstance.remove(t);
        }
    }

    public void insertforManagerInstance(ManagerInstance a){
        if (a != null) link = a.getLink();
    }

    public void removeforManagerInstance(ManagerInstance a){
        if (a != null) link = null;
    }

    //Container -> VNF
    public Set<LinkRun> getLinkRun() throws Exception{
            return (HashSet<LinkRun>)linkset.clone();
    }

    public void insertLinkRun(LinkRun t){
        if(t!=null && t.getContainer()==this){
            ManagerRun.insert(t);
        }
    }
    public void removeLinkRun(LinkRun t){
        if(t!=null && t.getContainer()==this){
            ManagerRun.remove(t);
        }
    }

    public void insertforManagerRun(ManagerRun a){
        if (a != null) linkset.add(a.getLink());
    }

    public void removeforManagerRun(ManagerRun a){
        if (a != null) linkset = null;
    }

    public String getTotalInfo(){
        String  info="";
        info+=this.getName()+" is instantiated by "+link.getCOTServer().getName();
        info+=" and runs "+ this.getVNFNumber()+" VNFs: ";
        for (LinkRun l: linkset) {
            info+= l.getVNF().getName()+"["+l.getVNF().getType()+"] ";
        }
        for (LinkRun l: linkset) {
            info+="\n"+l.getVNF().getTotalInfo();
        }

        return info;
    }

    public String getTotalResourcesInfo(){
        String info = this.name+" (";
        info+="ram: "+this.ram+"GB";
        info+=", cpu: "+this.cpu+" cores";
        info+=", storage: "+this.storage+"GB";
        info+=", network: "+this.network;
        info+=")";

        return info;
    }
}
