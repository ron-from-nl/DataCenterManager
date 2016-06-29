import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


class DCMMailAuthenticator extends Authenticator {
     String user;
     String pw;
     public DCMMailAuthenticator (String username, String password)
     {
        super();
        this.user = username;
        this.pw = password;
     }
     
    @Override
    public PasswordAuthentication getPasswordAuthentication()
    {
       return new PasswordAuthentication(user, pw);
    }
}