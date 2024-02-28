import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Classes.NFVIPoP;
import Classes.Service;
import Functions.*;
import RequestServeSample.*;

public class AppRS{
    double lambda;
    double duration;

    NFVIPoP pop;

    public AppRS(double lambda, double duration, NFVIPoP pop){
        this.pop = pop;
        this.lambda = lambda;
        this.duration = duration;

    }

    public static void main(String args[]){

    }

    public void run() throws Exception {

        //double lambda = 0.1;
        //double duration = 100;

        //Queue q = new Queue();

        //Requester requester = new Requester(lambda, duration, q);
        //Dispatcher dispatcher = new Dispatcher(duration, q);

        RequesterDispatcher.Requester r = new RequesterDispatcher.Requester(lambda, duration, pop);
        RequesterDispatcher.Dispatcher d = new RequesterDispatcher.Dispatcher(duration, pop);

        ExecutorService service = Executors.newFixedThreadPool(2); //with 1 they run sequentially
                                                                            //with 2 they run simultaneously
        service.submit(r);
        service.submit(d);
        //service.submit(monitor); //class that monitor and display the state of the servers

        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);

        //q.printQ();

        //System.exit(0);


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
