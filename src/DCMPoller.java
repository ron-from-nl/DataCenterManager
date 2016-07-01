import data.Configuration;
import data.ConfigurationCaller;
import data.Server;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import javax.swing.JFrame;

public class DCMPoller implements DCMDBClientCaller, ConfigurationCaller, DCMPollServerCaller // Invokes PollServer on a serverList loop
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
//            StringBuffer aString = new StringBuffer();
//            aString.append(b);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException
        {
//            String aString = new String(b , off , len);
//            StringBuffer aString = new StringBuffer();
//            aString.
        }
    }
//  End Redirection
 
    String serversListArray;

    int sessionTimeout = 10;
    int serverInstanceCounter = 0;
    
    DCMCommandLibraryLinux            commandsLibrary;
    private DCMDBClient         dcmDBClient;
    private DCMDBClientCaller   dcmMetaDBClientCaller;
    private DCMPoller               pollerReference;
    private DCMPollServer              pollServer;
    private DCMPollServerCaller        pollServerCaller;
    private DCMPoller               configurationCaller;
    private Configuration           configuration;
    private int                     dutyCycle = 40; // sec, will be used for all server poll timespan (all server poll cycle will take n secons)    
    private Timer                   pollTime;
    private int                     pollerInstance;
    private Server                  server;
    private boolean                 isDBClientReady = false;
    private boolean                 singleShot = false;
    private Timer                   pollServersTimer; // Has been shutdown, now in cron!!!
    private Timer                   pollerExitTimer; // Has been shutdown, now in cron!!!
    private long                    pollTimerInterval = 60000; // ms
    private int                     counter = 0;
    private boolean                 runThreadsAsDaemons  = true;
    private String              logDateString;
    private Calendar            currentTimeCalendar;
    private static final String THISPRODUCT = "DCMPoller";
    private FileWriter          logFileWriter;
    private String              logFileString;
    private String              logBuffer                                       = "";
    private boolean             daemon = false;
    private boolean             debug = false;
    
    public DCMPoller(boolean singleShotParam, int timeoutParam, boolean daemonParam, boolean debugParam) throws CloneNotSupportedException
    {
        daemon = daemonParam;
        debug = debugParam;
        sessionTimeout = timeoutParam;

//      STDIN & STDOUT Redirection
        if (!debug)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
            PrintStream aPrintStream  = new PrintStream(filteredStream);
            System.setOut(aPrintStream); System.setErr(aPrintStream);
        } else { log(THISPRODUCT + " debugging enabled (SessionTimeout set to: " + sessionTimeout + " seconds)", true,true,true); }

        if ( ! daemon ) { JFrame jframe = new JFrame(THISPRODUCT); jframe.setState(JFrame.ICONIFIED); jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); jframe.setVisible(true); } // Makes process windowed so users can at least see them
        
        configuration = new Configuration();

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
        
        
        pollerReference = this;
        server = new Server();
