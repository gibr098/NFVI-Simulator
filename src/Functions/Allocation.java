package Functions;

import java.util.Iterator;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkProvide;
import Classes.Links.LinkRun;
import Classes.Links.LinkVM;
import RequestServeSample.*;

public class Allocation {

    public static boolean AllocateService(Service s, NFVIPoP pop) throws Exception {
        boolean res = true;
        for (LinkChain lc : s.getLinkChainList()) {
            res = res && AllocateVNF(lc.getVNF(), pop);
        }
        if (res) { // if all VNFs of the service are allocated
            pop.getLinkCompose().getNFVI().insertLinkProvide(new LinkProvide(pop.getLinkCompose().getNFVI(), s)); // NFVI
                                                                                                                  // provide
                                                                                                                  // that
                                                                                                                  // service
            // NFVI nfvi = pop.getLinkCompose().getNFVI();
            // LinkProvide lp = new LinkProvide(pop.getLinkCompose().getNFVI(), s);
            // nfvi.insertLinkProvide(lp);
        }
        return res;
    }

    private static boolean AllocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            if (!vnf.isAllocated()) {
                for (LinkVM lvm : server.getLinkVM()) {
                    VirtualMachine vm = lvm.getVirtualMachine();
                    if (!vm.isRunningService()) {
                        for (LinkInstance li : vm.getLinkInstance()) {
                            Container container = li.getContainer();
                            if (!container.isBusy()) {
                                if (ContainerHasResources(container, vnf)) {
                                    LinkRun lr = new LinkRun(vnf, container);
                                    container.insertLinkRun(lr);
                                    container.setBusyState(true);
                                    vm.setRunningServiceState(true);
                                    vnf.setAllocated(true);
                                    AllocateServerResources(container);
                                    System.out.println("ALLOCATING " + vnf.getName() + " on "
                                            + vnf.getLinkRun().getContainer().getName() + "...");
                                    // break;
                                    return true; // Allocation has succeded
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void AllocateServerResources(Container container) throws Exception {
        int ram = container.getRam();
        int cpu = container.getCpu();
        int cpu_usage = container.getCPUusage();
        int storage = container.getStorage();
        int network = container.getNetwork();
        COTServer server = container.getLinkInstance().getVirtualMachine().getLinkVM().getCOTServer();
        server.allocateCPUusage(cpu_usage);
        server.allocateCpu(cpu);
        server.allocateRam(ram);
        server.allocateStorage(storage);

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

    private static boolean ContainerHasResourcesService(Container c, Service s) throws Exception {
        boolean ret = true;
        for (LinkChain lc : s.getLinkChainList()) {
            ret = ret && ContainerHasResources(c, lc.getVNF());
        }
        return ret;

    }

    public static boolean ServiceCanBeAllocated(Service s, NFVIPoP pop) throws Exception {
        int n = s.getLinkChainList().size();
        int count = 0;
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            for (LinkVM lvm : server.getLinkVM()) {
                VirtualMachine vm = lvm.getVirtualMachine();
                if (!vm.isRunningService()) {
                    for (LinkInstance li : vm.getLinkInstance()) {
                        Container container = li.getContainer();
                        if (!container.isBusy() && ContainerHasResourcesService(container, s)) {
                            count++;
                            if (count == n) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
