import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

//import org.apache.commons.math3.distribution.*;

public class test {

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

    public static void main(String[] args) {

        double x = 0;
        int t = 100;
        int n = 0;
        int n0 = 0;
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int i = 0;

        double alfa = 2.0;
        int min = 4; //number of elements(in my case size of request)

        

        while (i < t) {
            i++;

            //ZipfDistribution z = new ZipfDistribution(min, alfa);
            //PoissonDistribution p = new PoissonDistribution(1.0,1.0);
            //ExponentialDistribution e = new ExponentialDistribution(10);


            //n = (int) p.sample();
            n = getPoissonRandom(0.1);

            if(n==0){
                n0++;
            }
            if(n>=1){
                n1++;
            }
            if(n==2){
                n2++;
            }
            if(n==3){
                n3++;
            }
            if(n==4){
                n4++;
            }
            
            System.out.println(n);


        }
        System.out.println("0: "+n0+"/"+t);
        System.out.println("1: "+n1+"/"+t);
        //System.out.println(">1: "+n2+"/"+t);
        //System.out.println("2: "+n2+"/1000");
        //System.out.println("3: "+n3+"/1000");
        //System.out.println("4: "+n4+"/1000");
    }

}
