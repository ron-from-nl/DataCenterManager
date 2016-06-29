import data.Host;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DCMCommandLibrarySunOS
{
    private Host host;
    private String command =         "";
    private InetAddress inetAddress;
    private String dcmServerIP;
    private String mpstatDataFile;
    private String iostatDataFile;
    private String psCPUFile;
    private String psMEMFile;

    private final String OS =        "SunOS";
    
    private final int AWK =          0;
    private final int BC =           1;
    private final int DF =           2;
    private final int ECHO =         3;
    private final int EGREP =        4;
    private final int GREP =         5;
    private final int HEAD =         6;
    private final int IOSTAT =       7;
    private final int MPSTAT =       8;
    private final int NETSTAT =      9;
    private final int PRTCONF =      10;
    private final int PS =           11;
    private final int SED =          12;
    private final int SORT =         13;
    private final int SWAP =         14;
    private final int TAIL =         15;
    private final int TR =           16;
    private final int VMSTAT =       17;
    private final int W =            18;
    private final int WC =           19;
    private final int WHO =          20;
    private static String[][] cmdArray;
    
    public DCMCommandLibrarySunOS(Host hostParam) throws UnknownHostException
    {
        host = hostParam;
        
        try { inetAddress =         InetAddress.getLocalHost(); } catch (UnknownHostException ex) {  }
        dcmServerIP =               inetAddress.getHostAddress();

        mpstatDataFile = ".dcmmpstat_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        iostatDataFile = ".dcmiostat_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psCPUFile = ".dcmpscpu_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psMEMFile = ".dcmpsmem_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        
        cmdArray = new String[21][4]; // Id, command, problem, advise
        cmdArray[AWK][0] = Integer.toString(AWK);           cmdArray[AWK][1] =      "awk"; cmdArray[AWK][2] =                   OS + " install media"; cmdArray[AWK][3] =                       "binary " + cmdArray[AWK][1] + " not found, please set PATH or install from " + cmdArray[AWK][2];
        cmdArray[BC][0] = Integer.toString(BC);             cmdArray[BC][1] =       "bc"; cmdArray[BC][2] =                     OS + " install media"; cmdArray[BC][3] =                        "binary " + cmdArray[BC][1] + " not found, please set PATH or install from " + cmdArray[BC][2];
        cmdArray[DF][0] = Integer.toString(DF);             cmdArray[DF][1] =       "df"; cmdArray[DF][2] =                     OS + " install media"; cmdArray[DF][3] =                        "binary " + cmdArray[DF][1] + " not found, please set PATH or install from " + cmdArray[DF][2];
        cmdArray[ECHO][0] = Integer.toString(ECHO);         cmdArray[ECHO][1] =     "echo"; cmdArray[ECHO][2] =                 OS + " install media"; cmdArray[ECHO][3] =                      "binary " + cmdArray[ECHO][1] + " not found, please set PATH or install from " + cmdArray[ECHO][2];
        cmdArray[EGREP][0] = Integer.toString(EGREP);       cmdArray[EGREP][1] =    "egrep"; cmdArray[EGREP][2] =               OS + " install media"; cmdArray[EGREP][3] =                     "binary " + cmdArray[EGREP][1] + " not found, please set PATH or install from " + cmdArray[EGREP][2];
        cmdArray[GREP][0] = Integer.toString(GREP);         cmdArray[GREP][1] =     "grep"; cmdArray[GREP][2] =                 OS + " install media"; cmdArray[GREP][3] =                      "binary " + cmdArray[GREP][1] + " not found, please set PATH or install from " + cmdArray[GREP][2];
        cmdArray[HEAD][0] = Integer.toString(HEAD);         cmdArray[HEAD][1] =     "head"; cmdArray[HEAD][2] =                 OS + " install media"; cmdArray[HEAD][3] =                      "binary " + cmdArray[HEAD][1] + " not found, please set PATH or install from " + cmdArray[HEAD][2];
        cmdArray[IOSTAT][0] = Integer.toString(IOSTAT);     cmdArray[IOSTAT][1] =   "iostat"; cmdArray[IOSTAT][2] =             OS + " install media sysstat package"; cmdArray[IOSTAT][3] =    "binary " + cmdArray[IOSTAT][1] + " not found, please set PATH or install from " + cmdArray[IOSTAT][2];
        cmdArray[MPSTAT][0] = Integer.toString(MPSTAT);     cmdArray[MPSTAT][1] =   "mpstat"; cmdArray[MPSTAT][2] =             OS + " install media sysstat package"; cmdArray[MPSTAT][3] =    "binary " + cmdArray[MPSTAT][1] + " not found, please set PATH or install from " + cmdArray[MPSTAT][2];
        cmdArray[NETSTAT][0] = Integer.toString(NETSTAT);   cmdArray[NETSTAT][1] =  "netstat"; cmdArray[NETSTAT][2] =           OS + " install media"; cmdArray[NETSTAT][3] =                   "binary " + cmdArray[NETSTAT][1] + " not found, please set PATH or install from " + cmdArray[NETSTAT][2];
        cmdArray[PRTCONF][0] = Integer.toString(PRTCONF);   cmdArray[PRTCONF][1] =  "/usr/sbin/prtconf"; cmdArray[PRTCONF][2] = OS + " install media"; cmdArray[PRTCONF][3] =                   "binary " + cmdArray[PRTCONF][1] + " not found, please set PATH or install from " + cmdArray[PRTCONF][2];
        cmdArray[PS][0] = Integer.toString(PS);             cmdArray[PS][1] =       "ps"; cmdArray[PS][2] =                     OS + " install media"; cmdArray[PS][3] =                        "binary " + cmdArray[PS][1] + " not found, please set PATH or install from " + cmdArray[PS][2];
        cmdArray[SED][0] = Integer.toString(SED);           cmdArray[SED][1] =      "sed"; cmdArray[SED][2] =                   OS + " install media"; cmdArray[SED][3] =                       "binary " + cmdArray[SED][1] + " not found, please set PATH or install from " + cmdArray[SED][2];
        cmdArray[SORT][0] = Integer.toString(SORT);         cmdArray[SORT][1] =     "sort"; cmdArray[SORT][2] =                 OS + " install media"; cmdArray[SORT][3] =                      "binary " + cmdArray[SORT][1] + " not found, please set PATH or install from " + cmdArray[SORT][2];
        cmdArray[SWAP][0] = Integer.toString(SWAP);         cmdArray[SWAP][1] =     "/usr/sbin/swap"; cmdArray[SWAP][2] =       OS + " install media"; cmdArray[SWAP][3] =                      "binary " + cmdArray[SWAP][1] + " not found, please set PATH or install from " + cmdArray[SWAP][2];
        cmdArray[TAIL][0] = Integer.toString(TAIL);         cmdArray[TAIL][1] =     "tail"; cmdArray[TAIL][2] =                 OS + " install media"; cmdArray[TAIL][3] =                      "binary " + cmdArray[TAIL][1] + " not found, please set PATH or install from " + cmdArray[TAIL][2];
        cmdArray[TR][0] = Integer.toString(TR);             cmdArray[TR][1] =       "tr"; cmdArray[TR][2] =                     OS + " install media"; cmdArray[TR][3] =                        "binary " + cmdArray[TR][1] + " not found, please set PATH or install from " + cmdArray[TR][2];
        cmdArray[VMSTAT][0] = Integer.toString(VMSTAT);     cmdArray[VMSTAT][1] =   "vmstat"; cmdArray[VMSTAT][2] =             OS + " install media sysstat package"; cmdArray[VMSTAT][3] =    "binary " + cmdArray[VMSTAT][1] + " not found, please set PATH or install from " + cmdArray[VMSTAT][2];
        cmdArray[W][0] = Integer.toString(W);               cmdArray[W][1] =        "w"; cmdArray[W][2] =                       OS + " install media"; cmdArray[W][3] =                         "binary " + cmdArray[W][1] + " not found, please set PATH or install from " + cmdArray[W][2];
        cmdArray[WC][0] = Integer.toString(WC);             cmdArray[WC][1] =       "wc"; cmdArray[WC][2] =                     OS + " install media"; cmdArray[WC][3] =                        "binary " + cmdArray[WC][1] + " not found, please set PATH or install from " + cmdArray[WC][2];
        cmdArray[WHO][0] = Integer.toString(WHO);           cmdArray[WHO][1] =      "who"; cmdArray[WHO][2] =                   OS + " install media"; cmdArray[WHO][3] =                       "binary " + cmdArray[WHO][1] + " not found, please set PATH or install from " + cmdArray[WHO][2];
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

    public String getCPUHostCommand() // This a HOST Command
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[TAIL][1] + " -`" + cmdArray[MPSTAT][1] + " | " + cmdArray[GREP][1] + " -vi \"CPU\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'` > " + mpstatDataFile + " & \n"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 > " + mpstatDataFile + " & \n"; } return command;
    }
    public String getALLCPUHostCommand() // This a HOST Command
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -a 53 2 | " + cmdArray[SED][1] + " -e \"s/0/all/\" >> " + mpstatDataFile + " & \n"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " -a 53 2 | " + cmdArray[SED][1] + " -e \"s/0/all/\" >> " + mpstatDataFile + " & \n"; } return command;
    }
    
    // CPU minf mjf xcal  intr ithr  csw icsw migr smtx  srw syscl  usr sys  wt idl
    
    public String getCPUMinorFaultsCommand(String resourceParam) // Minor Faults
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$13 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUMajorFaultsCommand(String resourceParam) // Major Faults
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$14 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUCrossCallsCommand(String resourceParam) // Cross Calls
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$15 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUInterruptsCommand(String resourceParam) // Interrupts
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$16 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUThreadInterruptsCommand(String resourceParam) // Threaded Interrupts
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$16 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUContextSwitchesCommand(String resourceParam) // Context Switches
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$13 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $7 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUInvoluntaryContextSwitchesCommand(String resourceParam) // Involuntary Context Switches
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$14 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUThreadMigrationsCommand(String resourceParam) // Thread Migrations
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$15 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSpinsOnMutexesCommand(String resourceParam) // SpinsOnMutexes
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$15 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSpinsOnReadersWritersCommand(String resourceParam) // Spins On Readers & Writers
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$16 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSystemCallsCommand(String resourceParam) // SystemCalls
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$16 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $12 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUUserCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$13 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $13 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSystemCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$14 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $14 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUWIOCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$15 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $15 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUIDLECommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$16 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + mpstatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $16 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
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
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x -d 50 2 | " + cmdArray[EGREP][1] + " -vie \"^$|Device\" | " + cmdArray[TAIL][1] + " -`" + cmdArray[IOSTAT][1] + " -x -d | " + cmdArray[EGREP][1] + " -vie \"^$|Device\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'` > " + iostatDataFile + " & \n"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x -d 50 2 > " + iostatDataFile + " & \n"; } return command;
    }

    public String getDiskIOReadsPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOWritesPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOKBReadPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOKBWritePerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOTransactionsWaitingCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOTransactionsServedCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $7 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $7 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOAverageTranscationResponseTimeCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOTransactionsWaitingPercentageCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $9 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIODiskBusyPercentageCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^ *" + resourceParam + " +\" " + iostatDataFile +  " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    
