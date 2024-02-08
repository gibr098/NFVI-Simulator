import java.util.Scanner;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkCompose;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkOwn;
import Classes.Links.LinkProvide;
import Classes.Links.LinkRun;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        if(args[0]!=null && args[1]!=null){
            NFVI nfv = new NFVI("NFVI");

            //int npop = Integer.valueOf(args[1]);
            int npop = Integer.parseInt(args[0]);
            int nserver = Integer.parseInt(args[1]);

            for (int i = 0; i< npop; i++){
                NFVIPoP pop = new NFVIPoP("PoP-"+i);
                DataCenter dc = new DataCenter("DC-"+i,3);

                LinkCompose lc = new LinkCompose(nfv, pop);
                LinkOwn lo = new LinkOwn(pop, dc);

                nfv.insertLinkCompose(lc);
                pop.insertLinkOwn(lo);
                for (int j=0; j< nserver; j++){
                    COTServer s = new COTServer("Server-"+i+j,16, 8, 256, 2);
                    LinkContain l = new LinkContain(dc, s);
                    dc.insertLinkContain(l);

                }
            }
            System.out.println(nfv.getTotalInfo());
        }



        NFVI nfv1 = new NFVI("NFVI");
        NFVIPoP pop1 = new NFVIPoP("PoP-1");
        DataCenter dc1 = new DataCenter("DC-1",3);

        COTServer s1 = new COTServer("Server-1",16, 8, 256, 2);
        COTServer s2 = new COTServer("Server-2",16, 8, 256, 2);

        Container c1 = new Container("c1",4, 4, 64, 1);
        Container c2 = new Container("c2",4, 4, 64, 1);
        Container c3 = new Container("c3",4, 4, 64, 1);

        Service sv1 = new Service("Service1",0);
        Service sv2 = new Service("Service2",0);

        VNF vnf1 = new VNF("VNF-1","firewall");
        VNF vnf2 = new VNF("VNF-2","NAT");
        VNF vnf3 = new VNF("VNF-3","DHCP");
        VNF vnf4 = new VNF("VNF-4","routing");


        LinkCompose lc = new LinkCompose(nfv1, pop1);

        LinkOwn lo1 = new LinkOwn(pop1, dc1);

        LinkContain lc1 = new LinkContain(dc1, s1);
        LinkContain lc2 = new LinkContain(dc1, s2);

        LinkInstance li1 = new LinkInstance(s1, c1);
        LinkInstance li2 = new LinkInstance(s1, c2);
        LinkInstance li3 = new LinkInstance(s2, c3);

        LinkProvide lp = new LinkProvide(nfv1, sv1);
        LinkProvide lpp = new LinkProvide(nfv1, sv2);

        LinkChain lch1 = new LinkChain(sv1, vnf1);
        LinkChain lch2 = new LinkChain(sv1, vnf2);
        LinkChain lch3 = new LinkChain(sv1, vnf3);

        LinkChain lch4 = new LinkChain(sv2, vnf3);
        LinkChain lch5 = new LinkChain(sv2, vnf4);

        LinkRun lr1 = new LinkRun(vnf1, c1);
        LinkRun lr2 = new LinkRun(vnf2, c2);
        LinkRun lr3 = new LinkRun(vnf3, c3);

        nfv1.insertLinkCompose(lc);

        nfv1.insertLinkProvide(lp);
        nfv1.insertLinkProvide(lpp);

        sv1.insertLinkChain(lch1);
        sv1.insertLinkChain(lch2);
        sv1.insertLinkChain(lch3);

        sv2.insertLinkChain(lch4);
        sv2.insertLinkChain(lch5);

        vnf1.insertLinkRun(lr1);
        vnf2.insertLinkRun(lr2);
        vnf3.insertLinkRun(lr3);

        pop1.insertLinkOwn(lo1);

        dc1.insertLinkContain(lc1);
        dc1.insertLinkContain(lc2);

        c1.insertLinkInstance(li1);
        c2.insertLinkInstance(li2);
        c3.insertLinkInstance(li3);


        //System.out.println(nfv1.getTotalInfo());


        /*
        System.out.println("number of pop on NFVI: ");
        System.out.println(nfv1.getPopNum());

        System.out.println("number of services of NFVI: ");
        System.out.println(nfv1.getServicesNumber());

        System.out.println("number of VNF of service1: ");
        System.out.println(sv1.getVNFNumber());
        System.out.println("Chain: "+sv1.getChain());


        System.out.println("number of dc on pop1: ");
        System.out.println(pop1.OwnNumber());

        System.out.println("number of server on dc1: ");
        System.out.println(dc1.getNumberOfServer());

        System.out.println(s1.getRunningContainers()+" running on s1" );
        System.out.println(s2.getRunningContainers()+" running on s2" );

        System.out.println(vnf1.getType()+" runs on "+ vnf1.getContainers() );
        System.out.println(vnf2.getType()+" runs on "+ vnf2.getContainers() );
        System.out.println(vnf3.getType()+" runs on "+ vnf3.getContainers() );
        */





    }
}
