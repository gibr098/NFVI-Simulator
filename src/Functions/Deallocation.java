package Functions;

import Classes.COTServer;
import Classes.Container;
import Classes.NFVIPoP;
import Classes.Service;
import Classes.VNF;
import Classes.Links.LinkChain;

public class Deallocation {

    /*public static void DeallocateService(Service s, NFVIPoP pop) throws Exception {
            s.getLinkChainList().forEach(
                    (vnf) -> {
                        try {
                            //pop.getLinkCompose().getNFVI().removeLinkProvide(s.getLinkLinkProvide());
                            DeallocateVNF(vnf.getVNF(), pop);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
    }*/
    public static boolean DeallocateService(Service s, NFVIPoP pop) throws Exception {
        boolean res = true;
        for(LinkChain lc : s.getLinkChainList()){
            System.out.println("DEALLOCATINGGGG : "+lc.getVNF().getName());
            res = res && DeallocateVNF(lc.getVNF(), pop);
        }
        return res;
    }
    private static boolean DeallocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        //DeallocateServerResources(vnf.getLinkRun().iterator().next().getContainer());
        vnf.getLinkRun().getContainer().setBusyState(false);
        vnf.removeLinkRun(vnf.getLinkRun());
        vnf.setAllocated(false);
        DeallocateServerResources(vnf.getLinkRun().getContainer());
        return true;
    }

    

    private static void DeallocateServerResources(Container container) throws Exception {
        //container.setBusyState(false);
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
