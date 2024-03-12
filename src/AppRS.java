import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Classes.NFVIPoP;
import Classes.Service;
import Classes.Links.LinkContain;
import Functions.*;
import RequesterDispatcher.*;
import jxl.write.WritableSheet;


public class AppRS{
    double lambda;
    double duration;

    NFVIPoP pop;

    public AppRS(double lambda, double duration, NFVIPoP pop){
        this.pop = pop;
        this.lambda = lambda;
        this.duration = duration;

    }

    public void run(PrintWriter out, WritableSheet sheet) throws Exception {

        Requester r = new Requester(lambda, duration, pop);
        Dispatcher d = new Dispatcher(duration, pop, out, sheet);

        ExecutorService service = Executors.newFixedThreadPool(2); //with 1 they run sequentially
                                                                            //with 2 they run simultaneously
        service.submit(r);
        service.submit(d);
        //service.submit(monitor); //class that monitor and display the state of the servers

        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);

        //System.exit(0);


        //Alternative way to tun 2 Threads
        /*
        new Thread(new Runnable() {
            public void run() {
                System.out.println("REQUESTER RUNNING");
                try {
                    r.run();
                    q.printQ();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                System.out.println("DISPATCHER RUNNING");
                try {
                    d.run();
                    q.printQ();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */
    }
}
