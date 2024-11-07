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
    double alfa;  
    int maxSize; 
    String a_policy;
    String q_policy;
    double crash;

    NFVIPoP pop;

    public AppRS(double lambda, double duration, double alfa, int maxSize, NFVIPoP pop, String a_policy, String q_policy,double crash){
        this.pop = pop;
        this.lambda = lambda;
        this.duration = duration;
        this.alfa = alfa;
        this.maxSize = maxSize;
        this.a_policy = a_policy;
        this.q_policy = q_policy;
        this.crash = crash;

    }

    public void run(PrintWriter out, WritableSheet sheet1, WritableSheet sheet2) throws Exception {

        Requester r = new Requester(lambda, duration, alfa, maxSize, pop);
        Dispatcher d = new Dispatcher(duration, a_policy, q_policy, pop, out, sheet1);
        Monitor m = new Monitor(pop, duration, crash, out, sheet2);

        /*
        policy = getPolicy();
        switch(policy){
            case 1: //policy fifo
            Dispatcher1 = new Dispatcher1(...);
            break;

            case 2: //policy reduce cost
            Dispatcher2 = new Dispatcher2(...);
            break;

            case 3: //policy..
            Dispatcher3 = new Dispatcher3(...);
            break;

            .
            .
            .
        }
        */

        ExecutorService service = Executors.newFixedThreadPool(3); //with 1 they run sequentially
                                                                            //with 2 they run simultaneously
        service.submit(r);
        service.submit(d);
        service.submit(m); //class that monitor and display the state of the servers

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
