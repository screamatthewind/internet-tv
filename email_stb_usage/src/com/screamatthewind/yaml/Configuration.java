package com.screamatthewind.yaml;

import java.util.List;

public class Configuration {

	private DatabaseConfig databaseConfig;
	private EMailConfig emailConfig;
	private String reportDirectory;
	private List<Reports> reports;
	private List<String> exclusions;
	
	public DatabaseConfig getDatabaseConfig() {
		return databaseConfig;
	}
	
	public void setDatabaseConnection(DatabaseConfig databaseConfig){
		this.databaseConfig = databaseConfig;
	}

	public EMailConfig getEMailConfig()
	{
		return emailConfig;
	}
	
	public void setEmailConfig(EMailConfig emailConfig){
		this.emailConfig = emailConfig;
	}
	
	public String getReportDirectory()
	{
		return reportDirectory;
	}
	
	public void setReportDirectory(String reportDirectory)
	{
		this.reportDirectory = reportDirectory;
	}
	
	public List<Reports> getReports()
	{
		return reports;
	}
	
	public void setReports(List<Reports> reports){
		this.reports = reports;
	}
	
	public List<String> getExclusions() {
		return exclusions;
	}
	
	public void setExclusions(List<String> exclusions){
		this.exclusions = exclusions;
	}
	
}
