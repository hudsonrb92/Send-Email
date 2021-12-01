package com.elaudos.email;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.elaudos.report.ConnectToDb;
import com.elaudos.report.QueryRunner;

public class SendEmail {
    public static void main(String[] args) throws SQLException, IOException, AddressException, MessagingException {
        ConnectToDb cToDb = new ConnectToDb("db_user", "db_pass", 5432, "db_host", "db_name");
        Statement statement = cToDb.getConn().createStatement(); // statement to inject into queryRunner
        QueryRunner qRunner = new QueryRunner(statement, "PrefixName"); // Generate a csv to attach at email
        String relatorio = qRunner.geraRelatorio("/tmp/"); // generate a hard coded query,
		// could pass as parameter as well, but I made some string treatments, so I preferred set as hard coded
        cToDb.closeConn(); // Close db connection

        EnviaEmail email = new EnviaEmail(
                "emailTo@gmail.com", // as Name already says
                "Body text of email", // ''
                "My personalized subject", // ''
                "myEmailAndUser@myemail.com", // in this case email should be user also otherwise won't work
                "smtp.myBeatifullEmail.com", // smtp host
                "mySecretPass"); // ''

        try {
            email.adicionaAnexo(relatorio);
            email.comCopiaPara("sendTo@gmail.com"); // as name already say's;
            email.comCopiaPara("carbonCopy@gmail.com");
            email.comCopiaOcultaPara("blindCarbonCopy");
            email.sendEmail(); // finally send
        } catch (IOException e) {
            System.out.println("Erro ao tentar enviar email.");
            e.printStackTrace();
        }

    }
}
