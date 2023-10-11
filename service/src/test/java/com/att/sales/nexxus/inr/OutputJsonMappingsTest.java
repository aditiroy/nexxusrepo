package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class OutputJsonMappingsTest {

	@Test
	public void test() {
		List<OutputJsonMapping> mappings = new ArrayList<>();
		String arrayName = "arrayName";
		String destName = "destName";
		
		OutputJsonMappings outputJsonMappings = new OutputJsonMappings();
		outputJsonMappings.setArrayName(arrayName);
		outputJsonMappings.setDestName(destName);
		outputJsonMappings.setMappings(mappings);
		assertSame(arrayName, outputJsonMappings.getArrayName());
		assertSame(destName, outputJsonMappings.getDestName());
		assertSame(mappings, outputJsonMappings.getMappings());
		outputJsonMappings.toString();
	}
}