//  Memory
    
    public String getRAMTOTCommand(Host hostParam) 
    {
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; " + cmdArray[ECHO][1] + " $ramtot"; }
        return command;
    }
    public String getRAMUSEDCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=$((ramfreek/1024)); ramused=$((ramtot - ramfree)); " + cmdArray[ECHO][1] + " $ramused"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=`" + cmdArray[ECHO][1] + " \"$ramfreek / 1024\" | " + cmdArray[BC][1] + "`; ramused=`" + cmdArray[ECHO][1] + " \"$ramtot - $ramfree\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $ramused"; }
        return command;
    }
    public String getRAMFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=$((ramfreek/1024)); " + cmdArray[ECHO][1] + " $ramfree"; }
        if (host.getSysinfo().contains(OS)) { command = "ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=`" + cmdArray[ECHO][1] + " \"$ramfreek / 1024\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $ramfree"; }
        return command;
    }
    public String getSWAPTOTCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swaptotk=$((swapusedk+swapfreek)); swaptot=$((swaptotk/1024)); " + cmdArray[ECHO][1] + " $swaptot"; }
        if (host.getSysinfo().contains(OS)) { command = "swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swaptotk=`" + cmdArray[ECHO][1] + " \"$swapusedk + $swapfreek\" | " + cmdArray[BC][1] + "`; swaptot=`" + cmdArray[ECHO][1] + " \"$swaptotk / 1024\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $swaptot"; }
        return command;
    }
    public String getSWAPUSEDCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapused=$((swapusedk/1024)); " + cmdArray[ECHO][1] + " $swapused"; }
        if (host.getSysinfo().contains(OS)) { command = "swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapused=`" + cmdArray[ECHO][1] + " \"$swapusedk / 1024\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $swapused"; }
        return command;
    }
    public String getSWAPFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfree=$((swapfreek/1024)); " + cmdArray[ECHO][1] + " $swapfree"; }
        if (host.getSysinfo().contains(OS)) { command = "swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfree=`" + cmdArray[ECHO][1] + " \"$swapfreek / 1024\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $swapfree"; }
        return command;
    }
    public String getTOTMEMCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swaptotk=$((swapusedk+swapfreek)); swaptot=$((swaptotk/1024)); totmem=$((ramtot + swaptot)); " + cmdArray[ECHO][1] + " $totmem"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swaptotk=`" + cmdArray[ECHO][1] + " \"$swapusedk + $swapfreek\" | " + cmdArray[BC][1] + "`; swaptot=`" + cmdArray[ECHO][1] + " \"$swaptotk / 1024\" | " + cmdArray[BC][1] + "`; totmem=`" + cmdArray[ECHO][1] + " \"$ramtot + $swaptot\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totmem"; }
        return command;
    }
    public String getTOTUSEDCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=$((ramfreek/1024));  ramused=$((ramtot - ramfree)); swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapused=$((swapusedk/1024)); totused=$((ramused + swapused)); " + cmdArray[ECHO][1] + " $totused"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[PRTCONF][1] + " | " + cmdArray[GREP][1] + " \"Memory size\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=`" + cmdArray[ECHO][1] + " \"$ramfreek / 1024\" | " + cmdArray[BC][1] + "`;  ramused=`" + cmdArray[ECHO][1] + " \"$ramtot - $ramfree\" | " + cmdArray[BC][1] + "`; swapusedk=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapused=`" + cmdArray[ECHO][1] + " \"$swapusedk / 1024\" | " + cmdArray[BC][1] + "`; totused=`" + cmdArray[ECHO][1] + " \"$ramused + $swapused\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totused"; }
        return command;
    }
    public String getTOTFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=$((ramfreek/1024)); swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfree=$((swapfreek/1024)); totfree=$((ramfree + swapfree)); " + cmdArray[ECHO][1] + " $totfree"; }
        if (host.getSysinfo().contains(OS)) { command = "ramfreek=`" + cmdArray[VMSTAT][1] + " 2 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }'`; ramfree=`" + cmdArray[ECHO][1] + " \"$ramfreek / 1024\" | " + cmdArray[BC][1] + "`; swapfreek=`" + cmdArray[VMSTAT][1] + " -s | " + cmdArray[AWK][1] + " '{ print $11 }' | " + cmdArray[TR][1] + " -d \"k\"`; swapfree=`" + cmdArray[ECHO][1] + " \"$swapfreek / 1024\" | " + cmdArray[BC][1] + "`; totfree=`" + cmdArray[ECHO][1] + " \"$ramfree + $swapfree\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totfree"; }
        return command;
    }

