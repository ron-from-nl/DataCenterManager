package data;

import java.io.Serializable;

public class DCMUser implements Serializable, Cloneable
{
    
    private long        id;
    private String      username;
    private String      password;
    private boolean     administrator;

    public DCMUser()
    {
        id =                0;
        username =          "";
        password =          "";
        administrator =     true;
    }

    public DCMUser(
                    String usernameParam,
                    String passwordParam,
                    boolean administratorParam
                  )
    {
        id =                0;
        username =          usernameParam;
        password =          passwordParam;
        administrator =     true;
    }

    public DCMUser(
                    long   idParam,
                    String usernameParam,
                    String passwordParam,
                    boolean administratorParam
                  )
    {
        id =                idParam;
        username =          usernameParam;
        password =          passwordParam;
        administrator =     true;
    }

    public long     getId()                 { return id;}
    public String   getUsername()           { return username;}
    public String   getPassword()           { return password;}
    public boolean  getAdministrator()      { return administrator;}
    
    public void     setId(long idParam)                                 { id =       idParam; }
    public void     setUsername(String usernameParam)                   { username = usernameParam; }
    public void     setPassword(String passwordParam)                   { password = passwordParam; }
    public void     setAdministrator(boolean administratorParam)        { administrator = administratorParam; }

    @Override
    public String   toString()
    {
        String output = new String("");
        output += "Id: "                + Long.toString(id) + "\n";
        output += "Username: "          + username + "\n";
        output += "Password: "          + password + "\n";
        output += "Administrator: "     + administrator + "\n";

        return output;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
