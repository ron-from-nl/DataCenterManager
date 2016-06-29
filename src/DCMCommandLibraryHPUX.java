import data.Host;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DCMCommandLibraryHPUX
{
    private Host host;
    private String command =         "";
    private InetAddress inetAddress;
    private String dcmServerIP;
    private String sarDataFile;
    private String iostatDataFile;
    private String psCPUFile;
    private String psMEMFile;

    private final String OS =        "HP-UX";
    
    private final int AWK =          0;
    private final int BC =           1;
    private final int DF =           2;
    private final int ECHO =         3;
    private final int EGREP =        4;
    private final int GREP =         5;
    private final int HEAD =         6;
    private final int IOSTAT =       7;
    private final int SAR =          8;
    private final int MACHINFO =     9;
    private final int NETSTAT =      10;
    private final int PS =           11;
    private final int SED =          12;
    private final int SORT =         13;
    private final int SWAPINFO =     14;
    private final int TAIL =         15;
    private final int TR =           16;
    private final int VMSTAT =       17;
    private final int W =            18;
    private final int WC =           19;
    private final int WHO =          20;
    private static String[][] cmdArray;
    
    public DCMCommandLibraryHPUX(Host hostParam) throws UnknownHostException
    {
        host = hostParam;
        
        try { inetAddress =         InetAddress.getLocalHost(); } catch (UnknownHostException ex) {  }
        dcmServerIP =               inetAddress.getHostAddress();

        sarDataFile = ".dcmsar_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        iostatDataFile = ".dcmiostat_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psCPUFile = ".dcmpscpu_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psMEMFile = ".dcmpsmem_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        
        cmdArray = new String[21][4]; // Id, command, problem, advise
        cmdArray[AWK][0] = Integer.toString(AWK);           cmdArray[AWK][1] =      "awk"; cmdArray[AWK][2] =                       OS + " install media"; cmdArray[AWK][3] =                       "binary " + cmdArray[AWK][1] + " not found, please set PATH or install from " + cmdArray[AWK][2];
        cmdArray[BC][0] = Integer.toString(BC);             cmdArray[BC][1] =       "bc"; cmdArray[BC][2] =                         OS + " install media"; cmdArray[BC][3] =                        "binary " + cmdArray[BC][1] + " not found, please set PATH or install from " + cmdArray[BC][2];
        cmdArray[DF][0] = Integer.toString(DF);             cmdArray[DF][1] =       "df"; cmdArray[DF][2] =                         OS + " install media"; cmdArray[DF][3] =                        "binary " + cmdArray[DF][1] + " not found, please set PATH or install from " + cmdArray[DF][2];
        cmdArray[ECHO][0] = Integer.toString(ECHO);         cmdArray[ECHO][1] =     "echo"; cmdArray[ECHO][2] =                     OS + " install media"; cmdArray[ECHO][3] =                      "binary " + cmdArray[ECHO][1] + " not found, please set PATH or install from " + cmdArray[ECHO][2];
        cmdArray[EGREP][0] = Integer.toString(EGREP);       cmdArray[EGREP][1] =    "egrep"; cmdArray[EGREP][2] =                   OS + " install media"; cmdArray[EGREP][3] =                     "binary " + cmdArray[EGREP][1] + " not found, please set PATH or install from " + cmdArray[EGREP][2];
        cmdArray[GREP][0] = Integer.toString(GREP);         cmdArray[GREP][1] =     "grep"; cmdArray[GREP][2] =                     OS + " install media"; cmdArray[GREP][3] =                      "binary " + cmdArray[GREP][1] + " not found, please set PATH or install from " + cmdArray[GREP][2];
        cmdArray[HEAD][0] = Integer.toString(HEAD);         cmdArray[HEAD][1] =     "head"; cmdArray[HEAD][2] =                     OS + " install media"; cmdArray[HEAD][3] =                      "binary " + cmdArray[HEAD][1] + " not found, please set PATH or install from " + cmdArray[HEAD][2];
        cmdArray[IOSTAT][0] = Integer.toString(IOSTAT);     cmdArray[IOSTAT][1] =   "iostat"; cmdArray[IOSTAT][2] =                 OS + " install media sysstat package"; cmdArray[IOSTAT][3] =    "binary " + cmdArray[IOSTAT][1] + " not found, please set PATH or install from " + cmdArray[IOSTAT][2];
        cmdArray[SAR][0] = Integer.toString(SAR);           cmdArray[SAR][1] =      "sar"; cmdArray[SAR][2] =                       OS + " install media sysstat package"; cmdArray[SAR][3] =       "binary " + cmdArray[SAR][1] + " not found, please set PATH or install from " + cmdArray[SAR][2];
        cmdArray[MACHINFO][0] = Integer.toString(MACHINFO); cmdArray[MACHINFO][1] = "/usr/contrib/bin/machinfo"; cmdArray[MACHINFO][2] =             OS + " install media"; cmdArray[MACHINFO][3] =                  "binary " + cmdArray[MACHINFO][1] + " not found, please set PATH or install from " + cmdArray[MACHINFO][2];
        cmdArray[NETSTAT][0] = Integer.toString(NETSTAT);   cmdArray[NETSTAT][1] =  "netstat"; cmdArray[NETSTAT][2] =               OS + " install media"; cmdArray[NETSTAT][3] =                   "binary " + cmdArray[NETSTAT][1] + " not found, please set PATH or install from " + cmdArray[NETSTAT][2];
        cmdArray[PS][0] = Integer.toString(PS);             cmdArray[PS][1] =       "ps"; cmdArray[PS][2] =                         OS + " install media"; cmdArray[PS][3] =                        "binary " + cmdArray[PS][1] + " not found, please set PATH or install from " + cmdArray[PS][2];
        cmdArray[SED][0] = Integer.toString(SED);           cmdArray[SED][1] =      "sed"; cmdArray[SED][2] =                       OS + " install media"; cmdArray[SED][3] =                       "binary " + cmdArray[SED][1] + " not found, please set PATH or install from " + cmdArray[SED][2];
        cmdArray[SORT][0] = Integer.toString(SORT);         cmdArray[SORT][1] =     "sort"; cmdArray[SORT][2] =                     OS + " install media"; cmdArray[SORT][3] =                      "binary " + cmdArray[SORT][1] + " not found, please set PATH or install from " + cmdArray[SORT][2];
        cmdArray[SWAPINFO][0] = Integer.toString(SWAPINFO); cmdArray[SWAPINFO][1] = "/usr/sbin/swapinfo"; cmdArray[SWAPINFO][2] =   OS + " install media"; cmdArray[SWAPINFO][3] =                  "binary " + cmdArray[SWAPINFO][1] + " not found, please set PATH or install from " + cmdArray[SWAPINFO][2];
        cmdArray[TAIL][0] = Integer.toString(TAIL);         cmdArray[TAIL][1] =     "tail"; cmdArray[TAIL][2] =                     OS + " install media"; cmdArray[TAIL][3] =                      "binary " + cmdArray[TAIL][1] + " not found, please set PATH or install from " + cmdArray[TAIL][2];
        cmdArray[TR][0] = Integer.toString(TR);             cmdArray[TR][1] =       "tr"; cmdArray[TR][2] =                         OS + " install media"; cmdArray[TR][3] =                        "binary " + cmdArray[TR][1] + " not found, please set PATH or install from " + cmdArray[TR][2];
        cmdArray[VMSTAT][0] = Integer.toString(VMSTAT);     cmdArray[VMSTAT][1] =   "vmstat"; cmdArray[VMSTAT][2] =                 OS + " install media"; cmdArray[VMSTAT][3] =                    "binary " + cmdArray[VMSTAT][1] + " not found, please set PATH or install from " + cmdArray[VMSTAT][2];
        cmdArray[W][0] = Integer.toString(W);               cmdArray[W][1] =        "w"; cmdArray[W][2] =                           OS + " install media"; cmdArray[W][3] =                         "binary " + cmdArray[W][1] + " not found, please set PATH or install from " + cmdArray[W][2];
        cmdArray[WC][0] = Integer.toString(WC);             cmdArray[WC][1] =       "wc"; cmdArray[WC][2] =                         OS + " install media"; cmdArray[WC][3] =                        "binary " + cmdArray[WC][1] + " not found, please set PATH or install from " + cmdArray[WC][2];
        cmdArray[WHO][0] = Integer.toString(WHO);           cmdArray[WHO][1] =      "who"; cmdArray[WHO][2] =                       OS + " install media"; cmdArray[WHO][3] =                       "binary " + cmdArray[WHO][1] + " not found, please set PATH or install from " + cmdArray[WHO][2];
    }
    
//  PS
    
    public String getPSCPUHostCommand()
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[PS][1] + " -e -o pid,pcpu,comm | " + cmdArray[GREP][1] + " -iv \"pid\" | " + cmdArray[SORT][1] + " -k 2nr > " + psCPUFile + " \n"; } return command;
    }
    
    public String getPSMEMHostCommand()
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[PS][1] + " -e -o pid,vsz,comm | " + cmdArray[GREP][1] + " -iv \"pid\" | " + cmdArray[SORT][1] + " -k 2nr > " + psMEMFile + " \n"; } return command; // This is KB. HPUX does not support ps -o pmem. Later KB will be converted to Percentage used
    }
    
