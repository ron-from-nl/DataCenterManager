import data.Configuration;
import data.ValueLimits;
import data.ConfigurationCaller;
import data.RMIMessage;
import data.DCMPreset;
import data.Resource;
import data.Server;
import data.Host;
import data.DCMUser;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.jrobin.core.FetchData;
import org.jrobin.core.FetchRequest;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

public class DCMServer extends java.rmi.server.UnicastRemoteObject implements Serializable, DCMInventoryServerServerCaller, DCMDBClientCaller, ConfigurationCaller, DCMPollServerCaller, DCMPushPollScriptCaller, DCMServerRMI, DCMRemoteCommandCaller
{
            int         rmiport=20000;
            String      rmiserver;
    static  Registry    rmiregistry;    // rmi registry for lookup the remote objects.

    String host;
    String user;
    String userPassword;
    String superuserPassword = null;
    String serversListArray;

    Server inventoryServer;
    Server pollerServer;
    String command;
    String tmpOutput;
    String output;
    String data;
        
    int port = 22;
    int retentionTime = 1;
    int sessionTimeout = 5;

    int serverInstance = 0;
    int serverInstanceCounter = 0;

//    Server server;
    DCMInventoryServerServer inventoryServerServer;
    DCMPollServer poller;
    DCMInventoryServerCaller inventoryCaller;
    ConfigurationCaller configurationCaller;
    private final Configuration configuration;
    private DCMDBClient dcmDBClient;
    private Thread dcmDBClientThread;
    private boolean dcmMetaDBClientIsRead = false;
    private boolean trendIsViewable = true;
    private DCMServer dcmServerReference;
//    private ArrayList<Server> serverList;
//    private ResourcesTableCellRenderer resourcesTableCellRenderer;
//    private static final String VERGUNNINGTOEKENNER  = "IsNwtNp4L";
    public static final int     SECOND               = 1;
    public static final int     MINUTE               = SECOND * 60;
    public static final int     HOUR                 = MINUTE * 60;
    public static final int     DAY                  = HOUR   * 24;
    private boolean             runThreadsAsDaemons  = true;
//    private TableColumn resourcesTableColumn;
//    protected DCMAddServerForm addServerForm;
//    protected DCMDeleteServerForm deleteServerForm;
//    protected DCMDeleteServersForm deleteServersForm;
    private DCMShell shell;
    protected Calendar startCalendar;
    protected Calendar endCalendar;
//    private Color[] colorArray;
//    private Long    navigationXFactor = 6L;
//    private Double  navigationYFactor = 4D;
    protected boolean manualValueMode = false;
    protected ValueLimits valueLimits;

    private DCMVergunningServer          dcmVergunningServer;
//    private Calendar            vergunningStartCalendar;
    private Calendar            vergunningEndCalendar;
    protected float lineType = 2;
//    private  DCMPopupMenus popupMenus;
    protected int imageWidth;
    protected int imageHeight;
    boolean mousePressed = false;
//    private DCMTableCellListener hostTableCellListener;
//    private Action hostTableCellAction;
//    private boolean hostnameUpdated = false;
//    private DCMTableCellListener resourcesTableCellListener;
//    private Action resourcesTableCellAction;
//    private ArrayList<Color> lineColorList;
//    private final int xEdge1 = 30;
    private final int imageEdgeX = 82;
    private final int viewEdgeY = 30;
    
    private ImageIcon imageIcon; // Leave for MiddleWare
    private ArrayList<RMIMessage> rmiMessageList;
    private boolean   daemon = false;
    private boolean   debug = false;
//    private String    myIFIP = "";
    private DCMSysMonitor sysmon;
//    private Timer               updateSysPropsTimer;
//    private long                updateSysPropsTimerInterval = 1000; // mS
    private String              logDateString;
    private Calendar            currentTimeCalendar;
    private static final String THISPRODUCT = "DCMServer";
    private FileWriter          logFileWriter;
    private String              logFileString;
    private String              logBuffer                                       = "";
    private DCMRemoteCommandCaller   dcmRemoteCommandCaller;

/** Creates new form InventoryGUI */
    public DCMServer(int rmiportParam, boolean daemonParam, boolean debugParam) throws RemoteException, MalformedURLException
    {
        daemon = daemonParam;
        debug = debugParam;
        
//      STDIN & STDOUT Redirection
//        if ((!Configuration.DEBUG) && (!debug))
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        } else { System.out.println(THISPRODUCT + " debugging enabled..."); }
        
        if ( ! daemon ) { JFrame jframe = new JFrame(THISPRODUCT); jframe.setState(JFrame.ICONIFIED); jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); jframe.setVisible(true); } // Makes process windowed so users can at least see them
        rmiport = rmiportParam;
        rmiMessageList = new ArrayList<RMIMessage>();
//------------------------------------------------------------------------------------------------------

//        System.getProperties().put("java.rmi.server.hostname", serverParam);
        
        try { rmiserver = (InetAddress.getLocalHost()).toString(); }    catch (UnknownHostException ex) { System.out.println("Error: Server(): UnknownHostException InetAddress.getLocalHost() " + ex.getMessage()); }
        try { rmiregistry = LocateRegistry.createRegistry( rmiport ); } catch (RemoteException ex)      { System.out.println("Error: Server(): RemoteException: LocateRegistry.createRegistry(port); " + ex.getMessage()); }
        try { rmiregistry.rebind("ServerRMI", this); }                  catch (RemoteException ex)      { System.out.println("Error: Server(): RemoteException: registry.rebind(\"rmiServer\", this); " + ex.getMessage()); }

//------------------------------------------------------------------------------------------------------
                        
        dcmServerReference = this;
        configurationCaller = this;
        dcmRemoteCommandCaller = this;
        configuration = new Configuration(configurationCaller);
        
        // Just for the logToFile
        currentTimeCalendar = Calendar.getInstance();
        logDateString = "" +
        String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MONTH) + 1) +
        String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + "_" +
        String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.SECOND));
        logFileString = configuration.getLogDir() + logDateString + "_" + THISPRODUCT + ".log";

        
//        lineColorList = new ArrayList<Color>();
//        myIFIP = (String) System.getProperties().get("java.rmi.server.hostname");

        // Begin MiddleWare        
        try { dcmDBClientThread = new DCMDBClient(dcmServerReference, configuration.getJavaDB(), daemon, debug); }
        catch (SQLException ex)                 { inventoryCaller.log("InventoryGUI: SQLException", true, true, true); }
        catch (ClassNotFoundException ex)       { inventoryCaller.log("InventoryGUI: ClassNotFoundException: " + ex.getMessage(), true, true, true); }
        catch (InstantiationException ex)       { inventoryCaller.log("InventoryGUI: InstantiationException: " + ex.getMessage(), true, true, true); }
        catch (IllegalAccessException ex)       { inventoryCaller.log("InventoryGUI: IllegalAccessException: " + ex.getMessage(), true, true, true); }
        catch (NoSuchMethodException ex)        { inventoryCaller.log("InventoryGUI: NoSuchMethodException: " + ex.getMessage(), true, true, true); }
        catch (InvocationTargetException ex)    { inventoryCaller.log("InventoryGUI: InvocationTargetException: " + ex.getMessage(), true, true, true); }
        catch (Exception ex)                    { inventoryCaller.log("InventoryGUI: Exception: " + ex.getMessage(), true, true, true); }

        dcmDBClientThread.setName("dcmMetaDBClientThread");
        dcmDBClientThread.setDaemon(false);
        dcmDBClientThread.setPriority(Thread.NORM_PRIORITY);
        dcmDBClientThread.start();
        
        dcmDBClient = (DCMDBClient) dcmDBClientThread;
        while (!dcmMetaDBClientIsRead) { try { Thread.sleep(100); } catch (InterruptedException ex) {  }}
        // End MiddleWare        

        // Begin MiddleWare        
        shell = new DCMShell(debug);
        // End MiddleWare        
                
        startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis()-(DAY*1000));        
        endCalendar = Calendar.getInstance();

        valueLimits = new ValueLimits();
                
