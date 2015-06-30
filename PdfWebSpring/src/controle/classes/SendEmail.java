package controle.classes;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


public class SendEmail
{
	
	//enviar para
    private String to; 

    //enviado por
    private String from; // = "visoestester@gmail.com";
    
    //acesso ao smtp
    private String user;
    private String password;    
    
    //usando smtp gmail e tsl na porta 587
    private String host = "smtp.gmail.com";
    private String port = "587";
    
    private Session session;
    private  Properties properties = System.getProperties();
    
    
    //String user = "visoestester@gmail.com";
    //String password = "visoestester!!";
	public SendEmail(String to, String from){
		this.to = to;
		this.from = from;
		this.user = "visoestester@gmail.com";
		this.password ="visoestester!!";
		
		propertiesSetup();
		sessionSetup();
	}
	
	private void propertiesSetup(){
	  // Setup mail server
      properties.put("mail.smtp.starttls.enable", "true"); 
      properties.put("mail.smtp.host", host);
      properties.put("mail.smtp.user", user); 
      properties.put("mail.smtp.password", password);
      properties.put("mail.smtp.port", port);
      properties.put("mail.smtp.auth", "true");
	}
	
	//necessario para autenticacao
	private void sessionSetup(){
		session = Session.getDefaultInstance(properties, 
    		    new javax.mail.Authenticator(){
    		        protected PasswordAuthentication getPasswordAuthentication() {
    		            return new PasswordAuthentication(
    		                "visoestester@gmail.com", "visoestester!!");// Specify the Username and the PassWord
    		        }
    		});
	}
	
	public boolean sendMessage(String assunto, String mensagem){
	      try{
	          // Create a default MimeMessage object.
	          MimeMessage message = new MimeMessage(session);
	          message.setFrom(new InternetAddress(from));
	          message.addRecipient(Message.RecipientType.TO,
	                                   new InternetAddress(to));

	          //assunto do email
	          message.setSubject(assunto);

	          //mensagem do email
	          message.setText(mensagem);

	          // Send message
	          Transport.send(message);
	         
	       }catch (MessagingException mex) {
	          mex.printStackTrace();
	          return false;
	       }
	      return true;
	}
	
	public boolean sendHTMLMessage(String assunto, String mensagem){
	      try{
	          // Create a default MimeMessage object.
	          MimeMessage message = new MimeMessage(session);
	          message.setFrom(new InternetAddress(from));
	          message.addRecipient(Message.RecipientType.TO,
	                                   new InternetAddress(to));

	          //assunto do email
	          message.setSubject(assunto);

	          //mensagem do email, pode usar tags html
	         message.setContent(mensagem,
	                  "text/html" );

	          // Send message
	          Transport.send(message);
	         
	       }catch (MessagingException mex) {
	          mex.printStackTrace();
	          return false;
	       }
	      return true;
	}
	
	

	
}