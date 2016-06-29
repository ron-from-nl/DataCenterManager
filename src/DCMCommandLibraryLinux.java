import data.Host;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DCMCommandLibraryLinux
{
    private Host host;
    private String command =         "";
    private InetAddress inetAddress;
    private String dcmServerIP;
    private String mpstatDataFile;
    private String iostatDataFile;
    private String psCPUFile;
    private String psMEMFile;

    private final String OS =        "Linux";

    private final int AWK =          0;
    private final int BC =           1;
    private final int DF =           2;
    private final int ECHO =         3;
    private final int EGREP =        4;
    private final int FREE =         5;
    private final int GREP =         6;
    private final int HEAD =         7;
    private final int IOSTAT =       8;
    private final int MPSTAT =       9;
    private final int NETSTAT =      10;
    private final int PS =           11;
    private final int SED =          12;
    private final int SORT =         13;
    private final int TAIL =         14;
    private final int TR =           15;
    private final int W =            16;
    private final int WC =           17;
    private final int WHO =          18;
    private static String[][] cmdArray;

    public DCMCommandLibraryLinux(Host hostParam) throws UnknownHostException
    {
        host = hostParam;
        
        try { inetAddress =         InetAddress.getLocalHost(); } catch (UnknownHostException ex) {  }
        dcmServerIP =               inetAddress.getHostAddress();

        mpstatDataFile = ".dcmmpstat_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        iostatDataFile = ".dcmiostat_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psCPUFile = ".dcmpscpu_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psMEMFile = ".dcmpsmem_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        
        cmdArray = new String[19][4];
        cmdArray[AWK][0] = Integer.toString(AWK);           cmdArray[AWK][1] =      "awk"; cmdArray[AWK][2] =           OS + " install media"; cmdArray[AWK][3] =                       "binary " + cmdArray[AWK][1] + " not found, please set PATH or install from " + cmdArray[AWK][2];
        cmdArray[BC][0] = Integer.toString(BC);             cmdArray[BC][1] =       "bc"; cmdArray[BC][2] =             OS + " install media"; cmdArray[BC][3] =                        "binary " + cmdArray[BC][1] + " not found, please set PATH or install from " + cmdArray[BC][2];
        cmdArray[DF][0] = Integer.toString(DF);             cmdArray[DF][1] =       "df"; cmdArray[DF][2] =             OS + " install media"; cmdArray[DF][3] =                        "binary " + cmdArray[DF][1] + " not found, please set PATH or install from " + cmdArray[DF][2];
        cmdArray[ECHO][0] = Integer.toString(ECHO);         cmdArray[ECHO][1] =     "echo"; cmdArray[ECHO][2] =         OS + " install media"; cmdArray[ECHO][3] =                      "binary " + cmdArray[ECHO][1] + " not found, please set PATH or install from " + cmdArray[ECHO][2];
        cmdArray[EGREP][0] = Integer.toString(EGREP);       cmdArray[EGREP][1] =    "egrep"; cmdArray[EGREP][2] =       OS + " install media"; cmdArray[EGREP][3] =                     "binary " + cmdArray[EGREP][1] + " not found, please set PATH or install from " + cmdArray[EGREP][2];
        cmdArray[FREE][0] = Integer.toString(FREE);         cmdArray[FREE][1] =     "free"; cmdArray[FREE][2] =         OS + " install media"; cmdArray[FREE][3] =                      "binary " + cmdArray[FREE][1] + " not found, please set PATH or install from " + cmdArray[FREE][2];
        cmdArray[GREP][0] = Integer.toString(GREP);         cmdArray[GREP][1] =     "grep"; cmdArray[GREP][2] =         OS + " install media"; cmdArray[GREP][3] =                      "binary " + cmdArray[GREP][1] + " not found, please set PATH or install from " + cmdArray[GREP][2];
        cmdArray[HEAD][0] = Integer.toString(HEAD);         cmdArray[HEAD][1] =     "head"; cmdArray[HEAD][2] =         OS + " install media"; cmdArray[HEAD][3] =                      "binary " + cmdArray[HEAD][1] + " not found, please set PATH or install from " + cmdArray[HEAD][2];
        cmdArray[IOSTAT][0] = Integer.toString(IOSTAT);     cmdArray[IOSTAT][1] =   "iostat"; cmdArray[IOSTAT][2] =     OS + " install media sysstat package"; cmdArray[IOSTAT][3] =    "binary " + cmdArray[IOSTAT][1] + " not found, please set PATH or install from " + cmdArray[IOSTAT][2];
        cmdArray[MPSTAT][0] = Integer.toString(MPSTAT);     cmdArray[MPSTAT][1] =   "mpstat"; cmdArray[MPSTAT][2] =     OS + " install media sysstat package"; cmdArray[MPSTAT][3] =    "binary " + cmdArray[MPSTAT][1] + " not found, please set PATH or install from " + cmdArray[MPSTAT][2];
        cmdArray[NETSTAT][0] = Integer.toString(NETSTAT);   cmdArray[NETSTAT][1] =  "netstat"; cmdArray[NETSTAT][2] =   OS + " install media"; cmdArray[NETSTAT][3] =                   "binary " + cmdArray[NETSTAT][1] + " not found, please set PATH or install from " + cmdArray[NETSTAT][2];
        cmdArray[PS][0] = Integer.toString(PS);             cmdArray[PS][1] =       "ps"; cmdArray[PS][2] =             OS + " install media"; cmdArray[PS][3] =                        "binary " + cmdArray[PS][1] + " not found, please set PATH or install from " + cmdArray[PS][2];
        cmdArray[SED][0] = Integer.toString(SED);           cmdArray[SED][1] =      "sed"; cmdArray[SED][2] =           OS + " install media"; cmdArray[SED][3] =                       "binary " + cmdArray[SED][1] + " not found, please set PATH or install from " + cmdArray[SED][2];
        cmdArray[SORT][0] = Integer.toString(SORT);         cmdArray[SORT][1] =     "sort"; cmdArray[SORT][2] =         OS + " install media"; cmdArray[SORT][3] =                      "binary " + cmdArray[SORT][1] + " not found, please set PATH or install from " + cmdArray[SORT][2];
        cmdArray[TAIL][0] = Integer.toString(TAIL);         cmdArray[TAIL][1] =     "tail"; cmdArray[TAIL][2] =         OS + " install media"; cmdArray[TAIL][3] =                      "binary " + cmdArray[TAIL][1] + " not found, please set PATH or install from " + cmdArray[TAIL][2];
        cmdArray[TR][0] = Integer.toString(TR);             cmdArray[TR][1] =       "tr"; cmdArray[TR][2] =             OS + " install media"; cmdArray[TR][3] =                        "binary " + cmdArray[TR][1] + " not found, please set PATH or install from " + cmdArray[TR][2];
        cmdArray[W][0] = Integer.toString(W);               cmdArray[W][1] =        "w"; cmdArray[W][2] =               OS + " install media"; cmdArray[W][3] =                         "binary " + cmdArray[W][1] + " not found, please set PATH or install from " + cmdArray[W][2];
        cmdArray[WC][0] = Integer.toString(WC);             cmdArray[WC][1] =       "wc"; cmdArray[WC][2] =             OS + " install media"; cmdArray[WC][3] =                        "binary " + cmdArray[WC][1] + " not found, please set PATH or install from " + cmdArray[WC][2];
        cmdArray[WHO][0] = Integer.toString(WHO);           cmdArray[WHO][1] =      "who"; cmdArray[WHO][2] =           OS + " install media"; cmdArray[WHO][3] =                       "binary " + cmdArray[WHO][1] + " not found, please set PATH or install from " + cmdArray[WHO][2];
    }
    
//  PS
    
    public String getPSCPUHostCommand()
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[PS][1] + " -e -o pid,pcpu,comm | " + cmdArray[GREP][1] + " -iv \"pid\" | " + cmdArray[SORT][1] + " -k 2nr > " + psCPUFile + " \n"; } return command;
    }
    
    public String getPSMEMHostCommand()
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[PS][1] + " -e -o pid,pmem,comm | " + cmdArray[GREP][1] + " -iv \"pid\" | " + cmdArray[SORT][1] + " -k 2nr > " + psMEMFile + " \n"; } return command;
    }
    
