package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertSame;

import org.junit.jupiter.api.Test;

public class OutputJsonMappingTest {
	
	@Test
	public void test() {
		String type = "type";
		String sourceField = "sourceField";
		String destField = "destField";
		String source = "source";
		String destName = "destName";
		
		OutputJsonMapping outputJsonMapping = new OutputJsonMapping();
		outputJsonMapping.setDestField(destField);
		outputJsonMapping.setDestName(destName);
		outputJsonMapping.setSource(source);
		outputJsonMapping.setSourceField(sourceField);
		outputJsonMapping.setType(type);
		
		assertSame(destField, outputJsonMapping.getDestField());
		assertSame(destName, outputJsonMapping.getDestName());
		assertSame(source, outputJsonMapping.getSource());
		assertSame(sourceField, outputJsonMapping.getSourceField());
		assertSame(type, outputJsonMapping.getType());
		
		outputJsonMapping.toString();
	}
}
