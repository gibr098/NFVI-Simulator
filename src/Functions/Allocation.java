package Functions;

import java.util.Iterator;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkProvide;
import Classes.Links.LinkRun;
import Classes.Links.LinkVM;


public class Allocation {

    public static boolean NewAllocateService(Service s, NFVIPoP pop, String policy, String s_isolation) throws Exception{
        boolean res = true;
        COTServer server = new COTServer("init", 0, 0, 0, 0);
        // Server Selection
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        COTServer ss = dc.getLinkContain().iterator().next().getCOTServer();
        for (LinkContain l : dc.getLinkContain()) {
            //System.out.println(l.getCOTServer().getName()+" usage: "+l.getCOTServer().getResourcesSum()+" available cont: "+l.getCOTServer().getAvailableContainersNumber());
            if(l.getCOTServer().getAvailableContainersNumber() >= s.getVNFNumber() &&
               (double)l.getCOTServer().getAvailableVMNumber() >= (double)s.getVNFNumber()/l.getCOTServer().getContainerperVMNumber()){
            switch(policy){
                case "FAS":
                //if(l.getCOTServer().getAvailableContainersNumber() >= s.getVNFNumber()){
                    server = l.getCOTServer();
                //}
                break;

                case "LUS":
                if( l.getCOTServer().getResourcesSum() > ss.getResourcesSum()){
                    ss = l.getCOTServer();
                }
                server = ss;
                break;

                case "MUS":
                if( l.getCOTServer().getResourcesSum() < ss.getResourcesSum()){
                    ss = l.getCOTServer();
                }
                server = ss;
                break;

                case "RANDOM":
                break;

                default:
                System.out.println("Policy " + policy + " not supported");
                System.exit(0);
            }
            }  
        }

        //Allocation of VNFs
        System.out.println(">>>>>"+server.getName()+ " SELECTED FOR "+s.getName());
        //System.out.println("["+server.getAvailableContainersNumber()+" containers] allvmbusy: "+server.allVMBusy()+server.printContainerState());
        for (LinkChain lc : s.getLinkChainList()) {
            VNF vnf = lc.getVNF();
            res = res && NewAllocateVNF(vnf, server);
        }

        // Check
        if (res) { // if all VNFs of the service are allocated
            pop.getLinkCompose().getNFVI().insertLinkProvide(new LinkProvide(pop.getLinkCompose().getNFVI(), s)); // NFVI
                                                                                                                  // provide
                                                                                                                  // that
                                                                                                                  // service

            //VirtualMachine vm =
            //s.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine();
            //vm.setRunningServiceState(true);
            if(s_isolation.equals("no")){
                for (LinkChain lc : s.getLinkChainList()) {
                    VirtualMachine vm = lc.getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine();
                    if(vm.allContainersBusy()){
                        vm.setRunningServiceState(true);
                    }
                }

            }else{
                for (LinkChain lc : s.getLinkChainList()) {
                    VirtualMachine vm = lc.getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine();
                    vm.setRunningServiceState(true);
                }
                // NFVI nfvi = pop.getLinkCompose().getNFVI();
                // LinkProvide lp = new LinkProvide(pop.getLinkCompose().getNFVI(), s);
                // nfvi.insertLinkProvide(lp);
            }
        }else{
            System.out.println("//////////////// ERROR in ALLOCATING VNFs in AllocateService()");
        }
        return res;

    }

