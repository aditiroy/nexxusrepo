package com.att.sales.nexxus.helperTest;

import static org.mockito.Mockito.doNothing;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.TableNameConstants;
import com.att.sales.nexxus.helper.FileReaderHelper;
import com.att.sales.util.CpcCSVUtility;
import com.att.sales.util.UnZipUtil;
@ExtendWith(MockitoExtension.class)
public class FileReaderHelperTest {
	
	@Mock
	UnZipUtil unzipUtil;
	@Mock
	CpcCSVUtility csvReaderUtil;

	@Mock
	private Environment env;
	@Mock
	private Connection connection;
	@Mock
	private DriverManager manager;
	
	//@Mock
	//private MultipartBody multipartBody;
	
	@InjectMocks
	private FileReaderHelper fileReaderHelper;
	
	@Mock
	PreparedStatement preparedStatement;
	
	
	 @Rule
	    public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void test() throws IOException, SalesBusinessException {
		String fileName = "filename.zip";
		 final File file1 = folder.newFile("filename.zip");
	      //  final File file2 = folder.newFile("filename.zip");

		Mockito.when(env.getProperty("pv.directory.processed")).thenReturn("rd/rd/");
	/*	try {
			test.copyToPVAndUnzip(fileName);
		} catch (IOException | SalesBusinessException e) {
			
			e.printStackTrace();
		}*/
		//test.copyToPVAndUnzip(fileName);
	}
	
	@Test
	public void test1() {
		String fileName = "filename.zip";
		/*try {
			test.readFiles(fileName);
		} catch (IOException | SalesBusinessException e) {
			e.printStackTrace();
		}*/
		
	}
	@Test
	public void testProcessContents() throws IOException {
		String zipFilePath="C:\\\\Users\\\\IBM_ADMIN\\\\Desktop";
		List<File> archiveContents=new ArrayList<>();
		
		fileReaderHelper.processContents(archiveContents, zipFilePath);
	}

	
	@Test
	public void testProcessContents1()
	{
		List<File> archiveContents = new ArrayList<>();
		File file  =new File("etc\\consumer\\2018050801_Attribute.txt");
		archiveContents.add(file);
		String transactionId="04617052-db85-11e8-a44d-fa163eb29f92";
		fileReaderHelper.processContents(archiveContents, transactionId);
	}
	
	
	//@Test
	public void testDataProcessing() throws SalesBusinessException, SQLException {
		String driverClassName="oracle.jdbc.driver.OracleDriver";
		String transactionId="04617052-db85-11e8-a44d-fa163eb29f92"; 
		String activity	="loadDataFromArchToWrkTables";
		TableNameConstants constant=new TableNameConstants();
		List<String> jArc = new ArrayList<>();
		String s1="MS_ATTRIBUTE";
		jArc.add(s1);
		String url="jdbc:oracle:thin:@zld06047.vci.att.com:1524:d1c2d536";
		String userName="NEXXUS_DEV";
		String password="dVTMEsuXnApr2020";
		String query="query";
		Mockito.when(env.getProperty(Mockito.anyString())).thenReturn(driverClassName);
		doNothing().when(connection).setAutoCommit(Mockito.anyBoolean());
		DriverManager.getConnection(url,userName ,password);
		Mockito.when(connection.prepareStatement(query)).thenReturn(preparedStatement);
		try {
		fileReaderHelper.dataProcessing(transactionId);}
		catch(Exception exception) {
			
			
		}
	}
	
	
}
