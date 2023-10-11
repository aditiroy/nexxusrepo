package com.att.sales.nexxus.mylogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Component
public class MyloginService {
	
	private static Logger log = LoggerFactory.getLogger(MyloginService.class);

	@Value("${mylogin.userName}")
	private String userName;

	@Value("${mylogin.host}")
	private String host;
	
	@Value("${mylogin.port}")
	private int port;
	
	@Value("${mylogin.destPath}")
	private String destPath;
	
	@Value("${pv.directory.nexxusoutput}")
	private String sourcePath;
	
	@Value("${mylogin.prvKey.passphrase}")
	private String passPhrase;
	
	@Value("${mylogin.prvKey.path}")
	private String prvkeyPath;
	
    private Session session = null;
    
    private ChannelSftp sftpChannel = null;
    
    public void connect() throws JSchException {
    	JSch jsch = new JSch();
    	session = jsch.getSession(userName, host, port);
    	jsch.addIdentity(prvkeyPath, passPhrase);
    //	session.setPassword(password);
    	session.setConfig("StrictHostKeyChecking", "no");
    	session.connect();
    	Channel channel = session.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
        log.info("connection established");
     }
      
     public void upload(String fileName) throws SftpException {
    	 if(sftpChannel != null) {
    		 sftpChannel.put(sourcePath+fileName, destPath+fileName);
    	 }
    	 log.info("Upload done {}", fileName);
     }
  
     public void disconnect() {
    	 if(sftpChannel != null) {
    		 sftpChannel.exit();
    	 }
         if (session != null) {
             session.disconnect();
         }
         log.info("disconnect done");
      }
}
