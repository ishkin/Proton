package com.ibm.hrl.proton.adapters.email;

import com.ibm.hrl.proton.adapters.configuration.IOutputAdapterConfiguration;

public class GmailOutputAdapterConfiguration implements IOutputAdapterConfiguration {
	private String email;
	private String messageAttributeName;
	private String headerMessage;
	private String username;
	private String password;

	public GmailOutputAdapterConfiguration(String email,String messageAttributeName, String hearderMessage,String username,String password) {
		super();
		this.email = email;
		this.messageAttributeName = messageAttributeName;
		this.headerMessage = hearderMessage;
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getMessageAttributeName() {
		return messageAttributeName;
	}

	public String getHeaderMessage() {
		return headerMessage;
	}

	public String getEmail() {
		return email;
	}
	
	
}
