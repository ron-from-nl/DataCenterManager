import data.Host;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DCMCommandLibraryDarwin
{
    private Host host;
    private String command =         "";
    private InetAddress inetAddress;
    private String dcmServerIP;
    private String sarDataFile;
    private String psCPUFile;
    private String psMEMFile;
    private String iostatDataFile;
    
    private final String OS =        "Darwin";

    private final int AWK =          0;
    private final int BC =           1;
    private final int DF =           2;
    private final int ECHO =         3;
    private final int EGREP =        4;
    private final int GREP =         5;
    private final int HEAD =         6;
    private final int IOSTAT =       7;
    private final int NETSTAT =      8;
    private final int PS =           9;
    private final int SAR =          10;
    private final int SED =          11;
    private final int SORT =         12;
    private final int SYSCTL =       13;
    private final int TAIL =         14;
    private final int TOP =          15;
    private final int TR =           16;
    private final int W =            17;
    private final int WC =           18;
    private final int WHO =          19;
//    private final int TST =          18;
    private String[][] cmdArray;
    
    public DCMCommandLibraryDarwin(Host hostParam) throws UnknownHostException
    {
        host = hostParam;

        try { inetAddress =         InetAddress.getLocalHost(); } catch (UnknownHostException ex) {  }
        dcmServerIP =               inetAddress.getHostAddress();

        sarDataFile = ".dcmsar_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        iostatDataFile = ".dcmiostat_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psCPUFile = ".dcmpscpu_" + dcmServerIP  + "_" + host.getHostname() + ".dat";
        psMEMFile = ".dcmpsmem_" + dcmServerIP  + "_" + host.getHostname() + ".dat";

        cmdArray = new String[20][4];
        cmdArray[AWK][0] = Integer.toString(AWK);           cmdArray[AWK][1] =      "awk"; cmdArray[AWK][2] =           OS + " install media"; cmdArray[AWK][3] =                       "binary " + cmdArray[AWK][1] + " not found, please set PATH or install from " + cmdArray[AWK][2];
        cmdArray[BC][0] = Integer.toString(BC);             cmdArray[BC][1] =       "bc"; cmdArray[BC][2] =             OS + " install media"; cmdArray[BC][3] =                        "binary " + cmdArray[BC][1] + " not found, please set PATH or install from " + cmdArray[BC][2];
        cmdArray[DF][0] = Integer.toString(DF);             cmdArray[DF][1] =       "df"; cmdArray[DF][2] =             OS + " install media"; cmdArray[DF][3] =                        "binary " + cmdArray[DF][1] + " not found, please set PATH or install from " + cmdArray[DF][2];
        cmdArray[ECHO][0] = Integer.toString(ECHO);         cmdArray[ECHO][1] =     "echo"; cmdArray[ECHO][2] =         OS + " install media"; cmdArray[ECHO][3] =                      "binary " + cmdArray[ECHO][1] + " not found, please set PATH or install from " + cmdArray[ECHO][2];
        cmdArray[EGREP][0] = Integer.toString(EGREP);       cmdArray[EGREP][1] =    "egrep"; cmdArray[EGREP][2] =       OS + " install media"; cmdArray[EGREP][3] =                     "binary " + cmdArray[EGREP][1] + " not found, please set PATH or install from " + cmdArray[EGREP][2];
        cmdArray[GREP][0] = Integer.toString(GREP);         cmdArray[GREP][1] =     "grep"; cmdArray[GREP][2] =         OS + " install media"; cmdArray[GREP][3] =                      "binary " + cmdArray[GREP][1] + " not found, please set PATH or install from " + cmdArray[GREP][2];
        cmdArray[HEAD][0] = Integer.toString(HEAD);         cmdArray[HEAD][1] =     "head"; cmdArray[HEAD][2] =         OS + " install media"; cmdArray[HEAD][3] =                      "binary " + cmdArray[HEAD][1] + " not found, please set PATH or install from " + cmdArray[HEAD][2];
        cmdArray[IOSTAT][0] = Integer.toString(IOSTAT);     cmdArray[IOSTAT][1] =   "iostat"; cmdArray[IOSTAT][2] =     OS + " install media sysstat package"; cmdArray[IOSTAT][3] =    "binary " + cmdArray[IOSTAT][1] + " not found, please set PATH or install from " + cmdArray[IOSTAT][2];
        cmdArray[NETSTAT][0] = Integer.toString(NETSTAT);   cmdArray[NETSTAT][1] =  "netstat"; cmdArray[NETSTAT][2] =   OS + " install media"; cmdArray[NETSTAT][3] =                   "binary " + cmdArray[NETSTAT][1] + " not found, please set PATH or install from " + cmdArray[NETSTAT][2];
        cmdArray[PS][0] = Integer.toString(PS);             cmdArray[PS][1] =       "ps"; cmdArray[PS][2] =             OS + " install media"; cmdArray[PS][3] =                        "binary " + cmdArray[PS][1] + " not found, please set PATH or install from " + cmdArray[PS][2];
        cmdArray[SAR][0] = Integer.toString(SAR);           cmdArray[SAR][1] =      "sar"; cmdArray[SAR][2] =           OS + " install media sysstat package"; cmdArray[SAR][3] =       "binary " + cmdArray[SAR][1] + " not found, please set PATH or install from " + cmdArray[SAR][2];
        cmdArray[SED][0] = Integer.toString(SED);           cmdArray[SED][1] =      "sed"; cmdArray[SED][2] =           OS + " install media"; cmdArray[SED][3] =                       "binary " + cmdArray[SED][1] + " not found, please set PATH or install from " + cmdArray[SED][2];
        cmdArray[ECHO][0] = Integer.toString(ECHO);         cmdArray[ECHO][1] =     "echo"; cmdArray[ECHO][2] =         OS + " install media"; cmdArray[ECHO][3] =                      "binary " + cmdArray[ECHO][1] + " not found, please set PATH or install from " + cmdArray[ECHO][2];
        cmdArray[SORT][0] = Integer.toString(SORT);         cmdArray[SORT][1] =     "sort"; cmdArray[SORT][2] =         OS + " install media"; cmdArray[SORT][3] =                      "binary " + cmdArray[SORT][1] + " not found, please set PATH or install from " + cmdArray[SORT][2];
        cmdArray[SYSCTL][0] = Integer.toString(SYSCTL);     cmdArray[SYSCTL][1] =   "sysctl"; cmdArray[SYSCTL][2] =     OS + " install media"; cmdArray[SYSCTL][3] =                    "binary " + cmdArray[SYSCTL][1] + " not found, please set PATH or install from " + cmdArray[SYSCTL][2];
        cmdArray[TAIL][0] = Integer.toString(TAIL);         cmdArray[TAIL][1] =     "tail"; cmdArray[TAIL][2] =         OS + " install media"; cmdArray[TAIL][3] =                      "binary " + cmdArray[TAIL][1] + " not found, please set PATH or install from " + cmdArray[TAIL][2];
        cmdArray[TOP][0] = Integer.toString(TOP);           cmdArray[TOP][1] =      "top"; cmdArray[TOP][2] =           OS + " install media"; cmdArray[TOP][3] =                       "binary " + cmdArray[TOP][1] + " not found, please set PATH or install from " + cmdArray[TOP][2];
        cmdArray[TR][0] = Integer.toString(TR);             cmdArray[TR][1] =       "tr"; cmdArray[TR][2] =             OS + " install media"; cmdArray[TR][3] =                        "binary " + cmdArray[TR][1] + " not found, please set PATH or install from " + cmdArray[TR][2];
        cmdArray[W][0] = Integer.toString(W);               cmdArray[W][1] =        "w"; cmdArray[W][2] =               OS + " install media"; cmdArray[W][3] =                         "binary " + cmdArray[W][1] + " not found, please set PATH or install from " + cmdArray[W][2];
        cmdArray[WC][0] = Integer.toString(WC);             cmdArray[WC][1] =       "wc"; cmdArray[WC][2] =             OS + " install media"; cmdArray[WC][3] =                        "binary " + cmdArray[WC][1] + " not found, please set PATH or install from " + cmdArray[WC][2];
        cmdArray[WHO][0] = Integer.toString(WHO);           cmdArray[WHO][1] =      "who"; cmdArray[WHO][2] =           OS + " install media"; cmdArray[WHO][3] =                       "binary " + cmdArray[WHO][1] + " not found, please set PATH or install from " + cmdArray[WHO][2];
//        cmdArray[TST][0] = Integer.toString(TST);           cmdArray[TST][1] =      "tst"; cmdArray[TST][2] =           OS + " install media"; cmdArray[TST][3] =                       "binary " + cmdArray[TST][1] + " not found, please set PATH or install from " + cmdArray[TST][2];
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
    
    // Apple resists transparancy of performance. in a multi core machine, no mpstat
    public String getCPUHostCommand() // This a HOST Command: sar -M 2 2 | grep -i average | sed -e 's/^[a-zA-Z]*//'
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[SAR][1] + " -u 25 1 > " + sarDataFile + " & \n"; } return command;
    }
    
    public String getCPUUSERCommand(String resourceParam)// usr nice sys idle
    {
//        if (host.getSysinfo().contains(OS))   { command = cmdArray[SAR][1] + " -u 25 1 | " + cmdArray[GREP][1] + " -i \"average\" | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+: +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUSYSCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))   { command = cmdArray[SAR][1] + " -u 25 1 | " + cmdArray[GREP][1] + " -i \"average\" | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+: +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $4 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }
    public String getCPUIDLECommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))   { command = cmdArray[SAR][1] + " -u 25 1 | " + cmdArray[GREP][1] + " -i \"average\" | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        if (host.getSysinfo().contains(OS))    { command = cmdArray[EGREP][1] + " -ie \"^.+: +\" " + sarDataFile + " | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; } return command;
    }    

