import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail {

    private void process() {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.localhost", "yoursite.com");

            Session s = Session.getInstance(props, null);
            s.setDebug(true);

            // MimeMessage message = new MimeMessage(s);
            Message message = new MimeMessage(s);

            InternetAddress from = new InternetAddress("rientsvanburen@gmail.com", "Java");
            InternetAddress me = new InternetAddress("rientsvanburen@gmail.com");
            InternetAddress toIng = new InternetAddress("Rients.van.Buren@ing.nl");
            

            message.setSentDate(new Date());
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, me);
            message.addRecipient(Message.RecipientType.TO, toIng);

            // message.setSubject("aassddff");
            message.setSubject("dit is de befaamde rients test");
            message.setContent("aassddff", "text/plain");

            // create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // fill message
            messageBodyPart.setText("Test mail one");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String fileName = "d:\\PFData\\temp\\sample.pdf";
           // File fileAttachment = new File("d:\\PFData\\temp\\sample.pdf");
            DataSource source = new FileDataSource(fileName);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);
            // Put parts in message
            message.setContent(multipart);

            Transport tr = s.getTransport("smtp");
            tr.connect("smtp.gmail.com", "rientsvanburen@gmail.com", "straal01");
            message.saveChanges();
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();
            System.out.println("gelukt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new SendMail().process();
    }
}