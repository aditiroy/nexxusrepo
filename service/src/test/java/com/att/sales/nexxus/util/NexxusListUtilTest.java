package com.att.sales.nexxus.util;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;


public class NexxusListUtilTest {
	
	@Test
	public void batchesTest() {
		List<String> source = Arrays.asList();
		Stream<List<String>> res = NexxusListUtil.batches(source, 2);
		assertNotNull(res);
		
		source = Arrays.asList("1", "2", "3");
		res = NexxusListUtil.batches(source, 2);
		assertNotNull(res);
	}
	
	@Test
	public void batchesExceptionTest() {
		NexxusListUtil.batches(null, 0);
	}
}
