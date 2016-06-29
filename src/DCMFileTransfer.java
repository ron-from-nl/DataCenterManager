import com.jcraft.jsch.*;
import data.Host;
import java.io.*;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DCMFileTransfer
{
    private Host host;
    private String file;
    private Session session;
    private Channel channel;
    private DCMFileTranferCaller DCMFileTransferCaller;

    public DCMFileTransfer(DCMFileTranferCaller dmcFileTransferCallerParam, Host hostParam, String scriptfileParam) throws CloneNotSupportedException
    {
        DCMFileTransferCaller = dmcFileTransferCallerParam;
        host = new Host();
        host = (Host) hostParam.clone();
        file = new String();
        file = scriptfileParam;
        
        
        FileInputStream fileInputStream = null;
        try
        {
//          String lfile=scriptfileParam;
//          String user=hostParam.getUsername();
//          String host=hostParam.getHostname();
//          String rfile=scriptfileParam;;

          JSch jsch=new JSch();
          session = jsch.getSession(host.getUsername(), host.getHostname(), hostParam.getPort());

          // username and password will be given via UserInfo interface.
          UserInfo userInfo = new MyUserInfo();
          session.setUserInfo(userInfo);
          session.connect();
          
          if (session.isConnected()) { DCMFileTransferCaller.log("Success: DCMFileTransfer: Session Connected Successfully.", true, true, true); } else { DCMFileTransferCaller.log("Error:  DCMFileTransfer: Session NOT Connected!", true, true, true); }

//        exec 'scp -t rfile' remotely
          
          String command="scp -p -t " + file;
          channel = session.openChannel("exec");
          ((ChannelExec)channel).setCommand(command);

          // get I/O streams for remote scp
          OutputStream outputStream = channel.getOutputStream();
          InputStream inputStream = channel.getInputStream();

          channel.connect(); if (channel.isConnected()) { DCMFileTransferCaller.log("Success: DCMFileTransfer: Channel(exec) " + command + " Connected Successfully.", true, true, true); } else { DCMFileTransferCaller.log("Error:  DCMFileTransfer: Channel NOT Connected!", true, true, true); }

          if (checkAck(inputStream) != 0)
          {
//              DCMFileTransferCaller.log("Error 1: DCMFileTransfer: FileTransfer: checkAck(in) != 0",true,true,true);
              DCMFileTransferCaller.log("Error:   DCMFileTransfer: Channel InputStream1 after channel.connect() Failed", true, true, true);
          }
          else
          {
              DCMFileTransferCaller.log("Success: DCMFileTransfer: Channel InputStream1 after channel.connect() Successful", true, true, true);
          }

          // send "C0644 filesize filename", where filename should not include '/'
          
          long filesize = (new File(file)).length(); command = "C0644 " + filesize + " ";
          if ( file.lastIndexOf('/') > 0 ){ command += file.substring(file.lastIndexOf('/') +1 ); } else { command += file; } command += "\n";

          DCMFileTransferCaller.log("Action:  DCMFileTransfer: Writing Channel OutputStream", true, true, true);              
          
          outputStream.write(command.getBytes()); outputStream.flush();
          
          if (checkAck(inputStream) != 0)
          {
              DCMFileTransferCaller.log("Error:   DCMFileTransfer: Channel InputStream2 after outputStream.write(..) Failed", true, true, true);
//            DCMFileTransferCaller.log("Error 2: DCMFileTransfer: FileTransfer: checkAck(in) != 0", true,true,true);
          }
          else
          {
              DCMFileTransferCaller.log("Success: DCMFileTransfer: Channel InputStream2 after outputStream.write(..) Successful", true, true, true);
          }

          // Buffering Input Stream
                    
          fileInputStream = new FileInputStream(file);
          byte[] buffer = new byte[1024];
          
          while(true) // Read bye for bye and break
          {
            int fileLength = fileInputStream.read(buffer, 0, buffer.length); // Read filelength bytes from inputStream
            if( fileLength <= 0) break;
            outputStream.write(buffer, 0, fileLength); //out.flush();
          }
          
          fileInputStream.close();
          fileInputStream = null;
          
          // Sending Buffer to Output Stream
          buffer[0] = 0; outputStream.write(buffer, 0, 1); outputStream.flush();
          
          
          // Shouldn't this be checkOut ?????????????????????
          if (checkAck(inputStream) != 0)
          {
              DCMFileTransferCaller.log("Error:   DCMFileTransfer: InputStream after outputStream.write(..) Failed", true, true, true);
              //DCMFileTransferCaller.log("Error 3: DCMFileTransfer: FileTransfer: checkAck(in) != 0",true,true,true); 
          }
          else
          {
              DCMFileTransferCaller.log("Success: DCMFileTransfer: InputStream after outputStream.write(..) Successful", true, true, true);
          }
          
          outputStream.close();

          channel.disconnect();
          if (!channel.isConnected()) { DCMFileTransferCaller.log("Success: DCMFileTransfer: Channel Disconnected Successfully.", true, true, true); } else { DCMFileTransferCaller.log("Error:  DCMFileTransfer: Channel NOT Disconnected!", true, true, true); }
          session.disconnect();
          if (!channel.isConnected()) { DCMFileTransferCaller.log("Success: DCMFileTransfer: Session Disconnected Successfully.", true, true, true); } else { DCMFileTransferCaller.log("Error:  DCMFileTransfer: Session NOT Disconnected!", true, true, true); }

//          System.exit(0);
        }
        catch(Exception e)
        {
          DCMFileTransferCaller.log("Error3:   DCMFileTransfer: Exception: Codeblock Failure: " + e.getMessage(),true,true,true);
          try
          {
              if ( fileInputStream != null ) fileInputStream.close();
          } 
          catch(Exception ee) { DCMFileTransferCaller.log("Error3:   DCMFileTransfer: Exception: fileInputStream.close() " + e.getMessage(),true,true,true); }
        }
    }
    
  public static void main(String[] arg)
  {
  }

  static int checkAck(InputStream inputStream) throws IOException
  {
    int b = inputStream.read();
    // b may be 0 for success,
    //          1 for error,
    //          2 for fatal error,
    //          -1
    if( b == 0  ) return b;
    if( b == -1 ) return b;

    if( b == 1 || b == 2 )
    {
      StringBuffer stringBuffer = new StringBuffer();
      int character; do { character = inputStream.read(); stringBuffer.append((char)character); }
      
      while( character != '\n' );
      
      // error
      if ( b == 1 ) { System.out.print(stringBuffer.toString()); }
      
      // fatal error
      if( b == 2 ) { System.out.print(stringBuffer.toString()); }
    }
    return b;
  }

    private void log(String message, boolean logToStatus, boolean logToApplication, boolean logToFile)
    {
        if (logToStatus) { System.out.println(message); }
    }        

  public class MyUserInfo implements UserInfo, UIKeyboardInteractive
  {
    @Override public String getPassword(){ return host.getUserPassword(); }
    @Override public boolean promptYesNo(String str){return true;}
    String passwd=host.getUserPassword();
    JTextField passwordField=(JTextField)new JPasswordField(20);

    @Override public String getPassphrase(){ return null; }
    @Override public boolean promptPassphrase(String message){ return true; }
    @Override public boolean promptPassword(String message){return true;}
    @Override public void showMessage(String string) { System.out.println(string); }
    @Override public String[] promptKeyboardInteractive(String string, String string1, String string2, String[] strings, boolean[] blns) { String[] pw = new String(host.getUserPassword()).split(string); return pw; }    
  }
}