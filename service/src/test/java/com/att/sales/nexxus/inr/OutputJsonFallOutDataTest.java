package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
public class OutputJsonFallOutDataTest {
	@Test
	public void test() {
		NxOutputBean nxOutputBean = new NxOutputBean();
		String fallOutData = "";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.createObjectNode();
		OutputJsonFallOutData outputJsonFallOutData = new OutputJsonFallOutData(nxOutputBean, fallOutData, node, true, true);
		assertSame(nxOutputBean, outputJsonFallOutData.getNxOutputBean());
		assertSame(fallOutData, outputJsonFallOutData.getFallOutData());
		assertSame(node, outputJsonFallOutData.getMpOutputJson());
		assertTrue(outputJsonFallOutData.isBeanOutput());
		assertTrue(outputJsonFallOutData.hasValue());
	}
}
