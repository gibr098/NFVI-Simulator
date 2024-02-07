package Classes;

import Classes.LinkManagers.*;
import Classes.Links.*;

public class NFVIPoP {
    private final String name;

    //NFVIPoP -> DATACENTER
    private LinkOwn link;
    //private int MIN_LINK_OWN = 1;
    //private int MAX_LINK_OWN = 1;

    public int OwnNumber(){
        if(link == null){
            return 0;
        }else return 1;
    }

    //NFVIPoP -> NFVI
    private LinkCompose linkc;
    //private int MIN_LINK_COMPOSE = 1;
    //private int MAX_LINK_COMPOSE = 1;

    public NFVIPoP(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    //NFVIPoP -> DATACENTER
    public LinkOwn getLinkOwn()throws Exception{
        if(link == null)
            throw new Exception("cardinality 1..1 violated on NFVIPoP");
        else return link;
    }

    public void insertLinkOwn(LinkOwn t){
        if(t!=null && t.getNFVIPoP() == this){
            ManagerOwn.insert(t);
        }
    }

    public void removetLinkOwn(LinkOwn t){
        if(t!=null && t.getNFVIPoP() == this){
            ManagerOwn.remove(t);
        }
    }

    public void insertforManagerOwn(ManagerOwn a){
        if (a != null) link = a.getLink();
    }

    public void removeforManagerOwn(ManagerOwn a){
        if (a != null) link = null;
    }

    //NFVIPoP -> NFVI
    public LinkCompose getLinkCompose()throws Exception{
        if(link == null)
            throw new Exception("cardinality 1..1 violated on NFVIPoP");
        else return linkc;
    }

    public void insertLinkCompose(LinkCompose t){
        if(t!=null && t.getNFVIPoP() == this){
            ManagerCompose.insert(t);
        }
    }

    public void removetLinkCompose(LinkCompose t){
        if(t!=null && t.getNFVIPoP() == this){
            ManagerCompose.remove(t);
        }
    }

    public void insertforManagerCompose(ManagerCompose a){
        if (a != null) linkc = a.getLink();
    }

    public void removeforManagerCompose(ManagerCompose a){
        if (a != null) linkc = null;
    }

    public String getTotalInfo() throws Exception{
        String info="";
        info+=this.getName()+" is in "+this.getLinkCompose().getNFVI().getName();
        info+=" and owns Data Center "+this.getLinkOwn().getDataCenter().getName();
        info+="\n"+this.getLinkOwn().getDataCenter().getTotalInfo();
        return info;
    }





}
