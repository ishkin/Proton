package com.ibm.hrl.proton.adapters.email;


import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ibm.hrl.proton.adapters.configuration.IOutputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IOutputConnector;
import com.ibm.hrl.proton.adapters.interfaces.AbstractOutputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.epa.basic.IDataObject;
import com.ibm.hrl.proton.metadata.inout.ConsumerMetadata;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class GmailOutputAdapter extends AbstractOutputAdapter {
	private javax.mail.Session mailSession;
	private String username;
	private String password;
	private String email;
	private String headerMessage;
	private String bodyMessage;
	
	public GmailOutputAdapter(ConsumerMetadata consumerMetadata, IOutputConnector serverConnector,
			EventMetadataFacade eventMetadata,EepFacade eep) throws AdapterException {
		super(consumerMetadata, serverConnector, eventMetadata);
		this.username = ((GmailOutputAdapterConfiguration)configuration).getUsername();
		this.password = ((GmailOutputAdapterConfiguration)configuration).getPassword();
		this.email = ((GmailOutputAdapterConfiguration)configuration).getEmail();
		this.headerMessage = ((GmailOutputAdapterConfiguration)configuration).getHeaderMessage();
		this.bodyMessage = ((GmailOutputAdapterConfiguration)configuration).getMessageAttributeName();
	}

	@Override
	public void writeObject(IDataObject instance) throws AdapterException {
		try {    
	           MimeMessage mailMessage = new MimeMessage(mailSession);    
	           mailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(email));    
	           mailMessage.setSubject(headerMessage);   
	           //get the proper attributes from the event
	           Map<String,Object> instanceAttrs = instance.getFieldValues();
	           String bodyText = (String)instanceAttrs.get(bodyMessage);
	           if (bodyText != null) mailMessage.setText(bodyText);
	               
	           //send message  
	           Transport.send(mailMessage);    
	           
	   		   logger.fine("MailAdapter: writeObject: sent email successfully");
	          } catch (MessagingException e)
			  {
	        	  	throw new AdapterException("Could not send email to: "+email+",reason: "+e.getMessage());
	          }    

		
	}

	@Override
	public void initializeAdapter() throws AdapterException {
		super.initialize();
		
		try
		{		  		
			 Properties props = new Properties();    
	          props.put("mail.smtp.host", "smtp.gmail.com");    
	          props.put("mail.smtp.socketFactory.port", "465");    
	          props.put("mail.smtp.socketFactory.class",    
	                    "javax.net.ssl.SSLSocketFactory");    
	          props.put("mail.smtp.auth", "true");    
	          props.put("mail.smtp.port", "465");    
	          //get Session   
	           mailSession = javax.mail.Session.getDefaultInstance(props,    
	           new javax.mail.Authenticator() {    
	           protected PasswordAuthentication getPasswordAuthentication() {    
	           return new PasswordAuthentication(username,password);  
	           }    
	          });    
		  
		}
		catch(Exception e)
		{
			throw new AdapterException(e.getMessage());
		}	
		
	}

	@Override
	public void shutdownAdapter() throws AdapterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IOutputAdapterConfiguration createConfiguration(ConsumerMetadata consumerMetadata) {
		String email= (String)consumerMetadata.getConsumerProperty(MetadataParser.EMAIL);
		String headerMessage= (String)consumerMetadata.getConsumerProperty(MetadataParser.HEADER);
		String messageAttribute= (String)consumerMetadata.getConsumerProperty(MetadataParser.MESSAGE_ATTRIBUTE_NAME);
		String username = (String)consumerMetadata.getConsumerProperty(MetadataParser.EMAIL_USERNAME);
		String password = (String)consumerMetadata.getConsumerProperty(MetadataParser.EMAIL_PASSWORD);
		return new GmailOutputAdapterConfiguration(email,messageAttribute,headerMessage,username,password);
	}

	

}
