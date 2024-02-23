package Functions;

import java.util.Iterator;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkRun;
import Controller.Dispatcher;

public class Allocation {

    // sort first the service chain by how much resources the vnf needs?
    public static void AllocateService(Service s, NFVIPoP pop) throws Exception {
        if (ServiceCanBeAllocated(s, pop)) {
            s.getLinkChainList().forEach(
                    (vnf) -> {
                        try {
                            AllocateVNF(vnf.getVNF(), pop);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            pop.addElementToQueue(s);
            System.out.println(pop.getQueuePrint());
        }
        // System.out.println("\n" + pop.getTotalInfo());
    }

    public static void DeallocateService(Service s, NFVIPoP pop) throws Exception {
            s.getLinkChainList().forEach(
                    (vnf) -> {
                        try {
                            DeallocateVNF(vnf.getVNF(), pop);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
    }

    private static void AllocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            if (!vnf.isAllocated()) {
                for (LinkInstance li : server.getLinkInstance()) {
                    Container container = li.getContainer();
                    if (!container.isBusy()) {
                        if (ContainerHasResources(container, vnf)) {
                            LinkRun lr = new LinkRun(vnf, container);
                            container.insertLinkRun(lr);
                            container.setBusyState(true);
                            vnf.setAllocated(true);
                            AllocateServerResources(container);
                            System.out.println(vnf.getName()+" runs on "+ container.getName());
                            break; // return true; //Allocation has succeded
                        }
                    }
                }
            }
        }
    }
    private static void DeallocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        DeallocateServerResources(vnf.getLinkRun().iterator().next().getContainer());
        vnf.removeLinkRun(vnf.getLinkRun().iterator().next());
        
    }

    private static void AllocateServerResources(Container container) throws Exception {
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

    private static void DeallocateServerResources(Container container) throws Exception {
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

    private static boolean ContainerHasResources(Container container, VNF vnf) {
        if (container.getRam() < vnf.getRam() ||
                container.getCpu() < vnf.getCPU() ||
                container.getCPUusage() < vnf.getCPUusage() ||
                container.getStorage() < vnf.getStorage() ||
                container.getNetwork() < vnf.getNetwork()) {
            System.out.println(container.getName() + " hasn't resources to allocate " + vnf.getName());
            return false;

        } else
            return true;
    }

    private static boolean ContainerHasResourcesService(Container c, Service s) throws Exception{
        boolean ret = true;
        for (LinkChain lc : s.getLinkChainList()) {
            ret = ret && ContainerHasResources(c, lc.getVNF());
        }
        return ret;

    }

    private boolean checkContainerAvailability(Container container) {
        return container.isBusy();
    }

    private static boolean ServiceCanBeAllocated(Service s, NFVIPoP pop) throws Exception {
        int n = s.getLinkChainList().size();
        int count = 0;
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            for (LinkInstance li : server.getLinkInstance()) {
                Container container = li.getContainer();
                if (!container.isBusy() && ContainerHasResourcesService(container, s)) {
                    count++;
                    if (count == n) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
