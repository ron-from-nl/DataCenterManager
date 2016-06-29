import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DCMShell
{
    private String              platform;
    private String[]            status;
    private Process             process;
    private String[]            STARTJCONSOLE;
    private String[]            STARTUPDATER;
    private String[]            STARTDESKTOP;
    private String[]            STARTCLIENT;
    private String[]            STARTSERVER;
    private String[]            STARTJAVADBSERVER;
    private String[]            STARTPOLLER;
    private String[]            STARTCOMMANDER;
    
    private int                 heapMemMax;
    private int                 serverPort;
    private String              javaOptions;
    private String              javaParameters;
    private boolean             debug;
    private String              debugParameter;
    private String workingdir;

    public DCMShell(boolean debugParam)
    {
        status = new String[2];
        heapMemMax = 128;
        serverPort = 20000;
        javaOptions = "";
        javaParameters = "";
        debugParameter = "";
        debug = debugParam; if (debug) { debugParameter = " --debug "; }
	platform = System.getProperty("os.name").toLowerCase();
        setENV();
    }

    public void startJConsole() { try { Runtime.getRuntime().exec(STARTJCONSOLE); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); } }
    public void startUpdater() { try { Runtime.getRuntime().exec(STARTUPDATER); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); } }

    public void startDesktop(int heapMemMaxParam, String javaOptionsParam, String javaParametersParam)
    {
        heapMemMax = heapMemMaxParam; javaOptions = javaOptionsParam; javaParameters = javaParametersParam; setENV();
        try { Runtime.getRuntime().exec(STARTDESKTOP); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); }
    }

    public void startClient(int heapMemMaxParam, String javaOptionsParam, String javaParametersParam)
    {
        heapMemMax = heapMemMaxParam; javaOptions = javaOptionsParam; javaParameters = javaParametersParam; setENV();
//        try { Runtime.getRuntime().exec(STARTCLIENT); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); }
        
        ProcessBuilder processBuilder = new ProcessBuilder(STARTCLIENT);
        Map<String, String> env = processBuilder.environment();
//        pb.directory(new File(workingdir));
        File log = new File("startClient.log"); processBuilder.directory(new File(workingdir)); // System.out.println("[PBDir: " + processBuilder.directory().getAbsolutePath());
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(Redirect.appendTo(log));
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException ex) {
            Logger.getLogger(DCMShell.class.getName()).log(Level.SEVERE, null, ex);
        }
        assert processBuilder.redirectInput() == Redirect.PIPE;
        assert processBuilder.redirectOutput().file() == log;
        try {
            assert process.getInputStream().read() == -1;
        } catch (IOException ex) {
            Logger.getLogger(DCMShell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startServer(int heapMemMaxParam, String javaOptionsParam, int serverPortParam, String javaParametersParam)
    {
        serverPort = serverPortParam;
//        heapMemMax = heapMemMaxParam; javaOptions = " -server -Djava.rmi.server.hostname=localhost "; javaOptions += javaOptionsParam; javaParameters = javaParametersParam; setENV(); 
        heapMemMax = heapMemMaxParam; javaOptions = javaOptionsParam; javaParameters = javaParametersParam; setENV(); 
        try { Runtime.getRuntime().exec(STARTSERVER); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); }
    }

    public void startPoller(int heapMemMaxParam, String javaOptionsParam, String javaParametersParam)
    {
        heapMemMax = heapMemMaxParam; javaOptions = javaOptionsParam; javaParameters = javaParametersParam; setENV();
        try { Runtime.getRuntime().exec(STARTPOLLER); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); }
    }

    public void startCommander(int heapMemMaxParam, String javaOptionsParam, String javaParametersParam)
    {
        heapMemMax = heapMemMaxParam; javaOptions = javaOptionsParam; javaParameters = javaParametersParam; setENV();
        try { Runtime.getRuntime().exec(STARTCOMMANDER); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); }
    }

    public void startJavaDBServer(int heapMemMaxParam, String javaOptionsParam, String javaParametersParam )
    {
        heapMemMax = heapMemMaxParam; javaOptions = javaOptionsParam; javaParameters = javaParametersParam; setENV();
        try { Runtime.getRuntime().exec(STARTJAVADBSERVER); } catch (IOException ex) { System.err.println("RunTime Error: " + ex.getMessage()); }
    }

    public String getPlatform() { return platform; }

    private void setENV()
    {
        workingdir = System.getProperty("user.dir"); // System.out.println("[Platform: " + platform + "] "+ workingdir);
        if (( platform.contains("mac os")) || ( platform.contains("bsd" )))
        {
            STARTDESKTOP                        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDesktop" + javaParameters + debugParameter};
            STARTCLIENT                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMClient" + javaParameters + debugParameter};
            STARTSERVER                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMServer " + serverPort + javaParameters + debugParameter};
            STARTJAVADBSERVER                   = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDBServer" + javaParameters + debugParameter};
            STARTPOLLER                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMPoller" + javaParameters + debugParameter};
            STARTCOMMANDER                      = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMCommander" + javaParameters + debugParameter};
            STARTUPDATER                        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar data/bin/Updater.jar"};
            STARTJCONSOLE                       = new String[]{ "/bin/sh", "-c", "jconsole"};
        }
        else if ( platform.contains("linux") )
        {
            STARTDESKTOP                        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDesktop" + javaParameters + debugParameter};
            STARTCLIENT                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMClient" + javaParameters + debugParameter};
            STARTSERVER                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMServer " + serverPort + javaParameters + debugParameter};
            STARTJAVADBSERVER                   = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDBServer" + javaParameters + debugParameter};
            STARTPOLLER                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMPoller" + javaParameters + debugParameter};
            STARTCOMMANDER                      = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMCommander" + javaParameters + debugParameter};
            STARTUPDATER                        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -jar data/bin/Updater.jar"};
            STARTJCONSOLE                       = new String[]{ "/bin/sh", "-c", "jconsole"};
        }
        else if (( platform.contains("sunos")) || ( platform.contains("hp-ux") ) || ( platform.contains("aix") ))
        {
            STARTDESKTOP                        = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDesktop" + javaParameters + debugParameter};
            STARTCLIENT                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMClient" + javaParameters + debugParameter};
            STARTSERVER                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMServer " + serverPort + javaParameters + debugParameter};
            STARTJAVADBSERVER                   = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDBServer" + javaParameters + debugParameter};
            STARTPOLLER                         = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMPoller" + javaParameters + debugParameter};
            STARTCOMMANDER                      = new String[]{ "/bin/sh", "-c", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMCommander" + javaParameters + debugParameter};
            STARTUPDATER                        = new String[]{ "/bin/sh", "-c", "java -jar data/bin/Updater.jar"};
            STARTJCONSOLE                       = new String[]{ "/bin/sh", "-c", "jconsole"};
        }
        else if ( platform.contains("windows") )
        {
            STARTDESKTOP                        = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDesktop" + javaParameters + debugParameter};
            STARTCLIENT                         = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMClient" + javaParameters + debugParameter};
            STARTSERVER                         = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMServer " + serverPort + javaParameters + debugParameter};
            STARTJAVADBSERVER                   = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMDBServer" + javaParameters + debugParameter};
            STARTPOLLER                         = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMPoller" + javaParameters + debugParameter};
            STARTCOMMANDER                      = new String[]{ "cmd", "/C", "java " + javaOptions + " -Xmx" + Integer.toString(heapMemMax) + "m -cp DCManager.jar DCMCommander" + javaParameters + debugParameter};
            STARTUPDATER                        = new String[]{ "cmd", "/C", "java " + javaOptions + " -jar data\\bin\\Updater.jar"};
            STARTJCONSOLE                       = new String[]{ "cmd", "/C", "jconsole"};
        }
        else
        {
            System.err.println("Shell commands not implemented for " + platform);
        }
    }
    public void setDebug(boolean debugParam)
    {
        debug = debugParam;
        if (debug) { debugParameter = " --debug"; } else { debugParameter = ""; }
    }
    
}
