import data.Host;
import data.Resource;
import data.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.Calendar;

public class DCMDataConverterAIX // Being called by InventoryServer and PollServerto convert host & resources txt data to a server object(host,resources[]) and uses CommandsLibrary to get the right polling command for each resource
{
    //Server   server;
    Host     host;
    Resource resource;
    DCMCommandLibraryAIX osCommands;
    int resCnt = 0;
    int pollServerInstance;
    int retentionTime;

    public DCMDataConverterAIX(Host hostParam, int retentionTimeParam) throws UnknownHostException
    {
        host = hostParam;
        osCommands = new DCMCommandLibraryAIX(host);
	retentionTime = retentionTimeParam;
    }
    
    public Server convertInventoryDataToServer(String dataParam) // This method (used by the InventoryServer Object) takes a host and txt data and converts it into a full Server object and returns it
    {
        Server outputServer = new Server();
        String dataHeaders = "";
        BufferedReader reader;
        String line = "";
        String output = "";

        long        id =            0;
        long        hostId =        0;
        String      category =      "";
        String      resourceType =  "";
        String      valueType =     "";
        String      counterType =   "";
        String      resourceName =  "";
        boolean     enabled =       true;
        String      pollCommand =   "";
        Double      lastValue =     0D;
        Double      warningLimit =  0D;
        Double      criticalLimit = 0D;
        int         alertPolls =    30;
        Calendar    created =       Calendar.getInstance(); created.setTimeInMillis(0);
        String      rrdFile =       "";
        
                
// ====================================== Building Server.Host Sysinfo Resources from data ========================================        
        
        outputServer.setHost(host);        
        host.setCommand(osCommands.getPSCPUHostCommand()); // Add a host command
        host.addCommand(osCommands.getPSMEMHostCommand()); // Add a host command
        host.addCommand(osCommands.getCPUHostCommand()); // Set a host command
        host.addCommand(osCommands.getDiskIOHostCommand()); // Add a host command

// ====================================== Building Server MinLoad Resource from data ========================================

//      This section adds minuteload)        
        id =            0;
        hostId =        0;
        category =      "WORKLOAD";
        resourceType =  "MINUTELOAD";
        valueType =     "Factor";
        counterType =   "GAUGE";
        resourceName =  "MINLOAD";
        enabled =       true;
        pollCommand =   "";
        lastValue =     0D;
        warningLimit =  0D;
        criticalLimit = 0D;
        alertPolls =    30;
        created =       Calendar.getInstance(); created.setTimeInMillis(0);
        rrdFile =       "";

        id = resCnt; resCnt++; pollCommand = osCommands.getWorkload(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile, retentionTime);
        outputServer.addResource(resource);
        
// ====================================== Building Server CPU Resources from data ========================================
        
        dataHeaders = "CPU: ";
        reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders)));
        line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                id =            0;
                hostId =        0;
                category =      "CPUEXT";
                resourceType =  "";
                valueType =     "Percentage";
                counterType =   "GAUGE";
                resourceName =  line.replace(dataHeaders, "");
                enabled =       true;
                pollCommand =   "";
                lastValue =     0D;
                warningLimit =  0D;
                criticalLimit = 0D;
                alertPolls =    30;
                created =       Calendar.getInstance(); created.setTimeInMillis(0);
                rrdFile =       "";

                // CPU minf mjf xcal intr ithr csw icsw migr smtx srw  syscl usr sys wt idl                 //solaris
                // cpu  min maj mpc  int  cs   ics rq   mig  lpa  sysc us    sy  wa  id pc  %ec  lcs        //aix
                

                id = resCnt; resCnt++; resourceType = "MinorPageFaults"; valueType = "Faults"; pollCommand = osCommands.getCPUMinorPageFaultsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "MajorPageFaults"; valueType = "Faults"; pollCommand = osCommands.getCPUMajorPageFaultsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "MPCSendInterrupts"; valueType = "Interrupts"; pollCommand = osCommands.getCPUMPCSendInterruptsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "MPCReceiveInterrupts"; valueType = "Interrupts"; pollCommand = osCommands.getCPUMPCReceiveInterruptsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "DeviceInterrupts"; valueType = "Interrupts"; pollCommand = osCommands.getCPUDeviceInterruptsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "SoftInterrupts"; valueType = "Interrupts"; pollCommand = osCommands.getCPUSoftInterruptsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "DecrementerInterrupts"; valueType = "Interrupts"; pollCommand = osCommands.getCPUDecrementerInterruptsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "PhantomInterrupts"; valueType = "Interrupts"; pollCommand = osCommands.getCPUPhantomInterruptsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ContextSwitches"; valueType = "Switches"; pollCommand = osCommands.getCPUContextSwitchesCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadsBound"; valueType = "Threads"; pollCommand = osCommands.getCPUThreadsBoundCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "RunQueueZize"; valueType = "RunQueue"; pollCommand = osCommands.getCPURunQueueZizeCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "MigrationsStarved"; valueType = "Migrations"; pollCommand = osCommands.getCPUMigrationsStarvedCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "MigrationsOutsideD3"; valueType = "Migrations"; pollCommand = osCommands.getCPUMigrationsOutsideD3Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "GlobalRunqueueDispatchesD3"; valueType = "Dispatches"; pollCommand = osCommands.getCPUGlobalRunqueueDispatchesD3Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadRedispatchesD0"; valueType = "Percentage"; pollCommand = osCommands.getCPUThreadRedispatchesD0Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadRedispatchesD1"; valueType = "Percentage"; pollCommand = osCommands.getCPUThreadRedispatchesD1Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadRedispatchesD2"; valueType = "Percentage"; pollCommand = osCommands.getCPUThreadRedispatchesD2Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadRedispatchesD3"; valueType = "Percentage"; pollCommand = osCommands.getCPUThreadRedispatchesD3Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadRedispatchesD4"; valueType = "Percentage"; pollCommand = osCommands.getCPUThreadRedispatchesD4Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "ThreadRedispatchesD5"; valueType = "Percentage"; pollCommand = osCommands.getCPUThreadRedispatchesD5Command(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "SystemCalls"; valueType = "SystemCalls"; pollCommand = osCommands.getCPUSystemCallsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "PhysicalFractionsConsumed"; valueType = "Consumptions"; pollCommand = osCommands.getCPUPhysicalFractionsConsumedCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "EntitledCapacityConsumed"; valueType = "Consumptions"; pollCommand = osCommands.getCPUEntitledCapacityConsumedCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "InvoluntaryLogicalContextSwitches"; valueType = "Switches"; pollCommand = osCommands.getCPUInvoluntaryLogicalContextSwitchesCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "VoluntaryLogicalContextSwitches"; valueType = "Switches"; pollCommand = osCommands.getCPUVoluntaryLogicalContextSwitchesCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "LocalThreadDispatches"; valueType = "Dispatches"; pollCommand = osCommands.getCPULocalThreadDispatchesCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "NearThreadDispatches"; valueType = "Dispatches"; pollCommand = osCommands.getCPUNearThreadDispatchesCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "FarThreadDispatches"; valueType = "Dispatches"; pollCommand = osCommands.getCPUFarThreadDispatchesCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                
                category =      "CPU";

                id = resCnt; resCnt++; resourceType = "USR"; valueType = "Percentage"; pollCommand = osCommands.getCPUUserCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "SYS"; valueType = "Percentage"; pollCommand = osCommands.getCPUSysCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "WIO"; valueType = "Percentage"; pollCommand = osCommands.getCPUWAIOCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "IDL"; valueType = "Percentage"; pollCommand = osCommands.getCPUIDLECommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);
            }

        } catch(IOException e) { e.printStackTrace(); }

