package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
public class JsonPathTest {
	@Test
	public void test() {
		JsonPath rootPath = JsonPath.getRootPath();
		JsonPath ob1 = rootPath.resolveContainerNode("ob1");
		assertEquals("/ob1", ob1.getPath());
		assertEquals("ob1", ob1.getFieldName());
		assertEquals("/ob1/f1", ob1.resolveField("f1"));
		assertEquals("", rootPath.getFieldName());
		assertEquals(rootPath, rootPath.parent());
		assertEquals(rootPath, ob1.parent());
		assertTrue(rootPath.isAncestor(ob1));
		assertTrue(ob1.isDescendant(rootPath));
		assertEquals("/ob1", ob1.toString());
		rootPath.hashCode();
		assertTrue(rootPath.equals(rootPath));
		assertFalse(rootPath.equals(null));
		assertFalse(rootPath.equals(""));
		JsonPath nullCase = new JsonPath(null);
		nullCase.hashCode();
		assertFalse(nullCase.equals(rootPath));
		assertFalse(rootPath.equals(ob1));
	}
}
