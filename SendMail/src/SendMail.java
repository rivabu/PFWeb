import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendMail {

	
	private void process()
	{
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
			
			MimeMessage message = new MimeMessage(s);
			
			InternetAddress from = new InternetAddress("rientsvanburen@gmail.com", "Java");
			InternetAddress to = new InternetAddress("rientsvanburen@gmail.com");
			
			message.setSentDate( new Date() );
			message.setFrom( from );
			message.addRecipient(Message.RecipientType.TO, to);
			
			//message.setSubject("aassddff");
            message.setSubject("dit is de befaamde rients test");
			message.setContent("aassddff", "text/plain");
			
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