//  WORKLOAD
    public String getWorkload()
    {
        if (host.getSysinfo().contains(OS))   { command = cmdArray[W][1] + " | " + cmdArray[GREP][1] + " -i \"average\" | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        return command;
    }    
    
    //w | grep "load averages" | grep -v grep | awk '{ print $10 }'
//  DiskIO
    
    // Apple resists transparancy of performance. in comparison to SunOS very limited iostat details and a very clumsy format hard to automate

    public String getDiskIOHostCommand() // This a HOST Command: iostat 2 2 | egrep -ie "c[0-9]t[0-9]d[0-9]" | tail -`iostat | awk '{ print $1 }' | egrep -ie "^c[0-9]t[0-9]d[0-9]" | wc -l`
    {
        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -c 2 -w 50 -K -d > " + iostatDataFile + " & \n"; } return command;
    }

    public String getDiskIOKBPerTransferCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -c 2 -w 50 -K -d " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        if (host.getSysinfo().contains(OS))    { command = cmdArray[TAIL][1] + " -1 " + iostatDataFile + " | " + cmdArray[AWK][1] + " '{ print $1 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        return command;
    }
    public String getDiskIOTransfersPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -c 2 -w 50 -K -d " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        if (host.getSysinfo().contains(OS))    { command = cmdArray[TAIL][1] + " -1 " + iostatDataFile + " | " + cmdArray[AWK][1] + " '{ print $2 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        return command;
    }
    public String getDiskIOMBPerSecondCommand(String resourceParam)
    {
//        if (host.getSysinfo().contains(OS))    { command = cmdArray[IOSTAT][1] + " -c 2 -w 50 -K -d " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        if (host.getSysinfo().contains(OS))    { command = cmdArray[TAIL][1] + " -1 " + iostatDataFile + " | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'"; }
        return command;
    }
    
