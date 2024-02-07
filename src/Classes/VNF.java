package Classes;

import java.util.*;

import Classes.LinkManagers.*;
import Classes.Links.*;

public class VNF {
    private final String name;
    private final String type;

    //VNF -> Service
    private HashSet<LinkChain> linksetC;

    //VNF -> Container
    private HashSet<LinkRun> linkset;

    public VNF(String name, String type){
        this.name = name;
        this.type = type;

        linksetC = new HashSet<LinkChain>();
        linkset = new HashSet<LinkRun>();
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public String getContainers(){
        String s=" ";
        for (LinkRun linkRun : linkset) {
            s+= linkRun.getContainer().getName();
        }
        return s;
    }

    public int getContainerNumber(){
        return linkset.size();
    }

    //VNF -> Service
    public Set<LinkChain> getLinkChain(){
        return (HashSet<LinkChain>)linksetC.clone();
    }

    public void insertLinkChain(LinkChain t){
        if (t!=null && t.getVNF()==this){
            ManagerChain.insert(t);
        }
    }
    public void removeLinkChain(LinkChain t){
        if (t!=null && t.getVNF()==this){
            ManagerChain.remove(t);
        }
    }

    public void insertforManagerChain(ManagerChain a){
        if (a != null) linksetC.add(a.getLink());
    }

    public void removeforManagerChain(ManagerChain a){
        if (a != null) linksetC = null;
    }

    //VNF -> Container
    public Set<LinkRun> getLinkRun(){
        return (HashSet<LinkRun>)linkset.clone();
    }

    public void insertLinkRun(LinkRun t){
        if (t!=null && t.getVNF()==this){
            ManagerRun.insert(t);
        }
    }
    public void removeLinkRun(LinkRun t){
        if (t!=null && t.getVNF()==this){
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
        String info="";
        info+=this.getName()+"["+this.getType()+"] runs on "+this.getContainerNumber()+" containers: ";
        for (LinkRun l : linkset) {
            info+= l.getContainer().getName()+" ";
        }
        info+= "\n"+this.getName()+"["+this.getType()+"] is in the chain of "+linksetC.size()+" services: ";
        for (LinkChain l : linksetC) {
            info+=l.getService().getName()+" ";
        }
        
        

        return info;
    }

}
