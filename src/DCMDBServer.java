import data.Configuration;
import data.ConfigurationCaller;
import java.io.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.JFrame;
import org.apache.derby.drda.NetworkServerControl;

public class DCMDBServer extends Thread implements ConfigurationCaller
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
 
    private Connection connection	= null;
    private final String protocol	= "jdbc:derby:";
    private String database		= "DCMMetaDB";
    private final String attributes	= ";create=true";
    private final String user		= "dbadmin";
    private final String toegang	= "IsNwtNp4DB";
    private final String url		= protocol + database + attributes;
    private boolean keepRunning		= true;
    private Properties props;
    private NetworkServerControl networkServerControl;
    private DCMDBClient javaDBClient;
    private DCMDBServer javaDBServerReference;
    private Configuration configuration;
    private String              logDateString;
    private Calendar            currentTimeCalendar;
    private static final String THISPRODUCT = "DCMDBServer";
    private FileWriter          logFileWriter;
    private String              logFileString;
    private String              logBuffer                                       = "";
    private boolean       debug = false;
    private boolean       daemon = false;

    public DCMDBServer(DCMDBClient javaDBClientParam, String databaseParam, boolean testParam, boolean daemonParam, boolean debugParam) throws Exception
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
        
        javaDBClient = javaDBClientParam;
        javaDBServerReference = this;
        networkServerControl = new NetworkServerControl();
        database = databaseParam;
        configuration = new Configuration(javaDBServerReference);

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

        if (testParam)
        {
            System.setProperty("derby.system.durability",      "test");
            System.setProperty("derby.storage.rowLocking",     "true");
    //	System.setProperty("derby.storage.pageCacheSize",  "20000000");
        }

        props = new Properties();
        props.setProperty("derby.system.home",		    configuration.getDatabasesDir());
        props.setProperty("databaseName",		    database);
        props.setProperty("user",			    user);
        props.setProperty("password",			    toegang);


// ====================================================================================

        javaDBClient.log("JavaDB Database Server Starting...", true, true, true);

        Thread javaDBServerThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings("empty-statement")
            public void run()
            {
                try { networkServerControl.start(null); } catch (Exception ex) { javaDBClient.log("Error: Exception: JavaDBServer() networkServerControl.start(null): " + ex.getMessage(), true, true, true); }
                try { Thread.sleep(4000); } catch (InterruptedException ex) {} // Just give the server some time to start
                try { javaDBServerReference.waitForStart(); } catch (Exception ex) { javaDBClient.log("Error: Exception: JavaDBServer() javaDBServerReference.waitForStart(): " + ex.getMessage(), true, true, true); }
                try { connection = DriverManager.getConnection(url, props); } catch (SQLException ex) { javaDBClient.log("Error: SQLException: JavaDBServer() DriverManager.getConnection(url, props): " + ex.getMessage(), true, true, true); }
                try { test(connection); } catch (Exception ex) { javaDBClient.log("Error: Exception: JavaDBServer() test(connection): " + ex.getMessage(), true, true, true); }
                initRuntime();
//                do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(keepRunning);
                //super.waitForExit();
            }
        });
        javaDBServerThread.setName("javaDBServerThread");
        javaDBServerThread.setDaemon(false);
        javaDBServerThread.setPriority(9);
        javaDBServerThread.start();
    }

    public DCMDBServer(boolean daemonParam, boolean debugParam)
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
        
        javaDBServerReference = this;
        
        // Test mode (fast)
        System.setProperty("derby.system.durability",      "test");
        System.setProperty("derby.storage.rowLocking",     "true");
    //	System.setProperty("derby.storage.pageCacheSize",  "20000000");

        props = new Properties();
        System.setProperty("derby.system.home",		    configuration.getDatabasesDir());
        System.setProperty("databaseName",		    configuration.getJavaDB());
        props.setProperty("derby.system.home",		    configuration.getDatabasesDir());
        props.setProperty("databaseName",		    configuration.getJavaDB());
        props.setProperty("user",			    user);
        props.setProperty("password",			    toegang);

        try { networkServerControl = new NetworkServerControl(); } catch (Exception ex) { log("Error: Exception: JavaDBServer() new NetworkServerControl(): " + ex.getMessage()); }

