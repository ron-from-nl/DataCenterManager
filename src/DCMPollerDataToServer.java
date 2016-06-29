import data.Resource;
import data.Server;
import java.util.Calendar;

public class DCMPollerDataToServer // Being called by PollServer to convert host & resources txt data to a server object(host,resources[])
{
    public Server convertPollerDataToServer(Server serverParam, String dataParam) throws CloneNotSupportedException
    {
//        String headerKey = "DCMPoller:" + serverParam.getHost().getHostname() + ":";
        boolean valueFetchSucceeded = true;
        for (Resource resource:serverParam.getResourceList()) // Itterate through every resources in server
        {
//            String line = Tools.grep(dataParam, headerKey + " " + resource.getId() + " "); // grep in data for the resource id (which is a certain line in data)
//            String value = line.replace(headerKey + " " + resource.getId() + " ", ""); // get rid of the correlation part before the value
            String line = DCMTools.startsWith(dataParam, resource.getId() + " "); // grep in data for the resource id (which is a certain line in data)
            String value = line.replace(resource.getId() + " ", ""); // get rid of the correlation part before the value
//          System.out.println("DConv - Line: " + line);
//          resource.setLastValue(Double.parseDouble(value));
        
            try { resource.setLastValue(Double.parseDouble(value)); } catch (NumberFormatException ex) { resource.setLastValue(Double.NaN); valueFetchSucceeded = false;}
//            System.out.println("DConv - ResourceId: " + resource.getId() + " value: " + resource.getLastValue());
            if (valueFetchSucceeded) {resource.setUpdated(Calendar.getInstance());}
        }
        return (Server) serverParam.clone(); // serverParam is just one unique reference, that's why I can send it back
    }
}
