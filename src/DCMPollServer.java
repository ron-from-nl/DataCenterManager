import data.ConfigurationCaller;
import data.Host;
import data.Resource;
import data.Server;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DCMPollServer implements DCMRemoteCommandCaller, DCMDBClientCaller, ConfigurationCaller, DCMPushPollScriptCaller // Login to server for inventory (data) and uses Convert to convert to server object with resources
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
 
    private Server                      server;
    private StringBuffer                command;
    private String                      data;
        
    private int                         sessionTimeout = 10;

    private String                      customPromptFlat =  "DCMCommandEnd";
    private boolean                     debug = false;
    
    private DCMPollServerCaller            pollServerCaller;
    private final DCMRemoteCommandCaller   remoteCommandCaller;
    private DCMPollerDataToServer          pollerDataToServer;
    private DCMArchiveUpdate               rrdUpdate; // This is being used
    private DCMDBClientCaller           javaDBClientCaller;
    private DCMPollServer               pollerReference;
//    private Configuration               configuration;
    
    private final int                   POLLSTAGE = 0;
    private InetAddress                 inetAddress;
    private String                      pollerIP;
    private int                         pollerInstance;
//    private boolean                     superuserPasswordGiven;
//    private String                      pollerHeaderKey;
    private String                      dcmPollerScriptFile;
//    private String                      dcmPollerScriptFileContent;
    private String                      dcmPollerDataFile;
    private String                      dcmPollerErrorFile;
    private boolean                     runAsDeamon = true;
    private static final String         THISPRODUCT = "DCMPollServer";

    private final String                LINUX =     "Linux";      
    private final String                DARWIN =    "Darwin";      
    private final String                SUNOS =     "SunOS";      
    private final String                AIX =       "AIX";      
    private final String                HPUX =      "HP-UX";      
    
    public DCMPollServer(final DCMPollServerCaller pollerCallerParam, final int pollerInstanceParam, final Server serverParam, int timeoutParam, boolean debugParam) throws UnknownHostException, CloneNotSupportedException
    {
        debug = debugParam;
        sessionTimeout = timeoutParam;
        
//      STDIN & STDOUT Redirection
//        if ((!Configuration.DEBUG) && (!debug))
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        } else { System.out.println(THISPRODUCT + " debugging enabled for server: " + serverParam.getHost().getHostname()); }
        
        inetAddress =                   InetAddress.getLocalHost();
        pollerIP            =           inetAddress.getHostAddress();
        pollerReference =               this;
        pollServerCaller =              pollerCallerParam;
        remoteCommandCaller =           this;
        pollerInstance =                pollerInstanceParam;
        server =                        (Server) serverParam.clone();
//        pollerHeaderKey =               "DCMPoller:" + server.getHost().getHostname() + ":";
        dcmPollerScriptFile =           ".dcmpollscript_" + pollerIP + "_" + server.getHost().getHostname() + ".sh";
//        dcmPollerScriptFileContent =    "";
        dcmPollerDataFile =             ".dcmpolldata_" + pollerIP + "_" + server.getHost().getHostname() + ".dat";
        dcmPollerErrorFile =            ".dcmpolldata_" + pollerIP + "_" + server.getHost().getHostname() + ".err";

        //        ystem.out.println("DBServerTest: " + Boolean.toString(javaDBClient.getDBServerTest()));

        Thread pollServerThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
//              First harvest the data from the previous polling cycle
                command = new StringBuffer();
//                command.append("cat ").append(dcmPollerDataFile).append("  | sort -k 2n\n");
                command.append("sort -k 1n ").append(dcmPollerDataFile).append("\n"); // Might want to add a sleep 1 as a command