//  CPU

    public String getCPUHostCommand() // This a HOST Command: sar -M 2 2 | grep -i average | sed -e 's/^[a-zA-Z]*//'
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[SAR][1] + " -M 25 2 | " + cmdArray[GREP][1] + " -i \"Average\" | " + cmdArray[SED][1] + " -e 's/^[a-zA-Z]*//' > " + sarDataFile + " & \n"; } return command;
        // The kill is necessary as HPUX nohup causes the sar process to never finish and flood the system with processes
        if (host.getSysinfo().contains(OS))    { command = "kill `ps -u \\`id -u\\` | grep sadc | awk '{ print $1 }'`; " + cmdArray[SAR][1] + " -M 25 2 > " + sarDataFile + " & \n"; } return command;
    }
    
    // CPU minf mjf xcal  intr ithr  csw icsw migr smtx  srw syscl  usr sys  wt idl (SunOS)
    // Average cpu  usr sys  wt idl (HPUX)
    
    public String getCPUUserCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$13 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[SED][1] + " -e 's/system/all/' " + sarDataFile + " | " + cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSystemCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$14 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[SED][1] + " -e 's/system/all/' " + sarDataFile + " | " + cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUWIOCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$15 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[SED][1] + " -e 's/system/all/' " + sarDataFile + " | " + cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUIDLECommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[MPSTAT][1] + " 50 2 | " + cmdArray[AWK][1] + " '{ print $1\" \"$16 }' | " + cmdArray[GREP][1] + " -iv \"CPU\" | " + cmdArray[EGREP][1] + " -ie \"^" + resourceParam + "\" | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[SED][1] + " -e 's/system/all/' " + sarDataFile + " | " + cmdArray[EGREP][1] + " -ie \"^average +" + resourceParam + " +\" | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }    

