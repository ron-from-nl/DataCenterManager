import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.util.Properties;

// Do not use this Object directly, but use SendMailMessage object

public class DCMMailClient
{
    // FraJaWeb
    private final String MAILSERVER = "smtp.gmail.com";
    private final String USERNAME = "rszxvhiuyf";
    private final String PASSWORD = "rszxvhiuyf69";

    // WebReus

    public void sendMail(String mailServer, String from, String to, String subject, String textMessageBody, String htmlMessageBody, String[] attachments) throws MessagingException, AddressException
    {
        // Setup mail server
        Properties props = System.getProperties();


        props.put("mail.smtp.host", MAILSERVER );
        props.put("mail.smtp.user", USERNAME );
        props.put("mail.smtp.password", PASSWORD );

        props.put("mail.smtp.port", "25");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.debug", "true");

        // set connection properties
//        URLName url = new URLName("smtp", MAILSERVER, 25, "", USERNAME, PASSWORD);
        URLName url = new URLName("smtp", MAILSERVER, 25, "", USERNAME, PASSWORD);
        PasswordAuthentication passAuth = new PasswordAuthentication(USERNAME,PASSWORD);

        // Get a mail session
        Session session = Session.getDefaultInstance(props, null);
//        session.setPasswordAuthentication(url, passAuth);

        // Define a new mail message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);

        MimeMultipart content = new MimeMultipart("alternative");

        MimeBodyPart textMessage = new MimeBodyPart();
        MimeBodyPart htmlMessage = new MimeBodyPart();

        textMessage.setText(textMessageBody);
        htmlMessage.setContent(htmlMessageBody, "text/html");

        content.addBodyPart(textMessage);
        content.addBodyPart(htmlMessage);

        //Add any file attachments to the message
        if ( attachments.length > 0 ) { addAtachments(attachments, content); }

        //Put all message parts in the message
        message.setContent(content);

        //Send the message

        Transport.send(message);
    }
    public void addAtachments(String[] attachments, Multipart content) throws MessagingException, AddressException
    {
        for(int i = 0; i <= attachments.length -1; i++)
        {
            String filename = attachments[i];
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();

            // User a JAF FileDataSource as it does MIME type detection
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));

            //Assume that the filename you want to send is the same as the actual filename as this can be aletered to exclude the path of the filename
            attachmentBodyPart.setFileName(filename);

            // Add the attachment
            content.addBodyPart(attachmentBodyPart);
        }
    }
}
