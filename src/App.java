import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkCompose;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkOwn;
import Classes.Links.LinkProvide;
import Classes.Links.LinkRun;
import Functions.*;
import RequestServeSample.*;
import RequesterDispatcher.Dispatcher;
import RequesterDispatcher.Requester;

import java.io.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");


        Properties prop = new Properties();
        String fileName = "NFVI.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            // FileNotFoundException catch is optional and can be collapsed
        } catch (IOException ex) {

        }

        //System.out.println(prop.getProperty("number_of_servers"));
        //System.out.println(prop.getProperty("RAM(GB)"));
        //System.out.println(prop.getProperty("policy"));

        int number_of_servers = Integer.parseInt(prop.getProperty("number_of_servers"));
        int server_ram = Integer.parseInt(prop.getProperty("RAM(GB)"));
        int server_cpu = Integer.parseInt(prop.getProperty("CPU(Cores)"));
        int server_storage = Integer.parseInt(prop.getProperty("Storage(GB)"));
        int server_network = Integer.parseInt(prop.getProperty("Network(interfaces)"));

        int number_of_containers = Integer.parseInt(prop.getProperty("containers"));
        int container_cpu_usage = Integer.parseInt(prop.getProperty("container_cpu(%)"));
        int container_ram = Integer.parseInt(prop.getProperty("C-RAM(GB)"));
        int container_cpu = Integer.parseInt(prop.getProperty("C-CPU(Cores)"));
        int container_storage = Integer.parseInt(prop.getProperty("C-Storage(GB)"));
        int container_network = Integer.parseInt(prop.getProperty("C-Network(interfaces)"));

        double lambda = Double.parseDouble(prop.getProperty("lambda"));
        double duration = Double.parseDouble(prop.getProperty("time_of_simulation"));

        String policy = prop.getProperty("policy");


        NFVI nfvi = new NFVI("NFVI");
        NFVIPoP pop = new NFVIPoP("PoP-1");
        DataCenter dc = new DataCenter("DC-1");

        LinkCompose lc = new LinkCompose(nfvi, pop);
        LinkOwn lo = new LinkOwn(pop, dc);

        nfvi.insertLinkCompose(lc);
        pop.insertLinkOwn(lo);

        for(int i =0; i < number_of_servers; i++){
            COTServer si = new COTServer("Server-"+i,server_ram, server_cpu, server_storage, server_network);
            LinkContain lci = new LinkContain(dc, si);
            dc.insertLinkContain(lci);
            for(int j = 0; j < number_of_containers; j++){
                Container cij = new Container("Container-"+i +j, container_ram, container_cpu, container_storage, container_network, container_cpu_usage);
                LinkInstance lij = new LinkInstance(si, cij);
                si.insertLinkInstance(lij);
                System.out.println(cij.getTotalResourcesInfo());
            }
        }

        /* 
        COTServer s5 = new COTServer("Server-5", 100, server_cpu, server_storage, server_network);
        LinkContain lc5 = new LinkContain(dc, s5);
        dc.insertLinkContain(lc5);
        Container c51 = new Container("Container-51", 100, container_cpu, container_storage, container_network, container_cpu_usage);
        LinkInstance l51 = new LinkInstance(s5, c51);
        s5.insertLinkInstance(l51);
        System.out.println(c51.getTotalResourcesInfo());
        */

        //System.out.println(nfvi.getTotalInfo());


        
        System.out.println("\nServers' state before services allocation:");
        for (LinkContain l : dc.getLinkContain()) {
            System.out.println(l.getCOTServer().getTotalResourcesInfo());
        }

        Service sv1 = new Service("Service1",0,0);
        Service sv2 = new Service("Service2",0,0);
        Service sv3 = new Service("Service3",0,0);

        VNF vnf11 = new VNF("VNF-11","firewall");
        VNF vnf12 = new VNF("VNF-12","NAT");
        VNF vnf13 = new VNF("VNF-13","DHCP");

        VNF vnf21 = new VNF("VNF-21","routing");
        VNF vnf22 = new VNF("VNF-22","encryption");
        VNF vnf23 = new VNF("VNF-23","firewall");

        VNF vnf31 = new VNF("VNF-31","firewall");
        VNF vnf32 = new VNF("VNF-32","routing");
        VNF vnf33 = new VNF("VNF-33","encryption");
        VNF vnf34 = new VNF("VNF-34","SUPERVNF");

        LinkChain lch1 = new LinkChain(sv1, vnf11);
        LinkChain lch2 = new LinkChain(sv1, vnf12);
        LinkChain lch3 = new LinkChain(sv1, vnf13);

        LinkChain lch4 = new LinkChain(sv2, vnf21);
        LinkChain lch5 = new LinkChain(sv2, vnf22);
        LinkChain lch6 = new LinkChain(sv2, vnf23);

        LinkChain lch7 = new LinkChain(sv3, vnf31);
        LinkChain lch8 = new LinkChain(sv3, vnf32);
        LinkChain lch9 = new LinkChain(sv3, vnf33);
        LinkChain lch10 = new LinkChain(sv3, vnf34);

        sv1.insertLinkChain(lch1);
        sv1.insertLinkChain(lch2);
        sv1.insertLinkChain(lch3);

        sv2.insertLinkChain(lch4);
        sv2.insertLinkChain(lch5);
        sv2.insertLinkChain(lch6);

        sv3.insertLinkChain(lch7);
        sv3.insertLinkChain(lch8);
        sv3.insertLinkChain(lch9);
        sv3.insertLinkChain(lch10);


        System.out.println(sv1.getTotalResourceRequired());
        System.out.println(sv2.getTotalResourceRequired());
        System.out.println(sv3.getTotalResourceRequired());

        





        //Allocation.AllocateService(sv1,pop);
        //Allocation.AllocateService(sv2,pop);
        //Allocation.AllocateService(sv3,pop);


        System.out.println(pop.getQueuePrint());

        
        System.out.println("\nServers' state after services allocation:");
        for (LinkContain l : dc.getLinkContain()) {
            System.out.println(l.getCOTServer().getTotalResourcesInfo());
        }


        System.out.println(sv1.getChain());
        //Deallocation.DeallocateService(sv1,pop);
        //Deallocation.DeallocateService(sv2,pop);
        //Deallocation.DeallocateService(sv3,pop);



        

        System.out.println("\nServers' state after services Deallocation:");
        for (LinkContain l : dc.getLinkContain()) {
            System.out.println(l.getCOTServer().getTotalResourcesInfo());
        }

        

        AppRS app = new AppRS(lambda, duration, pop);
        app.run();
        System.out.println("FINAL QUEUE"+pop.getQueuePrint());


        for(Service s:pop.getQueue()){
            System.out.println(s.getName()+":");
            for(LinkChain l : s.getLinkChainList()){
                System.out.println("name: "+l.getVNF().getName()+" ,type: "+l.getVNF().getType());
            }
        }

        for (LinkContain lcc : dc.getLinkContain()){
            for(LinkInstance lii: lcc.getCOTServer().getLinkInstance()){
                System.out.println(lii.getContainer().getName() + ", busy: "+lii.getContainer().isBusy());
            }

        }
        


        //System.out.println("\n" + pop.getTotalInfo());


        
        /* 
        if(args[0]!=null && args[1]!=null){
            NFVI nfv = new NFVI("NFVI");

            //int npop = Integer.valueOf(args[1]);
            int npop = Integer.parseInt(args[0]);
            int nserver = Integer.parseInt(args[1]);

            for (int i = 0; i< npop; i++){
                NFVIPoP pop = new NFVIPoP("PoP-"+i);
                DataCenter dc = new DataCenter("DC-"+i);

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


        NFVI nfv1 = new NFVI("NFVI-1");
        NFVIPoP pop1 = new NFVIPoP("PoP-1");
        DataCenter dc1 = new DataCenter("DC-1");

        COTServer s1 = new COTServer("Server-1",16, 8, 256, 2);
        COTServer s2 = new COTServer("Server-2",16, 8, 256, 2);

        Container c1 = new Container("c1",4, 4, 64, 1,2);
        Container c2 = new Container("c2",4, 4, 64, 1,2);
        Container c3 = new Container("c3",4, 4, 64, 1,2);

        Service sv1 = new Service("Service1",10,0);
        Service sv2 = new Service("Service2",10,0);

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


        System.out.println("\n\n\n");
        System.out.println(nfv1.getTotalInfo());

        */



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
