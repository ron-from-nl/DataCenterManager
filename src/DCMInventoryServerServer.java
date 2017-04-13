import data.Configuration;
import data.ConfigurationCaller;
import data.Resource;
import data.Server;
import data.Host;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

// This object actually is the DCMServer version or should i say DCM(RMI)Server version, so you know what i mean
public class DCMInventoryServerServer implements DCMRemoteCommandCaller, DCMDBClientCaller, ConfigurationCaller, DCMPushPollScriptCaller, DCMFileTranferCaller // Login to server for inventory (data) and uses Convert to convert to server object with resources
{

//  STDIN & STDOUT Redirection
    public class FilteredStream extends FilterOutputStream
    {
        public FilteredStream(OutputStream aStream)
        {
            super(aStream);
        }

        @Override
        public void write(byte b[]) throws IOException
        {
//            String aString = new String(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException
        {
//            String aString = new String(b , off , len);
        }
    }
//  End Redirection

    private Server server;
    private Host host;
    String user;
    String userPassword;
    String superuserPassword = null;
    String serversListArray;

    DCMRemoteCommand remoteCommand;
    
    StringBuffer command;
    String tmpOutput;
    String sysinfo;
    String output;
    String data;
        
    int port = 22;
    int sessionTimeout = 3;

    int userCounter = 0;
    int userPasswordsCounter = 0;
    int superuserPasswordsCounter = 0;
    int serversCounter = 0;
    int commandCounter = 0;
    int lastCommandCounter = 0;
    int serverInstance = 0;
    int serverInstanceCounter = 0;
    String scriptTip = "# Commands or Script to Execute";

    boolean                         daemon = false;
    boolean                         debug = false;
    
    final DCMRemoteCommandCaller        remoteCommandCaller;
    DCMInventoryServerServerCaller      inventoryServerServerCaller;
    final DCMFileTranferCaller          dcmFileTranferCaller;
    private DCMDBClient                 javaDBClient;
    private DCMInventoryServerServer    inventoryServerServerReference;
    private Configuration               configuration;
    
    private String                      customPromptFlat =  "DCMCommandEnd";
    private final int                   IDENTIFICATIONSTAGE = 0;
    private final int                   IVENTORYSTAGE = 1;
    private String[]                    stageArray;
    private long                        inventoryInstance;
//    private String                      inventoryHeaderKey;
    private String                      pollerHeaderKey;
    private InetAddress                 inetAddress;
    private String                      dcmServerIP;
    private String                      dcmInventoryScriptFile;
    private String                      dcmPollerScriptFile;
    private String                      dcmPollerDataFile;

    private final String            LINUX =     "Linux";
    private final String            DARWIN =    "Darwin";
    private final String            SUNOS =     "SunOS";
    private final String            AIX =       "AIX";
    private final String            HPUX =      "HP-UX";

    private int retentionTime =     1;
    private int retryMax =          2;
    private int retryCounter =      0;
    
    public DCMInventoryServerServer(final DCMInventoryServerServerCaller inventoryServerServerCallerParam, final long inventoryInstanceParam, final Host hostParam, final int retentionTimeParam, final int timeoutParam, int retryMaxParam, final boolean daemonParam, final boolean debugParam)
    {
	retentionTime = retentionTimeParam;
        retryCounter = 0;
        retryMax = retryMaxParam;
        stageArray = new String[2];
        stageArray[IDENTIFICATIONSTAGE] = "IdentificationStage";
        stageArray[IVENTORYSTAGE] = "InventoryStage";

        daemon = daemonParam;
        debug = debugParam;
        sessionTimeout = timeoutParam;
//      STDIN & STDOUT Redirection
//        if ((!Configuration.DEBUG)&&(!debug))
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        }
        
        inventoryServerServerReference =    this;
        dcmFileTranferCaller =              this;
        inventoryServerServerCaller =       inventoryServerServerCallerParam;
        remoteCommandCaller =               this;
        inventoryInstance =                 inventoryInstanceParam;
        configuration =                     new Configuration(inventoryServerServerReference);
        try { inetAddress =                 InetAddress.getLocalHost(); } catch (UnknownHostException ex) {  }
        dcmServerIP =                       inetAddress.getHostAddress();
        host =                              hostParam;
        dcmInventoryScriptFile =            ".dcminventoryscript_" + dcmServerIP + "_" + host.getHostname() + ".sh";

