import data.Configuration;
import data.ConfigurationCaller;
import data.Resource;
import data.Server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;

public class DCMArchiveCreate implements ConfigurationCaller
{
    private DCMInventoryServer inventoryReference;
    private RrdDef rrdDef;
    private RrdDb rrdDb;
    private ArrayList<Resource> resourcesList;
    private Resource resource;
    private Configuration configuration;
    private String rrdHostDirString;
    private File rrdHostDir;
    private String rrdArchiveFileString;
    private File rrdArchiveFile;
    public DCMArchiveCreate(DCMInventoryServer inventoryReferenceParam, final Server server)
    {
        configuration = new Configuration(this);
        inventoryReference = inventoryReferenceParam;
        rrdHostDirString = configuration.getArchiveDBDir() + server.getHost().getHostname() + configuration.getFileSeparator();
        rrdHostDir = new File(rrdHostDirString);
        
//        Thread rrdCreateThread = new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
                if (!rrdHostDir.exists()) { rrdHostDir.mkdir(); log("Action:  DCMArchiveCreate: Creating missing directory: " + rrdHostDirString, false, true, true); };
                // Generic Properties
                int sampleTime = 60; // in Seconds
                
                // DataSource Properties
                int heartBeat = 120; // Defines the minimum heartbeat, the maximum number of seconds that can go by before a DS value is considered unknown.
                Double minValue = 0D;
                Double maxValue = Double.MAX_VALUE;

                // Archive Properties
                String consulFunction = "AVERAGE"; // AVERAGE, MIN, MAX or LAST.
                Double xff = 0.1; // Defines XFiles Factor, the percentage of data points that can be anally probed by martians before RRD gives a crap.
                int pdp = 1; // PDP Primary Data Points (number of bla bla per sample)
                int rows = 525600; // This makes up retention time (one year in our case)
                int totalArchives = server.getResourceList().size();
                int archiveCounter = 1;
                log("Action:  DCMArchiveCreate: Creating " + totalArchives + " archives for host: " + server.getHost().getHostname() + ". Please wait...", true, true, true);
                for (Resource resource:server.getResourceList())
                {
                    // Create the hostname dir inside the rrdbdir
                    rrdArchiveFileString = rrdHostDirString + resource.getRRDFile();
                    rrdArchiveFile = new File(rrdArchiveFileString);
                    if (! rrdArchiveFile.exists())
                    {
                        log("Action:  DCMArchiveCreate: Creating " + totalArchives + " archives for host: " + server.getHost().getHostname() + ". Please wait... (" + archiveCounter + "/" + totalArchives + ") "  + Math.round(archiveCounter / (totalArchives * 0.01)) + "%", true, false, false);
//                        log("Creating DS " + resource.getResource() + " Counter: " + resource.getCounterType() + " Archive: " + rrdHostDirString + resource.getRRDFile() + " at " + Tools.getHumanDate(server.getHost().getCreated()), true, true, true);
                        
                        // Set Generic Properties 
                        try { rrdDef = new RrdDef(rrdHostDirString + resource.getRRDFile()); } catch (RrdException ex) { inventoryReference.rrdCreateFailureResponse(" Error: RrdException: RRDCreate: new RrdDef(resource.getRRDFile() " + ex.getMessage()); }
                        rrdDef.setStartTime((Long)Calendar.getInstance().getTimeInMillis()/1000);
                        rrdDef.setStep(sampleTime);
                        
                        // Set DataSource Properties                         
                        try { rrdDef.addDatasource(resource.getResource(), resource.getCounterType(), heartBeat, minValue, maxValue); } catch (RrdException ex) { inventoryReference.rrdCreateFailureResponse("Error: RrdException: RRDCreate: addDatasource " + ex.getMessage()); }

                        // Set Archive Properties                         
                        try { rrdDef.addArchive(consulFunction, xff, pdp, rows); } catch (RrdException ex) { inventoryReference.rrdCreateFailureResponse(" Error: RrdException: RRDCreate: addArchive " + ex.getMessage()); }
                        try { rrdDb = new RrdDb(rrdDef); }
                        catch (RrdException ex) { inventoryReference.rrdCreateFailureResponse("Error:  DCMArchiveCreate: RrdException: RRDCreate: new RrdDb(rrdDef) " + ex.getMessage()); }
                        catch (IOException ex) { inventoryReference.rrdCreateFailureResponse("Error:  DCMArchiveCreate: IOException: RRDCreate: new RrdDb(rrdDef) " + ex.getMessage()); }
                        try { rrdDb.close(); } catch (IOException ex) { inventoryReference.rrdCreateFailureResponse(" Error: IOException: RRDCreate: rrdDb.close() " + ex.getMessage()); }                                
                    }
//                    else
//                    {
//                        log("Skipping: Archive: " + rrdHostDirString + resource.getRRDFile() + ": already exists !", true, true, true);                        
//                    }
                    archiveCounter++;
                }
                log("Action:  DCMArchiveCreate: Creating " + totalArchives + " archives for host: " + server.getHost().getHostname() + " finished.", true, true, true);
//            }
//        });
//        rrdCreateThread.setName("rrdCreateThread");
//        rrdCreateThread.setDaemon(false);
//        rrdCreateThread.setPriority(Thread.NORM_PRIORITY);
//        rrdCreateThread.start();        
    }

    @Override
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile)
    {
        inventoryReference.log(messageParam, logToStatus, logApplication, logToFile);
    }
}
