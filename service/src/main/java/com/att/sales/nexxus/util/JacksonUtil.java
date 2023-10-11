package com.att.sales.nexxus.util;

import java.beans.Introspector;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.inr.JsonPath;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonUtil {

	JacksonUtil() {
	}

	public static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();
	public static final org.codehaus.jackson.map.ObjectMapper OBJECT_MAPPER = new org.codehaus.jackson.map.ObjectMapper();

	public static <T> T fromString(String string, Class<T> clazz) {
		if (StringUtils.isNotEmpty(string)) {
			try {
				return JACKSON_OBJECT_MAPPER.readValue(string, clazz);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(
						"The given string value: " + string + " cannot be transformed to Json object");
			}
		}
		return null;
	}

	public static <T> T fromStringForCodeHaus(String string, Class<T> clazz) {
		if (StringUtils.isNotEmpty(string)) {
			try {
				return OBJECT_MAPPER.readValue(string, clazz);
			} catch (IOException e) {
				throw new IllegalArgumentException(
						"The given string value: " + string + " cannot be transformed to Json object");
			}
		}
		return null;
	}

	public static String toString(Object value) {
		if (null != value) {
			try {
				return JACKSON_OBJECT_MAPPER.writeValueAsString(value);
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException(
						"The given Json object value: " + value + " cannot be transformed to a String");
			}
		}
		return null;

	}
	
	public static String toStringForCodeHaus(Object value) {
		if (null != value) {
			try {
				return OBJECT_MAPPER.writeValueAsString(value);
			} catch (IOException e) {
				throw new IllegalArgumentException(
						"The given Json object value: " + value + " cannot be transformed to a String");
			}
		}
		return null;

	}

	public static JsonNode toJsonNode(String value) {
		try {
			return JACKSON_OBJECT_MAPPER.readTree(value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static <T> JsonNode jsonNodeFromObj(T value) {
		if(value!=null) {
			return JACKSON_OBJECT_MAPPER.convertValue(value, JsonNode.class);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(T value) {
		return fromString(toString(value), (Class<T>) value.getClass());
	}

	public static void trimJson(JsonNode node) {
		if (node.getNodeType() == JsonNodeType.OBJECT) {
			ObjectNode objNode = (ObjectNode) node;
			List<String> keysToBeRemoved = new LinkedList<>();
			Iterator<Entry<String, JsonNode>> fields = objNode.fields();
			while (fields.hasNext()) {
				Entry<String, JsonNode> next = fields.next();
				JsonNode value = next.getValue();
				trimJson(value);
				if (value.getNodeType() == JsonNodeType.OBJECT || value.getNodeType() == JsonNodeType.ARRAY) {
					if (value.size() == 0) {
						keysToBeRemoved.add(next.getKey());
					}
				} else {
					if (value.equals(NullNode.getInstance()) || value.asText().isEmpty()) {
						keysToBeRemoved.add(next.getKey());
					}
				}
			}
			keysToBeRemoved.forEach(objNode::remove);
		} else if (node.getNodeType() == JsonNodeType.ARRAY) {
			ArrayNode arrayNode = (ArrayNode) node;
			for (int i = arrayNode.size() - 1; i >= 0; i--) {
				JsonNode value = arrayNode.get(i);
				trimJson(value);
				if (value.getNodeType() == JsonNodeType.OBJECT || value.getNodeType() == JsonNodeType.ARRAY) {
					if (value.size() == 0) {
						arrayNode.remove(i);
					}
				} else {
					if (value.equals(NullNode.getInstance()) || value.asText().isEmpty()) {
						arrayNode.remove(i);
					}
				}
			}
		}
	}
	
	public static void trimJsonWithTagFilter(JsonNode node, Set<String> filter) {
		JsonPath rootPath = JsonPath.getRootPath();
		Set<JsonPath> whiteList = new HashSet<>();
		trimJsonWithTagFilterHelper(node, rootPath, whiteList, filter);
	}
	
	protected static void trimJsonWithTagFilterHelper(JsonNode node, JsonPath path, Set<JsonPath> whiteList,
			Set<String> filter) {
		JsonPath lnsPath=null;
		if (node.getNodeType() == JsonNodeType.OBJECT) {
			ObjectNode objNode = (ObjectNode) node;
			List<String> keysToBeRemoved = new LinkedList<>();
			Iterator<Entry<String, JsonNode>> fields = objNode.fields();
			while (fields.hasNext()) {
				Entry<String, JsonNode> next = fields.next();
				String key = next.getKey();
				JsonNode value = next.getValue();
				trimJsonWithTagFilterHelper(value, path.resolveContainerNode(key), whiteList, filter);
				if (value.getNodeType() == JsonNodeType.OBJECT || value.getNodeType() == JsonNodeType.ARRAY) {
					if (!whiteList.contains(path.resolveContainerNode(key))) {
						keysToBeRemoved.add(key);
					} else {
						if(key.equals("status") || key.equals("technology") ) {
		 					if(key.equals("technology")) {
								lnsPath=path;
							}else if(key.equals("status") && value.textValue().equals("ATTCLEC")){
								whiteList.add(lnsPath);
								whiteList.add(path);
							}else {
								keysToBeRemoved.add(key);
							}
						}else {
							whiteList.add(path);
						}
					}
				} else {
					if (!filter.contains(key)) {
						keysToBeRemoved.add(key);
					} else {
						if(key.equals("status") || key.equals("technology") ) {
		 					if(key.equals("technology")) {
								lnsPath=path;
							}else if(key.equals("status") && value.textValue().equals("ATTCLEC")){
								whiteList.add(lnsPath);
								whiteList.add(path);
							}else {
								keysToBeRemoved.add(key);
							}
						}else {
							whiteList.add(path);
						}
					}
				}
			}
			keysToBeRemoved.forEach(objNode::remove);
		} else if (node.getNodeType() == JsonNodeType.ARRAY) {
			ArrayNode arrayNode = (ArrayNode) node;
			for (int i = arrayNode.size() - 1; i >= 0; i--) {
				JsonNode value = arrayNode.get(i);
				trimJsonWithTagFilterHelper(value, path.resolveContainerNode(i), whiteList, filter);
				if (value.getNodeType() == JsonNodeType.OBJECT || value.getNodeType() == JsonNodeType.ARRAY) {
					if (!whiteList.contains(path.resolveContainerNode(i))) {
						arrayNode.remove(i);
					} else {
						whiteList.add(path);
					}
				} else {
					arrayNode.remove(i);
				}
			}
		}
	}
	public static String nodeAsTextNullToEmptyString(JsonNode node) {
		if (node == null || node.isNull() || node.isMissingNode()) {
			return "";
		}
		return node.asText();
	}

	public static String nodeAtPointerAsTextNullToEmptyString(JsonNode node, String jsonPtrExpr) {
		JsonNode at = node.at(jsonPtrExpr);
		if (at.isNull() || at.isMissingNode()) {
			return "";
		}
		return at.asText();
	}

	public static String nodeAtPointerAsText(JsonNode node, String jsonPtrExpr) {
		JsonNode at = node.at(jsonPtrExpr);
		if (at.isNull() || at.isMissingNode()) {
			return null;
		}
		return at.asText();
	}

	public static void objectNodePutStringValueIgnoreNullAndEmpty(JsonNode node, String fieldName, String v) {
		if (node == null || v == null || v.isEmpty()) {
			return;
		}
		((ObjectNode) node).put(fieldName, v);
	}

	public static JSONObject toJsonObject(String value) {
		if (StringUtils.isNotEmpty(value)) {
			JSONParser parser = new JSONParser();
			try {
				return (JSONObject) parser.parse(value);
			} catch (ParseException e) {
				throw new IllegalArgumentException(
						"The given string value: " + value + " cannot be transformed to Json object");
			}
		}
		return null;
	}

	public static <T> T toList(String value, TypeReference<T> typeRef) {
		if (StringUtils.isNotEmpty(value)) {
			try {
				return JACKSON_OBJECT_MAPPER.readValue(value, typeRef);
			} catch (IOException e) {
				throw new IllegalArgumentException(
						"The given string value: " + value + " cannot be transformed to List object");
			}
		}
		return null;
	}

	public static JSONArray toJsonArray(String value) {
		if (StringUtils.isNotEmpty(value)) {
			JSONParser parser = new JSONParser();
			try {
				return (JSONArray) parser.parse(value);
			} catch (ParseException e) {
				throw new IllegalArgumentException(
						"The given string value: " + value + " cannot be transformed to Json object");
			}
		}
		return null;
	}

	public static <T> JSONObject convertObjectToJsonObject(T t) {
		return toJsonObject(toString(t));
	}

	public static <T> JSONArray convertObjectToJsonArray(T t) {
		return toJsonArray(toString(t));
	}

	@SuppressWarnings("unchecked")
	public static JSONObject deepCopy(JSONObject jsonObject, List<String> itermLst) {
		JSONObject result = new JSONObject();
		Set<Object> set = jsonObject.keySet();
		Iterator<Object> iterator = set.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (itermLst.contains(key)) {
				if (jsonObject.get(key) instanceof JSONObject) {
					result.put(key, deepCopy((JSONObject) jsonObject.get(key), itermLst));
				} else if (jsonObject.get(key) instanceof JSONArray) {
					result.put(key, deepCopy((JSONArray) jsonObject.get(key), itermLst));
				} else {
					result.put(key, jsonObject.get(key));
				}
			}

		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray deepCopy(JSONArray jsonArray, List<String> skipItemLsit) {
		JSONArray result = new JSONArray();
		for (Object jsonElement : jsonArray) {
			if (jsonElement instanceof JSONObject) {
				result.add(deepCopy((JSONObject) jsonElement, skipItemLsit));
			} else if (jsonElement instanceof JSONArray) {
				result.add(deepCopy((JSONArray) jsonElement, skipItemLsit));
			} else {
				result.add(jsonElement);
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String toXmlString(Object o) throws SalesBusinessException {
		try {
			if(null!=o) {
				Class<?> clazz = o.getClass();
				JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true); // remove xml prolog
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // formatted output
				final QName name = new QName(Introspector.decapitalize(clazz.getSimpleName()));
				JAXBElement jaxbElement = new JAXBElement(name, clazz, o);
				StringWriter sw = new StringWriter();
				marshaller.marshal(jaxbElement, sw);
				return sw.toString();
			}
			return null;
		} catch (JAXBException e) {
			throw new SalesBusinessException(e.getMessage());
		}
	}

	public static List<JsonNode> arrayNodeToList(JsonNode arrayNode) {
		List<JsonNode> res = new ArrayList<>();
		arrayNode.elements().forEachRemaining(res::add);
		return res;
	}

	public static JsonNode listToArrayNode(List<JsonNode> arrayList) {
		ArrayNode res = JACKSON_OBJECT_MAPPER.createArrayNode();
		arrayList.forEach(res::add);
		return res;
	}

	public static JsonNode findComponentNode(JsonNode nodeContainingComponent, int componentCodeId) {
		return Optional.ofNullable(nodeContainingComponent).map(ncc -> {
			if (ncc.isMissingNode() || ncc.isNull()) {
				return null;
			} else {
				return ncc;
			}
		}).map(ncc -> ncc.get("component")).map(componentArray -> {
			Iterable<JsonNode> iterable = componentArray::iterator;
			return StreamSupport.stream(iterable.spliterator(), false);
		}).orElse(Stream.empty()).filter(component -> componentCodeId == component.path("componentCodeId").asInt())
				.findFirst().orElse(null);
	}

	public static Optional<JsonNode> findFirstDesignDetailInComponentNode(JsonNode component, int udfId) {
		return Optional.ofNullable(component).map(cmp -> {
			if (cmp.isMissingNode() || cmp.isNull()) {
				return null;
			} else {
				return cmp;
			}
		}).map(cmp -> cmp.get("designDetails")).map(dds -> {
			Iterable<JsonNode> iterable = dds::iterator;
			return StreamSupport.stream(iterable.spliterator(), false);
		}).orElse(Stream.empty()).filter(dd -> udfId == dd.path("udfId").asInt()).findFirst();
	}

	public static Long findUdfAttributeIdFromComponentNode(JsonNode component, int udfId) {
		return findFirstDesignDetailInComponentNode(component, udfId).map(dd -> dd.get("udfAttributeId"))
				.map(dd -> dd.get(0)).map(JsonNode::asLong).orElse(null);
	}

	public static String findUdfAttributeTextFromComponentNode(JsonNode component, int udfId) {
		return findFirstDesignDetailInComponentNode(component, udfId).map(dd -> dd.get("udfAttributeText"))
				.map(dd -> dd.get(0)).map(JsonNode::asText).orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean cleanJSON(Object arg) throws JSONException{
	    boolean valueExist = false;
	   
	    if(arg instanceof LinkedHashMap){
	    	LinkedHashMap<String,Object> obj = (LinkedHashMap<String,Object>)arg;
	        Iterator<String> iter = obj.keySet().iterator();
	        ArrayList<String> fields = new ArrayList<>();
	        while(iter.hasNext())   fields.add(iter.next());
	        for(String field:fields){
	            Object value = obj.get(field);
	            if(cleanJSON(value))    valueExist = true;
	            else                    obj.remove(field);
	        }
	    }else if(arg instanceof JSONObject){
	        JSONObject obj = (JSONObject)arg;
	        Iterator<String> iter = obj.keySet().iterator();
	        ArrayList<String> fields = new ArrayList<>();
	        while(iter.hasNext())   fields.add(iter.next());
	        for(String field:fields){
	            Object value = obj.get(field);
	            if(cleanJSON(value))    valueExist = true;
	            else                    obj.remove(field);
	        }
	    }else if(arg instanceof List){
	        List<Object> arr = (List<Object>)arg;
	        for(int i=0;i<arr.size();i++){
	            if(cleanJSON(arr.get(i)))   valueExist = true;
	            else{
	                arr.remove(i);
	                i--;
	            }
	        }
	    }else if(arg instanceof JSONArray){
	        JSONArray arr = (JSONArray)arg;
	        for(int i=0;i<arr.size();i++){
	            if(cleanJSON(arr.get(i)))   valueExist = true;
	            else{
	                arr.remove(i);
	                i--;
	            }
	        }
	    }else {
	    	if(null!=arg) {
	    		if(arg instanceof String) {
	    			//check string is empty or not
	    			if(String.valueOf(arg).length() > 0) valueExist = true;
	    		}else {
	    			valueExist = true;
	    		}
	    	}
	    }
	    return valueExist;
	}
	

}
