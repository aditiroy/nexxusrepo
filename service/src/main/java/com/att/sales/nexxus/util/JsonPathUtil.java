package com.att.sales.nexxus.util;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

/**
 * <b>JsonPathUtil</b> uses the jayway jsonpath library refer 
 * <a href="https://github.com/json-path/JsonPath">https://github.com/json-path/JsonPath</a> for API level details<br/>
 * <b>JsonPathUtil</b> provides wrapper to set and search functionality on top of jsonpath api to avoid boilerplate code.
 * 
 *
 */


@Component
public class JsonPathUtil {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(JsonPathUtil.class);
	
	/** The configuration. */
	@Autowired
	private Configuration configuration= getConfiguration();
	
	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	@Bean
	public Configuration getConfiguration() {
		return Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
			    .mappingProvider(new JacksonMappingProvider()).options(Option.SUPPRESS_EXCEPTIONS)
			    .build();
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper)
	 *  or a json string,path(json path expression, check
	 *  jsonpath api readme to find more details) and the newData and adds it to the existing list or array</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param newData the new data
	 * @return the document context
	 */
	public DocumentContext add(Object jsonObject,String path,Object newData) {	
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return add(jsonString, path, newData);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return add(jsonString, path,newData);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>add:DocumentContext>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) 
	 * ,path(json path expression, check
	 *  jsonpath api readme to find more details),newData and adds it to the existing list or array, 
	 *  a dummy boolean variable to return JSON string</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param newData the new data
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	public String add(Object jsonObject,String path,Object newData,boolean returnJsonString) {
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return add(jsonString, path, newData,returnJsonString);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return add(jsonString, path,newData,returnJsonString);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>add:String>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,
	 * path(json path expression, check
	 *  jsonpath api readme to find more details),newData and adds it to the existing list or array,
	 *  pass the typereference for the typed cast</i>.
	 *
	 * @param <T> the generic type
	 * @param jsonObject the json object
	 * @param path the path
	 * @param newData the new data
	 * @param typeRef the type ref
	 * @return the t
	 */
	//xy3208 detect StackOverflow in testing
	/*
	public <T> T add(Object jsonObject,String path,Object newData,TypeReference<T> typeRef) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return add(jsonString, path, newData,typeRef);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return add(jsonString, path,newData,typeRef);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>add:T(typeRef)>>caught exception:{}",ex);
			}
			return null;
		}
	}
	*/
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) 
	 * or a json string,path(json path expression, check
	 *  jsonpath api readme to find more details) String key and the newData and Add or 
	 *  update the key with a the given value at the given path</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param key the key
	 * @param newData the new data
	 * @return the document context
	 */
	public DocumentContext put(Object jsonObject,String path,String key,Object newData) {	
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return put(jsonString, path,key, newData);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return put(jsonString, path,key,newData);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>put:DocumentContext>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,
	 * path(json path expression, check
	 *  jsonpath api readme to find more details),newData, String key and Add or update the key with a the given value at the given path,
	 *   a dummy boolean variable to return JSON string</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param key the key
	 * @param newData the new data
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	public String put(Object jsonObject,String path,String key,Object newData,boolean returnJsonString) {
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return put(jsonString, path,key,newData,returnJsonString);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return put(jsonString, path,key,newData,returnJsonString);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>put:String>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,
	 * path(json path expression, check
	 *  jsonpath api readme to find more details),newData ,String key and Add or update the key with a 
	 *  the given value at the given path,pass the typereference for the typed cast</i>.
	 *
	 * @param <T> the generic type
	 * @param jsonObject the json object
	 * @param path the path
	 * @param key the key
	 * @param newData the new data
	 * @param typeRef the type ref
	 * @return the t
	 */
	//xy3208 detect StackOverflow in testing
	/*
	public <T> T put(Object jsonObject,String path,String key,Object newData,TypeReference<T> typeRef) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return put(jsonString, path, key , newData,typeRef);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return put(jsonString, path,key,newData,typeRef);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>add:T(typeRef)>>caught exception:{}",ex);
			}
			return null;
		}
	}
	*/
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) or
	 *  a json string,path(json path expression, check
	 *  jsonpath api readme to find more details) and the newData</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param newData the new data
	 * @return the document context
	 */
	public DocumentContext set(Object jsonObject,String path,Object newData) {	
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return set(jsonString, path, newData);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return set(jsonString, path,newData);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>set:DocumentContext>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,path(json path expression, check
	 *  jsonpath api readme to find more details),newData and a dummy boolean variable to return JSON string</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param newData the new data
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	public String set(Object jsonObject,String path,Object newData,boolean returnJsonString) {
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return set(jsonString, path, newData,returnJsonString);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return set(jsonString, path,newData,returnJsonString);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>set:String>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * Sets the.
	 *
	 * @param <T> the generic type
	 * @param jsonObject the json object
	 * @param path the path
	 * @param newData the new data
	 * @param typeRef the type ref
	 * @return the t
	 */
	public <T> T set(Object jsonObject,String path,Object newData,TypeReference<T> typeRef) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return set(jsonString, path, newData,typeRef);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return set(jsonString, path,newData,typeRef);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>set:T(typeRef)>>caught exception:{}",ex);
			}
			return null;
		}
	}
		
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,path(json path expression, check
	 *  jsonpath api readme to find more details) and returns the matched Node</i>
	 *  <br><br><b>NOTE:</b><i>The result will be always a List type</i>.
	 *
	 * @param <T> the generic type
	 * @param jsonObject the json object
	 * @param path the path
	 * @return the t
	 */
	public <T> T search(Object jsonObject,String path) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return search(jsonString, path);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return search(jsonString, path);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>search:T>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param jsonObject the json object
	 * @param path the path
	 * @param typeRef the type ref
	 * @return the t
	 */
	public <T> T search(Object jsonObject,String path,TypeRef<T> typeRef) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return search(jsonString, path,typeRef);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return search(jsonString, path,typeRef);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>search:T(typeRef)>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,path(json path expression, check
	 *  jsonpath api readme to find more details) and deletes the matched Node</i>
	 *  <br><br><b>NOTE:</b><i>The result will be always a List type</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @return the document context
	 */
	public DocumentContext delete(Object jsonObject,String path) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return delete(jsonString, path);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return delete(jsonString, path);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>delete:DocumentContext>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * Delete.
	 *
	 * @param <T> the generic type
	 * @param jsonObject the json object
	 * @param path the path
	 * @param typeRef the type ref
	 * @return the t
	 */
	public <T> T delete(Object jsonObject,String path,TypeReference<T> typeRef) {		
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return delete(jsonString, path,typeRef);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return delete(jsonString, path,typeRef);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>delete:T(typeRef)>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * 	
	 * <i>This method takes an Object (which will be converted into json string using jackson mapper) ,path(json path expression, check
	 *  jsonpath api readme to find more details), a dummy boolean variable to return JSON string and deletes the matched Node</i>
	 *  <br><br><b>NOTE:</b><i>The result will be always a List type</i>.
	 *
	 * @param jsonObject the json object
	 * @param path the path
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	public String delete(Object jsonObject,String path,boolean returnJsonString) {
		if(jsonObject instanceof String) {
			String jsonString = (String) jsonObject;
			return delete(jsonString, path,returnJsonString);
		}
		else {
			try {
				String jsonString = objectMapper.writeValueAsString(jsonObject);
				return delete(jsonString, path,returnJsonString);
			}
			catch(Exception ex) {
				log.error("JsonPathUtil>>delete:String>>caught exception:{}",ex);
			}
			return null;
		}
	}
	
	/**
	 * Private methods which will be utilized by the exposed public methods.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @return the document context
	 */
	private DocumentContext delete(String jsonString,String path) {
		 DocumentContext json = JsonPath.using(configuration).parse(jsonString);
		 json.delete(path);
		 return json;
	}
	
	/**
	 * Delete.
	 *
	 * @param <T> the generic type
	 * @param jsonString the json string
	 * @param path the path
	 * @param typeRef the type ref
	 * @return the t
	 */
	private <T> T delete(String jsonString,String path,TypeReference<T> typeRef) {
		 DocumentContext parsedJson = JsonPath.using(configuration).parse(jsonString);
		 parsedJson.delete(path);
		 T finalJson = null;
		 try {
			 objectMapper = new ObjectMapper();
			 finalJson = objectMapper.readValue(parsedJson.jsonString(), typeRef);			
		 } 
		 catch (IOException ioex) {
			log.error("JsonPathUtil>>delete:T(TypeReference)>>conversion error:{}",ioex);
		 }
		 return finalJson;	
	}
	
	/**
	 * Delete.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	private String delete(String jsonString,String path,boolean returnJsonString) {
		if(returnJsonString) {
			return delete(jsonString,path).jsonString();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Sets the.
	 *
	 * @param <T> the generic type
	 * @param jsonString the json string
	 * @param path the path
	 * @param newData the new data
	 * @param typeRef the type ref
	 * @return the t
	 */
	private <T> T set(String jsonString,String path,Object newData,TypeReference<T> typeRef) {
		 DocumentContext parsedJson = JsonPath.using(configuration).parse(jsonString);
		 parsedJson.set(path, newData);
		 T finalJson = null;
		 try {
			objectMapper = new ObjectMapper();
			finalJson = objectMapper.readValue(parsedJson.jsonString(), typeRef);			
		 } 
		 catch (IOException ioex) {
			log.error("JsonPathUtil>>set:T(TypeReference)>>conversion error:{}",ioex);
		 }
		 return finalJson;
	}
	
	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param jsonString the json string
	 * @param path the path
	 * @return the t
	 */
	private <T> T search(String jsonString,String path) {
		return JsonPath.using(getAlwaysListConfigWithSuppressedException()).parse(jsonString).read(path);
	}
	
	/**
	 * Search.
	 *
	 * @param <T> the generic type
	 * @param jsonString the json string
	 * @param path the path
	 * @param typeRef the type ref
	 * @return the t
	 */
	private <T> T search(String jsonString,String path,TypeRef<T> typeRef) {
		return JsonPath.using(getAlwaysListConfigWithSuppressedException()).parse(jsonString).read(path,typeRef);
	}
	
	/**
	 * Sets the.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param newData the new data
	 * @return the document context
	 */
	private DocumentContext set(String jsonString,String path,Object newData) {
		 DocumentContext json = JsonPath.using(configuration).parse(jsonString);
		 json.set(path, newData);
		 return json;
	}

	/**
	 * Sets the.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param newData the new data
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	private String set(String jsonString,String path,Object newData,boolean returnJsonString) {
		if(returnJsonString) {
			return set(jsonString, path,newData).jsonString();
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * Adds the.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param newData the new data
	 * @return the document context
	 */
	private DocumentContext add(String jsonString,String path,Object newData) {
		 DocumentContext json = JsonPath.using(configuration).parse(jsonString);
		 json.add(path, newData);
		 return json;
	}

	/**
	 * Adds the.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param newData the new data
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	private String add(String jsonString,String path,Object newData,boolean returnJsonString) {
		if(returnJsonString) {
			return add(jsonString, path,newData).jsonString();
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * Put.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param key the key
	 * @param newData the new data
	 * @return the document context
	 */
	private DocumentContext put(String jsonString,String path,String key,Object newData) {
		 DocumentContext json = JsonPath.using(configuration).parse(jsonString);
		 json.put(path,key, newData);
		 return json;
	}

	/**
	 * Put.
	 *
	 * @param jsonString the json string
	 * @param path the path
	 * @param key the key
	 * @param newData the new data
	 * @param returnJsonString the return json string
	 * @return the string
	 */
	private String put(String jsonString,String path,String key,Object newData,boolean returnJsonString) {
		if(returnJsonString) {
			return put(jsonString, path ,key,newData).jsonString();
		}
		else {
			return null;
		}
		
	}
	
	
	/**
	 * Gets the always list config with suppressed exception.
	 *
	 * @return the always list config with suppressed exception
	 */
	private Configuration getAlwaysListConfigWithSuppressedException() {
		return Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
	    .mappingProvider(new JacksonMappingProvider()).options(Option.ALWAYS_RETURN_LIST,Option.SUPPRESS_EXCEPTIONS)
	    .build();
	}
	
}
