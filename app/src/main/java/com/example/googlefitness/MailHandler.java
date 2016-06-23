package com.example.googlefitness;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by oscarn on 2016-06-16.
 */
public class MailHandler {

    public Session setUpMail() {
        final String username = "fiskenaetet@gmail.com";
        final String password = "fisk12345";

        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }

    public void sendThisEmail() {
        System.out.println("Hallå eller");

        try {

            Message message = new MimeMessage(setUpMail());
            message.setFrom(new InternetAddress("fiskenaetet@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("nordin.oscar@gmail.com"));
            message.setSubject("Google fitness mail");
            Transport.send(message);
            System.out.println("skickat");
        } catch (MessagingException e) {
            //throw new RuntimeException(e);
            System.out.println("Fel: " +e.getMessage());
        }


    }

}