//  CPU
    // mpstat -P ALL 1 1 | " + cmdArray[TAIL][1] + " -`" + cmdArray[MPSTAT][1] + " -P ALL | " + cmdArray[EGREP][1] + " -vie "^$|CPU" | " + cmdArray[WC][1] + " -l`

    public String getCPUHostCommand() // This a HOST Command
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -P ALL 50 1 | " + cmdArray[TAIL][1] + " -`" + cmdArray[MPSTAT][1] + " -P ALL | " + cmdArray[EGREP][1] + " -vie \"^$|CPU\" | " + cmdArray[WC][1] + " -l` > " + mpstatDataFile + " & \n"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -P ALL 50 1 > " + mpstatDataFile + " & \n"; } return command;
    }
    
    public String getCPUUSERCommand(String resourceParam)// usr nice sys idle
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -P " + resourceParam.toUpperCase() + " 50 1 | " + cmdArray[EGREP][1] + " -v \"CPU|^$\" | " + cmdArray[EGREP][1] + " -ie \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[AWK][1] + " -F\"[.,]\" '{ print $1\".\"$2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+:\\s+" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[HEAD][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSYSCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -P " + resourceParam.toUpperCase() + " 50 1 | " + cmdArray[EGREP][1] + " -v \"CPU|^$\" | " + cmdArray[EGREP][1] + " -ie \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[AWK][1] + " -F\"[.,]\" '{ print $1\".\"$2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+:\\s+" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[HEAD][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUIDLECommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -P " + resourceParam.toUpperCase() + " 50 1 | " + cmdArray[EGREP][1] + " -v \"CPU|^$\" | " + cmdArray[EGREP][1] + " -ie \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[AWK][1] + " -F\"[.,]\" '{ print $1\".\"$2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+:\\s+" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[HEAD][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUWIOCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -P " + resourceParam.toUpperCase() + " 50 1 | " + cmdArray[EGREP][1] + " -v \"CPU|^$\" | " + cmdArray[EGREP][1] + " -ie \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[AWK][1] + " -F\"[.,]\" '{ print $1\".\"$2 }'"; }  return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+:\\s+" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[HEAD][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }  return command;
    }

//  WORKLOAD
    public String getWorkload()
    {
        if (host.getSysinfo().contains(OS))   { command = cmdArray[W][1] + " | " + cmdArray[GREP][1] + " -i \"average\" | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        return command;
    }    
    
//  DiskIO
        
    public String getDiskIOHostCommand() // This a HOST Command
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x -d 50 2 | " + cmdArray[EGREP][1] + " -vie \"^$|Device|Linux\" | " + cmdArray[TAIL][1] + " -`iostat -x -d | " + cmdArray[EGREP][1] + " -vie \"^$|Device|Linux\" | " + cmdArray[WC][1] + " -l` > " + iostatDataFile + " & \n"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x -d 50 2 > " + iostatDataFile + " & \n"; } return command;
    }
    
    public String getDiskIOReadsPerSecondQuedCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOWritesPerSecondQuedCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOReadPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOWritesPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOKBReadPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOKBWritesPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $7 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $7 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOAverageTranscationSectorsCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOAverageQueueLengthCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $9 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOAverageTranscationResponseTimeMiliSecondsCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOAverageTranscationServiceTimeMiliSecondsCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $11 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOTransactionCPUUtilizationPercentageCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -xk " + resourceParam + " 1 2 | " + cmdArray[GREP][1] + " " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $12 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $12 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    
//  Memory 
    
    public String getRAMTOTCommand(Host hostParam) 
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[FREE][1] + " -m | " + cmdArray[GREP][1] + " Mem | " + cmdArray[AWK][1] + " '{ print $2 }'"; }
        return command;
    }
    public String getRAMUSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[FREE][1] + " -m | " + cmdArray[GREP][1] + " 'buffers/cache' | " + cmdArray[AWK][1] + " '{ print $3 }'"; }
        return command;
    }
    public String getRAMFREECommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[FREE][1] + " -m | " + cmdArray[GREP][1] + " 'buffers/cache' | " + cmdArray[AWK][1] + " '{ print $4 }'"; }
        return command;
    }
    public String getSWAPTOTCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[FREE][1] + " -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $2 }'"; }
        return command;
    }
    public String getSWAPUSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[FREE][1] + " -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $3 }'"; }
        return command;
    }
    public String getSWAPFREECommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[FREE][1] + " -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $4 }'"; }
        return command;
    }
    public String getTOTMEMCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`free -m | " + cmdArray[GREP][1] + " Mem | " + cmdArray[AWK][1] + " '{ print $2 }'`; swaptot=`free -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $2 }'` totmem=$((ramtot + swaptot)); " + cmdArray[ECHO][1] + " $totmem"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`free -m | " + cmdArray[GREP][1] + " Mem | " + cmdArray[AWK][1] + " '{ print $2 }'`; swaptot=`free -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $2 }'` totmem=`" + cmdArray[ECHO][1] + " \"$ramtot + $swaptot\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totmem"; }
        return command;
    }
    public String getTOTUSEDCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramused=`free -m | " + cmdArray[GREP][1] + " \"buffers/cache\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; swapused=`free -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $3 }'`; totused=$(( ramused + swapused )); " + cmdArray[ECHO][1] + " $totused"; }
        if (host.getSysinfo().contains(OS)) { command = "ramused=`free -m | " + cmdArray[GREP][1] + " \"buffers/cache\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; swapused=`free -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $3 }'`; totused=`" + cmdArray[ECHO][1] + " \"$ramused + $swapused\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totused"; }
        return command;
    }
    public String getTOTFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramfree=`free -m | " + cmdArray[GREP][1] + " \"buffers/cache\" | " + cmdArray[AWK][1] + " '{ print $4 }'`; swapfree=`free -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $4 }'` totfree=$((ramfree + swapfree)); " + cmdArray[ECHO][1] + " $totfree"; }
        if (host.getSysinfo().contains(OS)) { command = "ramfree=`free -m | " + cmdArray[GREP][1] + " \"buffers/cache\" | " + cmdArray[AWK][1] + " '{ print $4 }'`; swapfree=`free -m | " + cmdArray[GREP][1] + " Swap | " + cmdArray[AWK][1] + " '{ print $4 }'` totfree=`" + cmdArray[ECHO][1] + " \"$ramfree + $swapfree\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totfree"; }
        return command;
    }