//        vergunningStartCalendar = Calendar.getInstance();
//        vergunningEndCalendar   = Calendar.getInstance();
//        vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
//        vergunningStartCalendar.set(Calendar.MINUTE, (int)0);
//        vergunningStartCalendar.set(Calendar.SECOND, (int)0);
                
        // Begin MiddleWare

        dcmVergunningServer = new DCMVergunningServer(dcmServerReference,debug); // MiddleWare
        dcmVergunningServer.controleerVergunning();
        
        sysmon = new DCMSysMonitor();
        
//        vergunningJumpstart();
        vergunningDoorvoeren();
        
        // End MiddleWare        
    }

    @Override
    public boolean authenticate(String usernameParam, String passwordParam) throws RemoteException
    {
        boolean authenticated = false; authenticated = dcmDBClient.authenticateDCMUser(usernameParam,passwordParam);
        return authenticated;
    }

    @Override
    public DCMUser getDCMUserByUsername(String usernameParam) throws RemoteException
    {
        DCMUser dcmUser = dcmDBClient.getDCMUserByUsername(usernameParam);
        return dcmUser;
    }

    // Begin MiddleWare
    @Override
    public void exportInventory()
    {
        dcmDBClient.exportInventory();
    }

    // Begin MiddleWare
    @Override
    public void importInventory()
    {
        dcmDBClient.importInventory();
    }

    // MiddleWare
    @Override
    public ArrayList<RMIMessage> addDCMUser(DCMUser userParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.insertDCMUser(userParam); return rmiMessageList;
    }
    
    // MiddleWare
    @Override
    public ArrayList<RMIMessage> updateDCMUser(DCMUser userParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.updateDCMUser(userParam); return rmiMessageList;
    }
    
    // MiddleWare
    @Override
    public ArrayList<RMIMessage> deleteDCMUser(long idParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.deleteDCMUser(idParam); return rmiMessageList;
    }
    
    // Begin MiddleWare
    @Override
    public DCMUser getDCMUser(long idParam)
    {
//        DCMUser dcmUser = new DCMUser();try { dcmUser = (DCMUser) dcmMetaDBClient.getDCMUser(idParam).clone(); } catch (CloneNotSupportedException ex) {  }
        DCMUser dcmUser = new DCMUser(); dcmUser = dcmDBClient.getDCMUser(idParam);
        return dcmUser;
    }

    // Begin MiddleWare
    @Override
    public ArrayList<DCMUser> getDCMUserList()
    {
        ArrayList<DCMUser> dcmUserList = dcmDBClient.getDCMUserList();
        return dcmUserList;
    }
    
    @Override
    public ArrayList<RMIMessage> insertDCMPreset(DCMPreset viewParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.insertDCMPreset(viewParam); return rmiMessageList;
    }

    @Override
    public ArrayList<RMIMessage> updateDCMPreset(DCMPreset viewParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.updateDCMPreset(viewParam); return rmiMessageList;
    }

    @Override
    public ArrayList<RMIMessage> deleteDCMPreset(DCMPreset viewParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.deleteDCMPreset(viewParam.getId()); return rmiMessageList;
    }

    // Begin MiddleWare
    @Override
    public DCMPreset getDCMPreset(long idParam)
    {
//        DCMUser dcmUser = new DCMUser();try { dcmUser = (DCMUser) dcmMetaDBClient.getDCMUser(idParam).clone(); } catch (CloneNotSupportedException ex) {  }
        DCMPreset DCMPreset = new DCMPreset(); DCMPreset = dcmDBClient.getDCMPreset(idParam);
        return DCMPreset;
    }

    // Begin MiddleWare
    @Override
    public ArrayList<DCMPreset> getDCMPresetList()
    {
        ArrayList<DCMPreset> DCMPresetList = dcmDBClient.getDCMPresetList();
        return DCMPresetList;
    }
    
    public ArrayList<String[]> getJVMStaticPropertiesList()
    {
        ArrayList<String[]> staticPropertiesList = new ArrayList<String[]>();
        staticPropertiesList.add(new String[]{"Property", "Server"});
        staticPropertiesList.add(new String[]{"architecture", System.getProperty("sun.arch.data.model")});
        staticPropertiesList.add(new String[]{"cpu.endian", System.getProperty("sun.cpu.endian")});
        staticPropertiesList.add(new String[]{"os.arch", System.getProperty("os.arch")});
        staticPropertiesList.add(new String[]{"os.name", System.getProperty("os.name")});
        staticPropertiesList.add(new String[]{"os.version", System.getProperty("os.version")});
        staticPropertiesList.add(new String[]{"user.country", System.getProperty("user.country")});
        staticPropertiesList.add(new String[]{"user.language", System.getProperty("user.language")});
        staticPropertiesList.add(new String[]{"java.vendor", System.getProperty("java.vendor")});
        staticPropertiesList.add(new String[]{"java.version", System.getProperty("java.version")});
        staticPropertiesList.add(new String[]{"class.version", System.getProperty("java.class.version")});
        staticPropertiesList.add(new String[]{"DCM Version", DCMLicense.getVersion()});
        staticPropertiesList.add(new String[]{"heap max", Long.toString((Runtime.getRuntime().maxMemory()/(1024*1024))) + " MB"});
        staticPropertiesList.add(new String[]{"heap tot", Long.toString((Runtime.getRuntime().totalMemory()/(1024*1024))) + " MB"});
        staticPropertiesList.add(new String[]{"heap free", Long.toString((Runtime.getRuntime().freeMemory()/(1024*1024))) + " MB"});
        staticPropertiesList.add(new String[]{"Threads", Long.toString(Thread.activeCount())});
        staticPropertiesList.add(new String[]{"CPU", Integer.toString(sysmon.getProcessTime()) + "%"});
        return staticPropertiesList;
    }

    public ArrayList<String[]> getJVMDynamicPropertiesList()
    {
        ArrayList<String[]> dynamicPropertiesList = new ArrayList<String[]>();
        dynamicPropertiesList.add(new String[]{"heap max", Long.toString((Runtime.getRuntime().maxMemory()/(1024*1024))) + " MB"});
        dynamicPropertiesList.add(new String[]{"heap tot", Long.toString((Runtime.getRuntime().totalMemory()/(1024*1024))) + " MB"});
        dynamicPropertiesList.add(new String[]{"heap free", Long.toString((Runtime.getRuntime().freeMemory()/(1024*1024))) + " MB"});
        dynamicPropertiesList.add(new String[]{"Threads", Long.toString(Thread.activeCount())});
        dynamicPropertiesList.add(new String[]{"CPU", Integer.toString(sysmon.getProcessTime()) + "%"});
        return dynamicPropertiesList;
    }
    
    // Begin MiddleWare // No usages
    @Override
    public synchronized ArrayList<RMIMessage> inventoryServer(final long instanceParam, final Host hostParam, final int retentionTimeParam, final int timeoutParam, final int retryMaxParam, final boolean debugParam)
    {
        boolean succeeded = false;
        rmiMessageList.clear();
        rmiMessageList.add(new RMIMessage("Log","DCManagerServer.inventoryServer(instance,host)","\nAction:  DCMServer: Server inventory initiated for server: " + hostParam.getHostname() + ", please wait...",true));
        log("\nAction:  DCMServer: Server inventory initiated for server: " + hostParam.getHostname() + ", please wait...", true, true, true);
//        Thread inventoryThread = new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
                // Begin MiddleWare
                if (dcmDBClient.getHostCount() < dcmVergunningServer.getServersInLicense())
                {
                    new DCMInventoryServerServer(dcmServerReference, instanceParam, hostParam, retentionTimeParam, timeoutParam, retryMaxParam, daemon, debug);
                }
                else
                {
                    rmiMessageList.add(new RMIMessage("Log","DCManagerServer.inventoryServer(instance,host)","Server Limit Reached! Click [Admin] Tab to increase number of Servers.",true));
                    log("Server Limit Reached! Click [Admin] Tab to increase number of Servers.", true, true, true);
                }
                // End MiddleWare
//            }
//        });
//        inventoryThread.setName("inventoryThread");
//        inventoryThread.setDaemon(false);
//        inventoryThread.setPriority(Thread.NORM_PRIORITY);
//        inventoryThread.start();
//        try { inventoryThread.join(60000); } catch (InterruptedException ex) {  }
        return rmiMessageList;
    }
    // End MiddleWare
        
    public synchronized int getHostCount()
    {
        return dcmDBClient.getHostCount();
    }
    
    public synchronized int getResourceCount()
    {
        return dcmDBClient.getResourceCount();
    }
    
    // Begin MiddleWare
    @Override
    public synchronized ArrayList<RMIMessage> deleteServer(final Server serverParam, final boolean deleteArchivesParam)
    {
//        Thread deleteServerThread = new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
                boolean archivesSuccessfullyDeleted = true;
                boolean serverSuccessfullyDeleted = true;
                log("Server deletion initiated for server: " + serverParam.getHost().getHostname() + ", please wait...", true, true, true);
                rmiMessageList.clear(); rmiMessageList.add(new RMIMessage("Log","DCManagerServer.deleteServer(server,deleteArchives)","Server deletion initiated, please wait...",true));
                Server server = serverParam;
                
                if (deleteArchivesParam)
                {
                    // Getting list of FS files (rrdbFileList)
                    File hostArchiveDir = new File(configuration.getArchiveDBDir() + server.getHost().getHostname() + configuration.getFileSeparator());
                    ArrayList<String> archiveFileList = new ArrayList<String>();
                    for (File file:hostArchiveDir.listFiles())
                    {
                        if (file.delete()) { log("Removed archive file: " + file.getName(), false, false, false); archivesSuccessfullyDeleted = true; }
                        else { log("Could not remove archive file: " + file.getName(), true, true, true); archivesSuccessfullyDeleted = false; }
                    }

                    if (hostArchiveDir.delete())    { log("Removed archive dir: " + hostArchiveDir.getName(), true, true, true); serverSuccessfullyDeleted = true; }
                    else                            { log("Could not removed archive dir: " + hostArchiveDir.getName(), true, true, true); serverSuccessfullyDeleted = false; }
                }
                
                // Delete the server from the MetaDB
                try { dcmDBClient.deleteServerByHostname(server.getHost().getHostname()); } catch (CloneNotSupportedException ex) {  }
                
                if (deleteArchivesParam)
                {
                    if      (( serverSuccessfullyDeleted) && ( archivesSuccessfullyDeleted)) { log("Server " + server.getHost().getHostname() + " and archives successfully deleted", true, true, true); }
                    else if (( serverSuccessfullyDeleted) && (!archivesSuccessfullyDeleted)) { log("Server " + server.getHost().getHostname() + " successfully deleted, but archives might not be fully deleted", true, true, true); }
                    else if ((!serverSuccessfullyDeleted) && ( archivesSuccessfullyDeleted)) { log("Server " + server.getHost().getHostname() + " NOT successfully deleted, but archives were successfully deleted", true, true, true); }
                    else if ((!serverSuccessfullyDeleted) && (!archivesSuccessfullyDeleted)) { log("Server " + server.getHost().getHostname() + " and archives NOT successfully deleted", true, true, true); }                    
                }
                else
                {
                    if      ( serverSuccessfullyDeleted) { log("Server " + server.getHost().getHostname() + " successfully deleted", true, true, true); }
                    else if (!serverSuccessfullyDeleted) { log("Server " + server.getHost().getHostname() + " NOT successfully deleted", true, true, true); }
                }
//            }
//        });
//        deleteServerThread.setName("deleteServerThread");
//        deleteServerThread.setDaemon(false);
//        deleteServerThread.setPriority(Thread.NORM_PRIORITY);
//        deleteServerThread.start();
//        try { deleteServerThread.join(10000); } catch (InterruptedException ex) {}
        return rmiMessageList;
    }
    // End MiddleWare

    // Begin MiddleWare
    @Override
    public synchronized ArrayList<RMIMessage> deleteServers(final boolean deleteArchivesParam) throws CloneNotSupportedException
    {
        Thread deleteServersThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                rmiMessageList.clear(); rmiMessageList.add(new RMIMessage("Log","DCManagerServer.deleteServers(deleteArchives)","Deletion all servers initiated, please wait...",true));
                ArrayList<Server> serverList = new ArrayList<Server>();
                try { serverList = dcmDBClient.selectServers(); } catch (CloneNotSupportedException ex) {}
                for (Server server:serverList) { deleteServer(server, deleteArchivesParam); }
            }
        });
        deleteServersThread.setName("deleteServersThread");
        deleteServersThread.setDaemon(false);
        deleteServersThread.setPriority(Thread.NORM_PRIORITY);
        deleteServersThread.start();
        try { deleteServersThread.join(); } catch (InterruptedException ex) {}
        return rmiMessageList;
    }    
    // End MiddleWare
        
    // Begin MiddleWare
    public synchronized ArrayList<RMIMessage> enableHost(final Host hostParam, final boolean enableParam)
    {
        Thread enableServerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String action = ""; if (enableParam) {action="enabled";}else{action="disabled";}
                try { dcmDBClient.enableHost(hostParam, enableParam); } catch (CloneNotSupportedException ex) {  } // who cares
                
                log("Host: " + hostParam.getHostname() + " " + action + " for Polling.", true, true, true);
                rmiMessageList.clear(); rmiMessageList.add(new RMIMessage("Log","DCManagerServer.enableServer(host,enableParam)","Host: " + hostParam.getHostname() + " " + action + " for Polling.",true));
            }
        });
        enableServerThread.setName("enableServerThread");
        enableServerThread.setDaemon(false);
        enableServerThread.setPriority(Thread.NORM_PRIORITY);
        enableServerThread.start();
        return rmiMessageList;
    }
    // End MiddleWare

    // Begin MiddleWare
    public String searchExactHosts(String searchString, ArrayList<Server> serverList)
    {
        String thisOutput = "";
        if (searchString.length() > 0) { thisOutput = dcmDBClient.convertSearchStringToHostsText(searchString, serverList, true); }
        return thisOutput;
    }    
    // End MiddleWare
    
    // Begin MiddleWare
    public String searchNonExactHosts(String searchString, ArrayList<Server> serverList)
    {
        String thisOutput = "";
        if (searchString.length() > 0) { thisOutput = dcmDBClient.convertSearchStringToHostsText(searchString, serverList, false); }
        return thisOutput;
    }    
    // End MiddleWare

    // Begin MiddleWare
    @Override
    public String searchExactResources(String searchString, ArrayList<Server> serverList)
    {
        String thisOutput = "";
        if (searchString.length() > 0) { thisOutput = dcmDBClient.convertSearchStringToResourcesText(searchString, serverList, true); }
        return thisOutput;
    }    
    // End MiddleWare
    
    // Begin MiddleWare
    @Override
    public String searchNonExactResources(String searchString, ArrayList<Server> serverList)
    {
        String thisOutput = "";
        if (searchString.length() > 0) { thisOutput = dcmDBClient.convertSearchStringToResourcesText(searchString, serverList, false); }
        return thisOutput;
    }    
    // End MiddleWare
    
