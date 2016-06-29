import com.jcraft.jsch.*;
import data.Host;
import expectj.ExpectJ;
import expectj.ExpectJException;
import expectj.Spawn;
import expectj.TimeoutException;
import java.io.IOException;
import java.util.Properties;

public class DCMRemoteCommand
{
    ExpectJ         expect;
    Spawn           spawn; // SSH
    String          ipAddress;
    int             port;
    String          userPassword;
    String          superuserPassword;
    StringBuffer    command;
    int             defaultTimeout = 15;
    int             sessionTimeout;
    boolean         debugging;
    boolean         verbose;
    String          promptFlat;//DCMCommandEnd
    String          superuserPromptFlat;
    String          promptRegex;
    String          superuserPromptRegex;
    String          customPromptFlat;
    String          sysinfo;
    int             stage;
    int             state = 0;
    DCMRemoteCommandCaller remoteCommandCaller;
    private  Host   host = new Host();
    boolean         finalCommand;
    private boolean runThreadsAsDaemons  = true;
    
    private boolean customChannel = true;
    private JSch    jsch;
    private Session session;
    private Channel channel;
    private UserInfo userInfo;
    private Properties sessionConfig;
    private boolean loginSuccess;
    
//  Phase I Login, this is where it starts
    public DCMRemoteCommand(DCMRemoteCommandCaller callerParam, int stageParam, Host hostParam, StringBuffer commandParam, boolean finalCommandParam, int sessionTimeoutParam, boolean debuggingParam) throws CloneNotSupportedException // finalCommand reports back to caller, whom then can do DataConversion
    {
        // Generic objects
        remoteCommandCaller     = callerParam;
        stage                   = stageParam;
        host                    = (Host) hostParam.clone();
        finalCommand            = finalCommandParam;
        command                 = commandParam;
        sessionTimeout          = sessionTimeoutParam;
        debugging               = debuggingParam;
        if (host.getSuperuserPassword().length()==0)
        {
            promptFlat =            "$";
            promptRegex =           "[>$] ";
            superuserPromptFlat =   "#";
            superuserPromptRegex =  "#";
            customPromptFlat =      "DCMCommandEnd";
        }
        else
        {
            promptFlat =            "$";
            promptRegex =           "[>$] ";
            superuserPromptFlat =   "#";
            superuserPromptRegex =  "#";
            customPromptFlat =      "DCMCommandEnd";
        }
//        expect                  = new ExpectJ(defaultTimeout);
        expect                  = new ExpectJ(sessionTimeout); // Timeout Seconds

        loginSuccess = true;

        if (customChannel)
        {
            jsch = new JSch();
            try { session = jsch.getSession(host.getUsername(), host.getHostname(), host.getPort()); }
            catch (JSchException ex) { remoteCommandCaller.log("Error:   DCMRemoteCommand: session = jsch.getSession(" + host.getUsername() + ", " + host.getHostname() + ", " + host.getPort() + ");", false, true, true); loginSuccess = false; }
//            session.setPassword(host.getUserPassword());
            session.setPassword(host.getUserPassword());

            sessionConfig = new Properties();
            sessionConfig.put("StrictHostKeyChecking", "no");
            sessionConfig.put("userauth.keyboard-interactive", "com.jcraft.jsch.UserAuthKeyboardInteractive");
            sessionConfig.put("PreferredAuthentications","keyboard-interactive,password");
            session.setConfig(sessionConfig);


//            session.setConfig("PreferredAuthentications","keyboard-interactive,password,gssapi-with-mic");

//            session.setPassword(host.getUserPassword());

//            userInfo = new UserInfo()
//            {
//                public String getPassphrase() { return host.getUserPassword(); }
//                public String getPassword() { return host.getUserPassword(); }
//                public boolean promptPassword(String string) { return true; }
//                public boolean promptPassphrase(String string) { return true; }
//                public boolean promptYesNo(String string) { return true; }
//                public void showMessage(String message) { remoteCommandCaller.log("Info:    DCMRemoteCommand: showMessage: " + message, true, true, true); }
//            };
            
//            userInfo = session.getUserInfo();
//            userInfo.promptYesNo("Yes");
//            userInfo.promptPassphrase(host.getUserPassword());
//            session.setUserInfo(userInfo);

            if (debugging)
            {
                remoteCommandCaller.log("Action:  SSHLogin to " + host.getUsername() + ":"+ host.getUserPassword() + "@" + host.getHostname(), false, true, true);
            }
            else
            {
                remoteCommandCaller.log("Action:  DCMRemoteCommand: SSHLogin to " + host.getUsername() + "@" + host.getHostname(), false, true, true);
            }

            try { session.connect(sessionTimeout*1000); } catch (JSchException ex) { remoteCommandCaller.log("Error:   DCMRemoteCommand: session.connect()); (" + ex.getMessage() + ")", true, true, true); loginSuccess = false; }
            if (loginSuccess) { remoteCommandCaller.log("Success: DCMRemoteCommand: Session Connected Successfully.", true, true, true); }
            
            if (loginSuccess) { channel = null; try { channel = session.openChannel("shell"); } catch (JSchException ex) { remoteCommandCaller.log("Error:   DCMRemoteCommand: channel = session.openChannel(\"shell\"); (" + ex.getMessage() + ")", true, true, true); loginSuccess = false; } }
            if (loginSuccess) { remoteCommandCaller.log("Success: DCMRemoteCommand: Channel Opened Successfully.", true, true, true); }
            if (loginSuccess) { spawn = null; try { spawn = expect.spawn(channel); } catch (IOException ex) { remoteCommandCaller.log("Error:   DCMRemoteCommand: spawn = expect.spawn(channel)); (" + ex.getMessage() + ")", true, true, true); loginSuccess = false; } }
        }
        else // No custom channel
        {
            try { spawn = expect.spawn(host.getHostname(),host.getPort(),host.getUsername(),host.getUserPassword()); }
            catch (IOException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error: Login Failure: Could not login to " + host.getUsername() + "@" + host.getHostname() + " (" + ex.getMessage()+ ")"); loginSuccess = false;}            
        }

        // Logged on, continue inside remote shell
        if (loginSuccess)
        {
            remoteCommandCaller.log("Success: DCMRemoteCommand: Login successful for host " + host.getHostname(),true,true,true);
            try { Thread.sleep(500); } catch (InterruptedException ex) {  } // Give the MOTD and welcome some time to print etc.
            if (host.getSuperuserPassword().length()==0)
            {
                cmd(command.toString()); 
            }
            else
            {
                supasswd(host.getSuperuserPassword());
            }
        }
        else
        {
            closeShell(1);
            remoteCommandCaller.remoteCommandFailureResponse(stage, "Error:   DCMRemoteCommand: Login failed for host " + host.getHostname());
        }
    }

//  Phase II CMD
    private synchronized void cmd(String commandParam)
    {
        String response; response = "";

        remoteCommandCaller.log("Action:  DCMRemoteCommand: " + host.getHostname() + ": sending command: " + commandParam, false, true, true);
        try { spawn.send(commandParam + "\n"); } catch (IOException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error: " + host.getHostname() + ": IOException: RemoteCommand.cmd sshConnection.send(commandParam)" + ex.getMessage()); }

        try {spawn.expect(customPromptFlat,sessionTimeout);}
        catch (IOException ex)      { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error: " + host.getHostname() + ":IOException: RemoteCommand.cmd sshConnection.expect(\"" + customPromptFlat + "\"," + sessionTimeout + ")" + ex.getMessage()); }
        catch (TimeoutException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error: " + host.getHostname() + ":TimeoutException: RemoteCommand.cmd sshConnection.expect(\"" + customPromptFlat + "\"," + sessionTimeout + ")" + ex.getMessage()); }

        try { Thread.sleep(500); } catch (InterruptedException ex) {  } // Give the server some time to print to terminal
        
        String stdout = spawn.getCurrentStandardOutContents(); if (stdout == null) { stdout = "-"; }
        String stderror = spawn.getCurrentStandardErrContents(); if (stderror == null) { stderror = "-"; }

        if (debugging)
        {
            remoteCommandCaller.log("STDOUT: " + host.getHostname() + ": " + stdout ,true,true,true);
            remoteCommandCaller.log("STDERR: " + host.getHostname() + ": " + stderror ,true,true,true);
        }
        
        if (finalCommand)
        {
            closeShell(0);
            remoteCommandCaller.remoteFinalCommandSuccessResponse(stage, stdout, stderror );
        }
        else
        {
            closeShell(0);
            remoteCommandCaller.remoteCommandSuccessResponse(stage, stdout, stderror );
        }
    }

//  Phase Ia Login
    private synchronized void supasswd(String superuserPasswordParam)
    {
        String response; response = "";
        boolean suSuccessful = false;

        // The sending / invoking su part
        remoteCommandCaller.log("Action:  DCMRemoteCommand: " + host.getHostname() + ": sending su", false, true, true);
        try { spawn.send("su\n"); } catch (IOException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error: " + host.getHostname() + ": IOException: RemoteCommand.supasswd: sshConnection.send(su)" + ex.getMessage()); }

        try {spawn.expect("Password",2);}
        catch (IOException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, ex.getMessage()); }
        catch (TimeoutException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, ex.getMessage()); }
        
        try { Thread.sleep(500); } catch (InterruptedException ex) {  }
        
        response = spawn.getCurrentStandardOutContents();
        //remoteCommandCaller.log(response, true, true, true);
//        userInterface.log(serverInstance, "shell.getCurrentStandardOutContents(): " + shell.getCurrentStandardOutContents(), false, true, true);

//      Regular User Phase
        if(response.contains("Password"))
        {
            remoteCommandCaller.log("\nSuccess: DCMRemoteCommand: " + host.getHostname() + ": Password matched", false, true, true);
            if (debugging)
            {
                remoteCommandCaller.log("Action:  DCMRemoteCommand: " + host.getHostname() + ": sending password: " + host.getSuperuserPassword(), false, true, true);
            }
            else
            {
                remoteCommandCaller.log("Action:  DCMRemoteCommand: " + host.getHostname() + ": sending password: " + superuserPasswordParam.replaceAll(".", "*"), false, true, true);
            }

            // Sending the superuser password
            try { spawn.send(superuserPasswordParam + "\r\n"); } catch (IOException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error: " + host.getHostname() + ": IOException: RemoteCommand.supasswd sshConnection.send(superuserPassword)" + ex.getMessage()); }
            try {spawn.expect(superuserPromptFlat,sessionTimeout);}
            catch (IOException ex)      { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error:   DCMRemoteCommand: " + host.getHostname() + ": IOException: RemoteCommand.supasswd sshConnection.expect(\"" + superuserPromptFlat + "\"," + sessionTimeout + ")" + ex.getMessage()); }
            catch (TimeoutException ex) { remoteCommandCaller.remoteCommandFailureResponse(stage, "Error:   DCMRemoteCommand: " + host.getHostname() + ": TimeoutException: RemoteCommand.supasswd sshConnection.expect(\"" + superuserPromptFlat + "\"," + sessionTimeout + ")" + ex.getMessage()); }
            
            try { Thread.sleep(500); } catch (InterruptedException ex) {  }
            
            response = spawn.getCurrentStandardOutContents();
            //remoteCommandCaller.log(response, true, true, true);

            if(response.contains(superuserPromptFlat))
            {
                suSuccessful = true;
                remoteCommandCaller.log("\nSuccess: DCMRemoteCommand: " + host.getHostname() + ": su successful", false, true, true);
                remoteCommandCaller.log("Action:  DCMRemoteCommand: " + host.getHostname() + ": jumping to cmd", false, true, true);
            }
            else if(response.contains("incorrect"))
            {
                suSuccessful = false;
                closeShell(1);
                remoteCommandCaller.remoteCommandFailureResponse(stage, "incorrect password: " + response);
            }
            else if(response.contains("Sorry"))
            {
                suSuccessful = false;
                closeShell(1);
                remoteCommandCaller.remoteCommandFailureResponse(stage, "su was Sorry: " + response);
            }
            else
            {
                suSuccessful = false;
                closeShell(1);
                remoteCommandCaller.remoteCommandFailureResponse(stage, "Error:   DCMRemoteCommand: Unexpected response after superuser password: " + response);
            }
        }
        else
        {
            suSuccessful = false;
            closeShell(1);
            remoteCommandCaller.remoteCommandFailureResponse(stage, "Error:   DCMRemoteCommand: No Password response after invoking su: " + response);
        }

        if (suSuccessful)
        {
            cmd(command.toString());
        }
        else
        {
            closeShell(1);
            remoteCommandCaller.remoteCommandFailureResponse(stage, "Error:   DCMRemoteCommand: su login failed");
        }
    }

    public synchronized String getCommandOutput()
    {
        return spawn.getCurrentStandardOutContents();
    }
    
    private synchronized void closeShell(int exitCode)
    {
        remoteCommandCaller.log("Action:  DCMRemoteCommand: " + host.getHostname() + ": Closing Session with exit code: " + exitCode, true, true, true);
        
        if ( (spawn != null) && (!spawn.isClosed()))
        {
            try { spawn.expectClose(1); } // Can not be < 1 !!!
            catch (ExpectJException ex) { remoteCommandCaller.log("Error:   DCMRemoteCommand: " + host.getHostname() + ": ExpectJException: spawn.expectClose(1)" + ex.getMessage(), true, true, true); }
            catch (TimeoutException ex) { /*userInterface.log(serverInstance, "Error: TimeoutException: shell.expectClose(1)" + ex.getMessage(), false, true, true);*/ }            
        }
                
        if ( (spawn != null) && (!spawn.isClosed())) { spawn.stop(); }
        if (customChannel);
        {
            if ((session != null) && (session.isConnected())) { session.disconnect(); if (! session.isConnected()) { remoteCommandCaller.log("Success: DCMRemoteCommand: Session Closed Successfully.", true, true, true); } else { remoteCommandCaller.log("Error:   DCMRemoteCommand: Session dit NOT Close!", true, true, true); } }
            if ((channel != null) && (channel.isConnected())) { channel.disconnect(); if (! channel.isConnected()) { remoteCommandCaller.log("Success: DCMRemoteCommand: Channel Closed Successfully.", true, true, true); } else { remoteCommandCaller.log("Error:   DCMRemoteCommand: Channel dit NOT Close!", true, true, true); } }
        }

        if (spawn != null)
        {
            if (spawn.isClosed()) { remoteCommandCaller.log("Success: DCMRemoteCommand: Spawner Closed Successfully.", true, true, true); } else { remoteCommandCaller.log("Error:   DCMRemoteCommand: Spawner did NOT Close!", true, true, true); }
        }
    }
}