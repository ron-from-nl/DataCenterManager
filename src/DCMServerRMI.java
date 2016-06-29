import data.*;
import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;

public interface DCMServerRMI extends Remote
{
    // Login
    public boolean                  authenticate(String usernameParam, String passwordParam) throws RemoteException;
    public DCMUser                  getDCMUserByUsername(String usernameParam) throws RemoteException;
    public DCMUser                  getDCMUser(long idParam) throws RemoteException;
    public ArrayList<DCMUser>       getDCMUserList() throws RemoteException;
    public ArrayList<RMIMessage>    addDCMUser(DCMUser userParam) throws CloneNotSupportedException, RemoteException;
    public ArrayList<RMIMessage>    updateDCMUser(DCMUser userParam) throws CloneNotSupportedException, RemoteException;
    public ArrayList<RMIMessage>    deleteDCMUser(long idParam) throws CloneNotSupportedException, RemoteException;

    // Inventory
    public Server                   getServerById(long idParam) throws RemoteException;
    public Server                   getServerByHostname(String hostnameParam) throws RemoteException;
    public ArrayList<Host>          getHostList(String hostLines) throws CloneNotSupportedException,RemoteException;
    public ArrayList<Server>        getServerList() throws CloneNotSupportedException,RemoteException;
//    public void                     addServerList(final int serverOffset) throws RemoteException;
    public ArrayList<RMIMessage>    inventoryServer(final long instanceParam, final Host hostParam, final int timeoutParam, final int retryMaxParam, final boolean debugParam) throws RemoteException;
    public ArrayList<RMIMessage>    enableHost(final Host hostParam, final boolean enableParam) throws RemoteException;
    public ArrayList<RMIMessage>    deleteServer(final Server serverParam, final boolean deleteArchivesParam) throws RemoteException;
    public ArrayList<RMIMessage>    deleteServers(final boolean deleteArchivesParam) throws CloneNotSupportedException,RemoteException;
    public ArrayList<RMIMessage>    deleteServerByHostname(String hostnameParam) throws RemoteException;
    public Host                     getHostById(long idParam) throws CloneNotSupportedException,RemoteException;
    public Host                     getHostByHostname(String hostnameParam) throws RemoteException;
    public Resource                 getResourceById(long idParam) throws RemoteException;
    public boolean                  hostnameChangedInHostTable(String oldHostnameParam,String newHostnameParam) throws RemoteException;
    public ArrayList<RMIMessage>    updateHost(Host hostParam, boolean generateScriptParam) throws CloneNotSupportedException,RemoteException;
    public ArrayList<RMIMessage>    updateResource(Resource resourceParam) throws CloneNotSupportedException,RemoteException;
    public void                     exportInventory() throws RemoteException;
    public void                     importInventory() throws RemoteException;

    public String                   remoteCommand(int cmdStage, Host hostParam, StringBuffer commandParam, boolean finalCommand, int timeoutParam, boolean debug) throws CloneNotSupportedException,RemoteException;
//    public String                   remoteCommand() throws CloneNotSupportedException,RemoteException;
    
    // Trend Navigation
    public NavigatorPreset                  getDCMPreset(long idParam) throws RemoteException;
    public ArrayList<NavigatorPreset>       getDCMPresetList() throws RemoteException;
    public ArrayList<RMIMessage>    insertDCMPreset(NavigatorPreset dcmPresetParam) throws CloneNotSupportedException, RemoteException;
    public ArrayList<RMIMessage>    updateDCMPreset(NavigatorPreset dcmPresetParam) throws CloneNotSupportedException, RemoteException;
    public ArrayList<RMIMessage>    deleteDCMPreset(NavigatorPreset dcmPresetParam) throws CloneNotSupportedException, RemoteException;
    public String                   searchExactHosts(String searchString, ArrayList<Server> serverList) throws RemoteException;
    public String                   searchNonExactHosts(String searchString, ArrayList<Server> serverList) throws RemoteException;
    public String                   searchExactResources(String searchString, ArrayList<Server> serverList) throws RemoteException;
    public String                   searchNonExactResources(String searchString, ArrayList<Server> serverList) throws RemoteException;
    public ValueLimits              manualValueMode(final Calendar startCalendar, final Calendar endCalendar, String selectedResources) throws RemoteException;
    public ImageIcon                getTrend(final ArrayList<Color> lineColorList, final int imageWidth, final int imageHeight, final boolean manualValueMode, final Calendar startCalendar, final Calendar endCalendar, final ValueLimits valueLimits, final String selectedResources, final Float lineType) throws CloneNotSupportedException,RemoteException;
    public StringBuffer             getData(final Calendar startCalendar, final Calendar endCalendar, final String selectedResources) throws CloneNotSupportedException,RemoteException;
    public void                     applyReceiveVergunning(String activationCodeParam, String vergunningCodeParam) throws RemoteException;
    public String                   updateVergunning(int serversInLicenseParam, Calendar selectedDateParam, String vergunningPeriodParam) throws RemoteException;
    public String[]                 getAK() throws RemoteException;
    public boolean                  vergunningIsValid() throws RemoteException;
    public Calendar                 getDCMTijd() throws RemoteException;
    public String                   getVergunningPeriod() throws RemoteException;
    public String                   getVergunningStartDateString() throws RemoteException;
    public String                   getVergunningEndDateString() throws RemoteException;
    public Calendar                 getVergunningStartDate() throws RemoteException;
    public Calendar                 getVergunningEndDate() throws RemoteException;
    public int                      getServersInLicense() throws RemoteException;
    public int                      getHostCount() throws RemoteException;
    public int                      getResourceCount() throws RemoteException;
    public String                   getActivationCode() throws RemoteException;
    public String                   getVergunningCode() throws RemoteException;
    public String                   getVergunningInvalidAdvice() throws RemoteException;
    public String                   getVergunningInvalidReason() throws RemoteException;
    public void                     setVergunningOrderInProgress(boolean inProgress) throws RemoteException;
    public void                     setVergunningValid(boolean validParam) throws RemoteException;
    public void                     setDefaultServersInLicense() throws RemoteException;
    public void                     reloadVergunning() throws RemoteException;
    public void                     setDCMTijd(Calendar nowParam) throws RemoteException;
    public void                     setVergunningPeriod(String vergunningPeriodParam) throws RemoteException;
    public void                     setVergunningStartDate(Calendar startDateParam) throws RemoteException;
    public void                     setVergunningEndDate(Calendar endDateParam) throws RemoteException;
    public void                     setServersInLicense(int serversInLicenseParam) throws RemoteException;
    public void                     setActivationCode(String activationCodeParam) throws RemoteException;
    public void                     setVergunningCode(String vergunningCodeParam) throws RemoteException;

    public boolean                  vergunningReachedLimit() throws RemoteException;
    public String                   getServerVersion() throws RemoteException;
    public ArrayList<String[]>      getJVMStaticPropertiesList() throws RemoteException;
    public ArrayList<String[]>      getJVMDynamicPropertiesList() throws RemoteException;
    public ArrayList<RMIMessage>    pushPollScript(Server serverParam) throws RemoteException;
    public void                     startPoller() throws RemoteException;
}
