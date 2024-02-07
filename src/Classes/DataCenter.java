package Classes;

import Classes.LinkManagers.ManagerContain;
import Classes.LinkManagers.ManagerOwn;
import Classes.Links.*;

import java.util.HashSet;
import java.util.Set;

public class DataCenter {
    private final String name;
    private final int number_of_server;

    private HashSet<COTServer> servers;

    //DATACENTER -> COTS
    private HashSet<LinkContain> linkset;
    private int MIN_LINK_CONTAIN = 1;
    //private int MAX_LINK_CONTAIN = number_of_server;

    //DATACENTER -> NFVIPoP
    private LinkOwn link;
    //private int MIN_LINK_OWN = 1;


    public int ServerContainNumber(){
        if(linkset==null) return 0;
        else return linkset.size();
    }

    public int OwnNumber(){
        if(link == null) return 0;
        else return 1;
    }

    public DataCenter(String name, int n){
        this.name = name;
        number_of_server = n;

        linkset = new HashSet<LinkContain>();

        servers = new HashSet<COTServer>();
    }

    public String getName(){
        return name;
    }

    public int getNumberOfServer(){
        return linkset.size();
    }

    //DATACENTER -> COTS
    public Set<LinkContain> getLinkContain() throws Exception{
        if(ServerContainNumber() < MIN_LINK_CONTAIN){
            throw new Exception("Cardinality violted on COTS");
        }else
            return (HashSet<LinkContain>)linkset.clone();
    }

    public void insertLinkContain(LinkContain t){
        if(t!=null && t.getDataCenter()==this){
            ManagerContain.insert(t);
        }
    }

    public void removeLinkContain(LinkContain t){
        if(t!=null && t.getDataCenter()==this){
            ManagerContain.remove(t);
        }
    }

    public void insertforManagerContain(ManagerContain a){
        if(a!=null) linkset.add(a.getLink());

    }

    public void removeforManagerContain(ManagerContain a){
        if(a!=null) linkset = null;
    }

    //DATACENTER -> NFVIPoP
    public LinkOwn getLinkOwn() throws Exception{
        if (link == null) throw new Exception("cardinality violated on Data Center");
        else
            return link;
    }

    public void insertLinkOwn(LinkOwn t){
        if(t!=null && t.getDataCenter()==this){
            ManagerOwn.insert(t);
        }
    }

    public void removeLinkOwn(LinkOwn t){
        if(t!=null && t.getDataCenter()==this){
            ManagerOwn.remove(t);
        }
    }

    public void insertforManagerOwn(ManagerOwn a){
        if(a!=null) link = a.getLink();
    }

    public void removeforManagerOwn(ManagerOwn a){
        if(a!=null) link = null;
    }

    public String getTotalInfo() throws Exception{
        String info="";
        int n = this.getNumberOfServer();
        info+=this.getName()+" is owned by "+this.getLinkOwn().getNFVIPoP().getName()+" and contains "+n+" COTServers: ";
        for (LinkContain l : linkset) {
            info+=l.getCOTServer().getName()+" ";
        }
        for (LinkContain l : linkset) {
            info+="\n"+l.getCOTServer().getTotalInfo();
        }
        

        return info;
    }


}
