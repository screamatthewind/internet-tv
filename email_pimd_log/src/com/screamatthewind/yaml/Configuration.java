package com.screamatthewind.yaml;

public class Configuration {

	private EMailConfig emailConfig;
	private String logFilename;
	
	public EMailConfig getEMailConfig()
	{
		return emailConfig;
	}
	
	public void setEmailConfig(EMailConfig emailConfig){
		this.emailConfig = emailConfig;
	}
	
	public String getLogFilename()
	{
		return logFilename;
	}
	
	public void setLogFilename(String logFilename)
	{
		this.logFilename = logFilename;
	}
}
