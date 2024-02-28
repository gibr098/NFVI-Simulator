package Classes;

import java.util.*;


import Classes.*;
import Classes.LinkManagers.*;
import Classes.Links.*;

public class Service {
    private final String name;
    private double time;
    private int type;

    // service is allocated at certain initial time
    private boolean allocated;
    private double initial_time;



    private LinkedList<VNF> functions;

    //Service -> NFVI
    private LinkProvide link;
    //private int MIN_LINK_PROVIDE= 1;
    //private int MAX_LINK_PROVIDE = 1;

    //Service -> VNF
    private LinkedList<LinkChain> linkset;
    private int MIN_LINK_CHAIN = 1;

    public Service(String name, double time, int type){
        this.name = name;
        this.time = time;
        this.type = type;
        this.functions = new LinkedList<VNF>();

        //linkset = new HashSet<LinkProvide>();
        linkset = new LinkedList<LinkChain>();

        this.allocated = false;
        this.initial_time = 0.0;

    }

    public String getName(){
        return name;
    }

    public double getTime(){
        return time;
    }

    public int getVNFNumber(){
        return linkset.size();
    }

    public String getChain(){
        String s="";
        for (LinkChain l : linkset) {
            s+= l.getVNF().getName()+" ";
        }
        return s;
    }

    public double getInitalAllocationTime(){
        return initial_time;
    }

    public void setInitialAllocationTime(double time){
        initial_time = time;
    }

    public boolean isAllocated(){
        return allocated;
    }
    public void setAllocated(boolean state){
        this.allocated = state;
    }

    //Service -> NFVI
    public LinkProvide getLinkProvide() throws Exception{
        if(link == null) throw new Exception("cardinality violated: Service must be provided by a NFVI");
        else return link;
    }

    public void insertLinkProvide(LinkProvide t){
        if(t!=null && t.getService()==this){
            ManagerProvide.insert(t);}
    }

    public void removeLinkProvide(LinkProvide t){
        if(t!=null && t.getService()==this){
            ManagerProvide.remove(t);}
    }

    public void insertforManagerProvide(ManagerProvide a){
        if (a != null) link = a.getLink();
    }

    public void removeforManagerProvide(ManagerProvide a){
        if (a != null) link = null;
    }

    //Service -> VNF
    
    public Set<LinkChain> getLinkChain() throws Exception{
        if(linkset.size() == 0) throw new Exception("Cardinality violated: Service must contain at least 1 VNF");
        else return (HashSet<LinkChain>)linkset.clone();
    }
    
    public List<LinkChain> getLinkChainList() throws Exception{
        if(linkset.isEmpty()) throw new Exception("Cardinality violated: Service must contain at least 1 VNF");
        else return linkset;
    }

    public void insertLinkChain(LinkChain t){
        if(t!=null && t.getService()==this){
            ManagerChain.insert(t);
        }
    }

    public void removeLinkChain(LinkChain t){
        if(t!=null && t.getService()==this){
            ManagerChain.remove(t);}
    }

    public void insertforManagerChain(ManagerChain a){
        if (a != null) linkset.add(a.getLink());
    }

    public void removeforManagerChain(ManagerChain a){
        if (a != null) linkset.remove(a.getLink());
    }

    public String getTotalinfo(){
        String info = "";
        info+= this.getName()+" is provided by "+link.getNFVI().getName()+" and has a chain of "+this.getVNFNumber()+" VNFs: ";
        for (LinkChain l : linkset) {
            info+=l.getVNF().getName()+"["+l.getVNF().getType()+"] ";
        }

        return info;

    }

    public String getTotalResourceRequired(){
        String info="\n"+this.name+" has a chain of "+ this.getVNFNumber()+" VNFs: ";
        int ram=0; int cpu=0; int storage=0; int network = 0; int cpu_usage=0;
        for (LinkChain l : linkset) {
            info+="\n";
            info+=l.getVNF().getName()+"["+l.getVNF().getType()+"] ";
            info+=l.getVNF().getResourcesRequired();
            ram+=l.getVNF().getRam();
            cpu+=l.getVNF().getCPU();
            storage+=l.getVNF().getStorage();
            network+=l.getVNF().getNetwork();
            cpu_usage+=l.getVNF().getCPUusage();
        }
        info+="\n";
        info+= this.name+" requires in total (ram: "+ram+"GB, cpu: "+cpu+" cores, cpu usage: "+ cpu_usage +"%, storage: "+storage+" GB, network: "+network+" interfaces)";

        return info;
    }

}
