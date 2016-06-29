package data;

import java.io.Serializable;

public class RMIMessage implements Serializable
{
    private String  messageType;
    private String  objectMethod;
    private String  logMessage;
    private boolean status;
 
    public RMIMessage(String messageTypeParam, String objectMethodParam, String logMessageParam, boolean statusParam)
    {
        messageType =   messageTypeParam;
        objectMethod =  objectMethodParam;
        logMessage =    logMessageParam;
        status =        statusParam;
    }
    
    public String   getMessageType()                            { return messageType; }
    public String   getObjectMethod()                           { return objectMethod; }
    public String   getLogMessage()                             { return logMessage; }
    public boolean  getStatus()                                 { return status; }
    
    public void     setMessageType(String messageTypeParam)     { messageType = messageTypeParam; }
    public void     setObjectMethod(String objectMethodParam)   { objectMethod = objectMethodParam; }
    public void     setLogMessage(String logMessageParam)       { logMessage = logMessageParam + "\r\r"; }
    public void     setStatus(boolean statusParam)              { status = statusParam; }
    
    public void     addLogMessage(String logMessageParam)       { logMessage += logMessageParam + "\r\n"; }
    
    @Override
    public String   toString()
    {
        String output = "";
        output += "MessageType: " + messageType + " ObjectMethod: " + objectMethod + " LogMessage: " + logMessage + " Status: " + status;
        return output;
    }
}