//  Begin Middleware
    public ArrayList<Host> getHostList(String hostLines)
    {
        ArrayList<String> hostIdList = new ArrayList<String>();
        String[] lineArray = new String[5];
        BufferedReader reader = new BufferedReader(new StringReader(hostLines));
        String line = "";
        String thisOutput = "";
        final Object thisRef = this;

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { hostIdList.add(lineArray[0]); }
                }
            }

        } catch(IOException e) { e.printStackTrace(); }

        final ArrayList<Host> hostList = dcmDBClient.convertHostsTextToHostsList(hostIdList);
        return hostList;
    }
//  End Middleware
    public String remoteCommand(int cmdStage, Host hostParam, StringBuffer commandParam, boolean finalCommand, int timeoutParam, boolean debug)
//    public String remoteCommand()
    {
        DCMRemoteCommand dcmRemoteCommand = null;
        try { dcmRemoteCommand = new DCMRemoteCommand(dcmRemoteCommandCaller, 2, hostParam, commandParam, finalCommand, timeoutParam, debug); } catch (CloneNotSupportedException ex) { }
        return dcmRemoteCommand.getCommandOutput();

//        String output = cmdStage +" " + hostParam.getHostname() + " " + commandParam + " " + finalCommand + " " + timeoutParam + " " + debug;
//        output = remoteCommand2(cmdStage, hostParam, commandParam, finalCommand, timeoutParam, debug);
//        return output;
    }
    
    public String remoteCommand2(int cmdStage, Host hostParam, StringBuffer commandParam, boolean finalCommand, int timeoutParam, boolean debug)
    {
        DCMRemoteCommand dcmRemoteCommand = null;
        try { dcmRemoteCommand = new DCMRemoteCommand(dcmRemoteCommandCaller, 2, hostParam, commandParam, finalCommand, timeoutParam, debug); } catch (CloneNotSupportedException ex) { }
        return dcmRemoteCommand.getCommandOutput();
    }
    
    public void remoteFinalCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam) {
    }

    public void remoteCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam) {
    }

    public void remoteCommandFailureResponse(int stageParam, String messageParam) {
    }

    public void inventoryReady(Server serverParam) {
    }

    //  STDIN & STDOUT Redirection
    public class FilteredStream extends FilterOutputStream
    {
        public FilteredStream(OutputStream aStream) { super(aStream); }
        @Override public void write(byte b[]) throws IOException {}
        @Override public void write(byte b[], int off, int len) throws IOException { }
    }