//  Memory
    
    public String getRAMTOTCommand(Host hostParam) 
    {
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[SYSCTL][1] + " -n hw.memsize`; ramtot=$((ramtot/(1024*1024))); " + cmdArray[ECHO][1] + " $ramtot"; } // Bytes
        return command;
    }
    public String getRAMUSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "ramused=`" + cmdArray[TOP][1] + " -l 1 -n 0 | head -7 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; " + cmdArray[ECHO][1] + " $ramused"; }
        return command;
    }
    public String getRAMFREECommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "ramfree=`" + cmdArray[TOP][1] + " -l 1 -n 0 | head -7 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; " + cmdArray[ECHO][1] + " $ramfree"; }
        return command;
    }
    public String getSWAPTOTCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "swaptot=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; " + cmdArray[ECHO][1] + " $swaptot"; } // MB
        return command;
    }
    public String getSWAPUSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "swapused=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; " + cmdArray[ECHO][1] + " $swapused"; } // MB
        return command;
    }
    public String getSWAPFREECommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = "swapfree=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; " + cmdArray[ECHO][1] + " $swapfree"; } // MB
        return command;
    }
    public String getTOTMEMCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[SYSCTL][1] + " -n hw.memsize`; ramtot=$((ramtot/(1024*1024))); swaptot=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; totmem=$((ramtot + swaptot)); " + cmdArray[ECHO][1] + " $totmem"; }
        if (host.getSysinfo().contains(OS)) { command = "ramtot=`" + cmdArray[SYSCTL][1] + " -n hw.memsize`; ramtot=$((ramtot/(1024*1024))); swaptot=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $3 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; totmem=`" + cmdArray[ECHO][1] + " \"$ramtot + $swaptot\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totmem"; }
        return command;
    }
    public String getTOTUSEDCommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramused=`" + cmdArray[TOP][1] + " -l 1 -n 0 | head -7 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; swapused=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; totused=$(( ramused + swapused )); " + cmdArray[ECHO][1] + " $totused"; }
        if (host.getSysinfo().contains(OS)) { command = "ramused=`" + cmdArray[TOP][1] + " -l 1 -n 0 | head -7 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $8 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; swapused=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $6 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; totused=`" + cmdArray[ECHO][1] + " \"$ramused + $swapused\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totused"; }
        return command;
    }
    public String getTOTFREECommand(Host hostParam)
    {
//        if (host.getSysinfo().contains(OS)) { command = "ramfree=`" + cmdArray[TOP][1] + " -l 1 -n 0 | head -7 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; swapfree=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; totfree=$((ramfree + swapfree)); " + cmdArray[ECHO][1] + " $totfree"; }
        if (host.getSysinfo().contains(OS)) { command = "ramfree=`" + cmdArray[TOP][1] + " -l 1 -n 0 | head -7 | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $10 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; swapfree=`" + cmdArray[SYSCTL][1] + " -n vm.swapusage | " + cmdArray[AWK][1] + " '{ print $9 }' | " + cmdArray[SED][1] + " -e 's/,/./' -e 's/[^0-9.]//' -e 's/\\.$//'`; totfree=`" + cmdArray[ECHO][1] + " \"$ramfree + $swapfree\" | " + cmdArray[BC][1] + "`; " + cmdArray[ECHO][1] + " $totfree"; }
        return command;
    }

