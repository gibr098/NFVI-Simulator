package Classes;

import Classes.Links.*;
import Classes.LinkManagers.*;

import java.util.HashSet;
import java.util.Set;



public class COTServer {
    private final String name;
    private int ram;
    private int cpu;
    private int storage;
    private int network;

    private int cpu_capacity = 100;

    private HashSet<Container> containers;

    //COTS -> VM
    private HashSet<LinkVM> linkset;

    //COTS -> DATA CENTER
    private LinkContain link;
    //private int MIN_LINK_CONTAIN = 1;
    //private int MAX_LINK_CONTAIN = 1;

    public int ContainNumber(){
        if(link == null){
            return 0;
        }else return 1;
    }

    public COTServer(String name, int ram, int cpu, int storage, int network){
        this.name = name;
        this.ram = ram;
        this.cpu = cpu;
        this.storage = storage;
        this. network = network;

        this.containers = new HashSet<Container>();

        linkset = new HashSet<LinkVM>();
    }

    public String getName(){
        return name;
    }

    public int getRam(){
        return ram;
    }

    public void allocateRam(int r){
        ram-=r;
    }

    public void deallocateRam(int r){
        ram+=r;
    }

    public int getCpu(){
        return cpu;
    }

    public void allocateCpu(int c){
        cpu-=c;
    }

    public void deallocateCpu(int c){
        cpu+=c;
    }

    public int getStorage(){
        return storage;
    }

    public void allocateStorage(int st){
        storage-=st;
    }

    public void deallocateStorage(int st){
        storage+=st;
    }

    public void addStorage(int additionalStorage){
        storage+=additionalStorage;
    }

    public int getNetwork(){
        return network;
    }

    public void allocateCPUusage(int cu){
        cpu_capacity-=cu;
    }

    public void deallocateCPUusage(int cu){
        cpu_capacity+=cu;
    }

    public Set<Container> getContainers(){
        return (HashSet<Container>)containers.clone();
    }

    public void addContainer(Container c){
        if(c!=null) containers.add(c);
    }

    public void removeContainer(Container c){
        if(c!=null) containers.remove(c);
    }

    public int getContainerNumber(){
        return linkset.size();
    }


    //COTS -> VM
    public Set<LinkVM> getLinkVM(){
        return (HashSet<LinkVM>)linkset.clone();
    }

    public void insertLinkVM(LinkVM t){
        if(t!=null && t.getCOTServer()==this){
            ManagerVM.insert(t);
        }
    }
    public void removeLinkVM(LinkVM t){
        if(t!=null && t.getCOTServer()==this){
            ManagerVM.remove(t);
        }
    }

    public void insertforManagerVM(ManagerVM a){
        if (a != null) linkset.add(a.getLink());
    }

    public void removeforManagerVM(ManagerVM a){
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
        for (LinkVM l : linkset) {
            info+=l.getVirtualMachine().getName()+" ";
        }
        for (LinkVM l : linkset) {
            info+="\n"+l.getVirtualMachine().getTotalInfo();
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
