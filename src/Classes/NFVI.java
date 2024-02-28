package Classes;

import java.util.*;
import Classes.Links.*;
import Classes.LinkManagers.*;

public class NFVI {
    public final String name;

    private HashSet<NFVIPoP> PoPs;
    private HashSet<Service> services;

    // NFVI -> NFVI-PoP
    private HashSet<LinkCompose> linkset;
    private int MIN_LINK_COMPOSE = 1;
    // private int MAX_LINK_COMPOSE;

    // NFVI -> Service
    private HashSet<LinkProvide> linksetS;

    // private int number_of_pop = linkset.size();

    public NFVI(String name) {
        this.name = name;
        // number_of_pop = n;

        this.linkset = new HashSet<LinkCompose>();
        this.linksetS = new HashSet<LinkProvide>();

    }

    public String getName() {
        return name;
    }

    public int getPopNum() {
        return linkset.size();
    }

    public int getServicesNumber() {
        return linksetS.size();
    }

    public Set<NFVIPoP> getNFVIPoPs() {
        return (HashSet<NFVIPoP>) PoPs.clone();
    }

    public Set<Service> getServices() {
        return (HashSet<Service>) services.clone();
    }

    // NFVI -> NFVI-PoP
    public Set<LinkCompose> getLinkCompose() throws Exception {
        if (linkset.size() == 0)
            throw new Exception("Minimal cardinality violated on NFVI");
        return (HashSet<LinkCompose>) linkset.clone();
    }

    public void insertLinkCompose(LinkCompose t) {
        if (t != null && t.getNFVI() == this) {
            ManagerCompose.insert(t);
        }
    }

    public void removeLinkCompose(LinkCompose t) {
        if (t != null && t.getNFVI() == this) {
            ManagerCompose.remove(t);
        }
    }

    public void insertforManagerCompose(ManagerCompose a) {
        if (a != null)
            linkset.add(a.getLink());
    }

    public void removeforManagerCompose(ManagerCompose a) {
        if (a != null)
            linkset = null;
    }

    // NFVI -> Service
    public Set<LinkProvide> getLinkProvide() {
        return (HashSet<LinkProvide>) linksetS.clone();
    }

    public void insertLinkProvide(LinkProvide t) {
        if (t != null && t.getNFVI() == this) {
            ManagerProvide.insert(t);
        }
    }

    public void removeLinkProvide(LinkProvide t) {
        if (t != null && t.getNFVI() == this) {
            ManagerProvide.remove(t);
        }
    }

    public void insertforManagerProvide(ManagerProvide a) {
        if (a != null)
            linksetS.add(a.getLink());
    }

    public void removeforManagerProvide(ManagerProvide a) {
        if (a != null)
            linksetS.remove(a.getLink());
    }

    public String getTotalInfo() throws Exception {
        String info = "";
        int n = this.getPopNum();
        info += "NFVI is composed of " + n + " PoPs: ";
        for (LinkCompose l : linkset) {
            info += l.getNFVIPoP().getName() + " ";
        }
        for (LinkCompose l : linkset) {
            info += "\n" + l.getNFVIPoP().getTotalInfo();
        }
        n = this.getServicesNumber();
        info += "\nNFVI provide " + n + " services: ";
        for (LinkProvide s : linksetS) {
            info += s.getService().getName() + " ";
        }
        for (LinkProvide s : linksetS) {
            info += "\n" + s.getService().getTotalinfo();
        }

        return info;
    }

    public String getServicesRunning() {
        String info = "(";
        if (!linksetS.isEmpty()) {
            for (LinkProvide s : linksetS) {
                info += s.getService().getName() + " ";
            }
        }else{
            info+="empty";
        }
        return info + " )";
    }

}
