package com.att.sales.nexxus.util.xmlMerge;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class XMLNodeImpl implements StructuredNode {

	private Node node;
	private static final XPathFactory XPATH = XPathFactory.newInstance();

	public XMLNodeImpl(Node root) {
		node = root;
	}

	@Override
	public StructuredNode queryNode(String path)
			throws XPathExpressionException {
		Node result = (Node) XPATH.newXPath().compile(path)
				.evaluate(node, XPathConstants.NODE);
		if (result == null) {
			return null;
		}
		return new XMLNodeImpl(result);
	}

	@Override
	public List<StructuredNode> queryNodeList(String path)
			throws XPathExpressionException {
		NodeList result = (NodeList) XPATH.newXPath().compile(path)
				.evaluate(node, XPathConstants.NODESET);
		List<StructuredNode> resultList = new ArrayList<StructuredNode>(
				result.getLength());
		for (int i = 0; i < result.getLength(); i++) {
			resultList.add(new XMLNodeImpl(result.item(i)));
		}
		return resultList;
	}

	@Override
	public StructuredNode[] queryNodes(String path)
			throws XPathExpressionException {
		List<StructuredNode> nodes = queryNodeList(path);
		return nodes.toArray(new StructuredNode[nodes.size()]);
	}

	@Override
	public String queryString(String path) throws XPathExpressionException {
		Object result = XPATH.newXPath().compile(path)
				.evaluate(node, XPathConstants.NODE);
		if (result == null) {
			return null;
		}
		if (result instanceof Node) {
			String s = ((Node) result).getTextContent();
			if (s != null) {
				return s.trim();
			}
			return s;
		}
		return result.toString().trim();
	}

	@Override
	public boolean isEmpty(String path) throws XPathExpressionException {
		String result = queryString(path);
		return result == null || "".equals(result);
	}

	@Override
	public String getNodeName() {
		return node.getNodeName();
	}

	@Override
	public String toString() {
		return getNodeName();
	}

	@Override
	public Value queryValue(String path) throws XPathExpressionException {
		return Value.of(queryString(path));
	}

	@Override
	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		node.setNodeValue(nodeValue);
	}

	@Override
	public short getNodeType() {
		return node.getNodeType();
	}

	@Override
	public Node getParentNode() {
		return node.getParentNode();
	}

	@Override
	public NodeList getChildNodes() {
		return node.getChildNodes();
	}

	@Override
	public Node getFirstChild() {
		return node.getFirstChild();
	}

	@Override
	public Node getLastChild() {
		return node.getLastChild();
	}

	@Override
	public Node getPreviousSibling() {
		return node.getPreviousSibling();
	}

	@Override
	public Node getNextSibling() {
		return node.getNextSibling();
	}

	@Override
	public NamedNodeMap getAttributes() {
		return node.getAttributes();
	}

	@Override
	public Document getOwnerDocument() {
		return node.getOwnerDocument();
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return node.insertBefore(newChild, refChild);
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return null;
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		return node.removeChild(oldChild);
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		return node.appendChild(newChild);
	}

	@Override
	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}

	@Override
	public Node cloneNode(boolean deep) {
		return node.cloneNode(deep);
	}

	@Override
	public void normalize() {
		node.normalize();
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}

	@Override
	public String getNamespaceURI() {
		return node.getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return node.getPrefix();
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		node.setPrefix(prefix);
	}

	@Override
	public String getLocalName() {
		return node.getLocalName();
	}

	@Override
	public boolean hasAttributes() {
		return node.hasAttributes();
	}

	@Override
	public String getBaseURI() {
		return node.getBaseURI();
	}

	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		return node.compareDocumentPosition(other);
	}

	@Override
	public String getTextContent() throws DOMException {
		return node.getTextContent();
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		node.setTextContent(textContent);
	}

	@Override
	public boolean isSameNode(Node other) {
		return node.isSameNode(other);
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		return node.lookupPrefix(namespaceURI);
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return node.isDefaultNamespace(namespaceURI);
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return node.lookupNamespaceURI(prefix);
	}

	@Override
	public boolean isEqualNode(Node arg) {
		return node.isEqualNode(arg);
	}

	@Override
	public Object getFeature(String feature, String version) {
		return node.getFeature(feature, version);
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return node.setUserData(key, data, handler);
	}

	@Override
	public Object getUserData(String key) {
		return node.getUserData(key);
	}

}