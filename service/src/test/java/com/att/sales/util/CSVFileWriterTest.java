package com.att.sales.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class CSVFileWriterTest {

	
	@InjectMocks
	CSVFileWriter cSVFileWriter;
	
	 @Rule
	 public TemporaryFolder folder = new TemporaryFolder();

	 @Mock 
	 Path path;
	
	
	@Test
	public void test() throws IOException {
		String inputFiles = "etc/process/2018050801_1807_TEST_CPC_AVPN_ProductCatalog.zip";
		String outputFile="outputFile";
		 final File file1 = folder.newFile("myfile1.txt");
		 String file="etc/consumer/2018050801_1807_TEST_CPC_AVPN_ProductCatalog/2018050801_Attribute.txt";
		/* Mockito.when(Paths.get(outputFile)).thenReturn(path);*/
		try
		{
			//cSVFileWriter.generateZipfile(file, inputFiles);
		}catch(Exception e) {
			
		}
	}

}