//  End Redirection

    // Begin MiddleWare (Be aware that this method is being called also from the PopupMenu)
    @Override
    public ImageIcon getTrend(final ArrayList<Color> lineColorList, final int imageWidth, final int imageHeight, final boolean manualValueMode, final Calendar startCalendar, final Calendar endCalendar, final ValueLimits valueLimits, final String selectedResources, final Float lineType) throws CloneNotSupportedException
    {
        ArrayList<String> resourceIdList = new ArrayList<String>();
//        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        String[] lineArray = new String[5];
        BufferedReader reader = new BufferedReader(new StringReader(selectedResources));
        String line = "";
        output = "";
        final Object thisRef = this;

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { resourceIdList.add(lineArray[0]); }
                }
            }

        } catch(IOException e) { e.printStackTrace(); }

        final ArrayList<Resource> resourceList = dcmDBClient.convertResourcesTextToResources(resourceIdList);

        String rrdFile = "";
        BufferedImage bufferedImage = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

        RrdGraphDef rrdGraphDef = new RrdGraphDef();
        RrdGraph rrdGraph = null;
        //rrdGraphDef.setTitle("Graph"); // Caused null pointer in Swing

        int colorCounter = 0;
        for (Resource resource:resourceList)
        {
            //System.out.println("File: " + configuration.getRRDBDir() + javaDBClient.selectHost(resource.getHostId()).getHostname() + configuration.getFileSeparator() + resource.getRRDFile());
            String resourceHost = dcmDBClient.selectHost(resource.getHostId()).getHostname();
            rrdFile = configuration.getArchiveDBDir() + resourceHost + configuration.getFileSeparator() + resource.getRRDFile();
            rrdGraphDef.datasource(resource.getId()+resource.getResource(), rrdFile, resource.getResource(), "AVERAGE");
            rrdGraphDef.line(resource.getId()+resource.getResource(), lineColorList.get(colorCounter), resourceHost + " " + resource.getResourceType() + " " + resource.getResource(), lineType); // last=line
            colorCounter++;
        }
        rrdGraphDef.setStartTime((Long)startCalendar.getTimeInMillis()/1000);
        rrdGraphDef.setEndTime((Long)endCalendar.getTimeInMillis()/1000);