//  WORKLOAD
    public String getWorkload()
    {
        if (host.getSysinfo().contains(OS))   { command = cmdArray[W][1] + " | " + cmdArray[GREP][1] + " -i \"average\" | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        return command;
    }    
    
//  DiskIO
    
    public String getDiskIOHostCommand() // This a HOST Command: iostat 2 2 | egrep -ie "c[0-9]t[0-9]d[0-9]" | tail -`iostat | awk '{ print $1 }' | egrep -ie "^c[0-9]t[0-9]d[0-9]" | wc -l`
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " 25 2 | " + cmdArray[EGREP][1] + " -ie \"c[0-9]t[0-9]d[0-9]\" | " + cmdArray[TAIL][1] + " -`iostat | awk '{ print $1 }' | egrep -ie \"^c[0-9]t[0-9]d[0-9]\" | wc -l` > " + iostatDataFile + " & \n"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " 25 2 > " + iostatDataFile + " & \n"; } return command;
    }
/*
                    device       Device name
                    bps          Kilobytes transferred per second
                    sps          Number of seeks per second
                    msps         Milliseconds per average seek
 */
    public String getDiskIOKBTransferedPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[GREP][1] + " " + resourceParam + " " + iostatDataFile + " | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[GREP][1] + " " + resourceParam + " " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOSeeksPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $6 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[GREP][1] + " " + resourceParam + " " + iostatDataFile + " | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[GREP][1] + " " + resourceParam + " " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getDiskIOmSecPerAverageSeekCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -x " + resourceParam + " 50 2 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $7 }'"; } return command;
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[GREP][1] + " " + resourceParam + " " + iostatDataFile + " | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        if (host.getSysinfo().contains(OS))    { command = cmdArray[GREP][1] + " " + resourceParam + " " + iostatDataFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    
//  Memory
    
    public String getRAMTOTCommand(Host hostParam) 
    {
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; " + cmdArray[ECHO][1] + " $ramtot"; }
        return command;
    }
    public String getRAMUSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; " + cmdArray[ECHO][1] + " $ramused"; }
        return command;
    }
    public String getRAMFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; ramfree=$((ramtot - ramused)); " + cmdArray[ECHO][1] + " $ramfree"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; ramfree=`" + cmdArray[ECHO][1] + " \"$ramtot - $ramused\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $ramfree"; }
        return command;
    }
    public String getSWAPTOTCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "swaptot=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $2 }'`; " + cmdArray[ECHO][1] + " $swaptot"; }
        return command;
    }
    public String getSWAPUSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "swapused=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; " + cmdArray[ECHO][1] + " $swapused"; }
        return command;
    }
    public String getSWAPFREECommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "swapfree=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $4 }'`; " + cmdArray[ECHO][1] + " $swapfree"; }
        return command;
    }
    public String getTOTMEMCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; swaptot=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $2 }'`; totmem=$((ramtot + swaptot));  " + cmdArray[ECHO][1] + " $totmem"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; swaptot=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $2 }'`; totmem=`" + cmdArray[ECHO][1] + " \"$ramtot + $swaptot\" | " + cmdArray[BC][1] + "`;  " + cmdArray[ECHO][1] + " $totmem"; }
        return command;
    }
    public String getTOTUSEDCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; swapused=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; totused=$((ramused + swapused)); " + cmdArray[ECHO][1] + " $totused"; }
        if (host.getSysinfo().contains(OS)) { command = "ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; swapused=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $3 }'`; totused=`" + cmdArray[ECHO][1] + " \"$ramused + $swapused\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totused"; }
        return command;
    }
    public String getTOTFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; ramfree=$((ramtot - ramused)); swapfree=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $4 }'`;  totfree=$((ramfree + swapfree)); " + cmdArray[ECHO][1] + " $totfree"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramused=`" + cmdArray[VMSTAT][1] + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5*0.004 }'`; ramfree=`" + cmdArray[ECHO][1] + " \"$ramtot - $ramused\" | " + cmdArray[BC][1] + "`; swapfree=`" + cmdArray[SWAPINFO][1] + " -m | " + cmdArray[GREP][1] + " -i \"memory\" | " + cmdArray[AWK][1] + " '{ print $4 }'`;  totfree=`" + cmdArray[ECHO][1] + " \"$ramfree + $swapfree\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totfree"; }
        return command;
    }

