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
import org.jrobin.core.Sample;

public class DCMArchiveUpdate implements ConfigurationCaller
{
    private DCMPollServer pollerReference;
    private RrdDef rrdDef;
    private RrdDb rrdDb;
    private ArrayList<Resource> resourcesList;
    private Resource resource;
    private Configuration configuration;
    private String rrdHostDirString;
    private File rrdHostDir;
    private String rrdArchiveFileString;
    private File rrdArchiveFile;
    public DCMArchiveUpdate(DCMPollServer pollerReferenceParam, final Server server)
    {
        configuration = new Configuration(this);
        pollerReference = pollerReferenceParam;
        rrdHostDirString = configuration.getArchiveDBDir() + server.getHost().getHostname() + configuration.getFileSeparator();
        rrdHostDir = new File(rrdHostDirString);
        Thread rrdUpdateThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (!rrdHostDir.exists()) { rrdHostDir.mkdir(); log("Warning: Creating missing directory: " + rrdHostDirString, false, true, true); };
                for (Resource resource:server.getResourceList())
                {
                    // Create the hostname dir inside the rrdbdir
                    rrdArchiveFileString = rrdHostDirString + resource.getRRDFile();
                    rrdArchiveFile = new File(rrdArchiveFileString);
                    if (rrdArchiveFile.exists())
                    {
                        //log("Updating RRD Archive: " + rrdHostDirString + resource.getRRDFile(), true, true, true);
                        try { rrdDb = new RrdDb(rrdArchiveFileString); }
                        catch (RrdException ex) { pollerReference.rrdUpdateFailureResponse("Error:  DCMArchiveUpdate: RrdException: RRDUpdate: new RrdDb(rrdArchiveFileString) " + ex.getMessage()); }
                        catch (IOException ex) { pollerReference.rrdUpdateFailureResponse("Error:  DCMArchiveUpdate: IOException: RRDUpdate: new RrdDb(rrdArchiveFileString) " + ex.getMessage()); }
                        
                        Sample sample = null; try { sample = rrdDb.createSample(); }
                        catch (IOException ex) { pollerReference.rrdUpdateFailureResponse("Error:  DCMArchiveUpdate: IOException: RRDUpdate: rrdDb.createSample() " + ex.getMessage()); }
                        
                        sample.setTime((Long)(Calendar.getInstance().getTimeInMillis()/1000));
//                        sample.setTime(Calendar.getInstance().getTimeInMillis());
                        
                        try { sample.setValue(0, resource.getLastValue());}
                        catch (RrdException ex) { pollerReference.rrdUpdateFailureResponse("Error:  DCMArchiveUpdate: RrdException: RRDUpdate: sample.setValue(..) " + ex.getMessage()); }
                        
//                        try { sample.setValue(resource.getResource(), resource.getLastValue());}
//                        catch (RrdException ex) { pollerReference.rrdUpdateFailureResponse(" Error: RrdException: RRDUpdate: sample.setValue(..) " + ex.getMessage()); }
//                        
                        try { sample.update(); }
                        catch (IOException ex) { pollerReference.rrdUpdateFailureResponse("Error:  DCMArchiveUpdate: IOException: RRDUpdate: sample.update() " + ex.getMessage()); }
                        catch (RrdException ex) { pollerReference.rrdUpdateFailureResponse("Error:  DCMArchiveUpdate: RrdException: RRDUpdate: sample.update() " + ex.getMessage()); }
                        
                        try { rrdDb.close(); } catch (IOException ex) { pollerReference.rrdUpdateFailureResponse(" Error: IOException: RRDUpdate: rrdDb.close() " + ex.getMessage()); }                                
                    }
                    else
                    {
                        log("Action:  DCMArchiveUpdate: Skipping: RRD Archive: " + rrdHostDirString + resource.getRRDFile() + ": does not exists !", true, true, true);
                    }

                }
                log("Status:  DCMArchiveUpdate: Finished updating RRD Archives for host: " + server.getHost().getHostname(), true, true, true);
            }
        });
        rrdUpdateThread.setName("rrdUpdateThread");
        rrdUpdateThread.setDaemon(false);
        rrdUpdateThread.setPriority(Thread.NORM_PRIORITY);
        rrdUpdateThread.start();        
    }

    @Override
    public void log(String messageParam, boolean logToStatus, boolean logApplication, boolean logToFile)
    {
        pollerReference.log(messageParam, true, true, true);
    }
}
