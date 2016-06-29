import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DCMSendMailTLS extends Authenticator {
 
    public DCMSendMailTLS(String subjectParam, String messageParam)
    {
            String host = "smtp.gmail.com";
            int port = 587;
            String username = "rszxvhiuyf";
            String password = "rszxvhiuyf69";
            String from = "rszxvhiuyf@gmail.com";
            String to = "rszxvhiuyf@gmail.com";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
//            props.put("mail.debug", "true");
        
            Session session = Session.getInstance(props, new DCMMailAuthenticator(username,password));

            try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("rszxvhiuyf@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse("rszxvhiuyf@gmail.com"));
                    message.setSubject(subjectParam);
                    message.setText(messageParam);

                    Transport transport = session.getTransport("smtp");
                    transport.connect(host, port, username, password);

                    Transport.send(message);
            }
            catch (MessagingException e) { System.out.println("MessagingException: " + e.getMessage()); }        
    }    
}