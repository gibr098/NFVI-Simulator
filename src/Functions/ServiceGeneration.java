package Functions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Classes.Service;
import Classes.VNF;
import Classes.Links.LinkChain;

public class ServiceGeneration {

    public static String randomVNF(List<String> types) {
        String randomvnf = "";
        Random rand = new Random();

        int randomIndex = rand.nextInt(types.size());
        randomvnf = types.get(randomIndex);
        types.remove(randomIndex);

        return randomvnf;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static Service generateService(String name) throws Exception{
        List<String> types = new ArrayList<String>();
        types.add("firewall");
        types.add("routing");
        types.add("encryption");
        types.add("decryption");
        types.add("NAT");
        types.add("DHCP");
        types.add("VPN");
        types.add("DPI");

        int VNFnumber = getRandomNumber(3, 8);

        Service s = new Service(name, getRandomNumber(10, 20), 0);
        int j = Integer.parseInt(name.replaceAll("[^0-9]", ""));

        for (int i = 0; i<VNFnumber; i++){
            VNF vnf = new VNF("vnf-"+j+i, randomVNF(types));
            LinkChain lc = new LinkChain(s, vnf);
            s.insertLinkChain(lc);
        }

        return s;
    }
}
