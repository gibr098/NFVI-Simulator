package RequestServeSample;

public class service {
    private String name;
    private static double time;

    public service(String name, double time){
        this.name = name;
        this.time = time;
    }

    public String getName(){
        return name;
    }

    public static double getTime(){
        return time;
    }
    public static void reduceTime(){
        time--;
    }
}