        javaDBClient = null; try { javaDBClient = new DCMDBClient(inventoryServerServerReference, configuration.getJavaDB(), daemon, debug); }
        catch (SQLException ex)                 { inventoryServerServerCaller.log("SQLException", true, true, true); }
        catch (ClassNotFoundException ex)       { inventoryServerServerCaller.log("ClassNotFoundException: " + ex.getMessage(), true, true, true); }
        catch (InstantiationException ex)       { inventoryServerServerCaller.log("InstantiationException: " + ex.getMessage(), true, true, true); }
        catch (IllegalAccessException ex)       { inventoryServerServerCaller.log("IllegalAccessException: " + ex.getMessage(), true, true, true); }
        catch (NoSuchMethodException ex)        { inventoryServerServerCaller.log("NoSuchMethodException: " + ex.getMessage(), true, true, true); }
        catch (InvocationTargetException ex)    { inventoryServerServerCaller.log("InvocationTargetException: " + ex.getMessage(), true, true, true); }
        catch (Exception ex)                    { inventoryServerServerCaller.log("Exception: " + ex.getMessage(), true, true, true); }

//        ystem.out.println("DBServerTest: " + Boolean.toString(javaDBClient.getDBServerTest()));

        server = new Server();
        server.setHost(host);
        command = new StringBuffer();