    private static boolean NewAllocateVNF(VNF vnf, COTServer server) throws Exception {
        if (!vnf.isAllocated()) {
            for (LinkVM lvm : server.getLinkVM()) {
                VirtualMachine vm = lvm.getVirtualMachine();
                if (!vm.isRunningService()) {
                    for (LinkInstance li : vm.getLinkInstance()) {
                        Container container = li.getContainer();
                        if (!container.isBusy() && ContainerHasResources(container, vnf)) {
                                LinkRun lr = new LinkRun(vnf, container);
                                container.insertLinkRun(lr);
                                container.setBusyState(true);
                                vnf.setAllocated(true);
                                AllocateServerResources(container);
                                System.out
                                        .println("ALLOCATING " + vnf.getName() + "[" + vnf.getType() + "]" + " on "
                                                + vnf.getLinkRun().getContainer().getName() + "["
                                                + vnf.getLinkRun().getContainer().getLinkInstance()
                                                        .getVirtualMachine().getName()
                                                + "]" + "...");
                                // break;
                                return true; // Allocation has succeded
                            }
                        }
                    }
                }
            }
        System.out.println("XXXXXXXXXXXXXXXXX PROBLEM IN ALLOCATING "+vnf.getName());
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

    public static boolean NewServiceCanBeAllocated(Service s, NFVIPoP pop) throws Exception {
        int n = s.getVNFNumber();
        int demand = s.getDemand();
        //int count = 0;
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            //System.out.println("--------->"+server.getName()+ " HAS "+server.getAvailableContainersNumber() + " FOR "+ s.getName()+" "+s.getVNFNumber() );
            //System.out.println("Containers available on " +server.getName()+" "+server.getAvailableContainersNumber()+", vnf to allocate = "+n);
            if(server.getAvailableContainersNumber() >= n*demand && 
               (double)server.getAvailableVMNumber() >= (double)s.getVNFNumber()/server.getContainerperVMNumber()){ //&& !server.allVMBusy()){
                //System.out.println("----SELECTED----->"+server.getName()+ " HAS "+server.getAvailableContainersNumber() + " FOR "+ s.getName()+" "+s.getVNFNumber() );

                return true;
            }
        }
        System.out.println(">>>>>"+s.getName()+" WAITING...");
        return false;
    }



//------------------------------------------------
/* 
    public static boolean AllocateService(Service s, NFVIPoP pop, String policy) throws Exception {
        COTServer server = new COTServer("init", 0, 0, 0, 0);
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            if(policy.equals("FAS")){
                if(l.getCOTServer().getAvailableContainersNumber() < s.getVNFNumber()){
                    System.out.println("WWWWWWWWWWWWWW "+ server.getName()+" NOT GOOD");
                    //break;
                }else{
                    server = l.getCOTServer();
                    //System.out.println("WWWWWWWWWWWWWW "+ server.getName()+" SELECTED");
                }
            }
            //server = l.getCOTServer();
        }
        System.out.println("WWWWWWWWWWWWWW "+ server.getName()+" SELECTED");
        boolean res = true;
        for (LinkChain lc : s.getLinkChainList()) {
            switch (policy){
                case "FAS":
                res = res && NewAllocateVNF(lc.getVNF(), server);
                break;

                case "RANDOM":
                //res = res && RANDOM_AllocateVNF(lc.getVNF(), pop, server);
                break;

                default:
                System.out.println("Policy " + policy + " not supported");
                System.exit(0);
            }

            //res = res && AllocateVNF(lc.getVNF(), pop);
    }

        if (res) { // if all VNFs of the service are allocated
            pop.getLinkCompose().getNFVI().insertLinkProvide(new LinkProvide(pop.getLinkCompose().getNFVI(), s)); // NFVI
                                                                                                                  // provide
                                                                                                                  // that
                                                                                                                  // service

            //VirtualMachine vm =
            //s.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine();
            //vm.setRunningServiceState(true);
            for (LinkChain lc : s.getLinkChainList()) {
                VirtualMachine vm = lc.getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine();
                vm.setRunningServiceState(true);
            }
            // NFVI nfvi = pop.getLinkCompose().getNFVI();
            // LinkProvide lp = new LinkProvide(pop.getLinkCompose().getNFVI(), s);
            // nfvi.insertLinkProvide(lp);
        }else{
            System.out.println("////////////////ERROR in ALLOCATING VNFs in AllocateService()");
        }
        return res;
    }

    private static boolean AllocateVNF(VNF vnf, NFVIPoP pop, COTServer server) throws Exception {
        //DataCenter dc = pop.getLinkOwn().getDataCenter();
        //for (LinkContain l : dc.getLinkContain()) {
            //COTServer server = l.getCOTServer();
            //System.out.println(server.getName() + " SELECTED FOR ALLOCATING " + vnf.getName() + "- SERVER HAS "+server.getAvailableContainersNumber()+" CONTAINERS AVAILABLE");
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
                                    vnf.setAllocated(true);
                                    AllocateServerResources(container);
                                    System.out
                                            .println("ALLOCATING " + vnf.getName() + "[" + vnf.getType() + "]" + " on "
                                                    + vnf.getLinkRun().getContainer().getName() + "["
                                                    + vnf.getLinkRun().getContainer().getLinkInstance()
                                                            .getVirtualMachine().getName()
                                                    + "]" + "...");
                                    // break;
                                    return true; // Allocation has succeded
                                }
                            }
                        }
                    }
                }
            }
        //}
        return false;
    }

    public static boolean ServiceCanBeAllocated(Service s, NFVIPoP pop) throws Exception {
        //int n = s.getLinkChainList().size();
        int n = s.getVNFNumber();
        int demand = s.getDemand();
        int count = 0;
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            for (LinkVM lvm : server.getLinkVM()) {
                VirtualMachine vm = lvm.getVirtualMachine();
                //System.out.println("--------->"+server.getName()+ " HAS "+server.getAvailableContainersNumber() + " FOR "+ s.getName()+" "+s.getVNFNumber() );
                if (!vm.isRunningService()) {
                    count+=vm.getContainerNumber();
                    if (count >= n*demand) {
                        return true;
                    }
                }
            }
        }
        //System.out.println(">>>>>"+s.getName()+" WAITING...");
        return false;
    }
    */
}
