import data.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class DCMInventoryServer implements DCMRemoteCommandCaller, DCMDBClientCaller, ConfigurationCaller, DCMPushPollScriptCaller, DCMFileTranferCaller // Login to server for inventory (data) and uses Convert to convert to server object with resources
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
    int sessionTimeout = 5;

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
    
    DCMInventoryServerCaller        inventoryServerCaller;
    final DCMRemoteCommandCaller    remoteCommandCaller;
    private DCMDBClient             javaDBClient;
    private DCMInventoryServer      inventoryServerReference;
    private DCMFileTranferCaller    dcmFileTranferCaller;
    private Configuration           configuration;
    
    private String                  customPromptFlat =  "DCMCommandEnd";
    private final int               IDENTIFICATIONSTAGE = 0;
    private final int               IVENTORYSTAGE = 1;
    private String[]                stageArray;
    private long                    inventoryInstance;
//    private String                inventoryHeaderKey;
    private String                  pollerHeaderKey;
    private InetAddress             inetAddress;
    private String                  dcmServerIP;
    private String                  dcmInventoryScriptFile;
    private String                  dcmPollerScriptFile;
    private String                  dcmPollerDataFile;
    
    private final String            LINUX =     "Linux";
    private final String            DARWIN =    "Darwin";
    private final String            SUNOS =     "SunOS";
    private final String            AIX =       "AIX";
    private final String            HPUX =      "HP-UX";
    
    private int retryMax =                      2;
    private int retryCounter =                  0;

    public DCMInventoryServer(final DCMInventoryServerCaller inventoryCallerParam, final long inventoryInstanceParam, final Host hostParam, int sessionTimeParam, int retryMaxParam, boolean daemonParam, boolean debugParam)
    {
        retryCounter = 0;
        retryMax = retryMaxParam;
        stageArray = new String[2];
        stageArray[IDENTIFICATIONSTAGE] = "IdentificationStage";
        stageArray[IVENTORYSTAGE] = "InventoryStage";
        
        daemon = daemonParam;
        debug = debugParam;
        
        sessionTimeout = sessionTimeParam;
//      STDIN & STDOUT Redirection
//        if ((!Configuration.DEBUG)&&(!debug))
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        }
        
        inventoryServerReference =          this;
        dcmFileTranferCaller =              this;
        inventoryServerCaller =             inventoryCallerParam;
        remoteCommandCaller =               this;
        inventoryInstance =                 inventoryInstanceParam;
        configuration =                     new Configuration(inventoryServerReference);
        try { inetAddress =                 InetAddress.getLocalHost(); } catch (UnknownHostException ex) {  }
        dcmServerIP =                       inetAddress.getHostAddress();
        host =                              hostParam;
        dcmInventoryScriptFile =            ".dcminventoryscript_" + dcmServerIP + "_" + host.getHostname() + ".sh";
        
        javaDBClient = null; try { javaDBClient = new DCMDBClient(inventoryServerReference, configuration.getJavaDB(), daemon, debug); }
        catch (SQLException ex)                 { inventoryServerCaller.log("SQLException", true, true, true); }
        catch (ClassNotFoundException ex)       { inventoryServerCaller.log("ClassNotFoundException: " + ex.getMessage(), true, true, true); }
        catch (InstantiationException ex)       { inventoryServerCaller.log("InstantiationException: " + ex.getMessage(), true, true, true); }
        catch (IllegalAccessException ex)       { inventoryServerCaller.log("IllegalAccessException: " + ex.getMessage(), true, true, true); }
        catch (NoSuchMethodException ex)        { inventoryServerCaller.log("NoSuchMethodException: " + ex.getMessage(), true, true, true); }
        catch (InvocationTargetException ex)    { inventoryServerCaller.log("InvocationTargetException: " + ex.getMessage(), true, true, true); }
        catch (Exception ex)                    { inventoryServerCaller.log("Exception: " + ex.getMessage(), true, true, true); }

