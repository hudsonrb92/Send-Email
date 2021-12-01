package com.elaudos.email;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailMessageBuilder {
	private Properties properties = System.getProperties();
	private Session session;
	private Message message;
	private Multipart multipart;
	
	public EmailMessageBuilder(String host, String user, String password, String emailTo) throws AddressException, MessagingException {
		this.properties.put("mail.smtp.host", host);
		this.properties.put("mail.smtp.auth", true);
		this.properties.put("mail.smtp.starttls.enable", false);
		this.properties.put("mail.smtp.port", "587");
		this.session = Session.getInstance(this.properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);}});
		this.message = new MimeMessage(this.session);
		this.message.setFrom(new InternetAddress(user));
		this.message.setRecipient(RecipientType.TO, new InternetAddress(emailTo));
		this.multipart = new MimeMultipart();
		this.session.setDebug(true);
	
	}
	
	public void comCopiaPara(String email, RecipientType rct) throws AddressException, MessagingException {
		this.message.addRecipient(rct, new InternetAddress(email));
	}
	
	public void addAttachment(File arquivo) throws IOException, MessagingException {
		try {
			MimeBodyPart bp = new MimeBodyPart();
			bp.attachFile(arquivo);
			this.multipart.addBodyPart(bp);
		} catch (Exception e) {
			System.out.println("Não foi possível adicionar o anexo.");
		}
	}
	public void addSubject(String subject) throws MessagingException {
		this.message.setSubject(subject);
	}
	public void addMessageText(String message) throws MessagingException{
		MimeBodyPart bp = new MimeBodyPart();
		bp.setText(message);
		this.multipart.addBodyPart(bp);
	}
	public void send() throws MessagingException {
		this.message.setContent(this.multipart);
		Transport.send(this.message);
	}
}