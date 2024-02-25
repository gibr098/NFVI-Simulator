package RequestServe;

//Controller class used to simulate the behavior of an M/M/1 system.

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import Classes.Service;

import java.io.*;

public class Dispatcher {
    double endTime;
    double clock;
    Queue queue;
    boolean busy;
    int served;

    public Dispatcher(double endTime, Queue q) {
        this.endTime = endTime;
        this.clock = 0;
        this.queue = q;
        this.served = 0;

        this.busy = false;
    }

    public void run() throws InterruptedException {
        double time = 0;
        service s = new service("service0", 3);
        while (clock != endTime) {
            LinkedList<service> qq = queue.getQueue();
            TimeUnit.MILLISECONDS.sleep(1);
            clock += 1;

            if (busy == false) {
                if (!qq.isEmpty()) {
                    s = qq.remove();
                    System.out.println("\tD: " + s.getName() + " Allocated at: " + clock + "s");
                    served++;
                    time = clock;
                    busy = true;
                }else{
                    System.out.println("D: nothing "+clock);
                }
            }else {
                if (clock == time + 3) {
                    System.out.println("\tD: " + s.getName() + " Deallocated at: " + clock + "s");
                    busy = false;
                } else {
                    System.out.println("D: " + s.getName() + " running at " + clock);
                }
            }
        }
        System.out.println("D: Total requests served: " + served);
    }

}