//        ystem.out.println("DBServerTest: " + Boolean.toString(javaDBClient.getDBServerTest()));

        server = new Server();
        server.setHost(host);
        command = new StringBuffer();

        // Identification Command
        command.setLength(0); command.append(getIdentificationCommand());
        inventoryServerCaller.log("Action:  DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Identification Stage", true, true, true);
        try { new DCMRemoteCommand(remoteCommandCaller, IDENTIFICATIONSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // true = finalCommand
        data = "";
    }

    @Override
    public synchronized void remoteFinalCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam)
    {
        if (stageParam==IDENTIFICATIONSTAGE) // Identification succeeded, so we should have the sysinfo data
        {
            if (debug) { inventoryServerCaller.log("identificationstage: stdOut: " + stdOutParam, true,true,true); }
//            sysinfo = Tools.grep(stdOutParam, inventoryHeaderKey + "Sysinfo:");
//            sysinfo = Tools.grep(stdOutParam, "Sysinfo:");
            sysinfo = DCMTools.substitude(DCMTools.substitude(DCMTools.startsWith(stdOutParam, "Sysinfo:"), "Sysinfo:"), customPromptFlat + ".*$");
//            inventoryCaller.log("sysinfo: " + sysinfo, true,true,true);
            if ( sysinfo.length() > 2)
            {
                inventoryServerCaller.log("Success: DCMInventoryServer: " + host.getHostname()+ " Identification completed", true, true, true);
                host.setSysinfo(sysinfo);
                server.setHost(host);
                data += sysinfo;
                
                // Compile the Inventory Script and make sure the script also produces missing requirements data
//                command.setLength(0); command.append(getRequirementsScript(sysinfo)); command.append(getInventoryScript(sysinfo));
                command.setLength(0); command.append(getInventoryScript(sysinfo)); command.append(getRequirementsScript(sysinfo)); 

                // Write inventory script to file
                new DCMFileWrite(dcmInventoryScriptFile,command.toString());
                
                // Send the scriptfile to server
                inventoryServerCaller.log("Action:  DCMInventoryServer [" + inventoryInstance + "] transfering inventoryscript to Host: " + host.getHostname(), true, true, true);                
//                log("Sending file " + dcmInventoryScriptFile + " to Server " + server.getHost().getHostname(),true, true, true);
                try { new DCMFileTransfer(dcmFileTranferCaller,server.getHost(),dcmInventoryScriptFile); } catch (CloneNotSupportedException ex) { }
                
                // Create the execution script 
                command.setLength(0);
                command.append("sh " + dcmInventoryScriptFile + "; ").append("echo \"" + customPromptFlat + "\";"); // Arranged prompt output for expect string in RemoteCommand

                // Remote Execute the inventory script and wait remote call to response method below
                inventoryServerCaller.log("Action:  DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Inventory Stage", true, true, true);                
                try { remoteCommand = new DCMRemoteCommand(remoteCommandCaller, IVENTORYSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) {  }
            }
            else // remote login succeeeded, but sysinfo length is to small
            {
                retryCounter++;
                inventoryServerCaller.log("Failure: DCMInventoryServer [" + inventoryInstance + "] Host: " + host.getHostname()+ " failed to identify server", true, true, true);
                if ( retryCounter <= retryMax )
                {
//                    command.setLength(0);
//                    command.append("sh " + dcmInventoryScriptFile + "; ").append("echo \"" + customPromptFlat + "\";"); // Arranged prompt output for expect string in RemoteCommand
//                    inventoryServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Inventory Stage", true, true, true);                
//                    try { new DCMRemoteCommand(remoteCommandCaller, IVENTORYSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) {  }

                    command.setLength(0); command.append(getIdentificationCommand());
                    inventoryServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Identification Stage", true, true, true);
                    try { new DCMRemoteCommand(remoteCommandCaller, IDENTIFICATIONSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // true = finalCommand
                    data = "";                
                }
            }
        }
        else if ( stageParam==IVENTORYSTAGE) // remote identification succeeded, so we have the inventory data block
        {
            if (debug) { inventoryServerCaller.log("inventorystage: stdOut: " + stdOutParam, true,true,true); }
//            output = Tools.grep(stdOutParam, inventoryHeaderKey);
            output = stdOutParam;
//            if ( sysinfo.length() > 2)
//            {
                inventoryServerCaller.log("Success: DCMInventoryServer [" + inventoryInstance + "] Host: " + host.getHostname()+ " inventory completed", true, true, true);

                data += output; 
                
//              Log missing requirements advise                
                String missingServerRequirements = getMissingServerRequirements(sysinfo, data); if (missingServerRequirements.length() >0 ) { inventoryServerCaller.log(missingServerRequirements, true, true, true); }

                // This part creates the server object including a resource list with commands
                if      (sysinfo.contains(LINUX))
                {
                    DCMDataConverterLinux inventoryServerDataConverterLinux = null;
                    try { inventoryServerDataConverterLinux = new DCMDataConverterLinux(host); } catch (UnknownHostException ex) { inventoryServerCaller.log("InventoryServer Received Thrown UnknownHost from the LinuxCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterLinux.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(DARWIN))
                {
                    DCMDataConverterDarwin inventoryServerDataConverterDarwin = null;
                    try { inventoryServerDataConverterDarwin = new DCMDataConverterDarwin(host); } catch (UnknownHostException ex) { inventoryServerCaller.log("InventoryServer Received Thrown UnknownHost from the DarwinCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterDarwin.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(SUNOS))
                {
                    DCMDataConverterSunOS inventoryServerDataConverterSunOS = null;
                    try { inventoryServerDataConverterSunOS = new DCMDataConverterSunOS(host); } catch (UnknownHostException ex) { inventoryServerCaller.log("InventoryServer Received Thrown UnknownHost from the SunOSCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterSunOS.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(AIX))
                {
                    DCMDataConverterAIX inventoryServerDataConverterAIX = null;
                    try { inventoryServerDataConverterAIX = new DCMDataConverterAIX(host); } catch (UnknownHostException ex) { inventoryServerCaller.log("InventoryServer Received Thrown UnknownHost from the AIXCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterAIX.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerCaller.log("Inventory Clone server", true, true, true); }                    
                }
                else if (sysinfo.contains(HPUX))
                {
                    DCMDataConverterHPUX inventoryServerDataConverterHPUX = null;
                    try { inventoryServerDataConverterHPUX = new DCMDataConverterHPUX(host); } catch (UnknownHostException ex) { inventoryServerCaller.log("InventoryServer Received Thrown UnknownHost from the HPUXCommandLibrary", true, true, true); }
                    try { server = (Server) inventoryServerDataConverterHPUX.convertInventoryDataToServer(data).clone(); } catch (CloneNotSupportedException ex) { inventoryServerCaller.log("Inventory Clone server", true, true, true); }                    
                }

                // Before inserting the new server, delete previous server records (with same hostname)
                try { javaDBClient.deleteServerByHostname(server.getHost().getHostname()); } catch (CloneNotSupportedException ex) { }                
                
                // Create non existing RRD Archives
                new DCMArchiveCreate(inventoryServerReference, server);

                // Insert newly discovered server into javadb
                inventoryServerCaller.log("Action:  DCMInventoryServer [" + inventoryInstance + "] inserting server: " + host.getHostname()+ " into MetaDB", true, true, true);                
                javaDBClient.insertServer(server);
                                
                // Get the server back from the database as all her resources now have autonumbered id by the insertServer directly above
                try { server = (Server) javaDBClient.getServerByHostname(host.getHostname()); } catch (CloneNotSupportedException ex) {  }

                // Create the Poll Script and push it to the server
                try { DCMPushPollScript pushPollScript = new DCMPushPollScript(inventoryServerReference, server); } catch (UnknownHostException ex) { }
                
//              --------------------------------------------------------------------------------------------------------------------------------------------------------------
                                
                // Delete any unlinked RRD archives
                deleteUnlinkedRRDArchives();
                
                // Report inventory process ready
                inventoryServerCaller.log("Action:  DCMInventoryServer [" + inventoryInstance + "] inventory for server: " + host.getHostname()+ " completed", true, true, true);                
                inventoryServerCaller.inventoryReady();
//            }
//            else
//            {
//                inventoryServerCaller.log("Failure: DCMInventoryServer [" + inventoryInstance + "] Host: " + host.getHostname()+ " inventory failed", true, true, true);
//            }
        }
        else
        {
            inventoryServerCaller.log("Failure: DCMInventoryServer [" + inventoryInstance + "] Host: " + host.getHostname() + " Invalid inventory stage: " + stageParam + " returned", true, true, true);
        }
    }

    public synchronized void remoteCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam)
    {
        inventoryServerCaller.log("Success: DCMInventoryServer: remoteCommandSuccessResponse", true, true, true);
    }

    @Override
    public synchronized void remoteCommandFailureResponse(int stageParam, String messageParam)
    {
//        inventoryServerCaller.log("Error:   DCMInventoryServer: remoteCommandFailureResponse: on " + stageArray[stageParam] + " " + messageParam, true, true, true);

        retryCounter++;
        if ( retryCounter <= retryMax )
        {
            if ( stageParam==IDENTIFICATIONSTAGE )
            {
                command.setLength(0); command.append(getIdentificationCommand());
                inventoryServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Identification Stage", true, true, true);
                try { new DCMRemoteCommand(remoteCommandCaller, IDENTIFICATIONSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { } // true = finalCommand
                data = "";                
            }
            else if ( stageParam==IVENTORYSTAGE )
            {
                inventoryServerCaller.log("Retry[" + retryCounter + "/" + retryMax + "] DCMInventoryServer [" + inventoryInstance + "] connecting to Host: " + host.getHostname()+ " for Inventory Stage", true, true, true);                
                try { new DCMRemoteCommand(remoteCommandCaller, IVENTORYSTAGE, host, command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) {  }
            }
        }
    }
    
    public synchronized void rrdCreateSuccessResponse(String messageParam)
    {
        inventoryServerCaller.log("Success: DCMInventoryServer [" + inventoryInstance + "] Host: " + host.getHostname() + messageParam, true, true, true);
    }
    
    public void rrdCreateFailureResponse(String messageParam)
    {
        inventoryServerCaller.log("Failure: DCMInventoryServer [" + inventoryInstance + "] Host: " + host.getHostname() + messageParam, true, true, true);
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
                if (unlinkedFile.delete()) { inventoryServerCaller.log("Removed unlinked archive file: " + unlinkedFile.getName(), true, true, true); }
                else { inventoryServerCaller.log("Could not remove unlinked archive file: " + unlinkedFile.getName(), true, true, true); }
            };
        }
    }
    
    // echo -n is problem in superuser mode due root's shell "sh" inbuild echo (-n is printed) and leave the ";"
    public String getIdentificationCommand() { return "echo \"Sysinfo: `uname -a` " + customPromptFlat + "\""; } // echo -n seems to work again on mac

//    public StringBuffer getInventoryCommand(String headerKeyParam, String sysinfoParam) 
    public StringBuffer getInventoryScript(String sysinfoParam) 
    {
        StringBuffer cmd = new StringBuffer();
        if (sysinfoParam.contains(LINUX)) // This generates the variable datasources
        {//iostat | egrep -ive "cpu|device|^$|^ +"
            cmd.append("mpstat -P ALL | egrep -v \"CPU|^$\" | sed \"s/AM//\" | sed \"s/PM//\" | awk '{ print $2 }' | while read resource; do echo ").append("\"CPU: ${resource}\"; done; \n"); // CPU's
            cmd.append("iostat | egrep -ive \"cpu|device|^$|^ +\" | awk '{ print $1 }' | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done;\n"); // I/O
            cmd.append("df -kP | grep \"^/dev/\" | awk '{ print $6 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | egrep -e \"eth\" | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\";\r\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(DARWIN)) // This generates the variable datasources
        {
            cmd.append("echo \"CPU: all\"; \n"); // Cannot get individual core/cpu stats on Mac so I'm faking an all core resource to be there
            cmd.append("for resource in `iostat -d | head -1`; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -kP | grep \"^/dev/\" | awk '{ print $6 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | egrep \"en\" | grep Link | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces egrep -e option "-e" doesn't work on Mac
//            cmd.append("echo \"").append(customPromptFlat).append("\";\r\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(SUNOS)) // This generates the variable datasources
        {
            cmd.append("echo \"CPU: all\"; \n"); // Cannot get individual core/cpu stats on Mac so I'm faking an all core resource to be there
            cmd.append("mpstat | egrep -v \"CPU|^$\" | awk '{ print $1 }' | while read resource; do echo ").append("\"CPU: ${resource}\"; done;\n"); // CPU's
            cmd.append("iostat -xtc | grep -v device | awk '{ print $1 }' | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done;\n"); // I/O
            cmd.append("df -lk | grep \"^/\" | grep -v \"platform\" | awk '{ print $6 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done;\n"); // Storage filesystems
            cmd.append("netstat -i | awk '{ print $1 }' | egrep -ive \"name|^$\" | while read resource; do echo ").append("\"Network: ${resource}\"; done;\n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(AIX)) // This generates the variable datasources
        {
            //cmd.append("echo \"CPU: all\"\r\n"); // Not necessary ALL CPU included in mpstat
            cmd.append("mpstat | egrep -vi \"CPU|^$|U\" | awk '{ print $1 }' | while read resource; do echo ").append("\"CPU: ${resource}\"; done; \n"); // CPU's
            cmd.append("iostat -ld | egrep -e \"^hdisk\" | awk '{ print $1 }' | while read resource; do echo ").append("\"DiskIO: ${resource}\"; done; \n"); // I/O
            cmd.append("df -k | grep \"/\" | grep -v \"/proc\" | awk '{ print $7 }' | while read resource; do echo ").append("\"Storage: ${resource}\"; done; \n"); // Storage filesystems
            cmd.append("netstat -i | grep -i \"link\" | awk '{ print $1 }' | while read resource; do echo ").append("\"Network: ${resource}\"; done; \n"); // Network Interfaces
//            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
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
            for (String[] cmdRecord:dcmCommandLibraryLinux.getCommandArray()) { cmd.append("echo \"CMD: ").append(cmdRecord[0]).append(" ").append(cmdRecord[1]).append(" `which ").append(cmdRecord[1]).append("`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(DARWIN)) // This generates the variable datasources
        {
            DCMCommandLibraryDarwin dcmCommandLibraryDarwin = null; try { dcmCommandLibraryDarwin = new DCMCommandLibraryDarwin(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryDarwin.getCommandArray()) { cmd.append("echo \"CMD: ").append(cmdRecord[0]).append(" ").append(cmdRecord[1]).append(" `which ").append(cmdRecord[1]).append("`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(SUNOS)) // This generates the variable datasources
        {
            DCMCommandLibrarySunOS dcmCommandLibrarySunOS = null; try { dcmCommandLibrarySunOS = new DCMCommandLibrarySunOS(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibrarySunOS.getCommandArray()) { cmd.append("echo \"CMD: ").append(cmdRecord[0]).append(" ").append(cmdRecord[1]).append(" `which ").append(cmdRecord[1]).append("`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(AIX)) // This generates the variable datasources
        {
            DCMCommandLibraryAIX dcmCommandLibraryAIX = null; try { dcmCommandLibraryAIX = new DCMCommandLibraryAIX(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryAIX.getCommandArray()) { cmd.append("echo \"CMD: ").append(cmdRecord[0]).append(" ").append(cmdRecord[1]).append(" `which ").append(cmdRecord[1]).append("`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        else if (sysinfoParam.contains(HPUX)) // This generates the variable datasources
        {
            DCMCommandLibraryHPUX dcmCommandLibraryHPUX = null; try { dcmCommandLibraryHPUX = new DCMCommandLibraryHPUX(host); } catch (UnknownHostException ex) {  }
            for (String[] cmdRecord:dcmCommandLibraryHPUX.getCommandArray()) { cmd.append("echo \"CMD: ").append(cmdRecord[0]).append(" ").append(cmdRecord[1]).append(" `which ").append(cmdRecord[1]).append("`\"\n"); }
            cmd.append("echo \"").append(customPromptFlat).append("\";\n"); // Arranged prompt output for expect string in RemoteCommand
        }
        return cmd;
    }

    public String getMissingServerRequirements(String sysinfoParam, String dataParam)
    {
        String dataHeaders = ""; dataHeaders = "CMD: ";
        BufferedReader reader;
        String line = "";
        String thisOutput = "";
        
        if (sysinfoParam.contains(LINUX))
        {
            DCMCommandLibraryLinux dcmCommandLibraryLinux = null; try { dcmCommandLibraryLinux = new DCMCommandLibraryLinux(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryLinux.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; thisOutput = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { thisOutput += "Warning: DCMInventoryServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(DARWIN))
        {
            DCMCommandLibraryDarwin dcmCommandLibraryDarwin = null; try { dcmCommandLibraryDarwin = new DCMCommandLibraryDarwin(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryDarwin.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; thisOutput = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { thisOutput += "Warning: DCMInventoryServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(SUNOS))
        {
            DCMCommandLibrarySunOS dcmCommandLibrarySunOS = null; try { dcmCommandLibrarySunOS = new DCMCommandLibrarySunOS(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibrarySunOS.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; thisOutput = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { thisOutput += "Warning: DCMInventoryServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(AIX))
        {
            DCMCommandLibraryAIX dcmCommandLibraryAIX = null; try { dcmCommandLibraryAIX = new DCMCommandLibraryAIX(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryAIX.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; thisOutput = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { thisOutput += "Warning: DCMInventoryServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
        else if (sysinfoParam.contains(HPUX))
        {
            DCMCommandLibraryHPUX dcmCommandLibraryHPUX = null; try { dcmCommandLibraryHPUX = new DCMCommandLibraryHPUX(host); } catch (UnknownHostException ex) {  }
            String[][] commandArray = dcmCommandLibraryHPUX.getCommandArray(); reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); line = ""; thisOutput = "";
            try { while ((line = reader.readLine()) != null) { String[] inventoryDataFields = new String[4]; inventoryDataFields = line.split("\\s+",4); if (inventoryDataFields[3].length() < 1) { thisOutput += "Warning: DCMInventoryServer: Server: " + host.getHostname() + " " + commandArray[Integer.parseInt(inventoryDataFields[1])][3]; } } } catch(IOException e) { e.printStackTrace(); }
        }
                
        return thisOutput;
    }
    
    @Override    
    public synchronized void log(String messageParam, boolean logToStatusParam, boolean logToApplicationParam, boolean logToFileParam)
    {
        inventoryServerCaller.log(messageParam, logToStatusParam, logToApplicationParam, logToFileParam);
    }

    @Override
    public void dbClientReady() {
    }

    @Override public void updatedHost() { }

    @Override
    public void inventoryReady(Server serverParam) { }
}
