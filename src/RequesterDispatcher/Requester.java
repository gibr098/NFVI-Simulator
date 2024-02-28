package RequesterDispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import Classes.*;
import RequestServeSample.*;
import Functions.ServiceGeneration;

public class Requester implements Callable<Object>{

    NFVIPoP pop;

    LinkedList<Service> queue;

    double lambda; // rate of requests arrival
    double clock; // current time of simulation
    double endTime; // end time of simulation
    int requests;
    boolean busy;

    public Requester(double lambda, double endTime, NFVIPoP pop) {
        this.lambda = lambda;
        this.endTime = endTime;
        this.pop = pop;

        clock = 0;
        requests = 0;
        busy = false;

        queue = pop.getQueue();
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    private static int getPoissonRandom(double lambda) {
        Random r = new Random();
        double l = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > l);
        return k - 1;
    }

    public void run() throws InterruptedException, Exception {
        int num = 1;
        Service s;
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(1);
            clock += 1;
            if (getPoissonRandom(lambda) == 1) {
                s = ServiceGeneration.generateService("Service-"+num);
                pop.addElementToQueue(s);
                requests++;
                num++;
                System.out.println(clock + "s" +"\tR: Request of " + s.getName() + " arrived at: " + clock + "s");
            }else{
                System.out.println(clock+"s"+" R: nothing ");
            }

        }
        System.out.println("R: Total requests arrived: " + requests);
    }

    
}
