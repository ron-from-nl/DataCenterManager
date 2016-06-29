import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Calendar;

public class DCMSysMonitor
{
    private OperatingSystemMXBean bean;

    private Calendar    currCPUCalendar;
    private Calendar    lastCPUCalendar;
    private long        currCPUMilliesUsed = 0;
    private long        lastCPUMilliesUsed = 0;

    private long        currMemValue = 0;

    public DCMSysMonitor()
    {
        bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean( );

        currCPUCalendar = Calendar.getInstance();
        lastCPUCalendar = Calendar.getInstance();
    }

    public int getProcessTime()
    {
        int cpuPercent = 0;

        if ( (bean instanceof com.sun.management.OperatingSystemMXBean) )
        {
            currCPUMilliesUsed = (bean.getProcessCpuTime()/1000000);

            currCPUCalendar = Calendar.getInstance();
            long milliesPassed = (currCPUCalendar.getTimeInMillis() - lastCPUCalendar.getTimeInMillis());
            long cpuMilliesDiff = (currCPUMilliesUsed - lastCPUMilliesUsed);
            cpuPercent = (int)Math.round((cpuMilliesDiff / (milliesPassed * 0.001))/10);
            if (cpuPercent > 100) {cpuPercent = 100;}
            lastCPUMilliesUsed = currCPUMilliesUsed;
            lastCPUCalendar.setTimeInMillis(currCPUCalendar.getTimeInMillis());
        }
        else
        {
            cpuPercent = 50;
        }
        return cpuPercent;
    }

    public int getPhysMem()
    {
        if ( (bean instanceof com.sun.management.OperatingSystemMXBean) )
        {
            currMemValue = (bean.getFreePhysicalMemorySize() / ( 1024 * 1024 ));
        }
        else
        {
            currMemValue = 100;
        }
        return (int)currMemValue;
    }
}
