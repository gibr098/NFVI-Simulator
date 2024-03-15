package Classes;

import java.util.*;

import Classes.LinkManagers.*;
import Classes.Links.*;

public class VNF {
    private final String name;
    private final String type;

    private int vnf_ram;
    private int vnf_cpu;
    private int vnf_cpu_usage;
    private int vnf_storage;
    private int vnf_network;

    private boolean allocated;

    // VNF -> Service
    private HashSet<LinkChain> linksetC;

    // VNF -> Container
    //private HashSet<LinkRun> linkset;
    private LinkRun linkset;

    public VNF(String name, String type) {
        this.name = name;
        this.type = type;

        this.allocated = false;

        linksetC = new HashSet<LinkChain>();
        //linkset = new HashSet<LinkRun>();

        switch (type) {
            case "firewall":
                vnf_ram = 2;
                vnf_cpu = 1;
                vnf_storage = 4;
                vnf_cpu_usage = 10;
                vnf_network = 1;
                break;

            case "routing":
                vnf_ram = 2;
                vnf_cpu = 1;
                vnf_storage = 2;
                vnf_cpu_usage = 5;
                vnf_network = 1;
                break;

            case "NAT":
                vnf_ram = 4;
                vnf_cpu = 2;
                vnf_storage = 1;
                vnf_cpu_usage = 10;
                vnf_network = 1;
                break;

            case "DHCP":
                vnf_ram = 4;
                vnf_cpu = 2;
                vnf_storage = 2;
                vnf_cpu_usage = 15;
                vnf_network = 1;
                break;

            case "encryption":
                vnf_ram = 4;
                vnf_cpu = 2;
                vnf_storage = 0;
                vnf_cpu_usage = 20;
                vnf_network = 0;
                break;

            case "decryption":
                vnf_ram = 4;
                vnf_cpu = 2;
                vnf_storage = 0;
                vnf_cpu_usage = 20;
                vnf_network = 0;
                break;

            case "VPN":
                vnf_ram = 4;
                vnf_cpu = 2;
                vnf_storage = 0;
                vnf_cpu_usage = 20;
                vnf_network = 1;
                break;
            
            case "DPI":
                vnf_ram = 4;
                vnf_cpu = 2;
                vnf_storage = 4;
                vnf_cpu_usage = 20;
                vnf_network = 1;
                break;

        }

    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getRam() {
        return vnf_ram;
    }

    public int getCPU() {
        return vnf_cpu;
    }

    public int getStorage() {
        return vnf_storage;
    }

    public int getCPUusage() {
        return vnf_cpu_usage;
    }

    public int getNetwork() {
        return vnf_network;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean state) {
        allocated = state;
    }

    public void emptyChain(){
        this.linksetC.clear();
    }

    /*public String getContainers() {
        String s = " ";
        for (LinkRun linkRun : linkset) {
            s += linkRun.getContainer().getName();
        }
        return s;
    }

    public int getContainerNumber() {
        return linkset.size();
    }*/

    // VNF -> Service
    public Set<LinkChain> getLinkChain() {
        return (HashSet<LinkChain>) linksetC.clone();
    }

    public void insertLinkChain(LinkChain t) {
        if (t != null && t.getVNF() == this) {
            ManagerChain.insert(t);
        }
    }

    public void removeLinkChain(LinkChain t) {
        if (t != null && t.getVNF() == this) {
            ManagerChain.remove(t);
        }
    }

    public void insertforManagerChain(ManagerChain a) {
        if (a != null)
            linksetC.add(a.getLink());
    }

    public void removeforManagerChain(ManagerChain a) {
        if (a != null)
            linksetC.remove(a.getLink());
    }

    // VNF -> Container
    /* 
    public Set<LinkRun> getLinkRun() {
        return (HashSet<LinkRun>) linkset.clone();
    }*/

    public LinkRun getLinkRun() {
        return linkset;
    }

    public void insertLinkRun(LinkRun t) {
        if (t != null && t.getVNF() == this) {
            ManagerRun.insert(t);
        }
    }

    public void removeLinkRun(LinkRun t) {
        if (t != null && t.getVNF() == this) {
            ManagerRun.remove(t);
        }
    }

    public void insertforManagerRun(ManagerRun a) {
        //if (a != null)
            //linkset.add(a.getLink());
            if (a != null) linkset = a.getLink();
    }

    public void removeforManagerRun(ManagerRun a) {
        if (a != null)
            linkset = null;
    }

    public String getTotalInfo() {
        String info = "";
        info += this.getName() + "[" + this.getType() + "] runs on " + this.getLinkRun().getContainer().getName();
        info += "\n" + this.getName() + "[" + this.getType() + "] is in the chain of " + linksetC.size()
                + " services: ";
        for (LinkChain l : linksetC) {
            info += l.getService().getName() + " ";
        }

        return info;
    }

    public String getResourcesRequired() {
        String info = this.name + " (";
        info += "ram: " + this.vnf_ram + "GB";
        info += ", cpu: " + this.vnf_cpu + " cores";
        info += ", cpu usage: " + this.vnf_cpu_usage + " %";
        info += ", storage: " + this.vnf_storage + "GB";
        info += ", network: " + this.vnf_network;
        info += ")";

        return info;
    }

}
