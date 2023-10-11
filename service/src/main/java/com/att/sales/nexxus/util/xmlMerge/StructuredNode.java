package com.att.sales.nexxus.util.xmlMerge;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public interface StructuredNode extends Node{
	/**
	 * Returns a given node at the relative path.
	 */
	StructuredNode queryNode(String xpath) throws XPathExpressionException;

	/**
	 * Returns a list of nodes at the relative path.
	 */
	List<StructuredNode> queryNodeList(String xpath)
			throws XPathExpressionException;

	/**
	 * Boilerplate for array handling....
	 */
	StructuredNode[] queryNodes(String path) throws XPathExpressionException;

	/**
	 * Returns a property at the given part.
	 */
	String queryString(String path) throws XPathExpressionException;

	/**
	 * Queries a {@link Value} which provides various conversions.
	 */
	Value queryValue(String path) throws XPathExpressionException;

	/**
	 * Checks whether a node or non-empty content is reachable via the given
	 * XPath.
	 */
	boolean isEmpty(String path) throws XPathExpressionException;

	/**
	 * Returns the current node's name.
	 */
	String getNodeName();
}