//  Storage
    
    public String getFSTOTCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -m " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $2 }'"; }
        return command;
    }
    public String getFSUSEDCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -m " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $3 }'"; }
        return command;
    }
    public String getFSFREECommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -m " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $4 }'"; }
        return command;
    }
    public String getFSUSEDPercCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[DF][1] + " -m " + resourceParam + " | " + cmdArray[TAIL][1] + " -1 | " + cmdArray[AWK][1] + " '{ print $5 }' | " + cmdArray[TR][1] + " -d \"%\""; }
        return command;
    }

//  Network
    
    public String getIFTXPacketsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -I " + resourceParam + " -ni | " + cmdArray[GREP][1] + " Link | " + cmdArray[AWK][1] + " '{ print $7 }'"; }
        return command;
    }
    public String getIFRXPacketsCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -I " + resourceParam + " -ni | " + cmdArray[GREP][1] + " Link | " + cmdArray[AWK][1] + " '{ print $5 }'"; }
        return command;
    }
    public String getIFTXErrCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -I " + resourceParam + " -ni | " + cmdArray[GREP][1] + " Link | " + cmdArray[AWK][1] + " '{ print $8 }'"; }
        return command;
    }
    public String getIFRXErrCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -I " + resourceParam + " -ni | " + cmdArray[GREP][1] + " Link | " + cmdArray[AWK][1] + " '{ print $6 }'"; }
        return command;
    }
    public String getIFCollisionCommand(String resourceParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -I " + resourceParam + " -ni | " + cmdArray[GREP][1] + " Link | " + cmdArray[AWK][1] + " '{ print $9 }'"; }
        return command;
    }

//  Network
    
    public String getTCPSTATESTABLISHEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+ESTABLISHED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATSYN_SENTCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+SYN_SENT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATSYN_RECVCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+SYN_RECV\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATFIN_WAIT1Command(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+FIN_WAIT1\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATFIN_WAIT2Command(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+FIN_WAIT2\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATTIME_WAITCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+TIME_WAIT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSEDCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+CLOSED\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSE_WAITCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+CLOSE_WAIT\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATLAST_ACKCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+LAST_ACK\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATLISTENCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+LISTEN\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATCLOSINGCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+CLOSING\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
        return command;
    }
    public String getTCPSTATUNKNOWNCommand(Host hostParam)
    {
        if (host.getSysinfo().contains(OS)) { command = cmdArray[NETSTAT][1] + " -an | " + cmdArray[EGREP][1] + " \"tcp.+UNKNOWN\" | " + cmdArray[WC][1] + " -l | " + cmdArray[AWK][1] + " '{ print $1 }'"; }
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

