package RequestServeSample;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import Classes.*;
import RequestServeSample.*;

public class Requester implements Callable<Object>{

    NFVIPoP pop;

    // LinkedList<Service> queue;
    Queue queue;

    double lambda; // rate of requests arrival
    double clock; // current time of simulation
    double endTime; // end time of simulation
    int requests;
    boolean busy;

    public Requester(double lambda, double endTime, Queue q) {
        this.lambda = lambda;
        this.endTime = endTime;
        this.pop = pop;

        clock = 0;
        requests = 0;
        busy = false;

        // queue = new LinkedList<Service>();
        //queue = new LinkedList<service>();

        this.queue = q;

    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    /*
    public LinkedList<service> getQueue() {
        return queue;
    }*/

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

    public void run() throws InterruptedException {
        int num = 1;
        service s;
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(1);
            clock += 1;
            if (getPoissonRandom(lambda) == 1) {
                s = new service("service-" + num, 10);
                queue.addElement(s);
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
