import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.math3.distribution.*;

public class test {

    public static void ciao(String[] args) {

        double x = 0;
        int n = 0;
        int n0 = 0;
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int i = 0;

        double alfa = 2.0;
        int min = 4; //number of elements(in my case size of request)

        while (i < 1000) {
            i++;

            ZipfDistribution z = new ZipfDistribution(min, alfa);
            PoissonDistribution p = new PoissonDistribution(1.0,1.0);
            ExponentialDistribution e = new ExponentialDistribution(10);

            
            n = (int) p.sample();
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
        System.out.println("0: "+n0+"/1000");
        System.out.println("1: "+n1+"/1000");
        //System.out.println("2: "+n2+"/1000");
        //System.out.println("3: "+n3+"/1000");
        //System.out.println("4: "+n4+"/1000");
    }

}
