package Classes;

import java.util.*;
import Classes.*;
import Classes.LinkManagers.*;
import Classes.Links.*;

public class Service {
    private final String name;
    private int time;

    private LinkedList<VNF> functions;

    //Service -> NFVI
    private LinkProvide link;
    //private int MIN_LINK_PROVIDE= 1;
    //private int MAX_LINK_PROVIDE = 1;

    //Service -> VNF
    private LinkedList<LinkChain> linkset;
    private int MIN_LINK_CHAIN = 1;

    public Service(String name, int time){
        this.name = name;
        this.time = time;
        this.functions = new LinkedList<VNF>();

        //linkset = new HashSet<LinkProvide>();
        linkset = new LinkedList<LinkChain>();

    }

    public String getName(){
        return name;
    }

    public int getTime(){
        return time;
    }

    public int getVNFNumber(){
        return linkset.size();
    }

    public String getChain(){
        String s="";
        for (LinkChain l : linkset) {
            s+= l.getVNF().getType()+"-";
        }
        return s;
    }

    //Service -> NFVI
    public LinkProvide getLinkLinkProvide() throws Exception{
        if(link == null) throw new Exception("cardinality violated");
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
        if(linkset.size() == 0) throw new Exception("cardinality violated on service");
        else return (HashSet<LinkChain>)linkset.clone();
    }
    
    public LinkedList<LinkChain> getLinkChainList() throws Exception{
        if(linkset.size() == 0) throw new Exception("cardinality violated on service");
        else return linkset;
    }

    public void insertLinkChain(LinkChain t){
        if(t!=null && t.getService()==this){
            ManagerChain.insert(t);}
    }

    public void removeLinkChain(LinkChain t){
        if(t!=null && t.getService()==this){
            ManagerChain.remove(t);}
    }

    public void insertforManagerChain(ManagerChain a){
        if (a != null) linkset.add(a.getLink());
    }

    public void removeforManagerChain(ManagerChain a){
        if (a != null) linkset = null;
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
