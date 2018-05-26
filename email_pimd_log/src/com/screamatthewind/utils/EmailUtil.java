package com.screamatthewind.utils;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.screamatthewind.yaml.EMailConfig;
import com.sun.mail.util.MailSSLSocketFactory;

public class EmailUtil {

	private static String EXCEPTION_RECIPIENT = "screamatthewind@gmail.com";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

	static Session session = null;
	static MimeMessage msg = null;

	public static void initEmail(final EMailConfig emailConfig) throws GeneralSecurityException {
		Properties props = System.getProperties();

		if (emailConfig.getHost().length() != 0) {
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", emailConfig.getHost());
			props.put("mail.smtp.port", emailConfig.getPort());
			
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			
			props.put("mail.smtp.ssl.trust", "*");
			props.put("mail.smtp.ssl.socketFactory", sf);

			session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
				}
			});
		} else
			session = Session.getInstance(props, null);

		msg = new MimeMessage(session);

		try {
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
		} catch (Exception e) {
			ExceptionHandler.handleException("initEmail", null, e, EmailUtil.class);
		}
	}

	public static void sendExceptionEmail(String function, String text) {
		Properties props = System.getProperties();
		session = Session.getInstance(props, null);

		msg = new MimeMessage(session);

		try {
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress("no_reply@example.com", "NoReply"));
			msg.setReplyTo(InternetAddress.parse("no_reply@example.com", false));
			msg.setSubject("ERROR: Exception while mailing pimd log file", "UTF-8");
			msg.setSentDate(new Date());
			
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EXCEPTION_RECIPIENT, false));
			msg.setText("Function: " + function + "  Exception: " + text);

			Transport.send(msg);
			LOGGER.info("Exception EMail Sent Succesfully");

		} catch (Exception ignore) {
			LOGGER.error("Unable to send exception email");
			ignore.printStackTrace();
		}
	}

	public static void sendLogFileEmail(EMailConfig emailConfig, String filename) {

		try {

			initEmail(emailConfig);

			msg.setFrom(new InternetAddress(emailConfig.getFromAddress(), emailConfig.getFromName()));
			msg.setReplyTo(InternetAddress.parse(emailConfig.getFromAddress(), false));
			msg.setSubject("PIM Crash Log File", "UTF-8");
			msg.setSentDate(new Date());

			String emailAddresses = emailConfig.getErrorAddress();
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddresses, false));
													
			StringBuilder body = new StringBuilder();
			body.append("<b>PIMD crash log file is attached</b><br>");

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body.toString(), "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			DataSource source = new FileDataSource(filename);
			
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(source.getName());

			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			msg.setContent(multipart);

			Transport.send(msg);
			LOGGER.info("EMail Report Sent Succesfully");

		} catch (Exception e) {
			ExceptionHandler.handleException("sendReportEmail", null, e, EmailUtil.class);
		}
	}
}