package Functions;

import javax.swing.plaf.synth.SynthTextAreaUI;

import Classes.COTServer;
import Classes.Container;
import Classes.NFVI;
import Classes.NFVIPoP;
import Classes.Service;
import Classes.VNF;
import Classes.VirtualMachine;
import Classes.Links.LinkChain;
import Classes.Links.LinkProvide;



public class Deallocation {

    public static void DeallocateService(Service s, NFVIPoP pop) throws Exception {
        VirtualMachine vm = s.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine();
        for(LinkChain lc : s.getLinkChainList()){
            DeallocateVNF(lc.getVNF(), pop);
        }
        System.out.println("CLEARING "+s.getName()+"["+s.getDuration()+"s] from "+vm.getName()+"...");
        pop.getLinkCompose().getNFVI().removeLinkProvide(s.getLinkProvide());
        s.getLinkChainList().clear();
    }
    private static void DeallocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        System.out.println("DEALLOCATING "+vnf.getName()+"...");
        vnf.getLinkRun().getContainer().setBusyState(false);
        vnf.getLinkRun().getContainer().getLinkInstance().getVirtualMachine().setRunningServiceState(false);
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
        COTServer server = container.getLinkInstance().getVirtualMachine().getLinkVM().getCOTServer();
        server.deallocateCPUusage(cpu_usage);
        server.deallocateCpu(cpu);
        server.deallocateRam(ram);
        server.deallocateStorage(storage);
    }
}
