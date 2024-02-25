package RequestServe;
public class App2 {
    public static void main(String[] args) throws Exception {

        double lambda = 0.5;
        double duration = 200;      
        
        Queue q = new Queue();

        Requester r = new Requester(lambda,duration,q);
        Dispatcher d = new Dispatcher(duration,q);
        
        

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
    }
}