////      This section adds 4 extra average CPU stats (static addition)        
//        id =            0;
//        hostId =        0;
//        category =      "CPU";
//        resourceType =  "";
//        valueType =     "Percentage";
//        counterType =   "GAUGE";
//        resourceName =  "all";
//        enabled =       true;
//        pollCommand =   "";
//        lastValue =     0D;
//        warningLimit =  0D;
//        criticalLimit = 0D;
//        alertPolls =    30;
//        created =       Calendar.getInstance(); created.setTimeInMillis(0);
//        rrdFile =       "";
//
//        id = resCnt; resCnt++; resourceType = "CPUUSER"; pollCommand = aixCommands.getALLCPUUSERCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
//        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
//        outputServer.addResource(resource);
//
//        id = resCnt; resCnt++; resourceType = "CPUSYS"; pollCommand = aixCommands.getALLCPUSYSCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
//        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
//        outputServer.addResource(resource);
//
//        id = resCnt; resCnt++; resourceType = "CPUIDLE"; pollCommand = aixCommands.getALLCPUIDLECommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
//        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
//        outputServer.addResource(resource);
//
//        id = resCnt; resCnt++; resourceType = "CPUWAIO"; pollCommand = aixCommands.getALLCPUWIOCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
//        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
//        outputServer.addResource(resource);                    
        