//        pollServersTimer = new Timer();
        singleShot = singleShotParam;

        Thread pollerJavaDBClientThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                dcmDBClient = null; try { dcmDBClient = new DCMDBClient(pollerReference, configuration.getJavaDB(), daemon, debug); }
                catch (SQLException ex)                 { log("Poller: SQLException", true, true, true); }
                catch (ClassNotFoundException ex)       { log("Poller: ClassNotFoundException: " + ex.getMessage(), true, true, true); }
                catch (InstantiationException ex)       { log("Poller: InstantiationException: " + ex.getMessage(), true, true, true); }
                catch (IllegalAccessException ex)       { log("Poller: IllegalAccessException: " + ex.getMessage(), true, true, true); }
                catch (NoSuchMethodException ex)        { log("Poller: NoSuchMethodException: " + ex.getMessage(), true, true, true); }
                catch (InvocationTargetException ex)    { log("Poller: InvocationTargetException: " + ex.getMessage(), true, true, true); }
                catch (Exception ex)                    { log("Poller: Exception: " + ex.getMessage(), true, true, true); }
            }
        });
        pollerJavaDBClientThread.setName("pollerJavaDBClientThread");
        pollerJavaDBClientThread.setDaemon(runThreadsAsDaemons);
        pollerJavaDBClientThread.setPriority(Thread.NORM_PRIORITY);
        pollerJavaDBClientThread.start();
        
        try { waitForExit(); } catch (Exception ex) { log("Poller: Exception: waitForExit: " + ex.getMessage(), true, true, true); }
    }
    
    private void waitForExit() throws Exception
    {
        while (!isDBClientReady) { try { Thread.sleep(100); } catch (InterruptedException error) { }; }
        if (singleShot)
        {
            pollerExitTimer = new Timer(); pollerExitTimer.schedule(new DCMPollerExitTimer(this), pollTimerInterval); // this, delay

            pollServers();
        }
        else
        {
            pollServersTimer = new Timer(); pollServersTimer.scheduleAtFixedRate(new DCMPollServersTimer(this), 0L, pollTimerInterval); // this, delay, interval (ms)
        }
    }

    public void pollServers() throws CloneNotSupportedException, UnknownHostException
    {
        pollServerCaller = this;
        ArrayList<Server> serverList = new ArrayList<Server>();
        serverList = dcmDBClient.selectServers();
        
        counter = 0;
        for (final Server server:serverList)
        {
            long sleepPeriod = (dutyCycle*1000)/serverList.size(); if (sleepPeriod > 1000) {sleepPeriod = 1000;} // prevent long delays with small nr of servers
            
            if (server.getHost().getEnabled())
            {
                try {try { pollServer = new DCMPollServer(pollServerCaller, counter, server, sessionTimeout, debug); }
                catch (CloneNotSupportedException ex) { }}
                catch (UnknownHostException ex) { }
            }            
            counter++;
            try { Thread.sleep(sleepPeriod); } catch (InterruptedException error) { }; // Polling all servers always happens in pollTime seconds
        }
    }

    public void exitPoller()
    {
        System.exit(0);
    }
    
    @Override    
    public synchronized final void log(final String messageParam, final boolean logToStatusParam, final boolean logToApplicationParam, final boolean logToFileParam)
    {
        Thread LogThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (logToStatusParam)       {  }
                if (logToApplicationParam)  { if (debug) { System.out.println(messageParam);} }
                if (logToFileParam)         { if (debug) { logToFile(messageParam);} }
            }
        });
        LogThread.setName("LogThread");
        LogThread.setDaemon(false);
        LogThread.setPriority(Thread.NORM_PRIORITY);
        LogThread.start();
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
                catch (IOException ex) { System.out.println("Error: IOException: logFileWriter.close();"); return; }
            }
        });
        logToFileThread.setName("logToFileThread");
        logToFileThread.setDaemon(runThreadsAsDaemons);
        logToFileThread.start();
    }

    @Override
    public void dbClientReady()
    {
        isDBClientReady = true;
    }

    @Override
    public void pollServerReady()
    {
    }
    public static void main(final String argsParam[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                boolean singleShot = false;
                int timeout = 10;
                boolean daemon = false;
                boolean debug = false;
                
                for (int i=0; i < argsParam.length; i++)
                {
                    if (    argsParam[i].equals("--singleshot"))    { singleShot = true; }
                    else if(argsParam[i].equals("--continues"))     { singleShot = false; }
                    else if(argsParam[i].contains("timeout"))
                    {
                        String[] timeoutArray = new String[2];
                        timeoutArray = argsParam[i].split("=");
                        try { timeout = Integer.parseInt(timeoutArray[1]); } catch (NumberFormatException ex) { timeout = 10; }
                    }
                    else if(argsParam[i].equals("--daemon"))        { daemon = true; }
                    else if(argsParam[i].equals("--debug"))         { debug = true; }
                    else if(argsParam[i].equals("--help"))          { usage(); System.exit(1); }
                    else                                            { usage(); System.exit(1); }
                }
                try { new DCMPoller(singleShot, timeout, daemon, debug); } catch (Exception ex) { System.out.println("Error: main(): "); }
            }
        });
    }

    @Override public void updatedHost() { }

    private static void usage()
    {
        System.out.println("\n");
        System.out.println("Usage:   java -cp DCManager.jar DCMPoller [--singleshot|--continues] [--timeout=10] [--daemon] [--debug]\n");
        System.out.println(DCMLicense.getCopyright());
    }
}
