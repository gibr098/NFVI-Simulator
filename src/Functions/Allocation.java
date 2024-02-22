package Functions;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkRun;

public class Allocation {

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
        System.out.println("\n" + pop.getTotalInfo());

        /*
         * for (LinkChain l : s.getLinkChain()) {
         * AllocateVNF(l.getVNF(), pop);
         * }
         */
    }

    public static void AllocateVNF(VNF vnf, NFVIPoP pop) throws Exception {
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        for (LinkContain l : dc.getLinkContain()) {
            COTServer server = l.getCOTServer();
            if (!vnf.isAllocated()) {
                for (LinkInstance li : server.getLinkInstance()) {
                    Container container = li.getContainer();
                    if (!container.isBusy()) {
                        LinkRun lr = new LinkRun(vnf, container);
                        container.insertLinkRun(lr);
                        container.setBusyState(true);
                        vnf.setAllocated(true);
                        break;
                    }
                }
            }else{
                break;
            }
        }
    }

    public boolean checkContainerAvailability(Container container) {
        return container.isBusy();
    }
}