//              Empty the datafile
                command.append("> ").append(dcmPollerDataFile).append("\n");
                if      (server.getHost().getSysinfo().contains(SUNOS))
                {
                    command.append("nohup ksh " + dcmPollerScriptFile + " 2> " + dcmPollerErrorFile + " & \n"); // Execute script file
                }
                else if (server.getHost().getSysinfo().contains(DARWIN))
                {
                    command.append("sh " + dcmPollerScriptFile + " 2> " + dcmPollerErrorFile + " & \n"); // Execute script file
                    command.append("disown -ah \n"); // Make sure all bg processes will not receive HUP signal
                    command.append("unset HISTFILE \n"); // Make sure shell history is disabled
                }
                else if (server.getHost().getSysinfo().contains(LINUX))
                {
                    command.append("sh " + dcmPollerScriptFile + " & 2> " + dcmPollerErrorFile + "\n"); // Execute script file
                    command.append("disown -ah \n"); // Make sure all bg processes will not receive HUP signal
                    command.append("unset HISTFILE \n"); // Make sure shell history is disabled
                }
                else if (server.getHost().getSysinfo().contains(AIX))
                {
                    command.append("ksh93\n"); // Execute script file (enhanced ksh)
                    command.append("disown \n"); // Make sure all bg processes will not receive HUP signal
                    command.append("ksh " + dcmPollerScriptFile + " & 2> " + dcmPollerErrorFile + "\n"); // Execute script file (enhanced ksh)
                    command.append("unset HISTFILE \n"); // Make sure shell history is disabled
                }
                else if (server.getHost().getSysinfo().contains(HPUX))
                {
                    command.append("nohup ksh " + dcmPollerScriptFile + " 2> " + dcmPollerErrorFile + " & \n"); // Execute script file
                }
                else
                {
                    command.append("nohup sh " + dcmPollerScriptFile + " & 2> " + dcmPollerErrorFile + "\n"); // Execute script file
                    command.append("disown -ah \n"); // Make sure all bg processes will not receive HUP signal
                    command.append("unset HISTFILE \n"); // Make sure shell history is disabled
                }
                command.append("echo \"" + customPromptFlat + "\";\n"); // Arranged prompt output for expect string in RemoteCommand
                pollServerCaller.log("Action:  DCMPollServer [" + pollerInstance + "] connecting to Host: " + server.getHost().getHostname()+ " for collecting data...", true, true, true);
                try { new DCMRemoteCommand(remoteCommandCaller, POLLSTAGE, server.getHost(), command, true, sessionTimeout, debug); } catch (CloneNotSupportedException ex) { }
                
            }
        });
        pollServerThread.setName("pollServerThread");
        pollServerThread.setDaemon(runAsDeamon);
        pollServerThread.setPriority(Thread.NORM_PRIORITY);
        pollServerThread.start();

    }
    
    @Override
    public synchronized void remoteFinalCommandSuccessResponse(final int stageParam, final String stdOutParam, final String stdErrParam)
    {
        Thread updateArchiveThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if ( stageParam==POLLSTAGE)
                {
                    if (    
                            (
                                (stdOutParam.contains(dcmPollerScriptFile)) &&
                                (
                                    (stdOutParam.toUpperCase().contains("No such file".toUpperCase())) ||
                                    (stdOutParam.toUpperCase().contains("not found".toUpperCase()))
                                )
                            ) ||
                            (
                                (stdErrParam.contains(dcmPollerScriptFile)) &&
                                (
                                    (stdErrParam.toUpperCase().contains("No such file".toUpperCase())) ||
                                    (stdErrParam.toUpperCase().contains("not found".toUpperCase()))
                                )
                            ) 
                       )
                    {
                        pollServerCaller.log("Warning: DCMPollServer: pollscript " + dcmPollerScriptFile + " not found on server " + server.getHost().getHostname(), true, true, true);
                        
                        // Generate the Poll Script and push it to the server instead of doing it every time the pollServer kicks in
                        try { DCMPushPollScript pushPollScript = new DCMPushPollScript(pollerReference, server); } catch (UnknownHostException ex) { }
                    }
                    else
                    {
//                        data = Tools.grep(stdOutParam, pollerHeaderKey);
                        data = stdOutParam;
                        if ( data.length() > 2)
                        {
                            pollServerCaller.log("Success: DCMPollServer: [" + pollerInstance + "] Host: " + server.getHost().getHostname()+ " poll completed\r\n", true, true, true);                

                            // Convert data to a solid server object
                            pollerDataToServer = new DCMPollerDataToServer();
                            
//                            if (debug) { pollServerCaller.log("Debug: Collected Data from server: " + server.getHost().getHostname() + "\n\n" + data + "\n\n", false, true, true); }
                            try { server = (Server) pollerDataToServer.convertPollerDataToServer(server,data).clone(); } catch (CloneNotSupportedException ex) { pollServerCaller.log("Poller Clone server", true, true, true); }
                            
                            if (debug)
                            {
                                pollServerCaller.log("Debug:   DCMPollServer: Converted Data from server: " + server.getHost().getHostname() + ":\n", false, true, true);
                                for (Resource resource:server.getResourceList())
                                {
                                    pollServerCaller.log(resource.getId() + " " + resource.getRRDFile() + " " + resource.getCategory() + " " + resource.getResource() + " " + resource.getLastValue(), false, true, true);
                                }
                            }

                            // Update serverresources lastvalue in javaDB
                            //javaDBClient.updateServerResourcesValues(server); // cost twice the overall polling performance

                            // Update RRDUpdate
                            rrdUpdate = new DCMArchiveUpdate(pollerReference, server);

                            // Report inventory process ready
                            pollServerCaller.pollServerReady();
                        }
                        else
                        {
                            pollServerCaller.log("Failure: DCMPollServer[" + pollerInstance + "] Host: " + server.getHost().getHostname() + " no poller data", true, true, true);
                        }                        
                    }
                }
                else
                {
                    pollServerCaller.log("Failure: DCMPollServer[" + pollerInstance + "] Host: " + server.getHost().getHostname() + " Invalid stage: " + stageParam + " returned", true, true, true);
                }
            }
        });
        updateArchiveThread.setName("updateArchiveThread");
        updateArchiveThread.setDaemon(runAsDeamon);
        updateArchiveThread.setPriority(Thread.NORM_PRIORITY);
        updateArchiveThread.start();

    }

    @Override
    public void remoteCommandSuccessResponse(int stageParam, String stdOutParam, String stdErrParam)
    {
        if (    
                (
                    (stdOutParam.contains(dcmPollerScriptFile)) &&
                    (
                        (stdOutParam.toUpperCase().contains("No such file".toUpperCase())) ||
                        (stdOutParam.toUpperCase().contains("not found".toUpperCase()))
                    )
                ) ||
                (
                    (stdErrParam.contains(dcmPollerScriptFile)) &&
                    (
                        (stdErrParam.toUpperCase().contains("No such file".toUpperCase())) ||
                        (stdErrParam.toUpperCase().contains("not found".toUpperCase()))
                    )
                ) 
           )
        {
            pollServerCaller.log("Warning: DCMPollServer: pollscript " + dcmPollerScriptFile + " not found on server " + server.getHost().getHostname(), true, true, true);
            // Generate the Poll Script and push it to the server instead of doing it every time the pollServer kicks in
            try { DCMPushPollScript pushPollScript = new DCMPushPollScript(pollerReference, server); } catch (UnknownHostException ex) { }
        }
    }

    @Override
    public synchronized void remoteCommandFailureResponse(int stageParam, String messageParam)
    {
        pollServerCaller.log(messageParam, true, true, true);
    }
    
    public void rrdUpdateSuccessResponse(String messageParam)
    {
        pollServerCaller.log("Success: DCMPollServer[" + pollerInstance + "] Host: " + server.getHost().getHostname() + messageParam, true, true, true);
    }
    
    public void rrdUpdateFailureResponse(String messageParam)
    {
        pollServerCaller.log("Failure: DCMPollServer[" + pollerInstance + "] Host: " + server.getHost().getHostname() + messageParam, true, true, true);
    }
    
    
    public Server getServer()
    {
        return server;
    }

    @Override    
    public void log(String messageParam, boolean logToStatusParam, boolean logToApplicationParam, boolean logToFileParam)
    {
        pollServerCaller.log(messageParam, logToStatusParam, logToApplicationParam, logToFileParam);
    }

    @Override
    public void dbClientReady() {
    }

    @Override public void updatedHost() { }

    @Override
    public void inventoryReady(Server serverParam) {
    }
}
