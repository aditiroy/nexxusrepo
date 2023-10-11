package com.att.sales.nexxus.mylogin;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

@ExtendWith(MockitoExtension.class)
public class MyloginServiceTest {

	@InjectMocks
	private MyloginService myloginService;
	
	@BeforeEach
	public void init() {
				
		ReflectionTestUtils.setField(myloginService, "userName","itservices\\m25578");
	//	ReflectionTestUtils.setField(myloginService, "password","DecHolidays@2020");
		ReflectionTestUtils.setField(myloginService, "host","uadmftp.cso.att.com");
		ReflectionTestUtils.setField(myloginService, "port",22);
		ReflectionTestUtils.setField(myloginService, "destPath","/home/application_logins_test/");
		ReflectionTestUtils.setField(myloginService, "sourcePath","etc/nexxusoutput/");
		ReflectionTestUtils.setField(myloginService, "passPhrase","nexxusmyprice");
		ReflectionTestUtils.setField(myloginService, "prvkeyPath","src/main/resources/mylogin/nexxusmyprice.ppk");
	}
	
	@Test
	public void testConnect() {
		try {
			myloginService.connect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDisconnect() {
		myloginService.disconnect();
	}
	
	@Test
	public void testUpload() {
		try {
			myloginService.upload("test");
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
