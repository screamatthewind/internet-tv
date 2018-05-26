package com.screamatthewind.utils;

import java.util.Date;
import java.util.List;
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

import com.screamatthewind.yaml.Agent;
import com.screamatthewind.yaml.EMailConfig;
import com.screamatthewind.yaml.Reports;
import com.sun.mail.util.MailSSLSocketFactory;

public class EmailUtil {

	private static String EXCEPTION_RECIPIENT = "screamatthewind@gmail.com";

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

	static Session session = null;
	static MimeMessage msg = null;

	public static void initEmail(final EMailConfig emailConfig) {

		try {

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

			msg.setFrom(new InternetAddress("no_reply@screamatthewind.com", "NoReply"));
			msg.setReplyTo(InternetAddress.parse("no_reply@screamatthewind.com", false));
			msg.setSubject("ERROR: Exception while producing TV STB Reports", "UTF-8");
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

	public static void sendMissingAgentsEmail(EMailConfig emailConfig, List<String> missingAgents) {
		initEmail(emailConfig);

		try {
			msg.setFrom(new InternetAddress(emailConfig.getFromAddress(), emailConfig.getFromName()));
			msg.setReplyTo(InternetAddress.parse(emailConfig.getFromAddress(), false));
			msg.setSubject("ERROR: TV STB Usage Missing Agents", "UTF-8");
			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailConfig.getErrorAddress(), false));

			StringBuilder body = new StringBuilder();

			body.append("<b>The following agents are not accounted for in the STB Usage Reports:</b><br>");
			body.append("<ul>");

			for (String agentName : missingAgents) {
				body.append("<li>" + agentName + "</li>");
			}

			body.append("</ul><br>");
			body.append("<b>Please update the YAML config file</b>");

			msg.setContent(body.toString(), "text/html");

			Transport.send(msg);
			LOGGER.info("EMail Missing Agents Sent Succesfully");

		} catch (Exception e) {
			ExceptionHandler.handleException("sendMissingAgentsEmail", null, e, EmailUtil.class);
		}
	}

	public static void sendReportEmail(EMailConfig emailConfig, Reports report) {

		try {

			initEmail(emailConfig);

			msg.setFrom(new InternetAddress(emailConfig.getFromAddress(), emailConfig.getFromName()));
			msg.setReplyTo(InternetAddress.parse(emailConfig.getFromAddress(), false));
			msg.setSubject(report.getName(), "UTF-8");
			msg.setSentDate(new Date());

			String emailAddresses;

			if (report.getSendTo() != null) {
				emailAddresses = "";
				for (String emailAddress : report.getSendTo()) {
					if (emailAddresses.length() > 0)
						emailAddresses = emailAddresses + ",";

					emailAddresses = emailAddresses + emailAddress;
				}

				if (emailAddresses.length() > 0)
					msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddresses, false));
			}

			if (report.getSendCC() != null) {
				emailAddresses = "";
				for (String emailAddress : report.getSendCC()) {
					if (emailAddresses.length() > 0)
						emailAddresses = emailAddresses + ",";

					emailAddresses = emailAddresses + emailAddress;
				}

				if (emailAddresses.length() > 0)
					msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailAddresses, false));
			}

			if (report.getSendBCC() != null) {
				emailAddresses = "";
				for (String emailAddress : report.getSendBCC()) {
					if (emailAddresses.length() > 0)
						emailAddresses = emailAddresses + ",";

					emailAddresses = emailAddresses + emailAddress;
				}

				if (emailAddresses.length() > 0)
					msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailAddresses, false));
			}

			StringBuilder body = new StringBuilder();

			body.append("<b>Set Top Box Usage Reports for:</b><br>");
			body.append("<ul>");

			for (Agent agent : report.getAgents()) {
				body.append("<li>" + agent.getAgentName() + "</li>");
			}

			body.append("</ul>");

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(body.toString(), "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			for (Agent agent : report.getAgents()) {
				DataSource source = new FileDataSource(agent.getFilename());
				messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(agent.getAgentName().replace(" ", "_") + ".csv");
				multipart.addBodyPart(messageBodyPart);
			}

			// Send the complete message parts
			msg.setContent(multipart);

			Transport.send(msg);
			LOGGER.info("EMail Report Sent Succesfully");

		} catch (Exception e) {
			ExceptionHandler.handleException("sendReportEmail", null, e, EmailUtil.class);
		}
	}
}