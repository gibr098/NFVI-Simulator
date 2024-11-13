package Classes;

import Classes.Links.*;
import Classes.LinkManagers.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jfree.data.xy.XYSeries;



public class COTServer {
    private final String name;
    private int ram;
    private int cpu;
    private int storage;
    private int network;

    private boolean online;
    private boolean available;

    HashMap<Integer, Double> cpuUsage;
    HashMap<Integer, Double> ramUsage;
    HashMap<Integer, Double> storageUsage;

    private double totalUsage;

    private int cpu_capacity = 100;

    private XYSeries series;

    //private List TimeState;

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
        this.online = true;
        this.available = true;

        this.cpuUsage = new HashMap<>();
        this.ramUsage = new HashMap<>();
        this.storageUsage = new HashMap<>();

        this.totalUsage = 0.0;

        this.series= new XYSeries(name);

        this.containers = new HashSet<Container>();

        linkset = new HashSet<LinkVM>();
    }

    public boolean isOnline(){
        return online;
    }

    public void setOnlineState(boolean state){
        online = state;
    }

    public boolean isAvailable(){
        return available;
    }

    public void setAvailableState(boolean state){
        available = state;
    }

    public boolean isRunningAService(){
        for(LinkVM lv: this.getLinkVM()){
            VirtualMachine vm = lv.getVirtualMachine();
            if (vm.isRunningService()){
                return true;
            }
        }
        return false;
    }

    public Map<Integer, Double>  getCpuUsage(){
        return (HashMap<Integer, Double>)cpuUsage;
    }

    public Map<Integer, Double>  getRamUsage(){
        return (HashMap<Integer, Double>)ramUsage;
    }

    public Map<Integer, Double>  getStorageUsage(){
        return (HashMap<Integer, Double>)storageUsage;
    }

    public int getResourcesSum(){
        return ram+cpu+storage;
    }

    public boolean allVMBusy(){
        boolean ret = true;
        for(LinkVM l : this.linkset){
            ret = ret && l.getVirtualMachine().isRunningService();
        }
        return ret;
    }

    public String getRamUsagePrint() {
        String ret = this.name+"( ";
        for (Object ob : this.getRamUsage().keySet()) {
            String key = ob.toString();
            String value = this.getRamUsage().get(ob).toString();
            ret+=key+" GB" + " for " + value+" h ";
        }
        return ret+" )";
    }

    public String getCpuUsagePrint() {
        String ret = this.name+"( ";
        for (Object ob : this.getCpuUsage().keySet()) {
            String key = ob.toString();
            String value = this.getCpuUsage().get(ob).toString();
            ret+=key+" cores" + " for " + value+" h ";
        }
        return ret+" )";
    }

    public String getStorageUsagePrint() {
        String ret = this.name+"( ";
        for (Object ob : this.getCpuUsage().keySet()) {
            String key = ob.toString();
            String value = this.getCpuUsage().get(ob).toString();
            ret+=key+" GB" + " for " + value+" h ";
        }
        return ret+" )";
    }


    public void insertCpuUsage(int key, double value){
        cpuUsage.put(key, value);
    }

    public void insertRamUsage(int key, double value){
        ramUsage.put(key, value);
    }

    public void insertStorageUsage(int key, double value){
        storageUsage.put(key, value);
    }

    public XYSeries getSeries(){
        return this.series;
    }
    public void addToSeries(double clock, int cpu){
        this.series.add(clock,cpu);
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

    //Total
    public int getContainerNumber(){
        return linkset.size() * linkset.iterator().next().getVirtualMachine().getContainerNumber();
    }
    public int getContainerperVMNumber(){
        return linkset.iterator().next().getVirtualMachine().getContainerNumber();
    }

    public int getAvailableContainersNumber(){
        int ret = 0;
        for(LinkVM lvm: this.getLinkVM()){
            VirtualMachine vm = lvm.getVirtualMachine();
            for(LinkInstance lc : vm.getLinkInstance()){
                Container c = lc.getContainer();
                if(!c.isBusy()){
                    ret+=1;
                }
            }
        }
        return ret;
    }

    public int getAvailableVMNumber(){
        int ret = 0;
        for(LinkVM lvm: this.getLinkVM()){
            VirtualMachine vm = lvm.getVirtualMachine();
            if(!vm.isRunningService()){
                ret+=1;
            }
        }
        return ret;
    }
    public String printContainerState(){
        String ret = " \n";
        for(LinkVM lvm: this.getLinkVM()){
            ret+=lvm.getVirtualMachine().getName() + " runnignservice: "+lvm.getVirtualMachine().isRunningService();
            for(LinkInstance li : lvm.getVirtualMachine().getLinkInstance()){
                Container c = li.getContainer();
                ret+= c.getName()+" busy: "+c.isBusy()+" \n";

            }
        }
        return ret;
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