// ====================================================================================

        log("JavaDB Database Server Starting...");

        Thread javaDBServerThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings("empty-statement")
            public void run()
            {
                try { networkServerControl.start(null); } catch (Exception ex) { log("Error: Exception: JavaDBServer() networkServerControl.start(null): " + ex.getMessage()); }
                try { Thread.sleep(4000); } catch (InterruptedException ex) {} // Just give the server some time to start
                try { waitForStart(); } catch (Exception ex) { log("Error: Exception: JavaDBServer() javaDBServerReference.waitForStart(): " + ex.getMessage()); }
                try { connection = DriverManager.getConnection(url, props); } catch (SQLException ex) { log("Error: SQLException: JavaDBServer() DriverManager.getConnection(url, props): " + ex.getMessage()); }
                try { test(connection); } catch (Exception ex) { log("Error: Exception: JavaDBServer() test(connection): " + ex.getMessage()); }
                initRuntime2();
                do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(keepRunning);
                //super.waitForExit();
            }
        });
        javaDBServerThread.setName("javaDBServerThread");
        javaDBServerThread.setDaemon(false);
        javaDBServerThread.setPriority(9);
        javaDBServerThread.start();
    }

    @SuppressWarnings("static-access")
    private void waitForStart() throws Exception
    {
        org.apache.derby.drda.NetworkServerControl server = new NetworkServerControl();
//        javaDBClient.log("JavaDB Database Server Waiting for Completion...", true, true, true);
        log("JavaDB Database Server Waiting for Completion...");
        boolean dbserverok = false;
        int counter = 0;
        do
        {
//            try { Thread.currentThread().sleep(100); server.ping(); } catch (Exception e) { myUserInterface.showStatus("Try #" + counter + " " +e.toString(), true, true); }
//            try { Thread.currentThread().sleep(100); networkServerControl.ping(); } catch (Exception e) { javaDBClient.log("Try #" + counter + " " +e.toString(), true, true, true); }
            try { Thread.currentThread().sleep(100); networkServerControl.ping(); } catch (Exception e) { log("Try #" + counter + " " +e.toString()); }
            counter++;
        } while ((!dbserverok) && (counter<20));
//        javaDBClient.log("JavaDB Database Server Running", true, true, true);
//        javaDBClient.log("db_server_started", true, true, true);
        log("JavaDB Database Server Running");
    }

    public Connection getEmbeddedConnection() throws Exception { return DriverManager.getConnection(url, props); }

    public void test(Connection conn) throws Exception
    {
        Statement statement = null; ResultSet resultSet = null;
        try
        {
          statement = conn.createStatement();
          resultSet = statement.executeQuery("select count(*) from sys.systables");
//          if (resultSet.next()) { javaDBClient.log("JavaDB Database Server Ready for Connections", true, true, true); }
          if (resultSet.next()) { log("JavaDB Database Server Ready for Connections"); }
        }
//        catch(SQLException sqle) { javaDBClient.log("JavaDB Database Server Embedded Connection Failed Test: "+ sqle.getMessage(), true, true, true); throw sqle; } finally { if(resultSet != null) { resultSet.close(); } if(statement != null) { statement.close(); } }
        catch(SQLException sqle) { log("JavaDB Database Server Embedded Connection Failed Test: "+ sqle.getMessage()); throw sqle; } finally { if(resultSet != null) { resultSet.close(); } if(statement != null) { statement.close(); } }
    }

    public void initRuntime()
    {
        String getProperty	    = "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY(";
	String setProperty	    = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(";
        String requireAuth	    = "'derby.connection.requireAuthentication'";
        String defaultConnMode	    = "'derby.database.defaultConnectionMode'";
        String fullAccessUsers	    = "'derby.database.fullAccessUsers'";
        String provider		    = "'derby.authentication.provider'";
        String propertiesOnly	    = "'derby.database.propertiesOnly'";

        Statement statement = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { javaDBClient.log("1: " + ex.getMessage(), true, true, true); }
	
	String providerString = setProperty + provider + ", 'BUILTIN')";
//	ystem.out.println(providerString);
	try { statement.executeUpdate(providerString);	} catch (SQLException ex) { javaDBClient.log("3: " + ex.getMessage(), true, true, true); }

	String userString = setProperty + "'derby.user." + user + "', '" + toegang + "')";
//	ystem.out.println(userString);
	try { statement.executeUpdate(userString); } catch (SQLException ex) { javaDBClient.log("4: " + ex.getMessage(), true, true, true); }

	String fullAccessUsersString = setProperty + fullAccessUsers + ", '" + user + "')";
//	ystem.out.println(fullAccessUsersString);
	try { statement.executeUpdate(fullAccessUsersString); } catch (SQLException ex) { javaDBClient.log("6: " + ex.getMessage(), true, true, true); }

	String defaultConnectionModeString = setProperty + defaultConnMode + ", 'noAccess')";
//	ystem.out.println(defaultConnectionModeString);
	try { statement.executeUpdate(defaultConnectionModeString);	} catch (SQLException ex) { javaDBClient.log("5: " + ex.getMessage(), true, true, true); }

	String requirteAuthString = setProperty + requireAuth + ", 'true')";
//	ystem.out.println(requirteAuthString);
	try { statement.executeUpdate(requirteAuthString);	} catch (SQLException ex) { javaDBClient.log("2: " + ex.getMessage(), true, true, true); }

	String propertiesOnlyString = setProperty + propertiesOnly + ", 'true')";
//	ystem.out.println(propertiesOnlyString);
	try { statement.executeUpdate(propertiesOnlyString); } catch (SQLException ex) { javaDBClient.log("7: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { javaDBClient.log("8: " + ex.getMessage(), true, true, true); }
    }

    public void initRuntime2() // Stand alone via main constructor
    {
        String getProperty	    = "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY(";
	String setProperty	    = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(";
        String requireAuth	    = "'derby.connection.requireAuthentication'";
        String defaultConnMode	    = "'derby.database.defaultConnectionMode'";
        String fullAccessUsers	    = "'derby.database.fullAccessUsers'";
        String provider		    = "'derby.authentication.provider'";
        String propertiesOnly	    = "'derby.database.propertiesOnly'";

        Statement statement = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { log("1: " + ex.getMessage()); }
	
	String providerString = setProperty + provider + ", 'BUILTIN')";
//	ystem.out.println(providerString);
	try { statement.executeUpdate(providerString);	} catch (SQLException ex) { log("3: " + ex.getMessage()); }

	String userString = setProperty + "'derby.user." + user + "', '" + toegang + "')";
//	ystem.out.println(userString);
	try { statement.executeUpdate(userString); } catch (SQLException ex) { log("4: " + ex.getMessage()); }

	String fullAccessUsersString = setProperty + fullAccessUsers + ", '" + user + "')";
//	ystem.out.println(fullAccessUsersString);
	try { statement.executeUpdate(fullAccessUsersString); } catch (SQLException ex) { log("6: " + ex.getMessage()); }

	String defaultConnectionModeString = setProperty + defaultConnMode + ", 'noAccess')";
//	ystem.out.println(defaultConnectionModeString);
	try { statement.executeUpdate(defaultConnectionModeString);	} catch (SQLException ex) { log("5: " + ex.getMessage()); }

	String requirteAuthString = setProperty + requireAuth + ", 'true')";
//	ystem.out.println(requirteAuthString);
	try { statement.executeUpdate(requirteAuthString);	} catch (SQLException ex) { log("2: " + ex.getMessage()); }

	String propertiesOnlyString = setProperty + propertiesOnly + ", 'true')";
//	ystem.out.println(propertiesOnlyString);
	try { statement.executeUpdate(propertiesOnlyString); } catch (SQLException ex) { log("7: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { log("8: " + ex.getMessage()); }
    }

    //private void waitForExit() throws Exception { BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); ystem.out.println("[Enter] to stop"); in.readLine(); }
    @SuppressWarnings("empty-statement")
    private void waitForExit() throws Exception { do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(keepRunning); }

    public void shutdown()
    {
	keepRunning = false;
	try { networkServerControl.shutdown(); } catch (Exception ex) { System.err.println("Error: DBServer.shutdown: " + ex.getMessage()); }
    }

    private void log(String messageParam)
    {
        logToFile(messageParam);
        System.out.println(messageParam);
    }
    
    @Override
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile)
    {
        logToFile(messageParam);
        javaDBClient.log(messageParam,  logToStatus,  logApplication,  logToFile);
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
        logToFileThread.setDaemon(false);
        logToFileThread.start();
    }

    public static void main(final String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                boolean daemon = false;
                boolean debug = false;

                for (int i=0; i < args.length; i++)
                {
                    if      ( args[i].equals("--daemon") )  { daemon = true; }
                    else if ( args[i].equals("--debug") )   { debug = true; }
                    else if ( args[i].equals("--help") )    { usage(); System.exit(1); }
                }
                try { new DCMDBServer(daemon, debug); } catch (Exception ex) { System.out.println("Error: main(): "); ex.printStackTrace(); }
            }
        });
    }
    
    @Override
    public void run()
    {
    }

    private static void usage()
    {
        System.out.println("\n");
        System.out.println("Usage:   java -cp DCManager.jar DCMDBServer [--daemon] [--debug]\n");
        System.out.println(DCMLicense.getCopyright());
    }
}