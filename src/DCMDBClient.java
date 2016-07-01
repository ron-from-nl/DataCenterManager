import data.Configuration;
import data.ConfigurationCaller;
import data.RMIMessage;
import data.DCMPreset;
import data.MD5Converter;
import data.Resource;
import data.Server;
import data.Host;
import data.DCMUser;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class DCMDBClient extends Thread implements ConfigurationCaller
{
//  STDIN & STDOUT Redirection
    public class FilteredStream extends FilterOutputStream
    {
        public FilteredStream(OutputStream aStream) { super(aStream); }
        @Override public void write(byte b[]) throws IOException {}
        @Override public void write(byte b[], int off, int len) throws IOException { }
    }
//  End Redirection

    private boolean dbServerTest = true;
    private DCMDBClientCaller dcmDBClientCaller;
    public static final String DERBY_CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private static final String DERBY_CLIENT_DS = "org.apache.derby.jdbc.ClientDataSource";

    String jdbcDriver = DERBY_CLIENT_DRIVER;
    String jdbcDataSource = DERBY_CLIENT_DS;

    private ArrayList statements                        = new ArrayList(); // list of Statements, PreparedStatements
    private PreparedStatement psInsertServer            = null;
    private PreparedStatement psUpdateServer            = null;
    private PreparedStatement psInsertDCMTijd           = null;
    private PreparedStatement psUpdateDCMTijd           = null;
    private PreparedStatement psInsertDCMUser           = null;
    private PreparedStatement psUpdateDCMUser           = null;
    private PreparedStatement psInsertDCMPreset         = null;
    private PreparedStatement psUpdateDCMPreset         = null;
    private PreparedStatement psInsertHost              = null;
    private PreparedStatement psUpdateHost              = null;
    private PreparedStatement psInsertResource          = null;
    private PreparedStatement psUpdateResource          = null;
    private PreparedStatement psUpdateResourceValue     = null;
    private PreparedStatement psExportDCMUserTable      = null;
    private PreparedStatement psExportDCMPresetTable    = null;
    private PreparedStatement psExportHostTable         = null;
    private PreparedStatement psExportResourceTable     = null;
    private PreparedStatement psImportDCMUserTable      = null;
    private PreparedStatement psImportDCMPresetTable      = null;
    private PreparedStatement psImportHostTable         = null;
    private PreparedStatement psImportResourceTable     = null;

    private final String protocol                   = "jdbc:derby:";
    private final String dbServerAddress            = "//127.0.0.1:1527/";
    private String database                         = "";
    private final String attributes                 = ";create=false";
    private final String url                        = protocol + dbServerAddress + database + attributes;
    private final String user                       = "dbadmin";
    private final String toegang                    = "IsNwtNp4DB";

    private Thread dbServerThread;
    private ResultSetMetaData resultsetmetadata     = null;
    boolean runThreadsAsDaemons                     = true;
    private Connection connection                   = null;
    private static boolean dbServerInit =           false;
    private DCMShell shell                             = null;
    private boolean daemon                          = false;
    private boolean debug                           = false;

//    private Statement statement = null;
//    private ResultSet resultset = null;

    private Properties props;
    private Configuration configuration;

    @SuppressWarnings("static-access")
    public DCMDBClient(DCMDBClientCaller DCMDBClientCallerParam, final String databaseParam, final boolean daemonParam, final boolean debugParam) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception
    {
////      STDIN & STDOUT Redirection
//        if (!Configuration.DEBUG)
//        {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            FilteredStream filteredStream = new FilteredStream(byteArrayOutputStream);
//            PrintStream aPrintStream  = new PrintStream(filteredStream);
//            System.setOut(aPrintStream); System.setErr(aPrintStream);
//        }
        
	dcmDBClientCaller = DCMDBClientCallerParam;
        daemon = daemonParam;
        debug = debugParam;
        
        shell = new DCMShell(debug); // for starting the DCMMetaDBServer
        dcmDBClientCaller.log("Action:  DCMDBClient Invoked", true, true, true);
        
        configuration = new Configuration(this);
	String[] status = new String[2]; status = new String[2];
	database = databaseParam;
	System.setProperty("derby.system.home", configuration.getDatabasesDir());
//	System.setProperty("derby.connection.requireAuthentication", "true");

	props = new Properties(); // connection properties
//	props.setProperty("derby.connection.requireAuthentication", "true");
	props.setProperty("derby.system.home",	configuration.getDatabasesDir());
	props.setProperty("databaseName",	database);
	props.setProperty("user",		user);
	props.setProperty("password",		toegang);

        loadDriver();
	connection = initDatabaseClientServer(); // This connection should be the only execption

//        DCMDBClientCaller.log("db_client_connecting", false, true, true);
	test(connection); connection.setAutoCommit(false);
//        DCMDBClientCaller.log("db_client_connected", false, true, true);
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
	checkTables();
	initPrepareStatements();
        dcmDBClientCaller.dbClientReady();
    }

    public void loadDriver()
    {
	// Setting the driver settings and load the driver
	try { Class.forName(jdbcDriver).newInstance(); }
	catch (ClassNotFoundException cnfe) { dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client Failed Loading Driver " + jdbcDriver, true, true, true); cnfe.printStackTrace(System.err); }
	catch (InstantiationException ie) { dcmDBClientCaller.log( "Error:  DCMDBClient: JavaDB Database Client Failed Instantiate JDBC driver " + jdbcDriver, true, true, true); ie.printStackTrace(System.err); }
	catch (IllegalAccessException iae) { dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client NOT Allowed Access to JDBC Driver " + jdbcDriver, true, true, true); iae.printStackTrace(System.err); }
    }

    public Connection initDatabaseClientServer()
    {
        Connection tmpConnection = null;

	boolean dbServerStarted = false;
	boolean dbServerRunning = true;
	do
	{
	    dbServerRunning = true;
	    try { tmpConnection = DriverManager.getConnection(url, props); }
	    catch (SQLException ex)
	    {
		// Connection Failed, Start DBServer
		dbServerRunning = false;

// ============ internal JavaDBServer ================                
//		if ((!dbServerStarted) && (!isDBServerInitiated())) // Make sure that only the first DBClient instance will attempt to start DBServer
//		{
//                    try { new JavaDBServer(this, database, dbServerTest); }
//                    catch (Exception ex2) { javaDBClientCaller.log("JavaDB Database Server Failed to Start: " + ex2.getMessage(), true, true, true); }
//		      dbServerStarted = true;
//                    setDBServerInitiated(); // Make sure that only the first DBClient instance will attempt to start DBServer
//		}
// ============ internal JavaDBServer ================
// ============ external JavaDBServer ================                
		if ((!dbServerStarted)) // Make sure that only the first DBClient instance will attempt to start DBServer
		{
                    String javaParam = ""; if (daemon) { javaParam = " --daemon "; } else { javaParam = ""; }
                    shell.startJavaDBServer(128, "", javaParam);
//                    try { new JavaDBServer(this, database, dbServerTest); }
//                    catch (Exception ex2) { javaDBClientCaller.log("JavaDB Database Server Failed to Start: " + ex2.getMessage(), true, true, true); }
		    dbServerStarted = true;
		}
// ============ external JavaDBServer ================                
                try { Thread.sleep(100); } catch (InterruptedException ex1) { }
	    }
	}
	while (!dbServerRunning);

        dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Connection Opened", true, true, true);

        try { tmpConnection.clearWarnings(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: tmpConnection.clearWarnings()", true, true, true); }
	return tmpConnection;
    }

    public Connection getDriverClientConnection()
    {
	loadDriver();
	boolean notConnected = false;
	Connection tmpConnection = null;
	do
	{
	    try { tmpConnection = DriverManager.getConnection(url, props); }
	    catch (SQLException ex) { notConnected = true; dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client Connection Failed: " + ex.getMessage(), true, true, true); }
	    try { Thread.sleep(100); } catch (InterruptedException ex1) { }
	}
	while (notConnected);

	return tmpConnection;
    }

    public void test(Connection conn) throws Exception
    {
	Statement stmt = null; ResultSet rs = null;
	try { stmt = conn.createStatement(); rs = stmt.executeQuery("select count(*) from sys.systables"); while(rs.next())
        {
            dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Connection Established", true, true, true); }
        }
	catch(SQLException sqle)
        {
            dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client Connection NOT Established: " + sqle.getMessage(), true, true, true); throw sqle;
        }
	finally	{ if(rs != null) { rs.close(); } if(stmt != null) { stmt.close(); } }
    }

    public void shutdownDB(String databaseParam)
    {
	try { DriverManager.getConnection("jdbc:derby:" + databaseParam + ";password=" + toegang + ";shutdown=true"); }
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Server Failed to Shutdown: " + ex.getMessage(), true, true, true); }
    }

    public void dropDatabase(String databaseParam)
    {
        Statement statement = null; ResultSet resultset = null;
	shutdownDB(databaseParam);
	//connection = getDriverClientConnection();
	try { statement.execute("Error:  DCMDBClient: DROP DATABASE " + databaseParam); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client Failed to Drop Database: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) {  }
	//try { connection.close(); } catch (SQLException ex) {  }
    }
    
    public void exportInventory()
    {
        String backupFileString; File backupFile; boolean success;
        success = true; backupFileString = configuration.getBackupDir() + "DCMUser.csv"; backupFile = new File(backupFileString); if (backupFile.exists()) { if (backupFile.delete()) { dcmDBClientCaller.log("Success: DCMDBClient: Removed existing backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Could not remove backup file: " + backupFile.getName(), true, true, true); } }
        try { psExportDCMUserTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
        try { psExportDCMUserTable.setString(2,"DCMUSER"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // Table
        try { psExportDCMUserTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // exportfile
        try { psExportDCMUserTable.setString(4,","); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
        try { psExportDCMUserTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
        try { psExportDCMUserTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
        try { psExportDCMUserTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } 
	try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
        if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Exported backup file: " + backupFile.getName() + " (Beware: Delete this sensitive file ASAP!!!)", true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to export backup file: " + backupFile.getName(), true, true, true); }
        
        success = true; backupFileString = configuration.getBackupDir() + "DCMPreset.csv"; backupFile = new File(backupFileString); if (backupFile.exists()) { if (backupFile.delete()) { dcmDBClientCaller.log("Success: DCMDBClient: Removed existing backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Could not remove backup file: " + backupFile.getName(), true, true, true); } }
        try { psExportDCMPresetTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
        try { psExportDCMPresetTable.setString(2,"DCMPRESET"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // Table
        try { psExportDCMPresetTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // exportfile
        try { psExportDCMPresetTable.setString(4,","); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
        try { psExportDCMPresetTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
        try { psExportDCMPresetTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
        try { psExportDCMPresetTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } 
	try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
        if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Exported backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to export backup file: " + backupFile.getName(), true, true, true); }

        success = true; backupFileString = configuration.getBackupDir() + "Host.csv"; backupFile = new File(backupFileString); if (backupFile.exists()) { if (backupFile.delete()) { dcmDBClientCaller.log("Success: DCMDBClient: Removed existing backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Could not remove backup file: " + backupFile.getName(), true, true, true); } }
        try { psExportHostTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
        try { psExportHostTable.setString(2,"HOST"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // Table
        try { psExportHostTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // exportfile
        try { psExportHostTable.setString(4,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
        try { psExportHostTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
        try { psExportHostTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
        try { psExportHostTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } 
	try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
        if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Exported backup file: " + backupFile.getName() + " (Beware: Delete this extremely sensitive file ASAP!!!)", true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to export backup file: " + backupFile.getName(), true, true, true); }

        success = true; backupFileString = configuration.getBackupDir() + "Resource.csv"; backupFile = new File(backupFileString); if (backupFile.exists()) { if (backupFile.delete()) { dcmDBClientCaller.log("Success: DCMDBClient: Removed existing backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Could not remove backup file: " + backupFile.getName(), true, true, true); } }
        try { psExportResourceTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
        try { psExportResourceTable.setString(2,"RESOURCE"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // Table
        try { psExportResourceTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // exportfile
        try { psExportResourceTable.setString(4,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
        try { psExportResourceTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
        try { psExportResourceTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
        try { psExportResourceTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory.: " + ex.getMessage(), true, true, true); } 
        try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
        if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Exported backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to export backup file: " + backupFile.getName(), true, true, true); }
    }
    
    public void importInventory()
    {
        String backupFileString; File backupFile; boolean success;
        backupFileString = configuration.getBackupDir() + "DCMUser.csv"; backupFile = new File(backupFileString); if (backupFile.exists())
        {
            success = true; 
            try { psImportDCMUserTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
            try { psImportDCMUserTable.setString(2,"DCMUSER"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // Table
            try { psImportDCMUserTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // importfile
            try { psImportDCMUserTable.setString(4,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
            try { psImportDCMUserTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
            try { psImportDCMUserTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportDCMUserTable.setInt(7,1); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportDCMUserTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } 
            try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
            if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Imported backup file: " + backupFile.getName() + " (Beware: Delete this sensitive file ASAP!!!)", true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to import backup file: " + backupFile.getName(), true, true, true); }
        } else { dcmDBClientCaller.log("Warning: DCMDBClient: Backup file: " + backupFile.getName() + " does not exist!", true, true, true); }

        backupFileString = configuration.getBackupDir() + "DCMPreset.csv"; backupFile = new File(backupFileString); if (backupFile.exists())
        {
            success = true; 
            try { psImportDCMPresetTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
            try { psImportDCMPresetTable.setString(2,"DCMPRESET"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // Table
            try { psImportDCMPresetTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // importfile
            try { psImportDCMPresetTable.setString(4,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
            try { psImportDCMPresetTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
            try { psImportDCMPresetTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportDCMPresetTable.setInt(7,1); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportDCMPresetTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } 
            try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
            if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Imported backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to import backup file: " + backupFile.getName(), true, true, true); }
        } else { dcmDBClientCaller.log("Warning: DCMDBClient: Backup file: " + backupFile.getName() + " does not exist!", true, true, true); }

        backupFileString = configuration.getBackupDir() + "Host.csv"; backupFile = new File(backupFileString); if (backupFile.exists())
        {
            success = true; 
            try { psImportHostTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
            try { psImportHostTable.setString(2,"HOST"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // Table
            try { psImportHostTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // importfile
            try { psImportHostTable.setString(4,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
            try { psImportHostTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
            try { psImportHostTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportHostTable.setInt(7,1); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportHostTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } 
            try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
            if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Imported backup file: " + backupFile.getName() + " (Beware: Delete this extremely sensitive file ASAP!!!)", true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to import backup file: " + backupFile.getName(), true, true, true); }
        } else { dcmDBClientCaller.log("Warning: DCMDBClient: Backup file: " + backupFile.getName() + " does not exist!", true, true, true); }

        backupFileString = configuration.getBackupDir() + "Resource.csv"; backupFile = new File(backupFileString); if (backupFile.exists())
        {
            success = true; 
            try { psImportResourceTable.setString(1,"APP"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // scheme (default)
            try { psImportResourceTable.setString(2,"RESOURCE"); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // Table
            try { psImportResourceTable.setString(3,backupFileString); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // importfile
            try { psImportResourceTable.setString(4,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // column delimiter
            try { psImportResourceTable.setString(5,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // character delimiter
            try { psImportResourceTable.setString(6,null); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportResourceTable.setInt(7,1); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } // codeset (default)
            try { psImportResourceTable.execute(); } catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.importInventory.: " + ex.getMessage(), true, true, true); } 
            try { connection.commit(); }            catch (SQLException ex) { success = false; dcmDBClientCaller.log("Error: DCMDBClient.exportInventory. connection.commit(): " + ex.getMessage(), true, true, true); }
            if (success) { dcmDBClientCaller.log("Success: DCMDBClient: Imported backup file: " + backupFile.getName(), true, true, true); } else { dcmDBClientCaller.log("Failure: DCMDBClient: Failed to import backup file: " + backupFile.getName(), true, true, true); }
        } else { dcmDBClientCaller.log("Warning: DCMDBClient: Backup file: " + backupFile.getName() + " does not exist!", true, true, true); }
    }

// --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public synchronized void createDCMTijdTable()
    {
        //connection = getDriverClientConnection();
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMTijdTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); loadDriver(); connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.DCMTijd" +
						"( " +
						    "Id BIGINT," +
						    "Tijd BIGINT" +
						" )"
					); } catch (SQLException ex1) { dcmDBClientCaller.log("Error: DCMDBClient.createDCMTijdTable(): " + ex1.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

        Calendar cal = Calendar.getInstance();
        
        try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMTijdTable(): INSERT INTO APP.DCMTijd.. connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"INSERT INTO APP.DCMTijd VALUES (0, 0)"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMTijdTable(): INSERT INTO APP.DCMTijd.. connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMTijdTable(): INSERT INTO APP.DCMTijd.. connection.createStatement(): " + ex.getMessage(), true, true, true); }

        try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMTijdTable(): INSERT INTO APP.DCMTijd.. connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
    }

    public synchronized Calendar getDCMTijd()
    {
        Statement statement = null; ResultSet resultset = null;
	Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMTijdByUsername->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMTijd WHERE Id = " + 0); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.getDCMTijdByUsername: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.getDCMTijdByUsername: " + ex.getMessage(), true, true, true); }

	try { if (resultset.next())
	{
            cal.setTimeInMillis(resultset.getLong(2));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getUserByUsername : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return cal;
    }

    public synchronized void insertDCMTijd(Calendar calParam)
    {
//        DCMDBClientCaller.log("insertDCMTijd: " + userParam.toString(), true, true, true);

	//connection = getDriverClientConnection();
	try { psInsertDCMTijd.setLong(1, 0); }                                             catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMTijd.: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMTijd.setLong(2, calParam.getTimeInMillis()); }                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMTijd.: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMTijd.execute(); }                                                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMTijd: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                       catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMTijd : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void updateDCMTijd(Calendar calParam)
    {
        ArrayList<RMIMessage> rmiMessageList = new ArrayList<RMIMessage>();
	//connection = getDriverClientConnection();
	try { psUpdateDCMTijd.setLong(1, calParam.getTimeInMillis()); }                       catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMTijd.setLong(2, 0); }                       catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMTijd.executeUpdate(); }                                              catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMTijd : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void deleteDCMTijd(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteDCMTijd(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.DCMTijd WHERE Id = " + 0
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteDCMTijd: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }
    
    public synchronized void createDCMUserTable()
    {
        //connection = getDriverClientConnection();
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); loadDriver(); connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.DCMUser" +
						"( " +
						    "Id BIGINT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "Username VARCHAR(100) PRIMARY KEY," +
						    "Password VARCHAR(100)," +
						    "Administrator INT" +
						" )"
					); } catch (SQLException ex1) { dcmDBClientCaller.log("Error: DCMDBClient.createDCMUserTable(): " + ex1.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

        String adminpw = MD5Converter.getMD5SumFromString("admin");
        String dcmpw = MD5Converter.getMD5SumFromString("dcm");
        
        try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): INSERT INTO APP.DCMUser.. connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"INSERT INTO APP.DCMUser VALUES (DEFAULT,'admin','" + adminpw + "',1)"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): INSERT INTO APP.DCMUser.. connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): INSERT INTO APP.DCMUser.. connection.createStatement(): " + ex.getMessage(), true, true, true); }

        try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): INSERT INTO APP.DCMUser.. connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }

        try { statement.executeUpdate(
					"INSERT INTO APP.DCMUser VALUES (DEFAULT,'dcm','" + dcmpw + "',0)"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): INSERT INTO APP.DCMUser.. connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMUserTable(): INSERT INTO APP.DCMUser.. connection.createStatement(): " + ex.getMessage(), true, true, true); }
    }

    public synchronized DCMUser getDCMUserByUsername(String hostnameParam)
    {
        Statement statement = null; ResultSet resultset = null;
	DCMUser dcmUser = new DCMUser();
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMUserByUsername->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMUser WHERE Username = '" + hostnameParam + "'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.getDCMUserByUsername: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.getDCMUserByUsername: " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
	    dcmUser.setId(resultset.getLong(1));
	    dcmUser.setUsername(resultset.getString(2));
	    dcmUser.setPassword(resultset.getString(3));
	    dcmUser.setAdministrator((resultset.getInt(4) != 0)); // Convert int to boolean
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getUserByUsername : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return dcmUser;
    }

    public synchronized DCMUser getDCMUser(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	DCMUser dcmUser = new DCMUser();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMUserByUsername->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMUser WHERE Id = " + idParam); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.getDCMUserByUsername: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.getDCMUserByUsername: " + ex.getMessage(), true, true, true); }

	try { if (resultset.next())
	{
            dcmUser = new DCMUser();
	    dcmUser.setId(resultset.getLong(1));
	    dcmUser.setUsername(resultset.getString(2));
	    dcmUser.setPassword(resultset.getString(3));
	    dcmUser.setAdministrator((resultset.getInt(4) != 0)); // Convert int to boolean
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getUserByUsername : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return dcmUser;
    }

    public synchronized ArrayList<DCMUser> getDCMUserList()
    {
        Statement statement = null; ResultSet resultset = null;
        ArrayList<DCMUser> dcmUserList = new ArrayList<DCMUser>(); dcmUserList.clear();
        
	DCMUser user = new DCMUser();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMUserByUsername->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMUser ORDER BY Id"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
            user = new DCMUser();
	    user.setId(resultset.getLong(1));
	    user.setUsername(resultset.getString(2));
	    user.setPassword(resultset.getString(3));
	    user.setAdministrator((resultset.getInt(4) != 0)); // Convert int to boolean
            dcmUserList.add(user);
//            dcmUserList.add(new DCMUser(resultset.getLong(1),resultset.getString(2),resultset.getString(3),(resultset.getInt(4) != 0)));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getUserByUsername : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return dcmUserList;
    }

    public synchronized boolean authenticateDCMUser(String usernameParam, String passwordParam)
    {
        Statement statement = null; ResultSet resultset = null;
        boolean authenticated = false;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: authenticateDCMUser->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMUser WHERE Username = '" + usernameParam + "' AND Password = '" + passwordParam + "'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { if (resultset.next()) { authenticated = true; }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: authenticateDCMUser->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.authenticateDCMUser : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return authenticated;
    }

    public synchronized void insertDCMUser(DCMUser dcmUserParam)
    {
//        DCMDBClientCaller.log("insertDCMUser: " + userParam.toString(), true, true, true);

	//connection = getDriverClientConnection();
	try { psInsertDCMUser.setString(1, dcmUserParam.getUsername()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMUser.: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMUser.setString(2, dcmUserParam.getPassword()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMUser: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMUser.setInt(3,   (dcmUserParam.getAdministrator())?1:0); }        catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMUser: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMUser.execute(); }                                                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMUser: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                       catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMUser : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void updateDCMUser(DCMUser dcmUserParam)
    {
        ArrayList<RMIMessage> rmiMessageList = new ArrayList<RMIMessage>();
	//connection = getDriverClientConnection();
	try { psUpdateDCMUser.setString(1, dcmUserParam.getUsername()); }                     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMUser.setString(2, dcmUserParam.getPassword()); }                     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMUser.setInt(3,   (dcmUserParam.getAdministrator())?1:0); }           catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMUser.setLong(4,   dcmUserParam.getId()); }                           catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMUser.executeUpdate(); }                                              catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateUser: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMUser : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void deleteDCMUser(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteDCMUser(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.DCMUser WHERE Id = " + idParam
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteDCMUser: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void dropDCMUserTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: dropDCMUserTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.DCMUser"
				); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropDCMUserTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropDCMUserTable : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }    
    
// --------------------------------------------------------------------------------------------------------------------------------------------------------------

    public synchronized void createDCMPresetTable()
    {
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createDCMPresetTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); loadDriver(); connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.DCMPreset" +
						"( " +
						    "Id BIGINT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "UserId BIGINT," +
						    "ViewName VARCHAR(100)," +
						    "ViewDescription VARCHAR(1000)," +

						    "StartCalendarRelative BOOLEAN," +
						    "StartCalendar BIGINT," +
						    "StartMonthSupplement INT," +
						    "StartDaySupplement INT," +
						    "StartHourSupplement INT," +
						    "StartMinuteSupplement INT," +

                                                    "EndCalendarRelative BOOLEAN," +
						    "EndCalendar BIGINT," +
						    "EndMonthSupplement INT," +
						    "EndDaySupplement INT," +
						    "EndHourSupplement INT," +
						    "EndMinuteSupplement INT," +
                
						    "EnableSearch BOOLEAN," +
						    "SearchString VARCHAR(1000)," +
						    "SearchExact BOOLEAN," +                
						    "SelectedResources LONG VARCHAR," +
						    "Shared BOOLEAN" +
						" )"
					); } catch (SQLException ex1) { dcmDBClientCaller.log("Error: DCMDBClient.createDCMPresetTable(): " + ex1.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }
    }

    public synchronized DCMPreset getDCMPreset(long idParam)
    {
        Calendar cal = Calendar.getInstance();
        Statement statement = null; ResultSet resultset = null;
	DCMPreset DCMPreset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMPresetByUsername->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMPreset WHERE Id = " + idParam); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMPreset->statement.executeQuery(\"SELECT * FROM APP.DCMPreset\"): "+ ex.getMessage(), true, true, true); }

	try { if (resultset.next())
	{
            DCMPreset = new DCMPreset();
	    DCMPreset.setId(resultset.getLong(1));
	    DCMPreset.setUserId(resultset.getLong(2));
	    DCMPreset.setPresetName(resultset.getString(3));
	    DCMPreset.setPresetDescription(resultset.getString(4));
            
	    DCMPreset.setStartCalendarRelative(resultset.getBoolean(5));
            cal = Calendar.getInstance(); cal.setTimeInMillis(resultset.getLong(6)); DCMPreset.setStartCalendar(cal);
	    DCMPreset.setStartMonthSupplement(resultset.getInt(7));
	    DCMPreset.setStartDaySupplement(resultset.getInt(8));
	    DCMPreset.setStartHourSupplement(resultset.getInt(9));
	    DCMPreset.setStartMinuteSupplement(resultset.getInt(10));
            
	    DCMPreset.setEndCalendarRelative(resultset.getBoolean(11));
            cal = Calendar.getInstance(); cal.setTimeInMillis(resultset.getLong(12)); DCMPreset.setEndCalendar(cal);
	    DCMPreset.setEndMonthSupplement(resultset.getInt(13));
	    DCMPreset.setEndDaySupplement(resultset.getInt(14));
	    DCMPreset.setEndHourSupplement(resultset.getInt(15));
	    DCMPreset.setEndMinuteSupplement(resultset.getInt(16));

            DCMPreset.setEnableSearch(resultset.getBoolean(17));
	    DCMPreset.setSearchString(resultset.getString(18));
	    DCMPreset.setSearchExact(resultset.getBoolean(19));
            DCMPreset.setSelectedResources(resultset.getString(20));
	    DCMPreset.setShared(resultset.getBoolean(21));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMPreset->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getUserByUsername : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
	return DCMPreset;
    }

    public synchronized ArrayList<DCMPreset> getDCMPresetList()
    {
        Calendar cal = Calendar.getInstance();
        Statement statement = null; ResultSet resultset = null;
        ArrayList<DCMPreset> DCMPresetList = new ArrayList<DCMPreset>(); DCMPresetList.clear();
        
	DCMPreset DCMPreset = new DCMPreset();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMUserByUsername->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.DCMPreset ORDER BY Id"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getDCMPresetList->statement.executeQuery(\"SELECT * FROM APP.DCMPreset\"): "+ ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
            DCMPreset = new DCMPreset();
	    DCMPreset.setId(resultset.getLong(1));
	    DCMPreset.setUserId(resultset.getLong(2));
	    DCMPreset.setPresetName(resultset.getString(3));
	    DCMPreset.setPresetDescription(resultset.getString(4));
            
	    DCMPreset.setStartCalendarRelative(resultset.getBoolean(5));
            cal.setTimeInMillis(resultset.getLong(6)); DCMPreset.setStartCalendar(cal);
	    DCMPreset.setStartMonthSupplement(resultset.getInt(7));
	    DCMPreset.setStartDaySupplement(resultset.getInt(8));
	    DCMPreset.setStartHourSupplement(resultset.getInt(9));
	    DCMPreset.setStartMinuteSupplement(resultset.getInt(10));
            
	    DCMPreset.setEndCalendarRelative(resultset.getBoolean(11));
            cal.setTimeInMillis(resultset.getLong(12)); DCMPreset.setEndCalendar(cal);
	    DCMPreset.setEndMonthSupplement(resultset.getInt(13));
	    DCMPreset.setEndDaySupplement(resultset.getInt(14));
	    DCMPreset.setEndHourSupplement(resultset.getInt(15));
	    DCMPreset.setEndMinuteSupplement(resultset.getInt(16));

            DCMPreset.setEnableSearch(resultset.getBoolean(17));
	    DCMPreset.setSearchString(resultset.getString(18));
	    DCMPreset.setSearchExact(resultset.getBoolean(19));
            DCMPreset.setSelectedResources(resultset.getString(20));
	    DCMPreset.setShared(resultset.getBoolean(21));
            DCMPresetList.add(DCMPreset);
//            dcmUserList.add(new DCMUser(resultset.getLong(1),resultset.getString(2),resultset.getString(3),(resultset.getInt(4) != 0)));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getUserByUsername->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getUserByUsername : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return DCMPresetList;
    }

    public synchronized void insertDCMPreset(DCMPreset DCMPresetParam)
    {
//        DCMDBClientCaller.log("insertDCMPreset: " + userParam.toString(), true, true, true);

	//connection = getDriverClientConnection();
	try { psInsertDCMPreset.setLong(1, DCMPresetParam.getUserId()); }                             catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset.: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMPreset.setString(2, DCMPresetParam.getPresetName()); }                         catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMPreset.setString(3, DCMPresetParam.getPresetDescription()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setBoolean(4, DCMPresetParam.getStartCalendarRelative()); }           catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setLong(5, DCMPresetParam.getStartCalendar().getTimeInMillis()); }    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(6, DCMPresetParam.getStartMonthSupplement()); }                catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(7, DCMPresetParam.getStartDaySupplement()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(8, DCMPresetParam.getStartHourSupplement()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(9, DCMPresetParam.getStartMinuteSupplement()); }               catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setBoolean(10, DCMPresetParam.getEndCalendarRelative()); }            catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setLong(11, DCMPresetParam.getEndCalendar().getTimeInMillis()); }     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(12, DCMPresetParam.getEndMonthSupplement()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(13, DCMPresetParam.getEndDaySupplement()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(14, DCMPresetParam.getEndHourSupplement()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setInt(15, DCMPresetParam.getEndMinuteSupplement()); }                catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setBoolean(16, DCMPresetParam.getEnableSearch()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setString(17, DCMPresetParam.getSearchString()); }                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setBoolean(18, DCMPresetParam.getSearchExact()); }                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setString(19, DCMPresetParam.getSelectedResources()); }               catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.setBoolean(20, DCMPresetParam.getShared()); }                         catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psInsertDCMPreset.execute(); }                                                           catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void updateDCMPreset(DCMPreset userViewParam)
    {
        ArrayList<RMIMessage> rmiMessageList = new ArrayList<RMIMessage>();
	//connection = getDriverClientConnection();
	try { psUpdateDCMPreset.setLong(1, userViewParam.getUserId()); }                             catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset.: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMPreset.setString(2, userViewParam.getPresetName()); }                         catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMPreset.setString(3, userViewParam.getPresetDescription()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setBoolean(4, userViewParam.getStartCalendarRelative()); }           catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setLong(5, userViewParam.getStartCalendar().getTimeInMillis()); }    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(6, userViewParam.getStartMonthSupplement()); }                catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(7, userViewParam.getStartDaySupplement()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(8, userViewParam.getStartHourSupplement()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(9, userViewParam.getStartMinuteSupplement()); }               catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setBoolean(10, userViewParam.getEndCalendarRelative()); }             catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setLong(11, userViewParam.getEndCalendar().getTimeInMillis()); }     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(12, userViewParam.getEndMonthSupplement()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(13, userViewParam.getEndDaySupplement()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(14, userViewParam.getEndHourSupplement()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setInt(15, userViewParam.getEndMinuteSupplement()); }                catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setBoolean(16, userViewParam.getEnableSearch()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setString(17, userViewParam.getSearchString()); }                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setBoolean(18, userViewParam.getSearchExact()); }                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setString(19, userViewParam.getSelectedResources()); }               catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setBoolean(20, userViewParam.getShared()); }                         catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.setLong(21, userViewParam.getId()); }                                catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); } // Converts boolean to integer
	try { psUpdateDCMPreset.execute(); }                                                           catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.updateDCMPreset : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void deleteDCMPreset(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteDCMPreset(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.DCMPreset WHERE Id = " + idParam
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteDCMPreset: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void dropDCMPresetTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: dropDCMPresetTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.DCMPreset"
				); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropDCMPresetTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropDCMPresetTable : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }    
    
// --------------------------------------------------------------------------------------------------------------------------------------------------------------

    public synchronized void createHostTable()
    {
        //connection = getDriverClientConnection();
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createHostTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); loadDriver(); connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.Host" +
						"( " +
						    "Id BIGINT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "Created BIGINT," +
						    "Hostname VARCHAR(100)," +
						    "Port INT," +
						    "Username VARCHAR(100)," +
						    "UserPassword VARCHAR(100)," +
						    "SuperuserPassword VARCHAR(100)," +
						    "Sysinfo VARCHAR(1000)," +
						    "Enabled BOOLEAN," +
						    "ContactEmail VARCHAR(100)," +
						    "LastPolled BIGINT," +
						    "Errors VARCHAR(4096)," +
						    "Command VARCHAR(4096)," +
						    "Comment2 VARCHAR(4096)," +
						    "Comment3 VARCHAR(4096)" +
						" )"
					); } catch (SQLException ex1) { dcmDBClientCaller.log("Error: DCMDBClient.createHostTable(): " + ex1.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized int getHostCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.Host"); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getHostCount->statement.executeQuery(\"SELECT COUNT (*) FROM APP.Host\"): " + ex.getMessage(), true, true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }} catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient..getHostCount->resultset.next(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }
	return numberOfRecords;
    }

    public synchronized Host selectHost(long idParam) throws CloneNotSupportedException
    {
        Statement statement = null; ResultSet resultset = null;
	Host host = new Host();
	int colcount = 0;
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectCustomer(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Host WHERE Id = " + idParam
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHostCount->.statement.executeQuery(\"SELECT * FROM APP.Host WHERE Id = \"" + idParam + ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
	    host.setId(resultset.getLong(1));
	    cal.setTimeInMillis(resultset.getLong(2)); host.setCreated(cal);
	    host.setHostname(resultset.getString(3));
	    host.setPort(resultset.getInt(4));
	    host.setUsername(resultset.getString(5));
	    host.setUserPassword(resultset.getString(6));
	    host.setSuperuserPassword(resultset.getString(7));
	    host.setSysinfo(resultset.getString(8));
	    host.setEnabled(resultset.getBoolean(9)); // Convert int to boolean
	    host.setContactEmail(resultset.getString(10));
	    cal.setTimeInMillis(resultset.getLong(11)); host.setLastPolled(cal);
	    host.setErrors(resultset.getString(12));
	    host.setCommand(resultset.getString(13));
	    host.setComment2(resultset.getString(14));
	    host.setComment3(resultset.getString(15));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectHost->while (resultset.next()... "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectHost->connection.commit(): "+ ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return (Host) host.clone();
    }

    public synchronized ArrayList<Host> getHosts() throws CloneNotSupportedException
    {
        Statement statement = null; ResultSet resultset = null;
//        try { statement.setMaxRows(dcmVergunning.getServersInLicense()); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->statement.setMaxRows: "+ ex.getMessage(),true, true, true); } // setMaxRows or SQL LIMIT doesn't work yet in Derby
	Host host = new Host();
        ArrayList<Host> hostList = new ArrayList<Host>();
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.Host ORDER BY Hostname"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { while ((resultset.next()))
	{
	    host.setId(resultset.getLong(1));
	    cal.setTimeInMillis(resultset.getLong(2)); host.setCreated(cal);
	    host.setHostname(resultset.getString(3));
	    host.setPort(resultset.getInt(4));
	    host.setUsername(resultset.getString(5));
	    host.setUserPassword(resultset.getString(6));
	    host.setSuperuserPassword(resultset.getString(7));
	    host.setSysinfo(resultset.getString(8));
	    host.setEnabled(resultset.getBoolean(9)); // Convert int to boolean
	    host.setContactEmail(resultset.getString(10));
	    cal.setTimeInMillis(resultset.getLong(11)); host.setLastPolled(cal);
	    host.setErrors(resultset.getString(12));
	    host.setCommand(resultset.getString(13));
	    host.setComment2(resultset.getString(14));
	    host.setComment3(resultset.getString(15));
            hostList.add((Host)host.clone());
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getHosts : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return hostList;
    }

    public synchronized ArrayList<Host> getHostsByHostname(String hostnameParam) throws CloneNotSupportedException // Used by DeleteAllServers
    {
        Statement statement = null; ResultSet resultset = null;
	Host host = new Host();
        ArrayList<Host> hostList = new ArrayList<Host>();
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.Host WHERE Hostname = '" + hostnameParam + "'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
	    host.setId(resultset.getLong(1));
	    cal.setTimeInMillis(resultset.getLong(2)); host.setCreated(cal);
	    host.setHostname(resultset.getString(3));
	    host.setPort(resultset.getInt(4));
	    host.setUsername(resultset.getString(5));
	    host.setUserPassword(resultset.getString(6));
	    host.setSuperuserPassword(resultset.getString(7));
	    host.setSysinfo(resultset.getString(8));
	    host.setEnabled(resultset.getBoolean(9)); // Convert int to boolean
	    host.setContactEmail(resultset.getString(10));
	    cal.setTimeInMillis(resultset.getLong(11)); host.setLastPolled(cal);
	    host.setErrors(resultset.getString(12));
	    host.setCommand(resultset.getString(13));
	    host.setComment2(resultset.getString(14));
	    host.setComment3(resultset.getString(15));
            hostList.add((Host)host.clone());
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getHosts : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return hostList;
    }

    public synchronized Host getHostByHostname(String hostnameParam) throws CloneNotSupportedException // Used by AddServerByHostname
    {
        Statement statement = null; ResultSet resultset = null;
	Host host = new Host();
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.Host WHERE Hostname = '" + hostnameParam + "'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
	    host.setId(resultset.getLong(1));
	    cal.setTimeInMillis(resultset.getLong(2)); host.setCreated(cal);
	    host.setHostname(resultset.getString(3));
	    host.setPort(resultset.getInt(4));
	    host.setUsername(resultset.getString(5));
	    host.setUserPassword(resultset.getString(6));
	    host.setSuperuserPassword(resultset.getString(7));
	    host.setSysinfo(resultset.getString(8));
	    host.setEnabled(resultset.getBoolean(9)); // Convert int to boolean
	    host.setContactEmail(resultset.getString(10));
	    cal.setTimeInMillis(resultset.getLong(11)); host.setLastPolled(cal);
	    host.setErrors(resultset.getString(12));
	    host.setCommand(resultset.getString(13));
	    host.setComment2(resultset.getString(14));
	    host.setComment3(resultset.getString(15));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getHosts : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return host;
    }

    public synchronized void insertHost(Host hostParam)
    {
        dcmDBClientCaller.log("insertHost: " + hostParam.toString(), true, true, true);

	//connection = getDriverClientConnection();
	try { psInsertHost.setLong(1, hostParam.getCreated().getTimeInMillis()); }      catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(2, hostParam.getHostname()); }                     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setInt(3, hostParam.getPort()); }                            catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClientinsertHost.: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(4, hostParam.getUsername()); }                     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClientinsertHost.: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(5, hostParam.getUserPassword()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(6, hostParam.getSuperuserPassword()); }            catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(7, hostParam.getSysinfo()); }                      catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setBoolean(8, hostParam.getEnabled()); }                     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); } // Converts boolean to integer ?1:0
	try { psInsertHost.setString(9, hostParam.getContactEmail()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setLong(10, hostParam.getLastPolled().getTimeInMillis()); }  catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(11, hostParam.getErrors()); }                      catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(12, hostParam.getCommand()); }                     catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(13, hostParam.getComment2()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.setString(14, hostParam.getComment3()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { psInsertHost.execute(); }                                                 catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                    catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.insertHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

//	try { psUpdateHost = connection.prepareStatement("UPDATE APP.Host SET Hostname = ?, Port = ?, Username = ?, UserPassword = ?, SuperuserPassword = ?, Sysinfo = ?, Enabled = ?, ContactEmail = ?, LastPolled = ?, Errors = ?, Command = ?, Comment2 = ?, Comment3 = ? WHERE Id = ?"); } catch (SQLException ex) { DCMDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Host: " + ex.getMessage(), true, true, true); }

    public synchronized ArrayList<RMIMessage> updateHost(Host hostParam)
    {
        ArrayList<RMIMessage> rmiMessageList = new ArrayList<RMIMessage>();
	//connection = getDriverClientConnection();
//	try { psUpdateHost.setLong(1,   hostParam.getCreated().getTimeInMillis()); }    catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(1, hostParam.getHostname());","",false)); javaDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(1, hostParam.getHostname()); }                     catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(1, hostParam.getHostname());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setInt(2,    hostParam.getPort()); }                         catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setInt(2,    hostParam.getPort());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(3, hostParam.getUsername()); }                     catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(3, hostParam.getUsername());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(4, hostParam.getUserPassword()); }                 catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(4, hostParam.getUserPassword());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(5, hostParam.getSuperuserPassword()); }		catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(5, hostParam.getSuperuserPassword());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(6, hostParam.getSysinfo()); }                      catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(6, hostParam.getSysinfo());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setBoolean(7, hostParam.getEnabled()); }                     catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setBoolean(7,   hostParam.getEnabled());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); } // Converts boolean to integer ?1:0
	try { psUpdateHost.setString(8, hostParam.getContactEmail()); }                 catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(8, hostParam.getContactEmail());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setLong(9,   hostParam.getLastPolled().getTimeInMillis());}  catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setLong(9,   hostParam.getLastPolled().getTimeInMillis());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(10, hostParam.getErrors()); }                      catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(10, hostParam.getErrors());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(11, hostParam.getCommand()); }                     catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(11, hostParam.getComments1());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(12, hostParam.getComment2()); }                   catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(12, hostParam.getComments2());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setString(13, hostParam.getComment3()); }                   catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setString(13, hostParam.getComments3());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.setLong(14, hostParam.getId()); }                             catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.setInt(14, hostParam.getId());","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost.executeUpdate(); }                                           catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.psUpdateHost.executeUpdate();","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { rmiMessageList.add(new RMIMessage("Error","DCMDBClient.connection.commit();","",false)); dcmDBClientCaller.log("Error: DCMDBClient.updateHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
        dcmDBClientCaller.updatedHost();
        return rmiMessageList;
    }

    public synchronized void enableHost(Host hostParam, boolean enableParam) throws CloneNotSupportedException
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectCustomer(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
						 "UPDATE APP.Host SET Enabled = " + enableParam + " WHERE Id = " + hostParam.getId()
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHostCount->.statement.executeQuery(\"SELECT * FROM APP.Host WHERE Id = \"" + hostParam.getId() + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: enableHost->connection.commit(): "+ ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void deleteHosts()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHosts(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.Host"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"ALTER TABLE APP.Host ALTER COLUMN Id RESTART WITH 0"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void deleteHost(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteCustomer(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.Host WHERE Id = " + idParam
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: deleteHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void dropHostTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: dropCustomerTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.Host"
				); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropHostTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropHostTable : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }


// --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public synchronized void createResourceTable()
    {
        //connection = getDriverClientConnection();
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: createResourceTable connection.createStatement(): " + ex.getMessage(), true, true, true); loadDriver(); connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.Resource" +
						"( " +
						    "Id BIGINT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "HostId BIGINT," +
						    "Category VARCHAR(20)," +
						    "ResourceType VARCHAR(40)," +
						    "ValueType VARCHAR(20)," +
						    "CounterType VARCHAR(20)," +
						    "Resource VARCHAR(100)," +
						    "Enabled BOOLEAN," +
						    "PollCommand VARCHAR(4069)," +
						    "LastValue Double," +
						    "WarningLimit Double," +
						    "CriticalLimit Double," +
						    "AlertPolls INT," +
						    "Updated BIGINT," +
						    "RRDFile VARCHAR(250)" +
						" )"
					); } catch (SQLException ex1) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: createResourceTable: " + ex1.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: createResourceTable : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized int getResourceCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getResourceCount: connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.RESOURCE"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getResourceCount: " + ex.getMessage(), true, true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }} catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getResourceCount: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getResourceCount: connection.commit(): " + ex.getMessage(), true, true, true); }
	return numberOfRecords;
    }

    public synchronized Resource selectResource(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	Resource resource = new Resource();
	int colcount = 0;
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Resource WHERE Id = " + idParam
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): " + ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
	    resource.setId(resultset.getLong(1));
	    resource.setHostId(resultset.getLong(2));
	    resource.setCategory(resultset.getString(3));
	    resource.setResourceType(resultset.getString(4));
	    resource.setValueType(resultset.getString(5));
	    resource.setCounterType(resultset.getString(6));
	    resource.setResource(resultset.getString(7));
	    resource.setEnabled(resultset.getBoolean(8)); // Convert int to boolean != 0
	    resource.setPollCommand(resultset.getString(9));
            resource.setLastValue(resultset.getDouble(10));
            resource.setWarningLimit(resultset.getDouble(11));
	    resource.setCriticalLimit(resultset.getDouble(12));
	    resource.setAlertPolls(resultset.getInt(13));
            cal.setTimeInMillis(resultset.getLong(14)); resource.setUpdated(cal);
	    resource.setArchiveDBFile(resultset.getString(15));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource()->while (resultset.next() " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return resource;
    }

    public synchronized ArrayList<String> selectDistinctResources(long idParam, String categoryParam)
    {
        Statement statement = null; ResultSet resultset = null;
        ArrayList<String> resourceList = new ArrayList<String>();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT DISTINCT Resource FROM APP.Resource WHERE HostId = " + idParam + " AND Category = '" + categoryParam + "'"
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources(): " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
            resourceList.add(resultset.getString(1));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources()->while (resultset.next() " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources(): connection.commit(): " + ex.getMessage(), true, true, true); }

	return resourceList;
    }

    public synchronized ArrayList<String> selectDistinctResourceTypes(long idParam, String categoryParam)
    {
        Statement statement = null; ResultSet resultset = null;
        ArrayList<String> resourceTypeList = new ArrayList<String>();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT DISTINCT ResourceType FROM APP.Resource WHERE HostId = " + idParam + " AND Category = '" + categoryParam + "'"
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources(): " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
            resourceTypeList.add(resultset.getString(1));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources()->while (resultset.next() " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectDistinctResources(): connection.commit(): " + ex.getMessage(), true, true, true); }

	return resourceTypeList;
    }

    public synchronized String convertSearchStringToResourcesText(String searchPhraseParam, ArrayList<Server> serverList, boolean exactParam) // Inputs a search string & outputs a textlist with resources numbers
    {
        String col1,col2,col3,col4,col5;
        
        String wildcard = "";
        String output = "";        
        String unfilteredOutput = "";        
        if (exactParam) { wildcard = "";} else { wildcard = "%";}
        
        for (String searchParam:searchPhraseParam.split("\\s*\\|\\s*"))
        {
            String operator = "WHERE";
            String sqlString = "SELECT App.Resource.Id, APP.Host.Hostname, App.Resource.Category, App.Resource.ResourceType, App.Resource.ValueType, App.Resource.Resource ";
            sqlString +=       "FROM APP.Resource LEFT OUTER JOIN APP.Host ON App.Resource.HostId = APP.Host.Id ";
            for (String keyword:searchParam.split("\\s+"))
            {
                sqlString += operator + " (UPPER(APP.Host.Hostname) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR ";
                sqlString += " UPPER(APP.Host.Sysinfo) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR ";
                sqlString += " UPPER(App.Resource.CATEGORY) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR ";
                sqlString += " UPPER(App.Resource.RESOURCETYPE) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR ";
                sqlString += " UPPER(App.Resource.COUNTERTYPE) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR ";
                sqlString += " UPPER(App.Resource.VALUETYPE) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR ";
                sqlString += " UPPER(App.Resource.RESOURCE) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "'))";
                operator = "AND";
            }
            sqlString += " ORDER BY APP.Host.Hostname, APP.Resource.Id";
            if (debug) { dcmDBClientCaller.log(sqlString,false,true,true); }
            
            Statement statement = null; ResultSet resultset = null;
            try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { resultset = statement.executeQuery(sqlString); }
            catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): " + ex.getMessage(), true, true, true); }

            try { while (resultset.next()) // As this runs in a loop, this is the output adding part
            {
                col1 = Long.toString(resultset.getLong(1)) + " ";   unfilteredOutput += col1 + " ";
                col2 = resultset.getString(2);                      unfilteredOutput += col2 + " ";
                col3 = resultset.getString(3);                      unfilteredOutput += col3 + " ";
                col4 = resultset.getString(4);                      unfilteredOutput += col4 + " ";
                col4 = resultset.getString(5);                      unfilteredOutput += col4 + " ";
                col5 = resultset.getString(6);                      unfilteredOutput += col5 + "\r\n";
            }}
            catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource()->while (resultset.next() " + ex.getMessage(), true, true, true); }
            try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): connection.commit(): " + ex.getMessage(), true, true, true); }            
        }
        
        // Make sure users only get the servers they are licenced for
        for (Server server:serverList) { output += DCMTools.contains(unfilteredOutput, " " + server.getHost().getHostname() + " "); }
        
        return output;
    }

    // Frontend
    public synchronized String convertSearchStringToHostsText(String searchPhraseParam, ArrayList<Server> serverList, boolean exactParam) // Inputs a search string & outputs a textlist with resources numbers
    {
        String col1,col2,col3;
        
        String wildcard = "";
        String output = "";        
        String unfilteredOutput = "";
        
        if (exactParam) { wildcard = "";} else { wildcard = "%";}
        
        for (String searchParam:searchPhraseParam.split("\\s*\\|\\s*"))
        {
            String operator = "WHERE";
            String sqlString = "SELECT DISTINCT APP.Host.Id, APP.Host.Hostname, APP.Host.Sysinfo FROM APP.Host RIGHT OUTER JOIN APP.Resource ON APP.Host.Id = App.Resource.HostId ";
            for (String keyword:searchParam.split("\\s+"))
            {
                sqlString += operator + " (UPPER(APP.Host.Hostname) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR UPPER(APP.Host.Sysinfo) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR UPPER(App.Resource.CATEGORY) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR UPPER(App.Resource.RESOURCETYPE) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "') OR UPPER(App.Resource.RESOURCE) like UPPER('" + wildcard + "" + keyword + "" + wildcard + "'))";
                operator = "AND";
            }
            sqlString += " ORDER BY APP.Host.Id";
            Statement statement = null; ResultSet resultset = null;
            try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { resultset = statement.executeQuery(sqlString); }
            catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): " + ex.getMessage(), true, true, true); }

            try { while (resultset.next())
            {
                col1 = Long.toString(resultset.getLong(1));   unfilteredOutput += col1 + " ";
                col2 = resultset.getString(2);                unfilteredOutput += col2 + " ";
                col3 = resultset.getString(3);                unfilteredOutput += col3;
            }}
            catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource()->while (resultset.next() " + ex.getMessage(), true, true, true); }
            try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: selectResource(): connection.commit(): " + ex.getMessage(), true, true, true); }            
        }

        // Make sure users only get the servers they are licenced for
        for (Server server:serverList) { output += DCMTools.contains(unfilteredOutput, " " + server.getHost().getHostname() + " "); }
        
        return output;
    }

    public synchronized ArrayList<Resource> convertResourcesTextToResources(ArrayList<String> resourceIdList) // Inputs resourcesTXT, outputs Resources for RRDGraph
    {
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        for (String resourceId:resourceIdList)
        {
            boolean resourceIdIsValidNumber = true;
            try{ Long.parseLong(resourceId); } catch (Exception e) { resourceIdIsValidNumber = false; }
            if (resourceIdIsValidNumber) { resourceList.add(selectResource(Long.parseLong(resourceId))); }
        }
        return resourceList;
    }

    public synchronized ArrayList<Host> convertHostsTextToHostsList(ArrayList<String> hostTXTList) // Inputs hostsTXT, outputs HostList for DCMCommander
    {
        ArrayList<Host> hostList = new ArrayList<Host>();
        for (String hostId:hostTXTList)
        {
            boolean hostIdIsValidNumber = true;
            try{ Long.parseLong(hostId); } catch (Exception e) { hostIdIsValidNumber = false; }
            if (hostIdIsValidNumber) { try { hostList.add(selectHost(Long.parseLong(hostId))); } catch (CloneNotSupportedException ex) { }}
        }
        return hostList;
    }

    public synchronized void insertResource(Resource resourceParam)
    {
//connection = getDriverClientConnection();
        Calendar cal = Calendar.getInstance();
	try { psInsertResource.setLong(1, resourceParam.getHostId()); }             catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setString(2, resourceParam.getCategory()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setString(3, resourceParam.getResourceType()); }     catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setString(4, resourceParam.getValueType()); }        catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setString(5, resourceParam.getCounterType()); }      catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setString(6, resourceParam.getResource()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setBoolean(7, resourceParam.getEnabled()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); } // Converts boolean to integer ?1:0
	try { psInsertResource.setString(8, resourceParam.getPollCommand()); }      catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setDouble(9, resourceParam.getLastValue()); }        catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setDouble(10, resourceParam.getWarningLimit()); }    catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setDouble(11, resourceParam.getCriticalLimit()); }   catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setInt(12, resourceParam.getAlertPolls()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setLong(13, cal.getTimeInMillis()); }                catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.setString(14, resourceParam.getRRDFile()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { psInsertResource.execute(); }                                         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertResource: " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }
    
//	try { psInsertResource = connection.prepareStatement("INSERT INTO APP.Resource VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { javaDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT Resource: " + ex.getMessage(), true, true, true); }
//	try { psUpdateResource = connection.prepareStatement("UPDATE APP.Resource SET HostId = ?, Category = ?, ResourceType = ?, ValueType = ?, CounterType = ?, Resource = ?, Enabled = ?, PollCommand = ?, LastValue = ?, WarningLimit = ?, CriticalLimit = ?, AlertPolls = ?, Updated = ?, RRDFile = ? WHERE Id = ?"); } catch (SQLException ex) { javaDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Resource: " + ex.getMessage(), true, true, true); }

    public synchronized void updateResource(Resource resourceParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateResource.setLong(1, resourceParam.getHostId()); }             catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setString(2, resourceParam.getCategory()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setString(3, resourceParam.getResourceType()); }     catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setString(4, resourceParam.getValueType()); }        catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setString(5, resourceParam.getCounterType()); }      catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setString(6, resourceParam.getResource()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setBoolean(7, resourceParam.getEnabled()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); } // Converts boolean to integer ?1:0
	try { psUpdateResource.setString(8, resourceParam.getPollCommand()); }      catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setDouble(9, resourceParam.getLastValue()); }        catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setDouble(10, resourceParam.getWarningLimit()); }    catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setDouble(11, resourceParam.getCriticalLimit()); }   catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setInt(12, resourceParam.getAlertPolls()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setLong(13, resourceParam.getUpdated().getTimeInMillis()); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setString(14, resourceParam.getRRDFile()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.setLong(15, resourceParam.getId()); }                catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource.executeUpdate(); }                                   catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); }                                                catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void updateServerResourcesValues(Server serverParam) // Please use thread
    {
        Calendar cal = Calendar.getInstance();
        for (Resource resource:serverParam.getResourceList())
        {
            cal=resource.getUpdated();
            //System.out.println("Rid: " + resource.getId() + " RV: " + resource.getLastValue() + " RU: " + resource.getUpdated().getTimeInMillis());
            //connection = getDriverClientConnection();
            try { psUpdateResourceValue.setDouble(1, resource.getLastValue()); }     catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateServerResourcesValues: " + ex.getMessage(), true, true, true); }
            try { psUpdateResourceValue.setLong(2, cal.getTimeInMillis()); }                            catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateServerResourcesValues: " + ex.getMessage(), true, true, true); }
            try { psUpdateResourceValue.setLong(3, resource.getId()); }              catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateServerResourcesValues: " + ex.getMessage(), true, true, true); }
            try { psUpdateResourceValue.executeUpdate(); }                          catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateServerResourcesValues: ResourceId: " + resource.getId() + " ResourceValue: " + resource.getLastValue() + " " + ex.getMessage(), true, true, true); }
            try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: updateResource connection.commit(): " + ex.getMessage(), true, true, true); }
            //try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }            
        }
    }

    public synchronized void deleteResources()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.Resource"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"ALTER TABLE APP.Resource ALTER COLUMN Id RESTART WITH 0"
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void deleteResource(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.Resource WHERE Id = " + idParam
				     ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteResource(): connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    public synchronized void dropResourceTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropResourceTable()->connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.Resource"
				); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropResourceTable(): connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: dropResourceTable: connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }
    
// --------------------------------------------------------------------------------------------------------------------------------------------------------------

    public synchronized Server getServer(long idParam) throws CloneNotSupportedException // This (Server) is actually an object
    {
        Server      server =    new Server();
	Host        host =      new Host();
	Resource    resource =  new Resource();
        
        Statement statement = null; ResultSet resultset = null;
	int colcount = 0;
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Host WHERE Id = " + idParam
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer(): " + ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { if (resultset.next())
	{
	    host.setId(resultset.getLong(1));
	    cal.setTimeInMillis(resultset.getLong(2)); host.setCreated(cal);
	    host.setHostname(resultset.getString(3));
	    host.setPort(resultset.getInt(4));
	    host.setUsername(resultset.getString(5));
	    host.setUserPassword(resultset.getString(6));
	    host.setSuperuserPassword(resultset.getString(7));
	    host.setSysinfo(resultset.getString(8));
	    host.setEnabled(resultset.getBoolean(9)); // Convert int to boolean != 0
	    host.setContactEmail(resultset.getString(10));
	    cal.setTimeInMillis(resultset.getLong(11)); host.setLastPolled(cal);
	    host.setErrors(resultset.getString(12));
	    host.setCommand(resultset.getString(13));
	    host.setComment2(resultset.getString(14));
	    host.setComment3(resultset.getString(15));
        }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer():  connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

        server.setHost(host);
        
//      Get the related resources and add them to the server object        
        
	colcount = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer():  connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Resource WHERE HostId = " + idParam
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer(): " + ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { while (resultset.next())
	{
	    resource.setId(resultset.getLong(1));
	    resource.setHostId(resultset.getLong(2));
	    resource.setCategory(resultset.getString(3));
	    resource.setResourceType(resultset.getString(4));
	    resource.setValueType(resultset.getString(5));
	    resource.setCounterType(resultset.getString(6));
	    resource.setResource(resultset.getString(7));
	    resource.setEnabled(resultset.getBoolean(8)); // Convert int to boolean != 0
	    resource.setPollCommand(resultset.getString(9));
            resource.setLastValue(resultset.getDouble(10));
            resource.setWarningLimit(resultset.getDouble(11));
	    resource.setCriticalLimit(resultset.getDouble(12));
	    resource.setAlertPolls(resultset.getInt(13));
            cal.setTimeInMillis(resultset.getLong(14)); resource.setUpdated(cal);
	    resource.setArchiveDBFile(resultset.getString(15));
            //System.out.println("while Resources: " + resource.getId()); tested
            server.addResource((Resource)resource.clone());
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer(): " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getServer(): connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return server;
    }
    
    public synchronized ArrayList<Server> selectServers() throws CloneNotSupportedException // This (Server) is actually an object
    {
        ArrayList<Server> serverList = new ArrayList<Server>();
        for (Host host : getHosts())
        {
            serverList.add(getServer(host.getId()));
        }
        return serverList;
    }
    
    public synchronized ArrayList<Server> selectServersByIdList(ArrayList<Long> idListParam) throws CloneNotSupportedException // This (Server) is actually an object
    {
        ArrayList<Server> serverList = new ArrayList<Server>();
        for (Host host : getHosts())
        {
            serverList.add(getServer(host.getId()));
        }
        return serverList;
    }
    
    public synchronized Server getServerByHostname(String hostnameParam) throws CloneNotSupportedException // This (Server) is actually an object
    {
        Statement statement = null; ResultSet resultset = null;
        Server server = new Server();
	Host host = new Host();
        Calendar cal = Calendar.getInstance();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->connection.createStatement(): "+ ex.getMessage(),true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM APP.Host WHERE Hostname = '" + hostnameParam + "'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->statement.executeQuery(\"SELECT * FROM APP.Host\"): "+ ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { javaDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }

	try { if (resultset.next())
	{
	    host.setId(resultset.getLong(1));
	    cal.setTimeInMillis(resultset.getLong(2)); host.setCreated(cal);
	    host.setHostname(resultset.getString(3));
	    host.setPort(resultset.getInt(4));
	    host.setUsername(resultset.getString(5));
	    host.setUserPassword(resultset.getString(6));
	    host.setSuperuserPassword(resultset.getString(7));
	    host.setSysinfo(resultset.getString(8));
	    host.setEnabled(resultset.getBoolean(9)); // Convert int to boolean != 0
	    host.setContactEmail(resultset.getString(10));
	    cal.setTimeInMillis(resultset.getLong(11)); host.setLastPolled(cal);
	    host.setErrors(resultset.getString(12));
	    host.setCommand(resultset.getString(13));
	    host.setComment2(resultset.getString(14));
	    host.setComment3(resultset.getString(15));
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: getHosts->while (resultset.next()...: "+ ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.getHosts : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

        // Now that we know host.getHostId, get the server that belongs to this hostid
        server = getServer(host.getId());
        
	return server;
    }
    
    public synchronized boolean deleteServerByHostname(String hostnameParam) throws CloneNotSupportedException
    {
        boolean recordsDeleted = false;
        ArrayList<Host> hostList = new ArrayList<Host>();
        hostList = getHostsByHostname(hostnameParam);
        
        Statement statement = null; ResultSet resultset = null;

        for (Host host:hostList)
        {
            try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteServerByHostname(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { statement.executeUpdate("DELETE FROM APP.Host WHERE Id = " + host.getId()); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteServerByHostname(): DELETE FROM APP.Host WHERE Id = " + host.getId() + ": " + ex.getMessage(), true, true, true); }
            try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteServerByHostname(): connection.commit(): " + ex.getMessage(), true, true, true); }

            try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteServerByHostname(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { statement.executeUpdate("DELETE FROM APP.Resource WHERE HostId = " + host.getId()); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteServerByHostname(): DELETE FROM APP.Resource WHERE HostId = " + host.getId() + ": " + ex.getMessage(), true, true, true); }
            try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException DCMDBClient.deleteServerByHostname(): connection.commit(): " + ex.getMessage(), true, true, true); }            
        }
        recordsDeleted = true;
        return recordsDeleted;
    }

    public synchronized long selectLastHostId()
    {
        Statement statement = null; ResultSet resultset = null;
	long hostId = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Host ORDER BY Id DESC"
					    ); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: DCMDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: DCMDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    hostId = resultset.getLong(1);
	}}
	catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient.: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

	return hostId;
    }

    
    public synchronized void insertServer(Server serverParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertServer.setLong(1,    serverParam.getHost().getCreated().getTimeInMillis()); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(2,  serverParam.getHost().getHostname()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setInt(3,     serverParam.getHost().getPort()); }                      catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer.: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(4,  serverParam.getHost().getUsername()); }                  catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer.: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(5,  serverParam.getHost().getUserPassword()); }              catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(6,  serverParam.getHost().getSuperuserPassword()); }         catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(7,  serverParam.getHost().getSysinfo()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setBoolean(8, serverParam.getHost().getEnabled()); }                   catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); } // Converts boolean to integer ?1:0
	try { psInsertServer.setString(9,  serverParam.getHost().getContactEmail()); }              catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setLong(10,   serverParam.getHost().getLastPolled().getTimeInMillis()); }	  catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(11, serverParam.getHost().getErrors()); }                    catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(12, serverParam.getHost().getCommand()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(13, serverParam.getHost().getComment2()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.setString(14, serverParam.getHost().getComment3()); }                 catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { psInsertServer.execute(); }                                                           catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertServer: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: insertHost : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }

        long hostId = selectLastHostId();
        for (Resource resourse: serverParam.getResourceList())
        {
            resourse.setHostId(hostId); insertResource(resourse);
        }
    }

// ===============================================================================================================================================================

    public void executeSQL(String sqlStatementParam)
    {
        Statement statement = null; ResultSet resultset = null;
	String[] status = new String[2];status[0] = "0"; status[1] = "0";
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: executeSQL(): connection.createStatement(): " + ex.getMessage(), true, true, true); }
	try { statement.executeUpdate(sqlStatementParam); } catch (SQLException ex) { dcmDBClientCaller.log("Error: executeSQL(): statement.executeUpdate(sqlStatementParam): " + ex.getMessage(),true ,true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }

    private void checkTables()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
//        DCMDBClientCaller.log("db_tables_checking", false, true, true);

	boolean tablesMissing = false;
	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: checkTables(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'DCMTIJD'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): SQLException: " + ex.getMessage(), true, true, true); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("DCMTIJD"))) {} else { tablesMissing = true; createDCMTijdTable(); /* dcmDBClientCaller.log("JavaDB Database Client Creating table: DCMTIJD", true, true, true); */ }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): Failure " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: checkTables(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'DCMUSER'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): SQLException: " + ex.getMessage(), true, true, true); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("DCMUSER"))) {} else { tablesMissing = true; createDCMUserTable(); dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Creating table: DCMUSER", true, true, true); }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): checkDCMUserTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: checkTables(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'DCMPRESET'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): SQLException: " + ex.getMessage(), true, true, true); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("DCMPRESET"))) {} else { tablesMissing = true; createDCMPresetTable(); dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Creating table: DCMPRESET", true, true, true); }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): checkDCMPresetTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: checkTables(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'HOST'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): SQLException: " + ex.getMessage(), true, true, true); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("HOST"))) {} else { tablesMissing = true; createHostTable(); dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Creating table: HOST", true, true, true); }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): checkHostTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: checkTables(): connection.createStatement(): " + ex.getMessage(), true, true, true); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'RESOURCE'"); } catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): SQLException: " + ex.getMessage(), true, true, true); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("RESOURCE"))) {} else { tablesMissing = true; createResourceTable(); dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Creating table: RESOURCE", true, true, true); }}
	catch (SQLException ex) { dcmDBClientCaller.log("Error: createTables(): checkResourceTable: " + ex.getMessage(), true, true, true); }
	try { connection.commit(); } catch (SQLException ex) { dcmDBClientCaller.log("Error: DCMDBClient. : connection.commit(): " + ex.getMessage(), true, true, true); }

	if (tablesMissing) { dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Created " + database + " Database", true, true, true); }

	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
//        DCMDBClientCaller.log("db_tables_checked", false, true, true);
    }

    private synchronized void reportFailure(String message) { dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client Data Verification Failed: " + message, true, true,true); }

    public synchronized void printSQLException(SQLException error)
    {
        // Unwraps the entire exception chain to unveil the real cause of the Exception.
        while (error != null) { dcmDBClientCaller.log("SQLException: ", true, true, true); dcmDBClientCaller.log(" SQL State: " + error.getSQLState(), true, true, true); dcmDBClientCaller.log(" Error Code: " + error.getErrorCode(), true, true, true); dcmDBClientCaller.log(" Message: " + error.getMessage(), true, true, true); error = error.getNextException(); }
    }

    public void shutdownDB()
    {
	// Single DB Shutdown SQL state is "08006", and the error code is 45000.
	try { DriverManager.getConnection("jdbc:derby:;shutdown=true"); }
	catch (SQLException se) { if (( (se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState()) ))) { dcmDBClientCaller.log("Success: DCMDBClient: JavaDB Database Client Driver Shutdown Successfull", true, true, true); }
	else { dcmDBClientCaller.log("Error:  DCMDBClient: JavaDB Database Client Driver Shutdown Unsuccessfull", true, true, true); printSQLException(se); }}
    }
    public void unloadDriver()
    {
        Statement statement = null; ResultSet resultset = null;
	try { if (resultset != null) { resultset.close(); resultset = null; } } catch (SQLException sqle) { printSQLException(sqle); }
	int i = 0; while (!statements.isEmpty()) { Statement st = (Statement)statements.remove(i); try { if (st != null) { st.close(); st = null; } } catch (SQLException sqle) { printSQLException(sqle); }}

	try { if (connection != null) { connection.close(); connection = null; }} catch (SQLException sqle) { printSQLException(sqle); }
    }

    private void initPrepareStatements()
    {
	//connection = getDriverClientConnection();
	try { psInsertHost = connection.prepareStatement("INSERT INTO APP.Host VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT Host: " + ex.getMessage(), true, true, true); }
	try { psUpdateHost = connection.prepareStatement("UPDATE APP.Host SET Hostname = ?, Port = ?, Username = ?, UserPassword = ?, SuperuserPassword = ?, Sysinfo = ?, Enabled = ?, ContactEmail = ?, LastPolled = ?, Errors = ?, Command = ?, Comment2 = ?, Comment3 = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Host: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMTijd = connection.prepareStatement("INSERT INTO APP.DCMTijd VALUES (?, ?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT Failed: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMTijd = connection.prepareStatement("UPDATE APP.DCMTijd SET Tijd = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Failed: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMUser = connection.prepareStatement("INSERT INTO APP.DCMUser VALUES (DEFAULT, ?, ?, ?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT DCMUSer: " + ex.getMessage(), true, true, true); }
	try { psUpdateDCMUser = connection.prepareStatement("UPDATE APP.DCMUser SET Username = ?, Password = ?, Administrator = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE DCMUser: " + ex.getMessage(), true, true, true); }
	try { psInsertDCMPreset = connection.prepareStatement("INSERT INTO APP.DCMPreset VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT DCMPreset: " + ex.getMessage(), true, true, true); }
        try { psUpdateDCMPreset = connection.prepareStatement("UPDATE APP.DCMPreset SET UserId = ?, ViewName = ?, ViewDescription = ?, StartCalendarRelative = ?, StartCalendar = ?, StartMonthSupplement = ?, StartDaySupplement = ?, StartHourSupplement = ?, StartMinuteSupplement = ?, EndCalendarRelative = ?, EndCalendar = ?, EndMonthSupplement = ?, EndDaySupplement = ?, EndHourSupplement = ?, EndMinuteSupplement = ?, EnableSearch = ?, SearchString = ?, SearchExact = ?, SelectedResources = ?, Shared = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE DCMPreset: " + ex.getMessage(), true, true, true); }
        try { psInsertServer = connection.prepareStatement("INSERT INTO APP.Host VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT Host: " + ex.getMessage(), true, true, true); }
	try { psUpdateServer = connection.prepareStatement("UPDATE APP.Host SET Hostname = ?, Port = ?, Username = ?, UserPassword = ?, SuperuserPassword = ?, Sysinfo = ?, Enabled = ?, ContactEmail = ?, LastPolled = ?, Errors = ?, Command = ?, Comment2 = ?, Comment3 = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Host: " + ex.getMessage(), true, true, true); }
	try { psInsertResource = connection.prepareStatement("INSERT INTO APP.Resource VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->INSERT Resource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResource = connection.prepareStatement("UPDATE APP.Resource SET HostId = ?, Category = ?, ResourceType = ?, ValueType = ?, CounterType = ?, Resource = ?, Enabled = ?, PollCommand = ?, LastValue = ?, WarningLimit = ?, CriticalLimit = ?, AlertPolls = ?, Updated = ?, RRDFile = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Resource: " + ex.getMessage(), true, true, true); }
	try { psUpdateResourceValue = connection.prepareStatement("UPDATE APP.Resource SET LastValue = ?, Updated = ? WHERE Id = ?"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Resource: " + ex.getMessage(), true, true, true); }
//	try { psUpdateResourceValue = connection.prepareStatement("UPDATE APP.Resource SET LastValue = ? WHERE Id = ?"); } catch (SQLException ex) { javaDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->UPDATE Resource: " + ex.getMessage(), true, true, true); }

        try { psExportDCMUserTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psExportDCMPresetTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psExportHostTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psExportResourceTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psImportDCMUserTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_Import_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psImportDCMPresetTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_Import_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psImportHostTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_Import_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
        try { psImportResourceTable = connection.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)"); } catch (SQLException ex) { dcmDBClientCaller.log("Error:  DCMDBClient: SQLException: initPrepareStatements->CALL SYSCS_UTIL.SYSCS_Import_TABLE (?,?,?,?,?,?): " + ex.getMessage(), true, true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.log("Error: DCMDBClient. : connection.close(): " + ex.getMessage()); }
    }
/*
CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'STAFF','c:\output\myfile.del',';','%',null,0);
 */
    private static boolean isDBServerInitiated()
    {
        return dbServerInit;
    }
    
    private static void setDBServerInitiated()
    {
        dbServerInit = true;
    }
    
    public boolean getDBServerTest()
    {
        return dbServerTest;
    }

    @Override
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile) {
        dcmDBClientCaller.log(messageParam,  logToStatus,  logApplication,  logToFile);
    }
}