// ====================================== Building Server DiskIO Resources from data ========================================
        
//        dataHeaders = headerKey + "DiskIO: ";
        dataHeaders = "DiskIO: ";
        reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders)));
        line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                id =            0;
                hostId =        0;
                category =      "DiskIO";
                resourceType =  "";
                valueType =     "";
                counterType =   "GAUGE";
                resourceName =  line.replace(dataHeaders, "");
                enabled =       true;
                pollCommand =   "";
                lastValue =     0D;
                warningLimit =  0D;
                criticalLimit = 0D;
                alertPolls =    30;
                created =       Calendar.getInstance(); created.setTimeInMillis(0);
                rrdFile =       "";

                id = resCnt; resCnt++; resourceType = "ReadsPerSecond"; valueType  = "Reads"; pollCommand = osCommands.getDiskIOTimeActiveCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "WritesPerSecond"; valueType  = "Writes"; pollCommand = osCommands.getDiskIOBytesPerSecondCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "KBReadsPerSecond"; valueType  = "KB"; pollCommand = osCommands.getDiskIOTransfersPerSecondCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "KBWritesPerSecond"; valueType  = "KB"; pollCommand = osCommands.getDiskIOKBReadPerSecondCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "TransactionsWaiting"; valueType  = "Transactions"; pollCommand = osCommands.getDiskIOKBWrittenPerSecondCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);
            }

        } catch(IOException e) { e.printStackTrace(); }

// ====================================== Building Server Memory Resources from data ========================================
        
        id =            0;
        hostId =        0;
        category =      "Memory";
        resourceType =  "";
        valueType =     "MB";
        counterType =   "GAUGE";
        resourceName =  line;
        enabled =       true;
        pollCommand =   "";
        lastValue =     0D;
        warningLimit =  0D;
        criticalLimit = 0D;
        alertPolls =    30;
        created =       Calendar.getInstance(); created.setTimeInMillis(0);
        rrdFile =       "";

        id = resCnt; resCnt++; resourceType = "RAMTOT"; resourceName =  "RAMTOT"; pollCommand = osCommands.getRAMTOTCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "RAMUSED"; resourceName =  "RAMUSED"; pollCommand = osCommands.getRAMUSEDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "RAMFREE"; resourceName =  "RAMFREE"; pollCommand = osCommands.getRAMFREECommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "SWAPTOT"; resourceName =  "SWAPTOT"; pollCommand = osCommands.getSWAPTOTCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "SWAPUSED"; resourceName =  "SWAPUSED"; pollCommand = osCommands.getSWAPUSEDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "SWAPFREE"; resourceName =  "SWAPFREE"; pollCommand = osCommands.getSWAPFREECommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "TOTMEM"; resourceName =  "TOTMEM"; pollCommand = osCommands.getTOTMEMCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "TOTUSED"; resourceName =  "TOTUSED"; pollCommand = osCommands.getTOTUSEDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "TOTFREE"; resourceName =  "TOTFREE"; pollCommand = osCommands.getTOTFREECommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

// ====================================== Building Server Storage Resources from data ========================================
        