//  Storage
    
    public String getFSTOTCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -kP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $2/1024 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSUSEDCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -kP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $3/1024 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSFREECommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -kP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $4/1024 }' | " + cmdArray[TAIL][1] + " -1"; }
        return command;
    }
    public String getFSUSEDPercCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -kP " + resourceParam + " | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[TR][1] + " -d \"%\""; }
        return command;
    }

//  Network
    
    public String getIFTINPacketsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -in | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $5 }'"; }
        return command;
    }
    public String getIFINERRCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -in | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $6 }'"; }
        return command;
    }
    public String getIFOUTPacketsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -in | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $7 }'"; }
        return command;
    }
    public String getIFOUTERRCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -in | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $8 }'"; }
        return command;
    }
    public String getIFCollisionsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -in | " + cmdArray[GREP][1] + " \"" + resourceParam + "\" | " + cmdArray[AWK][1] + " '{ print $9 }'"; }
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
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -1 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS1MEMCommand()// Converting HPUX nightmare ps -o vsz to ps -o pmem (KB to Percentage) just like all other unixes
    {
        // totmem=`" + cmdArray[ECHO][1] + " \"$ramtot + $swaptot\" | " + cmdArray[BC][1] + "`;
        if (host.getSysinfo().contains(OS)) { command = "ramtotm=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramtotk=$(( ramtotm * 1024 )); "; }
        if (host.getSysinfo().contains(OS)) { command += "psusedk=`" +cmdArray[HEAD][1] + " -1 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`;"; }
//        if (host.getSysinfo().contains(OS)) { command += "psusedperc=$(( psusedk / (ramtotk / 100) )); " + cmdArray[ECHO][1] + " $psusedperc"; }
        if (host.getSysinfo().contains(OS)) { command += "psusedperc=`" + cmdArray[ECHO][1] + " \"$psusedk / ($ramtotk / 100)\" | " + cmdArray[BC][1] + " -l`; " + cmdArray[ECHO][1] + " $psusedperc"; }
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -1 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        return command;
    }

    public String getPS2MEMPIDCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -2 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS2MEMCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS)) { command = "ramtotm=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramtotk=$(( ramtotm * 1024 )); "; }
        if (host.getSysinfo().contains(OS)) { command += "psusedk=`" +cmdArray[HEAD][1] + " -2 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`;"; }
