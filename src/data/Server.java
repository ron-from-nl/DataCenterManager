package data;

import java.io.Serializable;
import java.util.ArrayList;

public class Server implements Serializable, Cloneable
{
    
    private Host                host;
    private ArrayList<Resource> resourceList;

    public Server()
    {
        host =         new Host();
        resourceList = new ArrayList<Resource>();
    }

    public Server(
                        Host        hostParam,
                        Resource[]  resourceArrayParam
                 )
    {
        host =          hostParam;
        resourceList = new ArrayList<Resource>();
    }

    public Host                 getHost()                           { return host;}
    public ArrayList<Resource>  getResourceList()                   { return resourceList;}
    public void                 setHost(Host hostParam)             { host =            hostParam; }
    public void                 addResource(Resource resourceParam) { resourceList.add(resourceParam); }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
