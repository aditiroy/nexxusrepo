package com.att.sales.nexxus.constantTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.constant.TableNameConstants;
@ExtendWith(MockitoExtension.class)
public class TableNameConstantsTest {
	@InjectMocks
	private TableNameConstants tableNameConstants;

	@SuppressWarnings("static-access")
	@Test
	public void test() {
		tableNameConstants.getArchieveTables();
		tableNameConstants.getWokingTable();
	}

}