//        if (host.getSysinfo().contains(OS)) { command += "psusedperc=$(( psusedk / (ramtotk / 100) )); " + cmdArray[ECHO][1] + " $psusedperc"; }
        if (host.getSysinfo().contains(OS)) { command += "psusedperc=`" + cmdArray[ECHO][1] + " \"$psusedk / ($ramtotk / 100)\" | " + cmdArray[BC][1] + " -l`; " + cmdArray[ECHO][1] + " $psusedperc"; }
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -2 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        return command;
    }

    public String getPS3MEMPIDCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -3 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }

    public String getPS3MEMCommand()// usr nice sys idle
    {
        if (host.getSysinfo().contains(OS)) { command = "ramtotm=`" + cmdArray[MACHINFO][1] + " | " + cmdArray[GREP][1] + " -i memory | " + cmdArray[AWK][1] + " '{ print $3 }'`; ramtotk=$(( ramtotm * 1024 )); "; }
        if (host.getSysinfo().contains(OS)) { command += "psusedk=`" +cmdArray[HEAD][1] + " -3 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`;"; }
//        if (host.getSysinfo().contains(OS)) { command += "psusedperc=$(( psusedk / (ramtotk / 100) )); " + cmdArray[ECHO][1] + " $psusedperc"; }
        if (host.getSysinfo().contains(OS)) { command += "psusedperc=`" + cmdArray[ECHO][1] + " \"$psusedk / ($ramtotk / 100)\" | " + cmdArray[BC][1] + " -l`; " + cmdArray[ECHO][1] + " $psusedperc"; }
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[HEAD][1] + " -3 " + psMEMFile + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
        return command;
    }

    public String[][] getCommandArray() { return cmdArray; }
}

