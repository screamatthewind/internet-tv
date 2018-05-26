package com.screamatthewind.yaml;

import java.util.List;

public class Reports {
	private String name;
	private List<String> sendTo;
	private List<String> sendCC;
	private List<String> sendBCC;
	private List<Agent> agents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgent(List<Agent> agents) {
		this.agents = agents;
	}

	public List<String> getSendTo() {
		return sendTo;
	}

	public void setSendTo(List<String> sendTo) {
		this.sendTo = sendTo;
	}

	public List<String> getSendCC() {
		return sendCC;
	}

	public void setSendCC(List<String> sendCC) {
		this.sendCC = sendCC;
	}

	public List<String> getSendBCC() {
		return sendBCC;
	}

	public void setSendBCC(List<String> sendBCC) {
		this.sendBCC = sendBCC;
	}
}