//  Storage
    
    public String getFSTOTCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -mP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSUSEDCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -mP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSFREECommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -mP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSUSEDPercCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -mP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[TR][1] + " -d \"%\""; }
        return command;
    }

//  Network ethtool requires superuer unfortunately
    //Iface   MTU Met    RX-OK RX-ERR RX-DRP RX-OVR    TX-OK TX-ERR TX-DRP TX-OVR Flg

    public String getIF_RX_OK_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " tx_bytes | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $4 }'"; } return command;
    }
    public String getIF_RX_ERR_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " rx_bytes | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $5 }'"; } return command;
    }
    public String getIF_RX_Drop_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " tx_errors_total | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $6 }'"; } return command;
    }
    public String getIF_RX_OVR_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " rx_errors_total | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $7 }'"; } return command;
    }
    public String getIF_TX_OK_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " tx_bytes | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $8 }'"; } return command;
    }
    public String getIF_TX_ERR_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " rx_bytes | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $9 }'"; } return command;
    }
    public String getIF_TX_Drop_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " tx_errors_total | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $10 }'"; } return command;
    }
    public String getIF_TX_OVR_Command(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "/sbin/ethtool -S " + resourceParam + " | " + cmdArray[GREP][1] + " rx_errors_total | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $11 }'"; } return command;
    }

