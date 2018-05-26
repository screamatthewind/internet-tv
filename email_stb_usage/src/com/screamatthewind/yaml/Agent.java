package com.screamatthewind.yaml;

public class Agent {

	private String agentName;
	private String filename;
	private Boolean exclude;
	
	public Agent(String agentName){
		this.agentName = agentName;
	}
	
	public Agent(String agentName, boolean exclude) {
		this.agentName = agentName;
		this.exclude = exclude;
	}

	public String getAgentName(){
		return agentName;
	}
	
	public void setAgentName(String agentName){
		this.agentName = agentName;
	}
	
	public String getFilename(){
		return filename;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	
	public Boolean getExclude()
	{
		return exclude;
	}

	public void setExclude(Boolean exclude){
		this.exclude = exclude;
	}
}