        // Identification Command
        command.append(getIdentificationCommand());
        inventoryServerServerCaller.log("Action:  DCMInventoryServerServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Identification Stage", true, true, true);
        try { remoteCommand = new DCMRemoteCommand(remoteCommandCaller, IDENTIFICATIONSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // true = finalCommand
    }

    @Override
    public synchronized void remoteFinalCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam)
    {
        if (stageParam==IDENTIFICATIONSTAGE)
        {
            sysinfo = DCMTools.substitude(DCMTools.substitude(DCMTools.startsWith(stdOutParam, "Sysinfo:"), "Sysinfo:"), customPromptFlat + ".*$");
            if ( sysinfo.length() > 2)
            {
                inventoryServerServerCaller.log("Success: DCMInventoryServerServer: " + host.getHostname()+ " Identification completed", true, true, true);
                host.setSysinfo(sysinfo);
                server.setHost(host);
                data += sysinfo;
                
                // Compile the Inventory Script
                command.setLength(0); command.append(getInventoryScript(sysinfo)); command.append(getRequirementsScript(sysinfo));
                
                new DCMFileWrite(dcmInventoryScriptFile,command.toString());
                
                // Send the file to the server
                inventoryServerServerCaller.log("Action:  DCMInventoryServerServer [" + inventoryInstance + "] transfering inventoryscript to Host: " + host.getHostname(), true, true, true);                
                try { new DCMFileTransfer(dcmFileTranferCaller,server.getHost(),dcmInventoryScriptFile); } catch (CloneNotSupportedException ex) { }
                
                // Set the RemoteCommand to run the script 
                command.setLength(0);
                command.append("sh " + dcmInventoryScriptFile + "; ").append("echo \"" + customPromptFlat + "\";"); // Arranged prompt output for expect string in RemoteCommand
//              -------------------------------------------------------------------------------------------------------------------------------------------------------------                

                inventoryServerServerCaller.log("Action:  DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Inventory Stage", true, true, true);                
                try { new DCMRemoteCommand(remoteCommandCaller, IVENTORYSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) {  }
            }
            else
            {
                retryCounter++;
                inventoryServerServerCaller.log("Failure: DCMInventoryServerServer [" + inventoryInstance + "] Host: " + host.getHostname()+ " failed to identify server", true, true, true);
                if (retryCounter <= retryMax)
                {
//                    command.setLength(0);
//                    command.append("sh " + dcmInventoryScriptFile + "; ").append("echo \"" + customPromptFlat + "\";"); // Arranged prompt output for expect string in RemoteCommand
//                    inventoryServerServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Inventory Stage", true, true, true);                
//                    try { new DCMRemoteCommand(remoteCommandCaller, IVENTORYSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) {  }

                    command.setLength(0); command.append(getIdentificationCommand());
                    inventoryServerServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Identification Stage", true, true, true);
                    try { new DCMRemoteCommand(remoteCommandCaller, IDENTIFICATIONSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // true = finalCommand
                    data = "";                
                }
            }
        }
        else if ( stageParam==IVENTORYSTAGE ) // remote identification succeeded, so we have the inventory data block
        {
//            output = Tools.grep(stdOutParam, inventoryHeaderKey);
            output = stdOutParam;
            if ( sysinfo.length() > 2)
            {
                inventoryServerServerCaller.log("Success: DCMInventoryServerServer [" + inventoryInstance + "] Host: " + host.getHostname()+ " inventory completed", true, true, true);

                data += output;
                
//              Log missing requirements advise                
                String missingServerRequirements = getMissingServerRequirements(sysinfo, data); if (missingServerRequirements.length() >0 ) { inventoryServerServerCaller.log(missingServerRequirements, true, true, true); }

                // This part creates the server object including a resource list with commands
                if      (sysinfo.contains(LINUX))
                {
                    DCMDataConverterLinux inventoryServerDataConverterLinux = null;
                    try { inventoryServerDataConverterLinux = new DCMDataConverterLinux(host,retentionTime); } catch (UnknownHostException ex) { inventoryServerServerCaller.log("InventoryServerServer Received Thrown UnknownHost from the LinuxCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterLinux.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(DARWIN))
                {
                    DCMDataConverterDarwin dcmDataConverterDarwin = null;
                    try { dcmDataConverterDarwin = new DCMDataConverterDarwin(host,retentionTime); } catch (UnknownHostException ex) { inventoryServerServerCaller.log("InventoryServerServer Received Thrown UnknownHost from the DarwinCommandLibrary", true, true, true); }
                    try { server = (Server) dcmDataConverterDarwin.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(SUNOS))
                {
                    DCMDataConverterSunOS inventoryServerDataConverterSunOS = null;
                    try { inventoryServerDataConverterSunOS = new DCMDataConverterSunOS(host,retentionTime); } catch (UnknownHostException ex) { inventoryServerServerCaller.log("InventoryServerServer Received Thrown UnknownHost from the SunOSCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterSunOS.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(AIX))
                {
                    DCMDataConverterAIX inventoryServerDataConverterAIX = null;
                    try { inventoryServerDataConverterAIX = new DCMDataConverterAIX(host,retentionTime); } catch (UnknownHostException ex) { inventoryServerServerCaller.log("InventoryServerServer Received Thrown UnknownHost from the AIXCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterAIX.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(HPUX))
                {
                    DCMDataConverterHPUX inventoryServerDataConverterHPUX = null;
                    try { inventoryServerDataConverterHPUX = new DCMDataConverterHPUX(host,retentionTime); } catch (UnknownHostException ex) { inventoryServerServerCaller.log("InventoryServerServer Received Thrown UnknownHost from the HPUXCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterHPUX.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerServerCaller.log("Inventory Clone server", true, true, true); }                    
                }

                // Before inserting the new server, delete previous server records (with same hostname)
                try { javaDBClient.deleteServerByHostname(server.getHost().getHostname()); } catch (CloneNotSupportedException ex) { }                
                
                // Create non existing RRD Archives
                new DCMArchiveCreateServer(inventoryServerServerReference, server);

                // Insert newly discovered server into javadb
                inventoryServerServerCaller.log("Action:  DCMInventoryServerServer [" + inventoryInstance + "] inserting server: " + host.getHostname()+ " into MetaDB", true, true, true);                
                javaDBClient.insertServer(server);
                
                // Get the server back from the database as all her resources now have autonumbered id by the insertServer directly above
                try { server = (Server) javaDBClient.getServerByHostname(host.getHostname()); } catch (CloneNotSupportedException ex) {  }

                // Create the Poll Script and push it to the server
                try { DCMPushPollScript pushPollScript = new DCMPushPollScript(inventoryServerServerReference, server); } catch (UnknownHostException ex) { }
                
//              --------------------------------------------------------------------------------------------------------------------------------------------------------------
                                
                // Delete any unlinked RRD archives
                deleteUnlinkedRRDArchives();
                
                // Report inventory process ready
                inventoryServerServerCaller.log("Action:  DCMInventoryServerServer [" + inventoryInstance + "] inventory for server: " + host.getHostname()+ " completed", true, true, true);                
                inventoryServerServerCaller.inventoryReady();
            }
            else
            {
                inventoryServerServerCaller.log("Failure: DCMInventoryServerServer [" + inventoryInstance + "] Host: " + host.getHostname()+ " inventory failed", true, true, true);
            }
        }
        else
        {
            inventoryServerServerCaller.log("Failure: DCMInventoryServerServer [" + inventoryInstance + "] Host: " + host.getHostname() + " Invalid inventory stage: " + stageParam + " returned", true, true, true);
        }
    }

    @Override
    public synchronized void remoteCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam)
    {
        inventoryServerServerCaller.log("Success: DCMInventoryServerServer: remoteCommandSuccessResponse", true, true, true);
    }

    @Override
    public synchronized void remoteCommandFailureResponse(int stageParam, String messageParam)
    {
//          inventoryServerServerCaller.log("Error:   DCMInventoryServerSetver: remoteCommandFailureResponse: on " + stageArray[stageParam] + " " + messageParam, true, true, true);

        retryCounter++;
        if ( retryCounter <= retryMax )
        {
            if ( stageParam==IDENTIFICATIONSTAGE )
            {
                command.setLength(0); command.append(getIdentificationCommand());
                inventoryServerServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Identification Stage", true, true, true);
                try { new DCMRemoteCommand(remoteCommandCaller, IDENTIFICATIONSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // true = finalCommand
                data = "";                
            }
            else if ( stageParam==IVENTORYSTAGE )
            {
                inventoryServerServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Inventory Stage", true, true, true);                
                try { new DCMRemoteCommand(remoteCommandCaller, IVENTORYSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) {  }
            }
        }
    }
    
    public synchronized void rrdCreateSuccessResponse(String messageParam)
    {
        inventoryServerServerCaller.log("Success: DCMInventoryServerServer [" + inventoryInstance + "] Host: " + host.getHostname() + messageParam, true, true, true);
    }
    
    public synchronized void rrdCreateFailureResponse(String messageParam)
    {
        inventoryServerServerCaller.log("Failure: DCMInventoryServerServer [" + inventoryInstance + "] Host: " + host.getHostname() + messageParam, true, true, true);
    }
    
    
    public Server getServer()
    {
        return server;
    }

    private synchronized void deleteUnlinkedRRDArchives()
    {
        // Getting list of DB files (javaDBFileList)
        ArrayList<String> javaDBFileList = new ArrayList<String>();
        for (Resource resource:server.getResourceList()) {javaDBFileList.add(resource.getRRDFile());}
        Collections.sort(javaDBFileList);
        
        //for (String file:javaDBFileList) { System.out.println("javaDBFileList: " + file); }

        // Getting list of FS files (rrdbFileList)
        File hostRRDBDir = new File(configuration.getArchiveDBDir() + server.getHost().getHostname() + configuration.getFileSeparator());
        //System.out.println("deleteUnlinkedRRDArchives(): hostRRDBDir.getAbsolutePath(): " + hostRRDBDir.getAbsolutePath());
        ArrayList<String> rrdbFileList = new ArrayList<String>();
        for (File file:hostRRDBDir.listFiles()) {rrdbFileList.add(file.getName());}
        Collections.sort(rrdbFileList);
        
        //for (String file:rrdbFileList) { System.out.println("rrdbFileList: " + file); }
        
        // Do the comparison
        for (String file:rrdbFileList)
        {
            if (! javaDBFileList.contains(file))
            {
                File unlinkedFile = new File(configuration.getArchiveDBDir() + server.getHost().getHostname() + configuration.getFileSeparator() + file);
                if (unlinkedFile.delete()) { inventoryServerServerCaller.log("Removed unlinked archive file: " + unlinkedFile.getName(), true, true, true); }
                else { inventoryServerServerCaller.log("Could not remove unlinked archive file: " + unlinkedFile.getName(), true, true, true); }
            };
        }
    }
    
    // echo -n is problem in superuser mode due root's shell "sh" inbuild echo (-n is printed) and leave the ";"
    public String getIdentificationCommand() { return "echo \"Sysinfo: `uname -a`\"; echo \"" + customPromptFlat + "\";\r\n"; } // echo -n seems to work again on mac

//    public StringBuffer getInventoryCommand(String headerKeyParam, String sysinfoParam) 
    public StringBuffer getInventoryScript(String sysinfoParam) 
    {
        StringBuffer cmd = new StringBuffer();
        if (sysinfoParam.contains(LINUX)) // This generates the variable datasources
        {
            cmd.append("mpstat -P ALL | egrep -v \"CPU|^$\" | sed \"s/AM//\" | sed \"s/PM//\" | awk '{ print $2 }' | while read resource; do echo ").append("\"CPU: ${resource}\"; done; \n"); // CPU's
            cmd.append("iostat | egrep -ive \"cpu|device|^$|^ +\" | awk '{ print $1 }' | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -kP | grep \"^/dev/\" | awk '{ print $6 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | egrep -vie \"kernel|^Iface|MTU\" | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\";\r\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(DARWIN)) // This generates the variable datasources
        {
            cmd.append("echo \"").append("\"CPU: all; \n"); // Cannot get individual core/cpu stats (average of all cores together)
            cmd.append("for resource in `iostat -d | head -1`; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -kP | grep \"^/dev/\" | awk '{ print $6 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | egrep \"en\" | grep Link | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces egrep -e option "-e" doesn't work on Mac
//            cmd.append("echo \"").append(customPromptFlat).append("\";\r\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(SUNOS)) // This generates the variable datasources
        {
            cmd.append("mpstat | egrep -v \"CPU|^$\" | awk '{ print $1 }' | while read resource; do echo ").append("\"CPU: ${resource}\"; done; \n"); // CPU's
            cmd.append("iostat -xtc | grep -v device | awk '{ print $1 }' | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -lk | grep \"^/\" | grep -v \"/platform/\" | awk '{ print $6 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | awk '{ print $1 }' | egrep -ive \"name|^$\" | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(AIX)) // This generates the variable datasources
        {
            cmd.append("mpstat | egrep -vi \"CPU|^$|U\" | awk '{ print $1 }' | while read resource; do echo ").append("\"CPU: ${resource}\"; done; \n"); // CPU's
            cmd.append("iostat -ld | egrep -e \"^hdisk\" | awk '{ print $1 }' | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -k | grep \"/\" | grep -v \"/proc\" | awk '{ print $7 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | grep -i \"link\" | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\"; \n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(HPUX)) // This generates the variable datasources
        {
            cmd.append("c=0;while [ $c -lt `machinfo | grep -i cpus | awk '{ print $5 }'` ]; do echo $c;c=`expr $c + 1`;done | while read resource; do echo ").append("\"CPU: ${resource}\"; done; \n"); // CPU's
            cmd.append("echo \"CPU: all\"\n"); // Due to the inconsistant format of sar the all cpu has to be manual added
            cmd.append("iostat | awk '{ print $1 }' | egrep -ie \"^c[0-9]t[0-9]d[0-9]\" | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -kP | awk '{ print $6 }' | egrep \"/\" | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -ni | grep -vi \"Name\" | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        return cmd;
    }

    public StringBuffer getRequirementsScript(String sysinfoParam) 
    {
        StringBuffer cmd = new StringBuffer();
        if (sysinfoParam.contains(LINUX)) // This generates the requirements data
        {
            DCMCommandLibraryLinux dcmCommandLibraryLinux = null; try { dcmCommandLibraryLinux = new DCMCommandLibraryLinux(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryLinux.getCommandArray()) { cmd.append("echo \"CMD: " + cmdRecord[0] + " " + cmdRecord[1] + " `which " + cmdRecord[1] + "`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(DARWIN)) // This generates the variable datasources
        {
            DCMCommandLibraryDarwin dcmCommandLibraryDarwin = null; try { dcmCommandLibraryDarwin = new DCMCommandLibraryDarwin(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryDarwin.getCommandArray()) { cmd.append("echo \"CMD: " + cmdRecord[0] + " " + cmdRecord[1] + " `which " + cmdRecord[1] + "`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(SUNOS)) // This generates the variable datasources
        {
            DCMCommandLibrarySunOS dcmCommandLibrarySunOS = null; try { dcmCommandLibrarySunOS = new DCMCommandLibrarySunOS(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibrarySunOS.getCommandArray()) { cmd.append("echo \"CMD: " + cmdRecord[0] + " " + cmdRecord[1] + " `which " + cmdRecord[1] + "`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(AIX)) // This generates the variable datasources
        {
            DCMCommandLibraryAIX dcmCommandLibraryAIX = null; try { dcmCommandLibraryAIX = new DCMCommandLibraryAIX(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryAIX.getCommandArray()) { cmd.append("echo \"CMD: " + cmdRecord[0] + " " + cmdRecord[1] + " `which " + cmdRecord[1] + "`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(HPUX)) // This generates the variable datasources
        {
            DCMCommandLibraryHPUX dcmCommandLibraryHPUX = null; try { dcmCommandLibraryHPUX = new DCMCommandLibraryHPUX(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryHPUX.getCommandArray()) { cmd.append("echo \"CMD: " + cmdRecord[0] + " " + cmdRecord[1] + " `which " + cmdRecord[1] + "`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        return cmd;
    }
    
    public String getMissingServerRequirements(String sysinfoParam, String dataParam)
    {
        String dataHeaders = ""; dataHeaders = "CMD: ";
        BufferedReader reader;
        String line = "";
        String output = "";
        
        if (sysinfoParam.contains(LINUX))
        {
            DCMCommandLibraryLinux dcmCommandLibraryLinux = null; try { dcmCommandLibraryLinux = new DCMCommandLibraryLinux(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryLinux.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; output = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { output += "Warning: DCMInventoryServerServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(DARWIN))
        {
            DCMCommandLibraryDarwin dcmCommandLibraryDarwin = null; try { dcmCommandLibraryDarwin = new DCMCommandLibraryDarwin(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryDarwin.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; output = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { output += "Warning: DCMInventoryServerServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(SUNOS))
        {
            DCMCommandLibrarySunOS dcmCommandLibrarySunOS = null; try { dcmCommandLibrarySunOS = new DCMCommandLibrarySunOS(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibrarySunOS.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; output = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { output += "Warning: DCMInventoryServerServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(AIX))
        {
            DCMCommandLibraryAIX dcmCommandLibraryAIX = null; try { dcmCommandLibraryAIX = new DCMCommandLibraryAIX(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryAIX.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; output = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { output += "Warning: DCMInventoryServerServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(HPUX))
        {
            DCMCommandLibraryHPUX dcmCommandLibraryHPUX = null; try { dcmCommandLibraryHPUX = new DCMCommandLibraryHPUX(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryHPUX.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; output = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { output += "Warning: DCMInventoryServerServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
                
        return output;
    }
    
    @Override    
    public synchronized void log(String messageParam, boolean logToStatusParam, boolean logToApplicationParam, boolean logToFileParam)
    {
        if (logToStatusParam) { inventoryServerServerCaller.log(messageParam, logToStatusParam, logToApplicationParam, logToFileParam); }
    }

    @Override
    public void dbClientReady() {
    }

    @Override public void updatedHost() { }

    @Override
    public void inventoryReady(Server serverParam) { }
}
