import data.ValueLimits;
import data.RMIMessage;
import data.DCMPreset;
import data.Resource;
import data.Server;
import data.Host;
import data.DCMUser;
import java.awt.Color;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;

public class DCMClientRMI implements Serializable
{
    private DCMServerRMI dcmServerRMI;
    String  rmiserver;
    private static Registry registry;
    private boolean rmiOK = true;
    private DCMClient dcmClient;
    private DCMClientRMI dcmClientRMI;
    private ArrayList<RMIMessage> rmiMessageList;
        
    public DCMClientRMI(DCMClient dcmClientParam)
    {
        dcmClientRMI = this;
        dcmClient = dcmClientParam;
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
                try { registry = LocateRegistry.getRegistry(serverParam, Integer.parseInt(portParam)); } catch (RemoteException ex) { dcmClient.log("Error: Client(,,): RemoteException: LocateRegistry.getRegistry(); " + ex.getMessage(), true, true, true); rmiOK = false; } // get the server “registry”
                try { dcmServerRMI = (DCMServerRMI)(registry.lookup("ServerRMI")); } // look up the remote object
                catch (RemoteException ex)      { dcmClient.log("Error: Client(,,): RemoteException: registry.lookup(\"ServerRMI\")); " + ex.getMessage(), true, true, true); rmiOK = false; }
                catch (NotBoundException ex)    { dcmClient.log("Error: Client(,,): NotBoundException: registry.lookup(\"ServerRMI\")); " + ex.getMessage(), true, true, true);  rmiOK = false; }
            }
        });
        rmiConnectThread.setName("rmiConnectThread");
        rmiConnectThread.setDaemon(false);
        rmiConnectThread.setPriority(Thread.NORM_PRIORITY);
        rmiConnectThread.start();
        try { rmiConnectThread.join(60000); } catch (InterruptedException ex) { System.out.println("Error: DCManagerClientRMI: connect(,,): InterruptedException: \")); " + ex.getMessage()); rmiOK = false; }
        return rmiOK;
    }
    
    // Login
    public boolean authenticate(final String usernameParam, final String passwordParam) { boolean authenticated = false; try { authenticated = dcmServerRMI.authenticate(usernameParam, passwordParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: authenticate " + ex.getMessage(), true, true, true); } return authenticated; }
    public DCMUser                  getDCMUser(long idParam){ DCMUser dcmUser = null; try { dcmUser = dcmServerRMI.getDCMUser(idParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getDCMUser " + ex.getMessage(), true, true, true); } return dcmUser; }
    public DCMUser                  getDCMUserByUsername(String usernameParam) { DCMUser dcmUser = null; try { dcmUser = dcmServerRMI.getDCMUserByUsername(usernameParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getDCMUserByUsername " + ex.getMessage(), true, true, true); } return dcmUser; }
    public ArrayList<DCMUser>       getDCMUserList() { ArrayList<DCMUser> dcmUserList = new ArrayList<DCMUser>(); try { dcmUserList = dcmServerRMI.getDCMUserList(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getDCMUserList " + ex.getMessage(), true, true, true); } return dcmUserList; }
    public ArrayList<RMIMessage>    addDCMUser(DCMUser userParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.addDCMUser(userParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: addDCMUser " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    updateDCMUser(DCMUser userParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.updateDCMUser(userParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: updateDCMUser " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    deleteDCMUser(long idParam) throws CloneNotSupportedException{ rmiMessageList.clear(); try { dcmServerRMI.deleteDCMUser(idParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getDCMUser " + ex.getMessage(), true, true, true); } return rmiMessageList; }

    // Inventory
    public Server                   getServerById(long idParam) { Server server = null; try { server = dcmServerRMI.getServerById(idParam); } catch (RemoteException ex) { dcmClient.log("Error: getServerById: RemoteException: getServerById " + ex.getMessage(), true, true, true); } return server; }
    public Server                   getServerByHostname(String hostnameParam) { Server server = null; try { server = dcmServerRMI.getServerByHostname(hostnameParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getServerByHostname " + ex.getMessage(), true, true, true); } return server; }
    public ArrayList<Server>        getServerList() throws CloneNotSupportedException { ArrayList<Server> serverList = new ArrayList<Server>(); try { serverList = dcmServerRMI.getServerList(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getServerList " + ex.getMessage(), true, true, true); } return serverList; }
    public ArrayList<RMIMessage>    inventoryServer(final long instanceParam, final Host hostParam, final int timeoutParam, final int retryMaxParam, final boolean debugParam) { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.inventoryServer(instanceParam, hostParam, timeoutParam, retryMaxParam, debugParam); } catch (RemoteException ex) {  } return rmiMessageList; }
    public ArrayList<RMIMessage>    enableHost(final Host hostParam, final boolean enableParam) { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.enableHost(hostParam, enableParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: enableServer " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    deleteServer(final Server serverParam, final boolean deleteArchivesParam) { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.deleteServer(serverParam, deleteArchivesParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: deleteServer " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    deleteServers(final boolean deleteArchivesParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.deleteServers(deleteArchivesParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: deleteServers " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    deleteServerByHostname(String hostnameParam) { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.deleteServerByHostname(hostnameParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: deleteServerByHostname " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    updateHost(Host hostParam, boolean generateScriptParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.updateHost(hostParam, generateScriptParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: updateHost(Host hostParam) " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public Host                     getHostById(long idParam) throws CloneNotSupportedException { Host host = new Host(); try { host = dcmServerRMI.getHostById(idParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getHostById " + ex.getMessage(), true, true, true); } return host; }
    public Host                     getHostByHostname(String hostnameParam) { Host host = new Host(); try { host = dcmServerRMI.getHostByHostname(hostnameParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getHostByHostname " + ex.getMessage(), true, true, true); } return host; }
    public Resource                 getResourceById(long idParam) { Resource resource = null; try { resource = dcmServerRMI.getResourceById(idParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getResourceById " + ex.getMessage(), true, true, true); } return resource;}
    public ArrayList<RMIMessage>    updateResource(Resource resourceParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.updateResource(resourceParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: updateResource " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public boolean                  hostnameChangedInHostTable(String oldHostnameParam,String newHostnameParam) { boolean succeeded = false; try { succeeded = dcmServerRMI.hostnameChangedInHostTable(oldHostnameParam, newHostnameParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: hostnameChangedInHostTable " + ex.getMessage(), true, true, true); } return succeeded; }
    public void                     exportInventory() { try { dcmServerRMI.exportInventory(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: exportInventory " + ex.getMessage(), true, true, true); } }
    public void                     importInventory() { try { dcmServerRMI.importInventory(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: importInventory " + ex.getMessage(), true, true, true); } }

    // Trend Navigation
    public DCMPreset                  getDCMPreset(long idParam){ DCMPreset dcmPreset = null; try { dcmPreset = dcmServerRMI.getDCMPreset(idParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getDCMPreset " + ex.getMessage(), true, true, true); } return dcmPreset; }
    public ArrayList<DCMPreset>       getDCMPresetList() { ArrayList<DCMPreset> dcmPresetList = new ArrayList<DCMPreset>(); try { dcmPresetList = dcmServerRMI.getDCMPresetList(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getgetDCMPresetList " + ex.getMessage(), true, true, true); } return dcmPresetList; }
    public ArrayList<RMIMessage>    insertDCMPreset(DCMPreset dcmPresetParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.insertDCMPreset(dcmPresetParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: insertDCMPreset " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    updateDCMPreset(DCMPreset dcmPresetParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.updateDCMPreset(dcmPresetParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: updateDCMPreset " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public ArrayList<RMIMessage>    deleteDCMPreset(DCMPreset dcmPresetParam) throws CloneNotSupportedException { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.deleteDCMPreset(dcmPresetParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: deleteDCMPreset " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public String                   searchExactResources(String searchString, ArrayList<Server> serverList) { String selectedResources = ""; try { selectedResources = dcmServerRMI.searchExactResources(searchString, serverList); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: searchExact " + ex.getMessage(), true, true, true); } return selectedResources; }
    public String                   searchNonExactResources(String searchString, ArrayList<Server> serverList) { String selectedResources = ""; try { selectedResources = dcmServerRMI.searchNonExactResources(searchString, serverList); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: searchNonExact " + ex.getMessage(), true, true, true);  } return selectedResources; }
    public ValueLimits              manualValueMode(final Calendar startCalendar, final Calendar endCalendar, String selectedResources) { ValueLimits valueLimits = new ValueLimits(); try { valueLimits = dcmServerRMI.manualValueMode(startCalendar, endCalendar, selectedResources); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: manualValueMode " + ex.getMessage(), true, true, true); } return valueLimits; }
    public ImageIcon                getTrend(final ArrayList<Color> lineColorList, final int imageWidth, final int imageHeight, final boolean manualValueMode, final Calendar startCalendar, final Calendar endCalendar, final ValueLimits valueLimits, final String selectedResources, final Float lineType) throws CloneNotSupportedException
    {ImageIcon imageIcon = null; try { imageIcon = dcmServerRMI.getTrend(lineColorList, imageWidth, imageHeight, manualValueMode, startCalendar, endCalendar, valueLimits, selectedResources, lineType); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getTrend " + ex.getMessage(), true, true, true); } return imageIcon; }

    public StringBuffer                getData(final Calendar startCalendar, final Calendar endCalendar, final String selectedResources) throws CloneNotSupportedException
    {StringBuffer returnData = null; try { returnData = dcmServerRMI.getData(startCalendar, endCalendar, selectedResources); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getData " + ex.getMessage(), true, true, true); } return returnData; }

    public void                     applyReceiveVergunning(String activatCode, String vergCode) { try { dcmServerRMI.applyReceiveVergunning(activatCode,vergCode); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: applyReceiveVergunning " + ex.getMessage(), true, true, true); } }
    public String                   updateVergunning(int serversInLicenseParam, Calendar selectedDateParam, String vergunningPeriodParam) { String vergunningCode = ""; try { vergunningCode = dcmServerRMI.updateVergunning(serversInLicenseParam, selectedDateParam, vergunningPeriodParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: orderVergunningCode " + ex.getMessage(), true, true, true); } return vergunningCode;}
    public boolean                  vergunningIsValid()                                     { boolean vergunningIsValid = false; try { vergunningIsValid = dcmServerRMI.vergunningIsValid(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: controleerVergunning " + ex.getMessage(), true, true, true); } return vergunningIsValid;}
    public String[]                 getAK()                                                 { String[] status = new String[2]; try { status = dcmServerRMI.getAK(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getAK " + ex.getMessage(), true, true, true); } return status;}
    public String                   getVergunningPeriod()                                   { String vergunningPeriod = ""; try { vergunningPeriod = dcmServerRMI.getVergunningPeriod(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningPeriod " + ex.getMessage(), true, true, true); } return vergunningPeriod;}
    public String                   getVergunningStartDateString()                          { String startDateString = ""; try { startDateString = dcmServerRMI.getVergunningStartDateString(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningStartDateString " + ex.getMessage(), true, true, true); } return startDateString;}
    public String                   getVergunningEndDateString()                            { String endDateString = ""; try { endDateString = dcmServerRMI.getVergunningEndDateString(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningEndDateString " + ex.getMessage(), true, true, true); } return endDateString;}
    public Calendar                 getVergunningStartDate()                                { Calendar startDate = Calendar.getInstance(); try { startDate = dcmServerRMI.getVergunningStartDate(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningStartDate " + ex.getMessage(), true, true, true); } return startDate;}
    public Calendar                 getVergunningEndDate()                                  { Calendar endDate = Calendar.getInstance(); try { endDate = dcmServerRMI.getVergunningEndDate(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningEndDate " + ex.getMessage(), true, true, true); } return endDate;}
    public int                      getServersInLicense()                                   { int serversInLicense = 0; try { serversInLicense = dcmServerRMI.getServersInLicense(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getServersInLicense " + ex.getMessage(), true, true, true); } return serversInLicense;}
    public int                      getHostCount()                                          { int hostCount = 0; try { hostCount = dcmServerRMI.getHostCount(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getHostCount " + ex.getMessage(), true, true, true); } return hostCount;}
    public int                      getResourceCount()                                      { int resourceCount = 0; try { resourceCount = dcmServerRMI.getResourceCount(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getResourceCount " + ex.getMessage(), true, true, true); } return resourceCount;}
    public String                   getServerVersion()                                      { String serverVersion = ""; try { serverVersion = dcmServerRMI.getServerVersion(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getServerVersion " + ex.getMessage(), true, true, true); } return serverVersion;}
    public ArrayList<String[]>      getJVMStaticPropertiesList()                            { ArrayList<String[]> staticPropertiesList = new ArrayList<String[]>(); try { staticPropertiesList = dcmServerRMI.getJVMStaticPropertiesList(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getJVMStaticPropertiesList " + ex.getMessage(), true, true, true); } return staticPropertiesList;}
    public ArrayList<String[]>      getJVMDynamicPropertiesList()                           { ArrayList<String[]> dynamicPropertiesList = new ArrayList<String[]>(); try { dynamicPropertiesList = dcmServerRMI.getJVMDynamicPropertiesList(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getJVMDynamicPropertiesList " + ex.getMessage(), true, true, true); } return dynamicPropertiesList;}

    public String                   getActivationCode()                                     { String activationCode = ""; try { activationCode = dcmServerRMI.getActivationCode(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getActivationCode " + ex.getMessage(), true, true, true); } return activationCode;}
    public String                   getVergunningCode()                                     { String vergunningCode = ""; try { vergunningCode = dcmServerRMI.getVergunningCode(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningCode " + ex.getMessage(), true, true, true); } return vergunningCode;}
    public String                   getVergunningInvalidAdvice()                            { String invalidAdvice = ""; try { invalidAdvice = dcmServerRMI.getVergunningInvalidAdvice(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningInvalidAdvice " + ex.getMessage(), true, true, true); } return invalidAdvice;}
    public String                   getVergunningInvalidReason()                            { String invalidReason = ""; try { invalidReason = dcmServerRMI.getVergunningInvalidReason(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getVergunningInvalidReason " + ex.getMessage(), true, true, true); } return invalidReason;}
    public void                     setVergunningOrderInProgress(boolean inProgress)        { try { dcmServerRMI.setVergunningOrderInProgress(inProgress); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningOrderInProgress " + ex.getMessage(), true, true, true); }}
    public void                     setVergunningValid(boolean validParam)                  { try { dcmServerRMI.setVergunningValid(validParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningValid " + ex.getMessage(), true, true, true); }}
    public void                     setDefaultServersInLicense()                            { try { dcmServerRMI.setDefaultServersInLicense(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningValid " + ex.getMessage(), true, true, true); }}
    public void                     reloadVergunning()                                      { try { dcmServerRMI.reloadVergunning(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: reloadV " + ex.getMessage(), true, true, true); }}

    public Calendar                 getDCMTijd()                                            { Calendar last = Calendar.getInstance(); try { last = dcmServerRMI.getDCMTijd(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: getDCMTijd " + ex.getMessage(), true, true, true); } return last;}
    public void                     setDCMTijd(Calendar nowParam)                           { try { dcmServerRMI.setDCMTijd(nowParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setDCMTijd " + ex.getMessage(), true, true, true); }}
    public void                     setVergunningPeriod(String vergunningPeriodParam)       { try { dcmServerRMI.setVergunningPeriod(vergunningPeriodParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningPeriod " + ex.getMessage(), true, true, true); }}
    public void                     setVergunningStartDate(Calendar startDateParam)         { try { dcmServerRMI.setVergunningStartDate(startDateParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningStartDate " + ex.getMessage(), true, true, true); }}
    public void                     setVergunningEndDate(Calendar endDateParam)             { try { dcmServerRMI.setVergunningEndDate(endDateParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningEndDate " + ex.getMessage(), true, true, true); }}
    public void                     setServersInLicense(int serversInLicenseParam)          { try { dcmServerRMI.setServersInLicense(serversInLicenseParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setServersInLicense " + ex.getMessage(), true, true, true); }}
    public void                     setActivationCode(String activationCodeParam)           { try { dcmServerRMI.setActivationCode(activationCodeParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setActivationCode " + ex.getMessage(), true, true, true); }}
    public void                     setVergunningCode(String vergunningCodeParam)           { try { dcmServerRMI.setVergunningCode(vergunningCodeParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: setVergunningCode " + ex.getMessage(), true, true, true); }}

    public boolean                  vergunningReachedLimit() { boolean vergunningReachedLimit = false; try { vergunningReachedLimit = dcmServerRMI.vergunningReachedLimit(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: vergunningReachedLimit " + ex.getMessage(), true, true, true); } return vergunningReachedLimit; }
    public ArrayList<RMIMessage>    pushPollScript(Server serverParam) { rmiMessageList.clear(); try { rmiMessageList = dcmServerRMI.pushPollScript(serverParam); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: pushPollScript " + ex.getMessage(), true, true, true); } return rmiMessageList; }
    public void                     startPoller() { try { dcmServerRMI.startPoller(); } catch (RemoteException ex) { dcmClient.log("Error: : RemoteException: startPoller " + ex.getMessage(), true, true, true); }}    
}