//  Storage
    
    public String getFSTOTCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -k " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSUSEDCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -k " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSFREECommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -k " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSUSEDPercCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -k " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[TR][1] + " -d \"%\""; }
        return command;
    }

//  Network
    
    public String getIFTINPacketsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $5 }'"; }
        return command;
    }
    public String getIFINERRCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $6 }'"; }
        return command;
    }
    public String getIFOUTPacketsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $7 }'"; }
        return command;
    }
    public String getIFOUTERRCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $8 }'"; }
        return command;
    }
    public String getIFCollisionsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $9 }'"; }
        return command;
    }
    public String getIFQueueCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -i | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $10 }'"; }
        return command;
    }

//  Network
    
    public String getTCPSTATBOUNDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"BOUND\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"CLOSED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSINGCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"CLOSING\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSE_WAITCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"CLOSE_WAIT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATESTABLISHEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"ESTABLISHED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATFIN_WAIT_1Command(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"FIN_WAIT_1\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATFIN_WAIT_2Command(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"FIN_WAIT_2\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATIDLECommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"IDLE\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATLAST_ACKCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"LAST_ACK\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATLISTENCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"LISTEN\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATSYN_RECEIVEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"SYN_RECEIVED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATSYN_SENTCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[GREP][1] + " \"SYN_SENT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATTIME_WAITCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " -ie \"TIME_WAIT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
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

