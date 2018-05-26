package com.screamatthewind.yaml;

public final class EMailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String fromAddress;
    private String fromName;
    private String errorAddress;
  
    EMailConfig() {
    	this.host = "";
    	this.port = 0;
    	this.username = "";
    	this.password = "";
    	this.fromAddress = "";
    	this.fromName = "";
    	this.errorAddress = "";
    }
    
    public String getHost() {
        return host;
    }
 
    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUsername(){
    	return username;
    }
    
    public void setUsername(String username){
    	this.username = username;
    }
    
    public String getPassword(){
    	return password;
    }
    
    public void setPassword(String password){
    	this.password = password;
    }
    
    public String getFromAddress(){
    	return fromAddress;
    }
    
    public void setFromAddress(String fromAddress) {
    	this.fromAddress = fromAddress;
    }
    
    public String getFromName() {
    	return fromName;
    }
    
    public void setFromName(String fromName){
    	this.fromName = fromName;
    }

    public String getErrorAddress(){
    	return errorAddress;
    }
    
    public void setErrorAddress(String errorAddress) {
    	this.errorAddress = errorAddress;
    }
}
