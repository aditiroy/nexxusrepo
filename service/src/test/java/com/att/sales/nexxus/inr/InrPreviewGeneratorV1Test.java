package com.att.sales.nexxus.inr;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class InrPreviewGeneratorV1Test {
	@Mock
	private PreviewWorkbookV1 wb;
	@Mock
	private UnmockableWrapper unmockableWrapper;
	@Mock
	private ObjectMapper mapper;
	private ObjectMapper realMapper = new ObjectMapper();
	
	@Test
	public void generateTest() throws IOException {
		ObjectNode node = realMapper.createObjectNode();
		ObjectNode obj = realMapper.createObjectNode();
		node.withArray("mainSheet").add(obj);
		node.withArray("falloutSheet").add(obj);
		ArrayNode arrayNode = realMapper.createArrayNode();
		arrayNode.add(node);
		InrPreviewGeneratorV1 inrPreviewGeneratorV1 = new InrPreviewGeneratorV1(arrayNode, "", "", unmockableWrapper, 1, mapper);
		ReflectionTestUtils.setField(inrPreviewGeneratorV1, "wb", wb);
		inrPreviewGeneratorV1.generate();
	}
}
