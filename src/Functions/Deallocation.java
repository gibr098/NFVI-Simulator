package Functions;

import javax.swing.plaf.synth.SynthTextAreaUI;

import Classes.COTServer;
import Classes.Container;
import Classes.NFVI;
import Classes.NFVIPoP;
import Classes.Service;
import Classes.VNF;
import Classes.Links.LinkChain;
import Classes.Links.LinkProvide;



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
    public static void DeallocateService(Service s, NFVIPoP pop) throws Exception {
        
        for(LinkChain lc : s.getLinkChainList()){
            //System.out.println("DEALLOCATING "+lc.getVNF().getName()+"...");
            DeallocateVNF(lc.getVNF(), pop);
        }
        System.out.println("CLEARING "+s.getName()+"...");
        //pop.getLinkCompose().getNFVI().removeLinkProvide(s.getLinkProvide());
        pop.getLinkCompose().getNFVI().removeLinkProvide(s.getLinkProvide());
        //System.out.println("remove: "+pop.getLinkCompose().getNFVI().getServicesRunning());
        s.getLinkChainList().clear();
        
    }
    private static void DeallocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        //DeallocateServerResources(vnf.getLinkRun().iterator().next().getContainer());
        System.out.println("DEALLOCATING "+vnf.getName()+"...");
        vnf.getLinkRun().getContainer().setBusyState(false);
        DeallocateServerResources(vnf.getLinkRun().getContainer());
        vnf.removeLinkRun(vnf.getLinkRun());
        vnf.setAllocated(false);
        
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
