package Functions;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkRun;

public class Allocation {

    //sort first the service chain by how much resources the vnf needs? 
    public static void AllocateService(Service s, NFVIPoP pop) throws Exception {
        s.getLinkChainList().forEach(
                (vnf) -> {
                    try {
                        AllocateVNF(vnf.getVNF(), pop);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        );
        //System.out.println("\n" + pop.getTotalInfo());
    }

    private static void AllocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            if (!vnf.isAllocated()) {
                for (LinkInstance li : server.getLinkInstance()) {
                    Container container = li.getContainer();
                    if (!container.isBusy() && ContainerHasResources(container, vnf)) {
                        LinkRun lr = new LinkRun(vnf, container);
                        container.insertLinkRun(lr);
                        container.setBusyState(true);
                        vnf.setAllocated(true);
                        AllocateServerResources(container);
                        break; //return 1; //Allocation has succeded
                    }else{
                        //return 0; //Allocation can not be done,
                                    // the service must be queued
                                    // to wait for a container to
                                    // release the resources
                    }
                }
            }
        }
    }

    private static void AllocateServerResources(Container container) throws Exception{
        int ram = container.getRam();
        int cpu = container.getCpu();
        int cpu_usage = container.getCPUusage();
        int storage = container.getStorage();
        int network = container.getNetwork();
        COTServer server = container.getLinkInstance().getCOTServer();
        server.allocateCPUusage(cpu_usage);
        server.allocateCpu(cpu);
        server.allocateRam(ram);
        server.allocateStorage(storage);

    }

    private static void DeallocateServerResources(Container container) throws Exception{
        int ram = container.getRam();
        int cpu = container.getCpu();
        int cpu_usage = container.getCPUusage();
        int storage = container.getStorage();
        int network = container.getNetwork();
        COTServer server = container.getLinkInstance().getCOTServer();
        server.deallocateCPUusage(cpu_usage);
        server.deallocateCpu(cpu);
        server.deallocateRam(ram);
        server.deallocateStorage(storage);

    }

    private static boolean ContainerHasResources(Container container, VNF vnf){
        if( 
            container.getRam() < vnf.getRam() ||
            container.getCpu() < vnf.getCPU() ||
            container.getCPUusage() < vnf.getCPUusage() ||
            container.getStorage() < vnf.getStorage() ||
            container.getNetwork() < vnf.getNetwork() 
            ){ 
                System.out.println(container.getName()+" hasn't resources to allocate "+vnf.getName());
                return false; 

            }else return true;
    }

    private boolean checkContainerAvailability(Container container) {
        return container.isBusy();
    }
}
