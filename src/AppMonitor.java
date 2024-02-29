import java.util.concurrent.Callable;

import Classes.DataCenter;
import Classes.NFVIPoP;
import Classes.Links.LinkContain;

public class AppMonitor implements Callable<Object>{

    static NFVIPoP pop;

    public AppMonitor(NFVIPoP pop){
        this.pop=pop;
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public static void run() throws Exception {
        for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
            System.out.print("\r"+lc.getCOTServer().getTotalResourcesInfo()+"\n");
        }
    }
}