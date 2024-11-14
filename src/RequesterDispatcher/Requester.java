package RequesterDispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.distribution.ZipfDistribution;

import Classes.*;
import Functions.ServiceGeneration;

public class Requester implements Callable<Object> {

    NFVIPoP pop;

    LinkedList<Service> queue;

    double lambda; // rate of requests arrival
    double clock; // current time of simulation
    double endTime; // end time of simulation
    double alfa; // zipf exponent
    int maxSize; // max size of the request
    int requests;
    boolean busy;

    ZipfDistribution z;

    public Requester(double lambda, double endTime, double alfa, int maxSize, NFVIPoP pop) {
        this.lambda = lambda;
        this.endTime = endTime;
        this.alfa = alfa;
        this.maxSize = maxSize;
        this.pop = pop;

        clock = 0;
        requests = 0;
        busy = false;

        queue = pop.getQueue();
        z = new ZipfDistribution(maxSize, alfa);
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
        int size = 1;
        Service s;
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(250);
            clock += 1;
            size = z.sample();
            if (getPoissonRandom(lambda) == 1) {
                s = ServiceGeneration.generateService("Service-" + num, size);
                s.setDemand(size);
                pop.addElementToQueue(s);
                System.out.println(
                        "t" + clock + "\tRequester: Request of " + s.getName() 
                        + "[demand: " + s.getDemand() + "x, "
                        + "duration: " + s.getDuration()+" h, "
                        + "chain: " + s.getVNFNumber()+" vnfs]"
                                + " arrived at: " + "t" + clock);
                requests++;
                num++;
            } else {
                System.out.println("t" + clock + " Requester: nothing ");
            }
            

        }
        System.out.println("Requester: Total requests arrived: " + requests);
    }

}
