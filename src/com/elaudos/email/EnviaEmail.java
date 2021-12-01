package com.elaudos.email;
import java.io.File;
import java.io.IOException;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class EnviaEmail {

	private File anexo;
	//private final String suporteEmail = "suporte@elaudos.com";
	private EmailMessageBuilder emailMessageBuilder;

	public EnviaEmail(String emailTo, String textoEmail, String subject, String emailFrom, String host, String password) throws AddressException, MessagingException {
		this.emailMessageBuilder = new EmailMessageBuilder(host, emailFrom, password, emailTo);
		this.emailMessageBuilder.addSubject(subject);
		this.emailMessageBuilder.addMessageText(textoEmail);
	}

	public void adicionaAnexo(String caminho) throws IOException, MessagingException {
		this.anexo = new File(caminho);
		this.emailMessageBuilder.addAttachment(anexo);
	}
	
	public void comCopiaPara(String emailDestino) throws AddressException, MessagingException {
		this.emailMessageBuilder.comCopiaPara(emailDestino, RecipientType.CC);
		//this.emailMessageBuilder.comCopiaPara(this.suporteEmail);
	}
	public void comCopiaOcultaPara(String emailDestino) throws AddressException, MessagingException {
		this.emailMessageBuilder.comCopiaPara(emailDestino, RecipientType.BCC);
	}
	
	public void sendEmail() throws IOException, AddressException, MessagingException {
		try {
			this.emailMessageBuilder.send();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
