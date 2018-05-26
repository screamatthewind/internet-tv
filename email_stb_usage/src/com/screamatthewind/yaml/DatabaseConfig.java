package com.screamatthewind.yaml;

public final class DatabaseConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String name;
  
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
    
    public String getName(){
    	return name;
    }
    
    public void setName(String name){
    	this.name = name;
    }
 
/*    @Override
    public String toString() {
        return String.format( "'%s' with pool of %d", getUrl(), getPoolSize() );
    }*/
}
