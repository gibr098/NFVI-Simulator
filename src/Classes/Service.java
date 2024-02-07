package Classes;

import java.util.*;
import Classes.*;
import Classes.LinkManagers.*;
import Classes.Links.*;

public class Service {
    private final String name;
    private int cost;

    private LinkedList<VNF> functions;

    //Service -> NFVI
    private LinkProvide link;
    //private int MIN_LINK_PROVIDE= 1;
    //private int MAX_LINK_PROVIDE = 1;

    //Service -> VNF
    private LinkedList<LinkChain> linkset;
    private int MIN_LINK_CHAIN = 1;

    public Service(String name, int cost){
        this.name = name;
        this.cost = cost;
        functions = new LinkedList<VNF>();

        //linkset = new HashSet<LinkProvide>();
        linkset = new LinkedList<LinkChain>();

    }

    public String getName(){
        return name;
    }

    public int getCost(){
        return cost;
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

}
