package com.att.sales.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnZipUtilTest {

	
	@InjectMocks
	UnZipUtil unZipUtil;
	
	@Test
	public void testUnZip() {
		String destDir = "etc/consumer";
		String zipFilePath="etc/process/2018050801_1807_TEST_CPC_AVPN_ProductCatalog";
		unZipUtil.unZip(zipFilePath, destDir);
	}
	
	
	

}
