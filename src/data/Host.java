package data;

import java.io.Serializable;
import java.util.Calendar;

public class Host implements Serializable, Cloneable
{
    
    private long        id;
    private Calendar    created;
    private String      hostname;
    private int         port;
    private String      username;
    private String      userPassword;
    private String      superuserPassword;
    private String      sysinfo;
    private boolean     enabled;
    private String      contactEmail;
    private Calendar    lastPolled;
    private String      errors;
    private String      command;
    private String      comment2;
    private String      comment3;

    public Host()
    {
        id =                0L;
        created =           Calendar.getInstance();
        hostname =          "";
        port =              22;
        username =          "";
        userPassword =      "";
        superuserPassword = "";
        sysinfo =           "";
        enabled =           true;
        contactEmail =      "";
        lastPolled =        Calendar.getInstance(); lastPolled.setTimeInMillis(0);
        errors =            "";
        command =           "";
        comment2 =          "";
        comment3 =          "";
    }

    public Host(
                    String hostParam,
                    int    portParam,
                    String usernameParam,
                    String userPasswordParam
               )
    {
        id =                0L;
        created =           Calendar.getInstance();
        hostname =          hostParam;
        port =              portParam;
        username =          usernameParam;
        userPassword =      userPasswordParam;
        superuserPassword = "";
        sysinfo =           "";
        enabled =           true;
        contactEmail =      "";
        lastPolled =        Calendar.getInstance(); lastPolled.setTimeInMillis(0);
        errors =            "";
        command =           "";
        comment2 =          "";
        comment3 =          "";
    }

    public Host(
                    String hostnameParam,
                    int    portParam,
                    String usernameParam,
                    String userPasswordParam,
                    String superuserPasswordParam
               )
    {
        id =                0L;
        created =           Calendar.getInstance();;
        hostname =          hostnameParam;
        port =              portParam;
        username =          usernameParam;
        userPassword =      userPasswordParam;
        superuserPassword = superuserPasswordParam;
        sysinfo =           "";
        enabled =           true;
        contactEmail =      "";
        lastPolled =        Calendar.getInstance(); lastPolled.setTimeInMillis(0);
        errors =            "";
        command =           "";
        comment2 =          "";
        comment3 =          "";
    }

    public Host(
                    long idParam,
                    Calendar createdParam,
                    String hostParam,
                    int    portParam,
                    String userParam,
                    String userPasswordParam,
                    String superuserPasswordParam,
                    String sysinfoParam,
                    boolean enabledParam,
                    String contactEmailParam,
                    Calendar lastPolledParam,
                    String errorsParam,
                    String commandParam,
                    String comment2Param,
                    String comment3Param
               )
    {
        id =                idParam;
        created =           createdParam;
        hostname =          hostParam;
        port =              portParam;
        username =          userParam;
        userPassword =      userPasswordParam;
        superuserPassword = superuserPasswordParam;
        sysinfo =           sysinfoParam;
        enabled =           enabledParam;
        contactEmail =      contactEmailParam;
        lastPolled =        lastPolledParam;
        errors =            errorsParam;
        command =           commandParam;
        comment2 =          comment2Param;
        comment3 =          comment3Param;
    }

    public long     getId()                 { return id;}
    public Calendar getCreated()            { return created;}
    public String   getHostname()           { return hostname;}
    public int      getPort()               { return port;}
    public String   getUsername()           { return username;}
    public String   getUserPassword()       { return userPassword;}
    public String   getSuperuserPassword()  { return superuserPassword;}
    public String   getSysinfo()            { return sysinfo;}
    public boolean  getEnabled()            { return enabled;}
    public String   getContactEmail()       { return contactEmail;}
    public Calendar getLastPolled()         { return lastPolled;}
    public String   getErrors()             { return errors;}
    public String   getCommand()            { return command;}
    public String   getComment2()           { return comment2;}
    public String   getComment3()           { return comment3;}
    
    public void     setId(long idParam)                                 { id =                  idParam; }
    public void     setCreated(Calendar createdParam)                   { created =             createdParam;}
    public void     setHostname(String hostParam)                       { hostname =            hostParam; }
    public void     setPort(int portParam)                              { port =                portParam; }
    public void     setUsername(String userParam)                       { username =            userParam; }
    public void     setUserPassword(String userPasswordParam)           { userPassword =        userPasswordParam; }
    public void     setSuperuserPassword(String superuserPasswordParam) { superuserPassword =   superuserPasswordParam; }
    public void     setSysinfo(String sysinfoParam)                     { sysinfo =             sysinfoParam; }
    public void     setEnabled(boolean enabledParam)                    { enabled =             enabledParam; }
    public void     setContactEmail(String contactEmailParam)           { contactEmail =        contactEmailParam; }
    public void     setLastPolled(Calendar lastPolledParam)             { lastPolled =          lastPolledParam; }
    public void     setErrors(String errorsParam)                       { errors =              errorsParam; }
    public void     addCommand(String commandParam)                     { command +=            commandParam; }
    public void     setCommand(String commandParam)                     { command =             commandParam; }
    public void     setComment2(String comment2Param)                   { comment2 =            comment2Param; }
    public void     setComment3(String comment3Param)                   { comment3 =            comment3Param; }

    @Override
    public String   toString()
    {
        String output = new String("");
        output += "Id: "                + Long.toString(id) + "\n";
        output += "Created: "           + created.getTimeInMillis() + "\n";
        output += "Hostname: "          + hostname + "\n";
        output += "Port: "              + port + "\n";
        output += "Username: "          + username + "\n";
        output += "userPassword: "      + userPassword + "\n";
        output += "superuserPassword: " + superuserPassword + "\n";
        output += "Sysinfo: "           + sysinfo + "\n";
        output += "enabled: "           + enabled + "\n";
        output += "contactEmail: "      + contactEmail + "\n";
        output += "lastPolled: "        + lastPolled.getTimeInMillis() + "\n";
        output += "errors: "            + errors + "\n";
        output += "command: "           + command + "\n";
        output += "comment2: "          + comment2 + "\n";
        output += "comment3: "          + comment3 + "\n";

        return output;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
