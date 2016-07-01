import data.RMIMessage;
import data.Server;
import data.Host;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class DCMCommanderClientRMI implements Serializable
{
    private DCMServerRMI dcmServerRMI;
    String  rmiserver;
    private static Registry registry;
    private boolean rmiOK = true;
    private DCMCommanderClient dcmCommanderClient;
    private DCMCommanderClientRMI dcmCommanderClientRMI;
    private ArrayList<RMIMessage> rmiMessageList;
        
    public DCMCommanderClientRMI(DCMCommanderClient dcmCommanderClientParam)
    {
        dcmCommanderClientRMI = this;
        dcmCommanderClient = dcmCommanderClientParam;
        rmiMessageList = new ArrayList<RMIMessage>();
    }
    
    public boolean connect(final String serverParam, final String portParam)
    {
        Thread rmiConnectThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                rmiOK = true;
                System.getProperties().put("java.rmi.server.hostname", serverParam);
                try { registry = LocateRegistry.getRegistry(serverParam, Integer.parseInt(portParam)); } catch (RemoteException ex) { dcmCommanderClient.log("Error: Client(,,): RemoteException: LocateRegistry.getRegistry(); " + ex.getMessage(), true, true, true); rmiOK = false; } // get the server “registry”
                try { dcmServerRMI = (DCMServerRMI)(registry.lookup("ServerRMI")); } // look up the remote object
                catch (RemoteException ex)      { dcmCommanderClient.log("Error: DCMCommanderClient(,,): RemoteException: registry.lookup(\"ServerRMI\")); " + ex.getMessage(), true, true, true); rmiOK = false; }
                catch (NotBoundException ex)    { dcmCommanderClient.log("Error: DCMCommanderClient(,,): NotBoundException: registry.lookup(\"ServerRMI\")); " + ex.getMessage(), true, true, true);  rmiOK = false; }
            }
        });
        rmiConnectThread.setName("rmiConnectThread");
        rmiConnectThread.setDaemon(false);
        rmiConnectThread.setPriority(Thread.NORM_PRIORITY);
        rmiConnectThread.start();
        try { rmiConnectThread.join(60000); } catch (InterruptedException ex) { System.out.println("Error: DCMCommanderClientRMI: connect(,,): InterruptedException: \")); " + ex.getMessage()); rmiOK = false; }
        return rmiOK;
    }
    
    // Inventory
    public ArrayList<Host>          getHostList(String hostLinesParam) throws CloneNotSupportedException { ArrayList<Host> hostList = new ArrayList<Host>(); try { hostList = dcmServerRMI.getHostList(hostLinesParam); } catch (RemoteException ex) { dcmCommanderClient.log("Error: : RemoteException: getHostList " + ex.getMessage(), true, true, true); } return hostList; }
    public ArrayList<Server>        getServerList() throws CloneNotSupportedException { ArrayList<Server> serverList = new ArrayList<Server>(); try { serverList = dcmServerRMI.getServerList(); } catch (RemoteException ex) { dcmCommanderClient.log("Error: : RemoteException: getServerList " + ex.getMessage(), true, true, true); } return serverList; }

    // Trend Navigation
    public String                   searchExactHosts(String searchString, ArrayList<Server> serverList) { String selectedHosts = ""; try { selectedHosts = dcmServerRMI.searchExactHosts(searchString, serverList); } catch (RemoteException ex) { dcmCommanderClient.log("Error: : RemoteException: searchExactHosts " + ex.getMessage(), true, true, true); } return selectedHosts; }
    public String                   searchNonExactHosts(String searchString, ArrayList<Server> serverList) { String selectedHosts = ""; try { selectedHosts = dcmServerRMI.searchNonExactHosts(searchString, serverList); } catch (RemoteException ex) { dcmCommanderClient.log("Error: : RemoteException: searchNonExactHosts " + ex.getMessage(), true, true, true);  } return selectedHosts; }
    public int                      getServersInLicense()                                   { int serversInLicense = 0; try { serversInLicense = dcmServerRMI.getServersInLicense(); } catch (RemoteException ex) { dcmCommanderClient.log("Error: : RemoteException: getServersInLicense " + ex.getMessage(), true, true, true); } return serversInLicense;}

    
    
//    public String                   remoteCommand(int cmdStage, Host hostParam, StringBuffer commandParam, boolean finalCommand, int timeoutParam, boolean debug) throws CloneNotSupportedException { String output = ""; output = dcmServerRMI.remoteCommand(cmdStage, hostParam, commandParam, finalCommand, timeoutParam, debug); return output; }
    public String                   remoteCommand(int cmdStage, Host hostParam, StringBuffer commandParam, boolean finalCommand, int timeoutParam, boolean debug) throws CloneNotSupportedException { String output = ""; try { output = dcmServerRMI.remoteCommand(cmdStage, hostParam, commandParam, finalCommand, timeoutParam, debug); } catch(RemoteException ex) { dcmCommanderClient.log("Error: : RemoteException: getHostList " + ex.getMessage(), true, true, true); } return output; }
}