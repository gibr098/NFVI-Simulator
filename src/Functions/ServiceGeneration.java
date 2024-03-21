package Functions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.text.html.FormView;

import org.apache.commons.math3.distribution.ZipfDistribution;

import Classes.NFVI;
import Classes.Service;
import Classes.VNF;
import Classes.Links.LinkChain;
import Classes.Links.LinkProvide;

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

    public static Service generateService(String name, int size) throws Exception{
        List<String> types = new ArrayList<>();
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

    public static Service generateCopyService(Service s, int i) throws Exception{
        Service copy = new Service(s.getName()+"["+i+"]", s.getTime(), 0);
        copy.setReqDemand(s.getDemand());
        for (LinkChain lc : s.getLinkChainList()) {
            VNF vnfcopy = new VNF(lc.getVNF().getName()+"["+i+"]", lc.getVNF().getType());
            LinkChain lcopy = new LinkChain(copy, vnfcopy);
            copy.insertLinkChain(lcopy);
        }
        return copy;
    }
}