//        rrdGraphDef.setVerticalLabel(resourceList.get(0).getCategory() + " " +resourceList.get(0).getValueType());
        rrdGraphDef.setVerticalLabel(resourceList.get(0).getValueType());
        rrdGraphDef.setAltAutoscale(true);
        rrdGraphDef.setWidth(imageWidth - imageEdgeX); // 90
        rrdGraphDef.setHeight(imageHeight - viewEdgeY); // 30 netto (zonder legenda) imageheight 439
        rrdGraphDef.setImageFormat("png");
        rrdGraphDef.setImageInfo(DCMLicense.getProcuct() + " " + DCMLicense.getVersion() + " Trend Analysis");
        rrdGraphDef.setTitle(DCMLicense.getProcuct() + " " + resourceList.get(0).getCategory() + " Trend. Period: [" + DCMTools.getHumanDateLong(startCalendar) + "] - [" + DCMTools.getHumanDateLong(endCalendar)+ "]");
        rrdGraphDef.setAntiAliasing(true);
        rrdGraphDef.setRigid(true);

        rrdGraphDef.setSmallFont(new java.awt.Font("STHeiti", 0, 9));
        rrdGraphDef.setLargeFont(new java.awt.Font("STHeiti", 0, 11));
        rrdGraphDef.setShowSignature(false);

        // Generate the graph to find out how big it becomes including legenda
        rrdGraphDef.setImageQuality(0F); // 0F worst 1F best 
        try { rrdGraph = new RrdGraph(rrdGraphDef); }
        catch (IOException ex)  { log("Error: IOException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }
        catch (RrdException ex) { log("Error: RrdException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }
        int additionalHeight = rrdGraph.getRrdGraphInfo().getHeight() - (imageHeight - viewEdgeY);

        rrdGraphDef.setHeight((imageHeight - (additionalHeight))); // 30 netto (zonder legenda) imageheight 439

//                if (manualValueMode) { rrdGraphDef.setMinValue(minValue); rrdGraphDef.setMaxValue(maxValue); }
        if (manualValueMode) { rrdGraphDef.setMinValue(valueLimits.getMin()); rrdGraphDef.setMaxValue(valueLimits.getMax()); }

        // Generate the graph again, but now shrunken, so the total height incl legenda becomes as big as without legenda
        rrdGraphDef.setImageQuality(0.5F); // 0F worst 1F best 
        try { rrdGraph = new RrdGraph(rrdGraphDef); }
        catch (IOException ex)  { log("Error: IOException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }
        catch (RrdException ex) { log("Error: RrdException: RrdGraph(graphDef)" + ex.getMessage(),true,true,true); }

        rrdGraph.render(bufferedImage.getGraphics());
        imageIcon = new ImageIcon(bufferedImage);

        return imageIcon;
//        return trendImage;
    }
    
    public StringBuffer getData(final Calendar startCalendar, final Calendar endCalendar, final String selectedResources) throws CloneNotSupportedException
    {
        StringBuffer returnData = new StringBuffer();
        ArrayList<String> resourceIdList = new ArrayList<String>();
        String[] lineArray = new String[5];
        BufferedReader reader = new BufferedReader(new StringReader(selectedResources));
        String line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { resourceIdList.add(lineArray[0]); }
                }
            }

        } catch(IOException e) { e.printStackTrace(); }

        final ArrayList<Resource> resourceList = dcmDBClient.convertResourcesTextToResources(resourceIdList);
        
//      RRDFetch being used print RRD values to STDOUT for debugging purposes
        String rrdFetchFile;
        RrdDb rrdDb = null;
        FetchRequest fetchRequest = null;
        FetchData fetchData = null;
        ArrayList<Double> valueList = new ArrayList<Double>();
        Host thisHost = new Host();

        for (Resource resource:resourceList)
        {
            thisHost = new Host(); try { thisHost = (Host) dcmDBClient.selectHost(resource.getHostId()).clone(); } catch (CloneNotSupportedException ex) { log("Error: CloneNotSupportedException: selectHost(resource.getHostId())" + ex.getMessage(),true,true,true); }
            returnData.append("Hostname: ").append(thisHost.getHostname()).append(" Category: ").append(resource.getCategory()).append(" Resource: ").append(resource.getResource()).append(" Archive: ").append(resource.getRRDFile()).append("\n\n");

            rrdFetchFile = configuration.getArchiveDBDir() + thisHost.getHostname() + configuration.getFileSeparator() + resource.getRRDFile();
            try { rrdDb = new RrdDb(rrdFetchFile); }
            catch (IOException ex) { log("Error: IOException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }
            catch (RrdException ex) { log("Error: RrdException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }

            try { fetchRequest = rrdDb.createFetchRequest("AVERAGE", (Long)startCalendar.getTimeInMillis()/1000, (Long)endCalendar.getTimeInMillis()/1000); }
            catch (RrdException ex) { log("Error: RrdException: rrdDb.createFetchRequest(\"AVERAGE\"" + ex.getMessage(),true,true,true); }

            try { fetchData = fetchRequest.fetchData(); }
            catch (RrdException ex) { log("Error: RrdException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }
            catch (IOException ex) { log("Error: IOException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }

            returnData.append(fetchData.dump());

            try { rrdDb.close(); } catch (IOException ex) { log("Error: IOException: rrdDb.close()" + ex.getMessage(),true,true,true); }
        }
        
        return returnData;
    }
    
    // Begin MiddleWare
//    private ValueLimits manualValueMode(String valueButton, String selectedResources) // Wrapper method for showTrend
    @Override
    public ValueLimits manualValueMode(final Calendar startCalendar, final Calendar endCalendar, String selectedResources) // Calculates the selected Archive min and max values
    {
        ValueLimits theseValueLimits = new ValueLimits();
        ArrayList<String> resourceIdList = new ArrayList<String>();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        String[] lineArray = new String[5];
        BufferedReader reader = new BufferedReader(new StringReader(selectedResources));
        String line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                if (line.length()>1)
                {
                    lineArray = line.split("\\s+");
                    if ((lineArray[0] != null) && (lineArray[0].length()>0)) { resourceIdList.add(lineArray[0]); }
                }
            }

        } catch(IOException e) { e.printStackTrace(); }

        resourceList = dcmDBClient.convertResourcesTextToResources(resourceIdList);

////      RRDFetch being used to get min and max value from all selected archives within selected period
        String rrdFetchFile;
        RrdDb rrdDb = null;
        FetchRequest fetchRequest = null;
        FetchData fetchData = null;
        ArrayList<Double> valueList = new ArrayList<Double>();
        Host thisHost = new Host();
        for (Resource resource:resourceList)
        {
            thisHost = new Host(); try { thisHost = (Host) dcmDBClient.selectHost(resource.getHostId()).clone(); } catch (CloneNotSupportedException ex) { log("Error: CloneNotSupportedException: selectHost(resource.getHostId())" + ex.getMessage(),true,true,true); }
            
            rrdFetchFile = configuration.getArchiveDBDir() + thisHost.getHostname() + configuration.getFileSeparator() + resource.getRRDFile();
//            System.out.println("Dumping: " + rrdFetchFile);
            try { rrdDb = new RrdDb(rrdFetchFile); }
            catch (IOException ex) { log("Error: IOException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }
            catch (RrdException ex) { log("Error: RrdException: rrdDb = new RrdDb(rrdFile)" + ex.getMessage(),true,true,true); }

            try { fetchRequest = rrdDb.createFetchRequest("AVERAGE", (Long)startCalendar.getTimeInMillis()/1000, (Long)endCalendar.getTimeInMillis()/1000); }
            catch (RrdException ex) { log("Error: RrdException: rrdDb.createFetchRequest(\"AVERAGE\"" + ex.getMessage(),true,true,true); }

            try { fetchData = fetchRequest.fetchData(); }
            catch (RrdException ex) { log("Error: RrdException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }
            catch (IOException ex) { log("Error: IOException: fetchRequest.fetchData()" + ex.getMessage(),true,true,true); }

//            System.out.println(fetchData.dump());
            for (Double value:fetchData.getValues(0)) { valueList.add(value); }
            try { rrdDb.close(); } catch (IOException ex) { log("Error: IOException: rrdDb.close()" + ex.getMessage(),true,true,true); }
        }
        theseValueLimits.setMin(Double.MAX_VALUE); for (Double value :valueList) { if (theseValueLimits.getMin() > value) {theseValueLimits.setMin(value);} }
        theseValueLimits.setMax(Double.MIN_VALUE); for (Double value :valueList) { if (theseValueLimits.getMax() < value) {theseValueLimits.setMax(value);} }
//        manualValueMode = true;
        
        return theseValueLimits;
    }
    
//    // MiddleWare
//    public void vergunningJumpstart()
//    {
////        while ((!isVisible()) || (!isActive())) { try { Thread.sleep(100); } catch (InterruptedException ex) { } }
//        Thread vergunningJumpstartThread = new Thread(new Runnable()
//        {
//            @Override
//            @SuppressWarnings({"static-access"})
//            public void run()
//            {
//                log("Verifying License, please wait...", true, true, true);
//                dcmVergunningServer.controleerVergunning(); // MiddleWare
//                if (dcmVergunningServer.isValid())
//                {
//                    log("License Validated", true, true, true);
////                    resizeWindowButton.setEnabled(true);
//                    executeVergunning();
//                }
//                else
//                {
//                    log("Failure: DCMServer: " + DCMVergunning.PRODUCT + " not Licensed", true, true, true); // showStatusInbound("", false, false);
//                    try { Thread.sleep(1000); } catch (InterruptedException ex) { }
//                    log("Advise:  DCMServer: Please select your License Type", true ,true, true);
////                    resizeWindowButton.setEnabled(true);
//                }
//            }
//        });
//        vergunningJumpstartThread.setName("vergunningJumpstartThread");
//        vergunningJumpstartThread.setDaemon(true);
//        vergunningJumpstartThread.setPriority(Thread.NORM_PRIORITY);
//        vergunningJumpstartThread.start();
//    }
    
    private void vergunningDoorvoeren() // Makes sure you also see Valid LicenseDetails in the LicenseTab
    {
        dcmVergunningServer = new DCMVergunningServer(dcmServerReference,debug); // MiddleWare
        dcmVergunningServer.controleerVergunning();

        log("Action:  DCMDesktop: License being Validated", false, true, true);
        dcmVergunningServer.controleerVergunning();
        
        // In case people turn back the time
        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance(); last = dcmDBClient.getDCMTijd(); last.add(Calendar.DAY_OF_MONTH, -1);
        if (   now.compareTo(last) < 0 )
        {
            dcmVergunningServer.setVergunningValid(false);
            dcmVergunningServer.setDefaultServersInLicense();
            log("Failure: DCMDesktop: Time issue detected...", true, true, true);
            if (debug) { log("Last: " + DCMTools.getHumanDateLong(last), false, true, true); log("Now:  " + DCMTools.getHumanDateLong(now), false, true, true); }
        }
        else
        {
            dcmDBClient.updateDCMTijd(now);
        }


        if (dcmVergunningServer.isValid()) // MiddleWare Invocation
        {
            log("Success: DCMDesktop: License Valid", true, true, true);
        }
        else
        {
            log("Advise:  DCMDesktop: Thank you for using this Free Lifetime 5 Server License", true, true, true);
            log("Advise:  DCMDesktop: Please click the Admin Tab if you want more Servers Licensed", true, true, true);
            
        }
    }

    // Begin MiddleWare
    // MiddleWare
    @Override public void                   applyReceiveVergunning(String activationCodeParam, String vergunningCodeParam) { dcmVergunningServer.setActivationCodeFromFile(activationCodeParam); dcmVergunningServer.setVergunningCodeFromFile(vergunningCodeParam); dcmVergunningServer.saveVergunning(); dcmVergunningServer.setVergunningOrderInProgress(false); dcmVergunningServer.controleerVergunning(); }
    @Override public String[]               getAK()                                                                 { return dcmVergunningServer.getAK(); }
    @Override public boolean                vergunningIsValid()                                                     { return dcmVergunningServer.isValid(); }
    @Override public Calendar               getDCMTijd()                                                            { return dcmDBClient.getDCMTijd();   }
    @Override public String                 getVergunningPeriod()                                                   { return dcmVergunningServer.getVergunningPeriod(); }
    @Override public String                 getServerVersion()                                                      { return DCMLicense.getVersion(); }
    @Override public String                 getVergunningStartDateString()                                          { String startDate = String.format("%04d", dcmVergunningServer.getVergunningStartDate().get(Calendar.YEAR)) + "-" + String.format("%02d", (dcmVergunningServer.getVergunningStartDate().get(Calendar.MONTH)) + 1) + "-" + String.format("%02d", dcmVergunningServer.getVergunningStartDate().get(Calendar.DAY_OF_MONTH)); return startDate; }
    @Override public String                 getVergunningEndDateString()                                            { String endDate =   String.format("%04d", dcmVergunningServer.getVergunningEndDate().get(Calendar.YEAR)) + "-" +   String.format("%02d", (dcmVergunningServer.getVergunningEndDate().get(Calendar.MONTH)) + 1) + "-" +   String.format("%02d", dcmVergunningServer.getVergunningEndDate().get(Calendar.DAY_OF_MONTH));   return endDate;   }
    @Override public Calendar               getVergunningStartDate()                                                { return dcmVergunningServer.getVergunningStartDate(); }
    @Override public Calendar               getVergunningEndDate()                                                  { return dcmVergunningServer.getVergunningEndDate();   }
    @Override public int                    getServersInLicense()                                                   { return dcmVergunningServer.getServersInLicense(); }
    @Override public String                 getActivationCode()                                                     { return dcmVergunningServer.getActivationCodeFromFile(); }
    @Override public String                 getVergunningCode()                                                     { return dcmVergunningServer.getVergunningCodeFromFile(); }
    @Override public String                 getVergunningInvalidAdvice()                                            { return dcmVergunningServer.getVergunningInvalidAdvice(); }
    @Override public String                 getVergunningInvalidReason()                                            { return dcmVergunningServer.getVergunningInvalidReason(); }
    @Override public void                   setVergunningOrderInProgress(boolean inProgress)                        { dcmVergunningServer.setVergunningOrderInProgress(inProgress); }
    @Override public void                   setVergunningValid(boolean validParam)                                  { dcmVergunningServer.setVergunningValid(validParam); }
    @Override public void                   setDefaultServersInLicense()                                            { dcmVergunningServer.setDefaultServersInLicense(); }
    @Override public void                   reloadVergunning()                                                      { dcmVergunningServer = new DCMVergunningServer(dcmServerReference,debug); dcmVergunningServer.controleerVergunning(); }
    @Override public void                   setDCMTijd(Calendar nowParam)                                           { dcmDBClient.updateDCMTijd(nowParam); }
    @Override public void                   setVergunningPeriod(String vergunningPeriodParam)                       { dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam); }
    @Override public void                   setVergunningStartDate(Calendar startDateParam)                         { dcmVergunningServer.setVergunningStartDate(startDateParam); }
    @Override public void                   setVergunningEndDate(Calendar endDateParam)                             { dcmVergunningServer.setVergunningEndDate(endDateParam); }
    @Override public void                   setServersInLicense(int serversInLicenseParam)                          { dcmVergunningServer.setServersInLicense(serversInLicenseParam); }
    @Override public void                   setActivationCode(String activationCodeParam)                           { dcmVergunningServer.setActivationCodeFromFile(activationCodeParam); }
    @Override public void                   setVergunningCode(String vergunningCodeParam)                           { dcmVergunningServer.setVergunningCodeFromFile(vergunningCodeParam); }
    // End MiddleWare
    

//    // MiddleWare
//    public void executeVergunning() // Makes sure you also see Valid LicenseDetails in the LicenseTab
//    {
//        log("Action:  DCMServer: License being Validated", false, true, true);
//        if (dcmVergunningServer.isValid())
//        {
//            log("Success: DCMServer: License Valid", true, true, true);
//            dcmVergunningServer.setVergunningOrderInProgress(false);
//        }
//        else
//        {
//            log("Failure: DCMServer: Product not Licensed", true, true, true);
//        }
//    }
    
    // Variables declaration - do not modify
    // End of variables declaration

    // MiddleEnd
    @Override public void inventoryReady()
    {
        // JavaDB & RRDB creation finished, now update serversTree
    }
    
    // MiddleWare
    @Override
    public ArrayList<Server> getServerList() throws CloneNotSupportedException
    {
        ArrayList<Server> thisServerList = new ArrayList<Server>();
        thisServerList = dcmDBClient.selectServers();
        return thisServerList;
    }
        
    // MiddleWare
    @Override
    public String updateVergunning(int serversInLicenseParam, Calendar selectedDateParam, String vergunningPeriodParam) // Basically sets the userpreferences and returns an AK
    {
        Calendar thisVergunningStartCalendar = Calendar.getInstance(); thisVergunningStartCalendar = selectedDateParam;
        Calendar thisVergunningEndCalendar = Calendar.getInstance();
        String[] status = new String[2];
        String activationCodeString = null;
        String activationCodeKeyString = null;

        setVergunningOrderInProgress(true); // Middleware Invocation

        // Set vergunning: ServerInLicense, startDate, endDate, vergunningPeriod, Activation
        setServersInLicense(serversInLicenseParam); // Middleware Invocation
        setVergunningStartDate(selectedDateParam);

        // Prematurely writing vergunning Start Date Details
        if (selectedDateParam != null)
        {
            thisVergunningEndCalendar.setTimeInMillis(thisVergunningStartCalendar.getTimeInMillis()); // nullpointer calendars not instanciated in contructor!!
            if      ( vergunningPeriodParam.equals("Day") )
            {
                thisVergunningEndCalendar.add(Calendar.DAY_OF_YEAR, 1);
                dcmVergunningServer.setVergunningEndDate(thisVergunningEndCalendar);
                dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam);
            }
            else if ( vergunningPeriodParam.equals("Week") )
            {
                thisVergunningEndCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                dcmVergunningServer.setVergunningEndDate(thisVergunningEndCalendar);
                dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam);
            }
            else if ( vergunningPeriodParam.equals("Month") )
            {
                thisVergunningEndCalendar.add(Calendar.MONTH, 1);
                dcmVergunningServer.setVergunningEndDate(thisVergunningEndCalendar);
                dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam);
            }
            else if ( vergunningPeriodParam.equals("Year") )
            {
                thisVergunningEndCalendar.add(Calendar.YEAR, 1);
                dcmVergunningServer.setVergunningEndDate(thisVergunningEndCalendar);
                dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam);
            }
            else if ( vergunningPeriodParam.equals("Lifetime") )
            {
                thisVergunningEndCalendar.add(Calendar.YEAR, 20);
                dcmVergunningServer.setVergunningEndDate(thisVergunningEndCalendar);
                dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam);
            }
        }

        // Prematurely writing vergunning Period Details
        if (vergunningPeriodParam.length()>0)
        {
            dcmVergunningServer.setVergunningPeriod(vergunningPeriodParam);
        }

        // If all vergunning fields are selected then write ActivationCode
        if (
                (thisVergunningStartCalendar != null) &&
                (vergunningPeriodParam != null)
           )
        {
            status = dcmVergunningServer.getAK(); // MiddleWare Invocation
            if (status[0].equals("0"))
            {
                // Get the activation string
                activationCodeKeyString = status[1];
                activationCodeString =  serversInLicenseParam + "-" +
                                        String.format("%04d", thisVergunningStartCalendar.get(Calendar.YEAR)) + "-" +
                                        String.format("%02d", (thisVergunningStartCalendar.get(Calendar.MONTH)) + 1 ) + "-" +
                                        String.format("%02d", thisVergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                                        vergunningPeriodParam + "-" +
                                        activationCodeKeyString; // Hash
            }
        }
        
        return activationCodeString;
    }
        
    // MiddleWare
    @Override
    public boolean vergunningReachedLimit() // Disables adding of to many servers
    {
        boolean limitExceeded = true;
        if ((dcmMetaDBClientIsRead)&&(dcmVergunningServer != null))
        {
            if ( dcmDBClient.getHostCount() >= dcmVergunningServer.getServersInLicense() )
            {
                limitExceeded = true;
            }
            else 
            {
                limitExceeded = false;
            }            
        }
        return limitExceeded;
    }
    
    // MiddleWare
    @Override
    public synchronized ArrayList<RMIMessage> deleteServerByHostname(String hostnameParam)
    {
        rmiMessageList.clear();
        try 
        {
            if ( dcmDBClient.deleteServerByHostname(hostnameParam))
            {
                rmiMessageList.add(new RMIMessage("Status","DCManagerServer.deleteServerByHostname","Success: DCMServer: Server successfully deleted",true));
            }
            else
            {
                rmiMessageList.add(new RMIMessage("Status","DCManagerServer.deleteServerByHostname","Error:   DCMServer: Server failed deletion, please check log",false));
            } 
        } catch (CloneNotSupportedException ex) {   }
        return rmiMessageList;
    }    
    
    // MiddleWare
    @Override
    public Host getHostByHostname(String hostnameParam)
    {
        Host thisHost = null; try { thisHost = dcmDBClient.getHostByHostname(hostnameParam); } catch (CloneNotSupportedException ex) {   }
        return thisHost;
    }

    // MiddleWare
    @Override
    public Server getServerByHostname(String hostnameParam)
    {
        Server server = null;
        try { server = (Server) dcmDBClient.getServerByHostname(hostnameParam).clone(); } catch (CloneNotSupportedException ex) { }
        return server;
    }
    
    // MiddleWare
    @Override
    public Host getHostById(long idParam) throws CloneNotSupportedException
    {
        Host thisHost = null;
//        try { host = dcmMetaDBClient.selectHost(idParam).clone(); } catch (CloneNotSupportedException ex) { }
        thisHost = (Host) dcmDBClient.selectHost(idParam).clone();
        return thisHost;
    }
    
    @Override
    public boolean hostnameChangedInHostTable(String oldHostnameParam,String newHostnameParam)
    {
        // ArchiveDir is named after hostname, so that needs to be renamed to
        boolean succeeded = false;
        File oldHostnameDir = new File(configuration.getArchiveDBDir() + oldHostnameParam + configuration.getFileSeparator());
        File newHostnameDir = new File(configuration.getArchiveDBDir() + newHostnameParam + configuration.getFileSeparator());
        if (oldHostnameDir.renameTo(newHostnameDir))
        {
            succeeded = true;
            log("Info: Renamed: " + oldHostnameDir.getPath() + " to: " + newHostnameDir.getPath(),true,true,true);
        }
        else
        {
            succeeded = false;
            log("Error: Renaming: " + oldHostnameDir.getPath() + " to: " + newHostnameDir.getPath(),true,true,true);
        }
        return succeeded;
    }
    
    // MiddleWare
    @Override
    public synchronized ArrayList<RMIMessage> updateHost(Host hostParam, boolean generateScriptParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); rmiMessageList = dcmDBClient.updateHost(hostParam);
        if (generateScriptParam) { pushPollScript(dcmDBClient.getServer(hostParam.getId())); }
        
        return rmiMessageList;
    }
    
    // Begin MiddleWare
    @Override
    public Server getServerById(long idParam)
    {
        Server server = new Server();try { server = dcmDBClient.getServer(idParam); } catch (CloneNotSupportedException ex) {  }
        return server;
    }
    // End MiddleWare
        
    // Begin MiddleWare
    @Override
    public Resource getResourceById(long idParam)
    {
        Resource resource = new Resource();try { resource = (Resource) dcmDBClient.selectResource(idParam).clone(); } catch (CloneNotSupportedException ex) {  }
        return resource;
    }
    // End MiddleWare
        
    // Begin MiddleWare
    @Override
    public synchronized ArrayList<RMIMessage> pushPollScript(Server serverParam)
    {
        rmiMessageList.clear();
        try { DCMPushPollScriptServer pushPollScriptServer = new DCMPushPollScriptServer(dcmServerReference, serverParam); } catch (UnknownHostException ex) { }
        return rmiMessageList;
    }
    // End MiddleWare
        
    // MiddleWare
    @Override
    public ArrayList<RMIMessage> updateResource(Resource resourceParam) throws CloneNotSupportedException
    {
        rmiMessageList.clear(); dcmDBClient.updateResource(resourceParam); return rmiMessageList;
    }
    
    @Override    
    public synchronized void log(final String messageParam, final boolean logToStatusParam, final boolean logToApplicationParam, final boolean logToFileParam)
    {
        if (logToStatusParam) { rmiMessageList.add(new RMIMessage("","",messageParam,true)); }
        Thread InventoryGUILogThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (logToStatusParam)       { System.out.println(messageParam); }
                if (logToApplicationParam)  {  }
                if (logToFileParam)         { logToFile(messageParam); }
            }
        });
        InventoryGUILogThread.setName("InventoryGUILogThread");
        InventoryGUILogThread.setDaemon(false);
        InventoryGUILogThread.setPriority(Thread.NORM_PRIORITY);
        InventoryGUILogThread.start();
    }

    public synchronized void logToFile(final String displaymessage)
    {
        Thread logToFileThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                Calendar logCalendar = Calendar.getInstance();

                String humanDate = "" +
                String.format("%04d", logCalendar.get(Calendar.YEAR)) + "-" +
                String.format("%02d", logCalendar.get(Calendar.MONTH) + 1) + "-" +
                String.format("%02d", logCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
                String.format("%02d", logCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", logCalendar.get(Calendar.MINUTE)) + ":" +
                String.format("%02d", logCalendar.get(Calendar.SECOND));

                try { logFileWriter = new FileWriter(logFileString, true ); } catch (IOException ex) { System.out.println("Error: IOException: new FileWriter(" + logFileString + ")" + ex.getMessage()); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }
                try { logFileWriter.flush(); } catch (IOException ex) { System.out.println("Error: IOException: logFileWriter.flush()1;"); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }
                try { logFileWriter.write(logBuffer + humanDate + " " + displaymessage + "\r\n"); } catch (IOException ex) { System.out.println("Error: IOException: logFileWriter.write()"); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }
                try { logFileWriter.flush(); } catch (IOException ex) { System.out.println("Error: IOException: logFileWriter.flush()2;"); logBuffer += humanDate + " " + displaymessage + "\r\n"; return; }

                logBuffer = "";

                try { logFileWriter.close(); }
                catch (IOException ex) { System.out.println("Error: IOException: logFileWriter.close(); " + ex.getMessage()); return; }
            }
        });
        logToFileThread.setName("logToFileThread");
        logToFileThread.setDaemon(runThreadsAsDaemons);
        logToFileThread.start();
    }

    @Override
    public void dbClientReady()
    {
        dcmMetaDBClientIsRead = true;
    }

    @Override public void updatedHost() // if the hostname changed then reload the serverTree hostname, but not with other hostchanges like Enabled change
    {
        // Used by DBClient after making hostchanges in DB
    }

    @Override
    public void startPoller()
    {
        String javaParam = ""; if (daemon) { javaParam = " --daemon "; }
        shell.startPoller(128," -Duser.country=US -Duser.language=en" + " ", javaParam);
    } // MiddleWare Invocation

    @Override
    public void pollServerReady()
    {
    }

    public static void main(final String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (args.length < 1) { usage(); System.exit(1); }
                else
                {
                    
                    try
                    {
                        try
                        {
                            boolean daemon = false;
                            boolean debug = false;
                            for (int i = 0; i < args.length; i++)
                            {
                                if      ( args[i].equals("--daemon"))  { daemon = true; }
                                else if ( args[i].equals("--debug"))   { debug = true; }
                                else if ( args[i].equals("--help"))    { usage(); System.exit(1); }
                            }
                            new DCMServer(Integer.parseInt(args[0]), daemon, debug);
                        } catch (MalformedURLException ex) {}
                    }
                    catch (RemoteException ex) {  }
                }
//                Thread runThread = new Thread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(true);
//                    }
//                });
//                runThread.setName("runThread");
//                runThread.setDaemon(false);
//                runThread.setPriority(Thread.MIN_PRIORITY);
//                runThread.start();
            }
        });
    }
    
    private static void usage()
    {
        System.out.println("\n");
        System.out.println("Usage:   java -server -Djava.rmi.server.hostname=server.domain.com -cp DCManager.jar DCMServer <port> [--daemon] [--debug]\n");
        System.out.println(DCMLicense.getCopyright());
    }
    
}
