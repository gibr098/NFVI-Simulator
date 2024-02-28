# NFVI Simulator
Parameters for running a NFVI with a Point of Presence can be set in the NFVI.config file.
Running App.java will build an NFVI with the desired configuration and will run the simulation:
it consists in simulating the arriving and the management of random-service requests to be instantiated into the NFVI.

### Working on: 
1. Multiplicity constraints(done)
2. Resources allocation/de-allocation constraints(done)
3. Construction validity controls
4. Functional part: Time slotted service requests (in progress)
5. Output a log containing simulation info


# Model
![Alt text](./res/NFVI%20Model.png)
### NFVI & NFVI-PoP
The Network Function Virtualization Infrastructure (NFVI) is a network of nodes, each virtualizing computation, storage, and networking, and able to host VNFs. Each NFVI node is called NFVI Point-of-Presence (NFVI PoP); it might belong to Internet service providers, cloud/edge operators, or simply infrastructure providers.The NFVI includes a virtualization layer that sits on the hardware with the objective of abstracting the HW resources. In this way, these can be logically partitioned and provided to the VNFs to perform their functions and ensure their lifecycle. To this purpose, NFVI contains all the essential hardware and software elements to run instances of VNFs and compose virtual Network Services.

### VNF
Virtualized Network Function (VNFs) are software implementations of network functions that can be deployed on a network functions virtualization infrastructure (NFVI).


### SFC
Virtual functions also work as building blocks that can be interconnected to implement complex Network Services. The traditional deployment of a NS requires data flows to pass through a certain fixed set of middleboxes in a specific order, with the aim of receiving some processing according to the functions they perform. This is described with the term Service Function Chaining (or simply service chaining), which denotes “an ordered list of instances of service functions”.


### COTS
A COTS (Commercial Off-the-Shelf) server is a readily available, all-purpose, standardized and highly compatible piece of hardware, which can have various kinds of software programs installed on it.

### Containers
The most traditional approach for virtualization is based on using a hypervisor, i.e. a middleware installed on a hardware infrastructure to run and monitor one or more virtual machines and the relevant usage of resources on the same physical devices. Hypervisors also supervise and coordinate the VM instances for efficient hardware resource sharing across them. However, other virtualization solutions can be adopted for VNF deployment: besides hosting VNFs inside VMs, they can run inside Containers.

### Time-slotted Service requests
Requests of service instantiation in the NFVI-PoP are generated according to Poisson distribution with arrival rate λ. Requests are served by the **Dispatcher** element who first checks if the related Service (so its corresponding chain of VNFs) can be allocated into the system, i.e if the system has the necessary resources to run the Service, and then runs each VNF in a single container for the time the Service is requested to be active; once the Service time runs out all its VNFs are terminated, the related resources are de-allocated and containers are free to host new incoming VNFs.




