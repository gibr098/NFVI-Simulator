package Functions;

import Classes.COTServer;
import Classes.Container;
import Classes.NFVIPoP;
import Classes.Service;
import Classes.VNF;

public class Deallocation {

    public static void DeallocateService(Service s, NFVIPoP pop) throws Exception {
            s.getLinkChainList().forEach(
                    (vnf) -> {
                        try {
                            //pop.getLinkCompose().getNFVI().removeLinkProvide(s.getLinkLinkProvide());
                            DeallocateVNF(vnf.getVNF(), pop);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
    }
    private static void DeallocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        DeallocateServerResources(vnf.getLinkRun().iterator().next().getContainer());
        //vnf.removeLinkRun(vnf.getLinkRun().iterator().next());
        vnf.getLinkRun().clear();
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
}