//  Network
    
    public String getTCPSTATESTABLISHEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+ESTABLISHED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATSYN_SENTCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+SYN_SENT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATSYN_RECVCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+SYN_RECV\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATFIN_WAIT1Command(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+FIN_WAIT1\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATFIN_WAIT2Command(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+FIN_WAIT2\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATTIME_WAITCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+TIME_WAIT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+CLOSED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSE_WAITCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+CLOSE_WAIT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATLAST_ACKCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+LAST_ACK\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATLISTENCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+LISTEN\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSINGCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+CLOSING\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATUNKNOWNCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"tcp.+UNKNOWN\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }

//  Generic
    
    public String getNUMOFUSERSCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[WHO][1] + " | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getNUMOFPROCSCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[PS][1] + " -e | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }

//  PS

    public String getPS1CPUPIDCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -1 " + psCPUFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS1CPUCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -1 " + psCPUFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS2CPUPIDCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -2 " + psCPUFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS2CPUCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -2 " + psCPUFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS3CPUPIDCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -3 " + psCPUFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS3CPUCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -3 " + psCPUFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS1MEMPIDCommand()// usr nice sys idle
    {//head -2 rontmp1 | tail -1 | awk '{ print $2 }'
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -1 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS1MEMCommand()// usr nice sys idle
    {//head -2 rontmp1 | tail -1 | awk '{ print $2 }'
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -1 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS2MEMPIDCommand()// usr nice sys idle
    {//head -2 rontmp1 | tail -1 | awk '{ print $2 }'
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -2 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS2MEMCommand()// usr nice sys idle
    {//head -2 rontmp1 | tail -1 | awk '{ print $2 }'
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -2 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS3MEMPIDCommand()// usr nice sys idle
    {//head -2 rontmp1 | tail -1 | awk '{ print $2 }'
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -3 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS3MEMCommand()// usr nice sys idle
    {//head -2 rontmp1 | tail -1 | awk '{ print $2 }'
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -3 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String[][] getCommandArray() { return cmdArray; }
}

