package data;

import java.io.Serializable;
import java.util.Calendar;

public class Resource implements Serializable, Cloneable
{
    
    private long        id;
    private long        hostId;
    private String      category;
    private String      resourceType;
    private String      valueType;
    private String      counterType;
    private String      resource;
    private boolean     enabled;
    private String      pollCommand;
    private Double      lastValue;
    private Double      warningLimit;
    private Double      criticalLimit;
    private int         alertPolls;
    private Calendar    updated;
    private String      rrdFile;

    public Resource()
    {
        id =                0L;
        hostId =            0L;
        category =          "";
        resourceType =      "";
        valueType =         "";
        counterType =       "";
        resource =           "";
        enabled =           true;
        pollCommand =       "";
        lastValue =         0D;
        warningLimit =      0D;
        criticalLimit =     0D;
        alertPolls =        0;
        updated =           Calendar.getInstance(); updated.setTimeInMillis(0);
        rrdFile =         "";
    }

    public Resource(
                    long        idParam,
                    long        hostIdParam,
                    String      categoryParam,
                    String      resourceTypeParam,
                    String      valueTypeParam,
                    String      counterTypeParam,
                    String      resourceParam,
                    boolean     enabledParam,
                    String      pollCommandParam,
                    Double      lastValueParam,
                    Double      warningLimitParam,
                    Double      criticalLimitParam,
                    int         alertPollsParam,
                    Calendar    updatedParam,
                    String      rrdFileParam
               )
    {
        id =                idParam;
        hostId =            hostIdParam;
        category =          categoryParam;
        resourceType =      resourceTypeParam;
        valueType =         valueTypeParam;
        counterType =       counterTypeParam;
        resource =          resourceParam;
        enabled =           enabledParam;
        pollCommand =       pollCommandParam;
        lastValue =         lastValueParam;
        warningLimit =      warningLimitParam;
        criticalLimit =     criticalLimitParam;
        alertPolls =        alertPollsParam;
        updated =           updatedParam;
        rrdFile =           rrdFileParam;
    }

    public long     getId()                { return id;}
    public long     getHostId()            { return hostId;}
    public String   getCategory()          { return category;}
    public String   getResourceType()      { return resourceType;}
    public String   getValueType()         { return valueType;}
    public String   getCounterType()       { return counterType;}
    public String   getResource()          { return resource;}
    public boolean  getEnabled()           { return enabled;}
    public String   getPollCommand()       { return pollCommand;}
    public Double   getLastValue()         { return lastValue;}
    public Double   getWarningLimit()      { return warningLimit;}
    public Double   getCriticalLimit()     { return criticalLimit;}
    public int      getAlertPolls()        { return alertPolls;}
    public Calendar getUpdated()           { return updated;}
    public String   getRRDFile()           { return rrdFile;}
    
    public void     setId(long idParam)                         { id =              idParam; }
    public void     setHostId(long hostIdParam)                 { hostId =          hostIdParam; }
    public void     setCategory(String categoryParam)           { category =        categoryParam; }
    public void     setResourceType(String resourceTypeParam)   { resourceType =    resourceTypeParam; }
    public void     setValueType(String valueTypeParam)         { valueType =       valueTypeParam; }
    public void     setCounterType(String counterTypeParam)     { counterType =     counterTypeParam; }
    public void     setResource(String resourceParam)           { resource =        resourceParam; }
    public void     setEnabled(boolean enabledParam)            { enabled =         enabledParam; }
    public void     setPollCommand(String pollCommandParam)     { pollCommand =     pollCommandParam; }
    public void     setLastValue(Double lastValueParam)         { lastValue =       lastValueParam; }
    public void     setWarningLimit(Double warningLimitParam)   { warningLimit =    warningLimitParam; }
    public void     setCriticalLimit(Double criticalLimitParam) { criticalLimit =   criticalLimitParam; }
    public void     setAlertPolls(int alertPollsParam)          { alertPolls =      alertPollsParam; }
    public void     setUpdated(Calendar updatedParam)           { updated =         updatedParam;}
    public void     setArchiveDBFile(String rrdFileParam)       { rrdFile =         rrdFileParam; }

    @Override
    public String toString()
    {
        String output = "";
        output += "Id: "                + Long.toString(id) + "\n";
        output += "HostId: "            + Long.toString(hostId) + "\n";
        output += "Category: "          + category + "\n";
        output += "ResourceType: "      + resourceType + "\n";
        output += "valueType: "         + valueType + "\n";
        output += "counterType: "       + counterType + "\n";
        output += "Resource: "          + resource + "\n";
        output += "enabled: "           + enabled + "\n";
        output += "PollCommand: "       + pollCommand + "\n";
        output += "lastValue: "         + lastValue + "\n";
        output += "WarningLimit: "      + warningLimit + "\n";
        output += "CriticalLimit: "     + criticalLimit + "\n";
        output += "alertPolls: "        + alertPolls + "\n";
        output += "Updated: "           + updated.getTimeInMillis() + "\n";
        output += "rrdFile: "           + rrdFile + "\n";

        return output;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