//        dataHeaders = headerKey + "Storage: ";
        dataHeaders = "Storage: ";
        reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders))); // This only gets the "Storage: " filesystems lines from the data and creates 4 resources for every FS
        line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                id =            0;
                hostId =        0;
                category =      "Storage";
                resourceType =  "";
                valueType =     "MB";
                counterType =   "GAUGE";
                resourceName =  line.replace(dataHeaders, "");
                enabled =       true;
                pollCommand =   "";
                lastValue =     0D;
                warningLimit =  0D;
                criticalLimit = 0D;
                alertPolls =    30;
                created =       Calendar.getInstance(); created.setTimeInMillis(0);
                rrdFile =       "";

                id = resCnt; resCnt++; resourceType = "FSTot"; valueType  = "MB"; pollCommand = osCommands.getFSTOTCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                if (resourceName.length() <= 20) {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                else {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName.substring(resourceName.length()-20, resourceName.length()),true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                outputServer.addResource(resource);
                
                id = resCnt; resCnt++; resourceType = "FSUsed"; valueType = "MB"; pollCommand = osCommands.getFSUSEDCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                if (resourceName.length() <= 20) {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                else {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName.substring(resourceName.length()-20, resourceName.length()),true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "FSFree"; valueType = "MB"; pollCommand = osCommands.getFSFREECommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                if (resourceName.length() <= 20) {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                else {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName.substring(resourceName.length()-20, resourceName.length()),true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "FSUsedPerc"; valueType = "Percentage"; pollCommand = osCommands.getFSUSEDPercCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                if (resourceName.length() <= 20) {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                else {resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName.substring(resourceName.length()-20, resourceName.length()),true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);}
                outputServer.addResource(resource);
            }

        } catch(IOException e) { e.printStackTrace(); }
        
// ====================================== Building Server Network Resources from data ========================================
        
//        dataHeaders = headerKey + "Network: ";        
        dataHeaders = "Network: ";        
        reader = new BufferedReader(new StringReader(DCMTools.startsWith(dataParam, dataHeaders)));
        line = "";
        output = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                id =            0;
                hostId =        0;
                category =      "Network";
                resourceType =  "";
                valueType =     "";
                counterType =   "COUNTER";
                resourceName =  line.replace(dataHeaders, "");
                enabled =       true;
                pollCommand =   "";
                lastValue =     0D;
                warningLimit =  0D;
                criticalLimit = 0D;
                alertPolls =    30;
                created =       Calendar.getInstance(); created.setTimeInMillis(0);
                rrdFile =       "";

                id = resCnt; resCnt++; resourceType = "IFINPackets"; valueType  = "Packets"; pollCommand = osCommands.getIFTINPacketsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "IFINErr"; valueType = "Errors"; pollCommand = osCommands.getIFINERRCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "IFOUTPackets"; valueType = "Packets"; pollCommand = osCommands.getIFOUTPacketsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "IFOUTErr"; valueType = "Errors"; pollCommand = osCommands.getIFOUTERRCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);

                id = resCnt; resCnt++; resourceType = "IFCollisions"; valueType = "Collisions"; pollCommand = osCommands.getIFCollisionsCommand(resourceName); rrdFile = category + "_" + resourceType + "_" + resourceName.replace("/", "-") + ".rrd";
                resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
                outputServer.addResource(resource);
            }

        } catch(IOException e) { e.printStackTrace(); }
        
// ====================================== Building Server TCPSTATS Resources from data ========================================
        
        id =            0;
        hostId =        0;
        category =      "TCPSTATS";
        resourceType =  "";
        valueType =     "NumOf";
        counterType =   "GAUGE";
        resourceName =  line;
        enabled =       true;
        pollCommand =   "";
        lastValue =     0D;
        warningLimit =  0D;
        criticalLimit = 0D;
        alertPolls =    30;
        created =       Calendar.getInstance(); created.setTimeInMillis(0);
        rrdFile =       "";

        id = resCnt; resCnt++; resourceType = "BOUND"; resourceName =  "BOUND"; pollCommand = osCommands.getTCPSTATBOUNDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "CLOSED"; resourceName =  "CLOSED"; pollCommand = osCommands.getTCPSTATCLOSEDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "CLOSING"; resourceName =  "CLOSING"; pollCommand = osCommands.getTCPSTATCLOSINGCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "CLOSE_WAIT"; resourceName =  "CLOSE_WAIT"; pollCommand = osCommands.getTCPSTATCLOSE_WAITCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "ESTABLISHED"; resourceName =  "ESTABLISHED"; pollCommand = osCommands.getTCPSTATESTABLISHEDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "FIN_WAIT1"; resourceName =  "FIN_WAIT1"; pollCommand = osCommands.getTCPSTATFIN_WAIT_1Command(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "FIN_WAIT2"; resourceName =  "FIN_WAIT2"; pollCommand = osCommands.getTCPSTATFIN_WAIT_2Command(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "IDLE"; resourceName =  "IDLE"; pollCommand = osCommands.getTCPSTATIDLECommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);
        
        id = resCnt; resCnt++; resourceType = "LAST_ACK"; resourceName =  "LAST_ACK"; pollCommand = osCommands.getTCPSTATLAST_ACKCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "LISTEN"; resourceName =  "LISTEN"; pollCommand = osCommands.getTCPSTATLISTENCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "SYN_RECV"; resourceName =  "SYN_RECV"; pollCommand = osCommands.getTCPSTATSYN_RECEIVEDCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);        
        
        id = resCnt; resCnt++; resourceType = "SYN_SENT"; resourceName =  "SYN_SENT"; pollCommand = osCommands.getTCPSTATSYN_SENTCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "TIME_WAIT"; resourceName =  "TIME_WAIT"; pollCommand = osCommands.getTCPSTATTIME_WAITCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

// ====================================== Building Server Generic System Resources from data ========================================
        
        id =            0;
        hostId =        0;
        category =      "GENERIC";
        resourceType =  "";
        valueType =     "";
        counterType =   "GAUGE";
        resourceName =  line;
        enabled =       true;
        pollCommand =   "";
        lastValue =     0D;
        warningLimit =  0D;
        criticalLimit = 0D;
        alertPolls =    30;
        created =       Calendar.getInstance(); created.setTimeInMillis(0);
        rrdFile =       "";

        id = resCnt; resCnt++; resourceType = "NUMOFUSERS"; resourceName =  "NUMOFUSERS"; valueType = "Users"; pollCommand = osCommands.getNUMOFUSERSCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "NUMOFPROCS"; resourceName =  "NUMOFPROCS"; valueType = "Processes"; pollCommand = osCommands.getNUMOFPROCSCommand(host); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        // ====================================== Building Server Process Resources from data ========================================
        
        id =            0;
        hostId =        0;
        category =      "PROCESS";
        resourceType =  "";
        valueType =     "";
        counterType =   "GAUGE";
        resourceName =  line;
        enabled =       true;
        pollCommand =   "";
        lastValue =     0D;
        warningLimit =  0D;
        criticalLimit = 0D;
        alertPolls =    30;
        created =       Calendar.getInstance(); created.setTimeInMillis(0);
        rrdFile =       "";

        id = resCnt; resCnt++; resourceType = "PSCPUPID"; resourceName =  "PS1CPUPID"; valueType = "PID"; pollCommand = osCommands.getPS1CPUPIDCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSCPU"; resourceName =  "PS1CPU"; valueType = "Percentage"; pollCommand = osCommands.getPS1CPUCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSCPUPID"; resourceName =  "PS2CPUPID"; valueType = "PID"; pollCommand = osCommands.getPS2CPUPIDCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSCPU"; resourceName =  "PS2CPU"; valueType = "Percentage"; pollCommand = osCommands.getPS2CPUCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSCPUPID"; resourceName =  "PS3CPUPID"; valueType = "PID"; pollCommand = osCommands.getPS3CPUPIDCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSCPU"; resourceName =  "PS3CPU"; valueType = "Percentage"; pollCommand = osCommands.getPS3CPUCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);
        
        id = resCnt; resCnt++; resourceType = "PSMEMPID"; resourceName =  "PS1MEMPID"; valueType = "PID"; pollCommand = osCommands.getPS1MEMPIDCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSMEM"; resourceName =  "PS1MEM"; valueType = "Percentage"; pollCommand = osCommands.getPS1MEMCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSMEMPID"; resourceName =  "PS2MEMPID"; valueType = "PID"; pollCommand = osCommands.getPS2MEMPIDCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSMEM"; resourceName =  "PS2MEM"; valueType = "Percentage"; pollCommand = osCommands.getPS2MEMCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSMEMPID"; resourceName =  "PS3MEMPID"; valueType = "PID"; pollCommand = osCommands.getPS3MEMPIDCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        id = resCnt; resCnt++; resourceType = "PSMEM"; resourceName =  "PS3MEM"; valueType = "Percentage"; pollCommand = osCommands.getPS3MEMCommand(); rrdFile = category + "_" + resourceType + "_" + resourceName + ".rrd";
        resource = new Resource(id,hostId,category,resourceType,valueType,counterType,resourceName,true,pollCommand,lastValue,warningLimit,criticalLimit,alertPolls,created,rrdFile,retentionTime);
        outputServer.addResource(resource);

        return outputServer;
    }
}
